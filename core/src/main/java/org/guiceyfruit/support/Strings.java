/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guiceyfruit.support;

/** @version $Revision: 1.1 $ */
public class Strings {
  /**
   * Tests whether the value is <b>not</b> <tt>null</tt> or an empty string.
   *
   * @param value the value, if its a String it will be tested for text length as well
   * @return true if <b>not</b> empty
   */
  public static boolean isNotEmpty(String value) {
    if (value != null) {
      return value.length() > 0;
    }
    else {
      return false;
    }
  }
}
