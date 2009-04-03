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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/** @version $Revision: 1.1 $ */
public class Members {

  /**
   * Returns the type of the value to be injected into the member which is either the type of the
   * field or the type of the first parameter on a method
   */
  public static Class<?> getInjectionValueType(Member member) {
    if (member instanceof Constructor) {
      Constructor constructor = (Constructor) member;
      return oneParameterType(member, constructor.getParameterTypes());
    }
    else if (member instanceof Method) {
      Method method = (Method) member;
      return oneParameterType(member, (Class[]) method.getParameterTypes());
    }
    else if (member instanceof Field) {
      Field field = (Field) member;
      return field.getType();
    }
    else {
      throw new IllegalArgumentException("Unknown Member");
    }
  }

  /**
   * Returns the first parameter type if there is only one or throws an exception if there is not
   * only one parameter
   */
  private static Class<?> oneParameterType(Member member, Class[] types) {
    if (types.length == 1) {
      return types[0];
    }
    if (types.length > 1) {
      throw new IllegalArgumentException("There must only be one parameter on " + member);
    }
    else {
      throw new IllegalArgumentException("Must take a parameter " + member);
    }
  }
}
