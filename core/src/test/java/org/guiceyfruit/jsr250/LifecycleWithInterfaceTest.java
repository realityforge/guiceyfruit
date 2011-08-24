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

package org.guiceyfruit.jsr250;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import junit.framework.TestCase;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.support.CloseFailedException;

/** @author james.strachan@gmail.com (James Strachan) */
public class LifecycleWithInterfaceTest extends TestCase {

  public void testBeanLifecyclesWhenUsingInterfaceAsKey() throws CreationException, CloseFailedException {
    Injector injector = Guice.createInjector(new Jsr250Module(), new AbstractModule() {
      protected void configure() {
        bind(MyInterface.class).to(MyBean.class).in(Singleton.class);
      }
    });

    MyBean bean = (MyBean) injector.getInstance(MyInterface.class);
    assertNotNull("Should have instantiated the bean", bean);
    assertTrue("The post construct lifecycle should have been invoked on bean", bean.postConstruct);

    AnotherBean another = bean.another;
    assertNotNull("Should have instantiated the another", another);
    assertTrue("The post construct lifecycle should have been invoked on another",
        another.postConstruct);

    assertFalse("The pre destroy lifecycle not should have been invoked on bean", bean.preDestroy);
    Injectors.close(injector);

    assertTrue("The pre destroy lifecycle should have been invoked on bean", bean.preDestroy);

    // if we look up a bean using the implementation type as the key then we're not a singleton any more
    bean = injector.getInstance(MyBean.class);
    assertFalse("The pre destroy lifecycle not should have been invoked on bean", bean.preDestroy);
  }

  public void testBeanLifecyclesWhenBindingImplementationAsSingleton() throws CreationException, CloseFailedException {
    Injector injector = Guice.createInjector(new Jsr250Module(), new AbstractModule() {
      protected void configure() {
        bind(MyInterface.class).to(MyBean.class);
        bind(MyBean.class).in(Singleton.class);
      }
    });

    MyBean bean = (MyBean) injector.getInstance(MyInterface.class);
    assertNotNull("Should have instantiated the bean", bean);
    assertTrue("The post construct lifecycle should have been invoked on bean", bean.postConstruct);

    AnotherBean another = bean.another;
    assertNotNull("Should have instantiated the another", another);
    assertTrue("The post construct lifecycle should have been invoked on another",
        another.postConstruct);

    assertFalse("The pre destroy lifecycle not should have been invoked on bean", bean.preDestroy);
    Injectors.close(injector);

    assertTrue("The pre destroy lifecycle should have been invoked on bean", bean.preDestroy);

    bean = injector.getInstance(MyBean.class);
    assertTrue("The pre destroy lifecycle not should have been invoked on bean", bean.preDestroy);
  }

  static interface MyInterface {
  }

  public static class MyBean implements MyInterface {
    @Inject
    public AnotherBean another;

    public boolean postConstruct;
    public boolean preDestroy;

    @PostConstruct
    public void postConstruct() throws Exception {
      postConstruct = true;
    }

    @PreDestroy
    public void preDestroy() throws Exception {
      preDestroy = true;
    }
  }

  static class AnotherBean {
    public boolean postConstruct;

    @PostConstruct
    public void postConstruct() throws Exception {
      postConstruct = true;
    }
  }
}