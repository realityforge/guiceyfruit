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

package org.guiceyfruit;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import java.util.Collection;
import junit.framework.TestCase;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/** @version $Revision: 1.1 $ */
public class InjectorsTest extends TestCase {
  public void testMatchers() throws Exception {
    Injector injector = Guice.createInjector(new MyModule());

    assertMatches(Injectors.getInstancesOf(injector, A.class), hasSize(2));
    assertMatches(Injectors.getInstancesOf(injector, B.class), hasSize(1));
    assertMatches(Injectors.getInstancesOf(injector, C.class), hasSize(1));

    assertMatches(Injectors.getInstancesOf(injector, Matchers.subclassesOf(C.class).and(Matchers.annotatedWith(Blue.class))), hasSize(1));
  }


  public static class MyModule extends AbstractModule {
    protected void configure() {
      bind(C.class);
      bind(B.class);
    }
  }

  public static class A {
    public String name = "A";
  }

  public static class B extends A {
    public B() {
      name = "B";
    }
  }

  @Blue
  public static class C extends A {
    public C() {
      name = "C";
    }
  }


  // TODO hack to get around generics issue with hamcrest
  private void assertMatches(Collection<?> set, Matcher<Collection<Object>> matcher) {
    Collection<Object> list = Lists.newArrayList(set);
    assertThat(list, matcher);
  }
}
