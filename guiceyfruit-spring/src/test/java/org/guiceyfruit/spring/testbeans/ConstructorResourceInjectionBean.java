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

import org.guiceyfruit.spring.testbeans.ResourceInjectionBean;
import org.guiceyfruit.spring.testbeans.ITestBean;
import org.guiceyfruit.spring.testbeans.NestedTestBean;
import org.guiceyfruit.spring.testbeans.TestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/** @version $Revision: 1.1 $ */
public class ConstructorResourceInjectionBean extends ResourceInjectionBean {

  @Autowired
  protected ITestBean testBean3;

  private ITestBean testBean4;

  private NestedTestBean nestedTestBean;

  private ConfigurableListableBeanFactory beanFactory;

  public ConstructorResourceInjectionBean() {
    throw new UnsupportedOperationException();
  }

  public ConstructorResourceInjectionBean(ITestBean testBean3) {
    throw new UnsupportedOperationException();
  }

  @Autowired
  public ConstructorResourceInjectionBean(ITestBean testBean4, NestedTestBean nestedTestBean,
      ConfigurableListableBeanFactory beanFactory) {
    this.testBean4 = testBean4;
    this.nestedTestBean = nestedTestBean;
    this.beanFactory = beanFactory;
  }

  public ConstructorResourceInjectionBean(NestedTestBean nestedTestBean) {
    throw new UnsupportedOperationException();
  }

  public ConstructorResourceInjectionBean(ITestBean testBean3, ITestBean testBean4,
      NestedTestBean nestedTestBean) {
    throw new UnsupportedOperationException();
  }

  @Autowired
  public void setTestBean2(TestBean testBean2) {
    super.setTestBean2(testBean2);
  }

  public ITestBean getTestBean3() {
    return this.testBean3;
  }

  public ITestBean getTestBean4() {
    return this.testBean4;
  }

  public NestedTestBean getNestedTestBean() {
    return this.nestedTestBean;
  }

  public ConfigurableListableBeanFactory getBeanFactory() {
    return this.beanFactory;
  }
}
