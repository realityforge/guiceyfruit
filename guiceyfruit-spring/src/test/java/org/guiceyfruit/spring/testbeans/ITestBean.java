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

package org.guiceyfruit.spring.testbeans;

import org.guiceyfruit.spring.testbeans.INestedTestBean;
import org.guiceyfruit.spring.testbeans.IndexedTestBean;
import java.io.IOException;

/** @version $Revision: 1.1 $ */
public interface ITestBean {

  int getAge();

  void setAge(int age);

  String getName();

  void setName(String name);

  ITestBean getSpouse();

  void setSpouse(ITestBean spouse);

  ITestBean[] getSpouses();

  String[] getStringArray();

  void setStringArray(String[] stringArray);

  /** Throws a given (non-null) exception. */
  void exceptional(Throwable t) throws Throwable;

  Object returnsThis();

  INestedTestBean getDoctor();

  INestedTestBean getLawyer();

  IndexedTestBean getNestedIndexedBean();

  /**
   * Increment the age by one.
   *
   * @return the previous age
   */
  int haveBirthday();

  void unreliableFileOperation() throws IOException;

}
