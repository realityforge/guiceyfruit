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

package org.guiceyfruit.testing.junit3.scopes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.guiceyfruit.jsr250.Jsr250Module;
import org.guiceyfruit.testing.ClassScoped;
import org.guiceyfruit.testing.junit3.GuiceyTestCase;

/** @version $Revision: 1.1 $ */
public class ScopeTest extends GuiceyTestCase {
  protected static final boolean verbose = false;

  @Inject
  protected SingletonCounter singletonCounter;
  @Inject
  protected InstanceCounter instanceCounter;
  @Inject
  protected InstanceCounter instanceCounter2;
  @Inject
  protected MethodCounter methodCounter;
  @Inject
  protected MethodCounter methodCounter2;

  int testCounter;

  public void testOne() {
    if (verbose) {
      System.out.println("testOne with instance counter: " + instanceCounter);
    }

    assertNotNull("instanceCounter", instanceCounter);
    assertNotNull("instanceCounter2", instanceCounter2);
    assertNotNull("methodCounter", methodCounter);
    assertNotNull("methodCounter2", methodCounter2);
    assertNotNull("singletonCounter", singletonCounter);
  }

  public void testTwo() {
    if (verbose) {
      System.out.println("testTwo with instance counter: " + instanceCounter);
    }

    assertNotNull("instanceCounter", instanceCounter);
    assertNotNull("instanceCounter2", instanceCounter2);
    assertNotNull("methodCounter", methodCounter);
    assertNotNull("methodCounter2", methodCounter2);
    assertNotNull("singletonCounter", singletonCounter);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    if (++testCounter == 2) {
      // lets make an explicit stop
      injectorManager.afterClasses();

      if (verbose) {
        System.out.printf("InstanceCounter start %s stop %s\n", InstanceCounter.startCounter.get(),
            InstanceCounter.stopCounter.get());
        System.out.printf("MethodCounter start %s stop %s\n", MethodCounter.startCounter.get(),
            MethodCounter.stopCounter.get());
        System.out
            .printf("SingletonCounter start %s stop %s\n", SingletonCounter.startCounter.get(),
                SingletonCounter.stopCounter.get());
      }

      // this is only invoked afer *one* test method has run!
      assertEquals("InstanceCounter.startCounter", 4, InstanceCounter.startCounter.get());

      // Note that objects which are not associated with a scope that is closeable are never closed!
      assertEquals("InstanceCounter.stopCounter", 0, InstanceCounter.stopCounter.get());

      assertEquals("MethodCounter.startCounter", 1, MethodCounter.startCounter.get());
      assertEquals("MethodCounter.stopCounter", 1, MethodCounter.stopCounter.get());

      assertEquals("SingletonCounter.startCounter", 1, SingletonCounter.startCounter.get());
      assertEquals("SingletonCounter.stopCounter", 1, SingletonCounter.stopCounter.get());
    }
  }


  public static class TestModule extends Jsr250Module {
    @Override
    protected void configure() {
      super.configure();

      // TODO this should not be required!
      bind(SingletonCounter.class).in(Singleton.class);
      bind(ClassCounter.class).in(ClassScoped.class);
    }
  }
}