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

package org.guiceyfruit.jndi.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.guiceyfruit.jndi.JndiBind;

/** @version $Revision: 1.1 $ */
public class ExampleModule extends AbstractModule {
  protected void configure() {
    bind(Cheese.class);
    bind(SomeBean.class);
  }

  @Provides @JndiBind("blah")
  public MyBean blah() {
    return new MyBean(new AnotherBean("Blah.another"), "Blah");
  }

  @Provides @JndiBind("foo")
  public MyBean foo() {
    return new MyBean(new AnotherBean("Foo.another"), "Foo");
  }
}
