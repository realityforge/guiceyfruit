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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.BeanFactory;

/** @version $Revision: 1.1 $ */
public class ExtendedResourceInjectionBean<T> extends ResourceInjectionBean {

  @Autowired
  protected ITestBean testBean3;

  private T nestedTestBean;

  private ITestBean testBean4;

  private BeanFactory beanFactory;

  public ExtendedResourceInjectionBean() {
  }

  @Autowired
  @Required
  public void setTestBean2(TestBean testBean2) {
    super.setTestBean2(testBean2);
  }

  @Autowired
  private void inject(ITestBean testBean4, T nestedTestBean) {
    this.testBean4 = testBean4;
    this.nestedTestBean = nestedTestBean;
  }

  @Autowired
  protected void initBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public ITestBean getTestBean3() {
    return this.testBean3;
  }

  public ITestBean getTestBean4() {
    return this.testBean4;
  }

  public T getNestedTestBean() {
    return this.nestedTestBean;
  }

  public BeanFactory getBeanFactory() {
    return this.beanFactory;
  }
}
