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
import java.util.List;

/** @version $Revision: 1.1 $ */
public class OptionalCollectionResourceInjectionBean extends ResourceInjectionBean {

  @Autowired(required = false)
  protected ITestBean testBean3;

  private IndexedTestBean indexedTestBean;

  private List<NestedTestBean> nestedTestBeans;

  public List<NestedTestBean> nestedTestBeansSetter;

  @Autowired(required = false)
  public List<NestedTestBean> nestedTestBeansField;

  private ITestBean testBean4;

  @Autowired(required = false)
  public void setTestBean2(TestBean testBean2) {
    super.setTestBean2(testBean2);
  }

  @Autowired(required = false)
  private void inject(ITestBean testBean4, List<NestedTestBean> nestedTestBeans,
      IndexedTestBean indexedTestBean) {
    this.testBean4 = testBean4;
    this.indexedTestBean = indexedTestBean;
    this.nestedTestBeans = nestedTestBeans;
  }

  @Autowired(required = false)
  public void setNestedTestBeans(List<NestedTestBean> nestedTestBeans) {
    this.nestedTestBeansSetter = nestedTestBeans;
  }

  public ITestBean getTestBean3() {
    return this.testBean3;
  }

  public ITestBean getTestBean4() {
    return this.testBean4;
  }

  public IndexedTestBean getIndexedTestBean() {
    return this.indexedTestBean;
  }

  public List<NestedTestBean> getNestedTestBeans() {
    return this.nestedTestBeans;
  }
}
