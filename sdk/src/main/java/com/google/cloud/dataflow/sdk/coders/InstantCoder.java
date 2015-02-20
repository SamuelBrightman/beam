/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.dataflow.sdk.coders;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.joda.time.Instant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A InstantCoder encodes joda Instant.
 */
@SuppressWarnings("serial")
public class InstantCoder extends AtomicCoder<Instant> {
  @JsonCreator
  public static InstantCoder of() {
    return INSTANCE;
  }

  /////////////////////////////////////////////////////////////////////////////

  private static final InstantCoder INSTANCE = new InstantCoder();

  private InstantCoder() {}

  @Override
  public void encode(Instant value, OutputStream outStream, Context context)
      throws CoderException, IOException {
    // Shift the millis by Long.MIN_VALUE so that negative values sort before positive
    // values when encoded.  The overflow is well-defined:
    // http://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.18.2
    BigEndianLongCoder.of().encode(value.getMillis() - Long.MIN_VALUE, outStream, context);
  }

  @Override
  public Instant decode(InputStream inStream, Context context)
      throws CoderException, IOException {
      return new Instant(BigEndianLongCoder.of().decode(inStream, context) + Long.MIN_VALUE);
  }

  @Override
  @Deprecated
  public boolean isDeterministic() {
    return true;
  }

  @Override
  public void verifyDeterministic() { }
}
