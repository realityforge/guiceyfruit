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

import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.annotation.Resource;
import javax.naming.Context;
import junit.framework.TestCase;
import org.guiceyfruit.jndi.internal.JndiContext;
import org.guiceyfruit.support.CloseFailedException;

/** @author james.strachan@gmail.com (James Strachan) */
public class ResourceUsingBaseModuleTest extends TestCase {

  public void testResourceInjection() throws CreationException, CloseFailedException {
    Injector injector = Guice.createInjector(new Jsr250Module() {
      protected void configure() {
        super.configure();

        bind(MyBean.class).in(Singleton.class);
      }

      @Provides
      public Context createJndiContext() throws Exception {
        Context answer = new JndiContext();
        answer.bind("foo", new AnotherBean("Foo"));
        answer.bind("xyz", new AnotherBean("XYZ"));
        return answer;
      }

      @Override
      protected void bindJndiContext() {
        // TODO this is ugly - how can we allow a provides method for a Context
        // but also have a lazily created one?
      }
    });

    MyBean bean = injector.getInstance(MyBean.class);
    assertNotNull("Should have instantiated the bean", bean);
    assertNotNull("Should have injected a foo", bean.foo);
    assertNotNull("Should have injected a bar", bean.bar);

    assertEquals("Should have injected correct foo", "Foo", bean.foo.name);
    assertEquals("Should have injected correct bar", "XYZ", bean.bar.name);
  }

  public static class MyBean {
    @Resource
    public AnotherBean foo;

    public AnotherBean bar;

    @Resource(name = "xyz")
    public void bar(AnotherBean bar) {
      this.bar = bar;
    }
  }

  static class AnotherBean {
    public String name = "undefined";

    AnotherBean(String name) {
      this.name = name;
    }
  }
}