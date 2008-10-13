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

package com.google.inject.spring;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.spi.CloseFailedException;
import junit.framework.TestCase;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/** @author james.strachan@gmail.com (James Strachan) */
public class LifecycleTest extends TestCase {

  public void testBeanInitialised() throws CreationException, CloseFailedException {
    Injector injector = Guice.createInjector(new AbstractModule() {
      protected void configure() {
        SpringIntegration.bindLifecycle(binder());

        bind(MyBean.class).in(Singleton.class);
      }
    });

    MyBean bean = injector.getInstance(MyBean.class);
    assertNotNull("Should have instantiated the bean", bean);
    assertTrue("Should have properties set on bean", bean.propertiesSet);

    assertFalse("Should not be destroyed yet", bean.destroyed);
    injector.close();

    assertTrue("Should have destroyed the bean", bean.destroyed);
  }

  static class MyBean implements InitializingBean, DisposableBean {
    public boolean propertiesSet = false;
    public boolean destroyed;

    public void afterPropertiesSet() throws Exception {
      propertiesSet = true;
    }

    public void destroy() throws Exception {
      destroyed = true;
    }
  }
}
