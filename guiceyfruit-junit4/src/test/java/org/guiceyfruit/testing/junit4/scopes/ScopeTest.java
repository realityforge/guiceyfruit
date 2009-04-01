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

package org.guiceyfruit.testing.junit4.scopes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.guiceyfruit.jsr250.Jsr250Module;
import org.guiceyfruit.testing.ClassScoped;
import org.guiceyfruit.testing.junit4.GuiceyJUnit4;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/** @version $Revision: 1.1 $ */
@RunWith(GuiceyJUnit4.class)
//@UseModule(Jsr250Module.class)
public class ScopeTest {
  protected static final boolean verbose = false;

  @Inject
  protected SingletonCounter singletonCounter;
  @Inject
  protected ClassCounter classCounter;
  @Inject
  protected InstanceCounter instanceCounter;
  @Inject
  protected InstanceCounter instanceCounter2;
  @Inject
  protected MethodCounter methodCounter;
  @Inject
  protected MethodCounter methodCounter2;

  @Test
  public void testOne() {
    if (verbose) {
      System.out.println("testOne with instance singletonCounter: " + instanceCounter);
    }

    Assert.assertNotNull("instanceCounter", instanceCounter);
    Assert.assertNotNull("instanceCounter2", instanceCounter2);
    Assert.assertNotNull("methodCounter", methodCounter);
    Assert.assertNotNull("methodCounter2", methodCounter2);
    Assert.assertNotNull("classCounter", classCounter);
    Assert.assertNotNull("singletonCounter", singletonCounter);
  }

  @Test
  public void testTwo() {
    if (verbose) {
      System.out.println("testTwo with instance singletonCounter: " + instanceCounter);
    }

    Assert.assertNotNull("instanceCounter", instanceCounter);
    Assert.assertNotNull("instanceCounter2", instanceCounter2);
    Assert.assertNotNull("methodCounter", methodCounter);
    Assert.assertNotNull("methodCounter2", methodCounter2);
    Assert.assertNotNull("classCounter", classCounter);
    Assert.assertNotNull("singletonCounter", singletonCounter);
  }

  @AfterClass
  public static void afterClass() {
    if (verbose) {
      System.out.printf("InstanceCounter start %s stop %s\n", InstanceCounter.startCounter.get(),
          InstanceCounter.stopCounter.get());
      System.out.printf("MethodCounter start %s stop %s\n", MethodCounter.startCounter.get(),
          MethodCounter.stopCounter.get());
      System.out.printf("SingletonCounter start %s stop %s\n", SingletonCounter.startCounter.get(),
          SingletonCounter.stopCounter.get());
    }
    Assert.assertEquals("InstanceCounter.startCounter", 4, InstanceCounter.startCounter.get());

    // Note that objects which are not associated with a scope that is closeable are never closed!
    Assert.assertEquals("InstanceCounter.stopCounter", 0, InstanceCounter.stopCounter.get());

    Assert.assertEquals("MethodCounter.startCounter", 2, MethodCounter.startCounter.get());
    Assert.assertEquals("MethodCounter.stopCounter", 2, MethodCounter.stopCounter.get());

    Assert.assertEquals("ClassCounter.startCounter", 1, ClassCounter.startCounter.get());

    // TODO if we have ran a previous test case then this will probably be set to 1
    // otherwise its zero
    // its kinda hard to reliably test this as its kinda hard for us to add assertions after the tests
    // have run
    //Assert.assertEquals("ClassCounter.stopCounter", 1, ClassCounter.stopCounter.get());

    Assert.assertEquals("SingletonCounter.startCounter", 1, SingletonCounter.startCounter.get());
    // TODO we are invoked before the singleton objects are closed
    //Assert.assertEquals("SingletonCounter.stopCounter", 1, SingletonCounter.stopCounter.get());
    Assert.assertEquals("SingletonCounter.stopCounter", 0, SingletonCounter.stopCounter.get());
  }

  @AfterClass
  public static void reset() {
    ClassCounter.reset();
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