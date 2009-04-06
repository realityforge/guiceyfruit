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

import org.guiceyfruit.spring.testbeans.ITestBean;
import org.guiceyfruit.spring.testbeans.NestedTestBean;
import org.springframework.beans.factory.annotation.Autowired;

/** @version $Revision: 1.1 $ */
public class ConstructorsResourceInjectionBean {

  protected ITestBean testBean3;

  private ITestBean testBean4;

  private NestedTestBean[] nestedTestBeans;

  public ConstructorsResourceInjectionBean() {
  }

  @Autowired(required = false)
  public ConstructorsResourceInjectionBean(ITestBean testBean3) {
    this.testBean3 = testBean3;
  }

  @Autowired(required = false)
  public ConstructorsResourceInjectionBean(ITestBean testBean4,
      NestedTestBean[] nestedTestBeans) {
    this.testBean4 = testBean4;
    this.nestedTestBeans = nestedTestBeans;
  }

  public ConstructorsResourceInjectionBean(NestedTestBean nestedTestBean) {
    throw new UnsupportedOperationException();
  }

  public ConstructorsResourceInjectionBean(ITestBean testBean3, ITestBean testBean4,
      NestedTestBean nestedTestBean) {
    throw new UnsupportedOperationException();
  }

  public ITestBean getTestBean3() {
    return this.testBean3;
  }

  public ITestBean getTestBean4() {
    return this.testBean4;
  }

  public NestedTestBean[] getNestedTestBeans() {
    return this.nestedTestBeans;
  }
}
