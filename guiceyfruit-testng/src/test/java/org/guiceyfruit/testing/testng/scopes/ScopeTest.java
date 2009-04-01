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

package org.guiceyfruit.testing.testng.scopes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.guiceyfruit.jsr250.Jsr250Module;
import org.guiceyfruit.testing.ClassScoped;
import org.guiceyfruit.testing.testng.GuiceyTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

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

  @Test
  public void testOne() {
    if (verbose) {
      System.out.println("testOne with instance counter: " + instanceCounter);
    }

    Assert.assertNotNull(instanceCounter);
    Assert.assertNotNull(instanceCounter2);
    Assert.assertNotNull(methodCounter);
    Assert.assertNotNull(methodCounter2);
    Assert.assertNotNull(singletonCounter);
  }

  @Test
  public void testTwo() {
    if (verbose) {
      System.out.println("testTwo with instance counter: " + instanceCounter);
    }

    Assert.assertNotNull(instanceCounter);
    Assert.assertNotNull(instanceCounter2);
    Assert.assertNotNull(methodCounter);
    Assert.assertNotNull(methodCounter2);
    Assert.assertNotNull(singletonCounter);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    if (++testCounter == 2) {
      // lets make an explicit stop
      GuiceyTestCase.injectorManager.afterClasses();

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
      Assert.assertEquals(4, InstanceCounter.startCounter.get(), "InstanceCounter.startCounter");

      // Note that objects which are not associated with a scope that is closeable are never closed!
      Assert.assertEquals(0, InstanceCounter.stopCounter.get(), "InstanceCounter.stopCounter");

      Assert.assertEquals(1, MethodCounter.startCounter.get(), "MethodCounter.startCounter");
      Assert.assertEquals(1, MethodCounter.stopCounter.get(), "MethodCounter.stopCounter");

      Assert.assertEquals(1, SingletonCounter.startCounter.get(), "SingletonCounter.startCounter");
      Assert.assertEquals(1, SingletonCounter.stopCounter.get(), "SingletonCounter.stopCounter");
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