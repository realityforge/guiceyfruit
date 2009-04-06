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

import org.springframework.beans.factory.annotation.Autowired;

/** @version $Revision: 1.1 $ */
public class ResourceInjectionBean {

  @Autowired(required = false)
  private TestBean testBean;

  private TestBean testBean2;

  @Autowired
  public void setTestBean2(TestBean testBean2) {
    if (this.testBean2 != null) {
      throw new IllegalStateException("Already called");
    }
    this.testBean2 = testBean2;
  }

  public TestBean getTestBean() {
    return this.testBean;
  }

  public TestBean getTestBean2() {
    return this.testBean2;
  }
}
