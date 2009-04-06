/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guiceyfruit.spring;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import junit.framework.TestCase;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.support.CloseFailedException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/** @author james.strachan@gmail.com (James Strachan) */
public class LifecycleTest extends TestCase {

  // TODO: fixme!!
  public static final boolean preDestroySupported = true;

  public void testBeanInitialised() throws CreationException, CloseFailedException {
    Injector injector = Guice.createInjector(new SpringModule(), new AbstractModule() {
      protected void configure() {
        bind(MyBean.class).in(Singleton.class);
      }
    });

    MyBean bean = injector.getInstance(MyBean.class);
    assertNotNull("Should have instantiated the bean", bean);
    assertTrue("The post construct lifecycle should have been invoked on bean", bean.postConstruct);

    AnotherBean another = bean.another;
    assertNotNull("Should have instantiated the another", another);
    assertTrue("The post construct lifecycle should have been invoked on another",
        another.postConstruct);

    assertFalse("The pre destroy lifecycle not should have been invoked on bean", bean.preDestroy);
    Injectors.close(injector);

    if (preDestroySupported) {
      assertTrue("The pre destroy lifecycle should have been invoked on bean", bean.preDestroy);
    }
  }

  static class MyBean implements InitializingBean, DisposableBean {
    @Autowired
    public AnotherBean another;

    public boolean postConstruct;
    public boolean preDestroy;

    public void afterPropertiesSet() throws Exception {
      postConstruct = true;
    }

    public void destroy() throws Exception {
      preDestroy = true;
    }
  }

  static class AnotherBean  implements InitializingBean {
    public boolean postConstruct;

    public void afterPropertiesSet() throws Exception {
      postConstruct = true;
    }
  }
}