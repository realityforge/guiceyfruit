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

package com.google.inject;

import com.google.inject.spi.Closer;
import junit.framework.TestCase;

/**
 * @author james.strachan@gmail.com (James Strachan)
 */
public class CloseTest extends TestCase {

  public void testClosing() throws Exception {
    final MyCloser counter = new MyCloser();

    Injector injector = Guice.createInjector(new AbstractModule() {
      protected void configure() {
        bind(Foo.class);
        bind(MyCloser.class).toInstance(counter);
      }
    });

    Foo foo = injector.getInstance(Key.get(Foo.class));
    foo.foo();
    assertTrue(foo.invoked);

    foo = injector.getInstance(Foo.class);
    foo.foo();
    assertTrue(foo.invoked);

    injector.close();

    assertEquals(1, counter.count);
    assertSame(foo, counter.closeObject);
  }

  @Singleton
  static class Foo {
    boolean invoked;
    public void foo() {
      invoked = true;
    }
  }

  static class MyCloser implements Closer {

    int count;
    Object closeObject;

    public void close(Object object) throws Throwable {
      count++;
      this.closeObject = object;
    }
  }
}
