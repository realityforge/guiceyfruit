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

package org.guiceyfruit.testing;

import com.google.common.base.Objects;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.internal.CloseErrorsImpl;
import com.google.inject.spi.CloseErrors;
import com.google.inject.spi.CloseFailedException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.util.CloseableScope;

/**
 * Used to manage the injectors for the various injection points
 *
 * @version $Revision: 1.1 $
 */
public class InjectorManager {

  private Map<Object, Injector> injectors = new ConcurrentHashMap<Object, Injector>();
  private AtomicInteger initializeCounter = new AtomicInteger(0);
  private CloseableScope testScope = new CloseableScope();

  public void beforeClasses() {
    int counter = initializeCounter.incrementAndGet();
    if (counter > 1) {
      System.out.println("WARNING! Initialised more than once! Counter: " + counter);
    }

  }

  /** Lets close all of the injectors we have created so far */
  public void afterClasses() throws CloseFailedException {
    CloseErrors errors = new CloseErrorsImpl(this);
    Set<Entry<Object, Injector>> entries = injectors.entrySet();
    for (Entry<Object, Injector> entry : entries) {
      Object key = entry.getKey();
      Injector injector = entry.getValue();
      try {
        injector.close();
      }
      catch (CloseFailedException e) {
        errors.closeError(key, injector, e);
      }
    }
    errors.throwIfNecessary();
  }

  public void beforeTest(Object test) throws Exception {
    Objects.nonNull(test, "test");

    Class<? extends Object> testType = test.getClass();
    Injector classInjector;
    synchronized (injectors) {
      classInjector = injectors.get(testType);
      if (classInjector == null) {
        classInjector = createInjectorForTestClass(testType);
        Objects.nonNull(classInjector, "classInjector");
        injectors.put(testType, classInjector);
      }
    }

    classInjector.injectMembers(test);

/*
    TestModule testModule = new TestModule();
    Injector testInjector = classInjector.createChildInjector(testModule);
    injectors.put(test, testInjector);

    testInjector.injectMembers(test);
*/
  }

  public void afterTest(Object test) throws Exception {
    Injector injector = injectors.get(test.getClass());
    if (injector == null) {
      System.out.println("Warning - no injector available for: " + test);
    }
    else {
      testScope.close(injector);
    }

/*
    Injector injector = injectors.remove(test);
    if (injector != null) {
      injector.close();
    }
*/
  }

  public void completed() {

  }

  protected class TestModule extends AbstractModule {

    protected void configure() {
      bindScope(TestScoped.class, testScope);
    }

  }

  /**
   * Factory method to create a Guice Injector for some kind of test object <p/> The default
   * implementation will use the system property <code>org.guiceyfruit.modules</code> (see {@link
   * Injectors#MODULE_CLASS_NAMES} otherwise if that is not set it will look for the {@link
   * Configuration} annotation and use the module defined on that otherwise it will try look for the
   * inner class called <code>ClassName$Configuration</code>
   */
  protected Injector createInjectorForTestClass(Class<?> objectType)
      throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    TestModule testModule = new TestModule();
    String modules = System.getProperty(Injectors.MODULE_CLASS_NAMES);
    if (modules != null) {
      modules = modules.trim();
      if (modules.length() > 0) {
        System.out.println("Overloading Guice Modules: " + modules);
        return Injectors.createInjector(System.getProperties(), testModule);
      }
    }
    Class<? extends Module> moduleType;
    Configuration config = objectType.getAnnotation(Configuration.class);
    if (config != null) {
      moduleType = config.value();
    }
    else {
      String name = objectType.getName() + "$Configuration";
      Class<?> type;
      try {
        type = objectType.getClassLoader().loadClass(name);
      }
      catch (ClassNotFoundException e) {
        try {
          type = Thread.currentThread().getContextClassLoader().loadClass(name);
        }
        catch (ClassNotFoundException e2) {
          throw new ClassNotFoundException("Class " + name + " not found: " + e, e);
        }
      }
      try {
        moduleType = (Class<? extends Module>) type;
      }
      catch (Exception e) {
        throw new IllegalArgumentException("Class " + type.getName() + " is not a Guice Module!",
            e);
      }
    }
    //System.out.println("Creating Guice Injector from module: " + moduleType.getName());
    Module module = moduleType.newInstance();
    return Guice.createInjector(module, testModule);
  }

}
