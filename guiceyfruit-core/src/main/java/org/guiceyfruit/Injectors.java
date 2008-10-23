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
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import org.guiceyfruit.jndi.GuiceInitialContextFactory;
import org.guiceyfruit.jndi.internal.Classes;

/** @version $Revision: 1.1 $ */
public class Injectors {
  public static final String MODULE_CLASS_NAMES = "org.guiceyfruit.modules";

  /**
   * Creates an injector from the given properties, loading any modules define by the {@link
   * #MODULE_CLASS_NAMES} property value (space separated) along with any other modules passed as
   * an argument.
   *
   * @param environment the properties used to create the injector
   * @param overridingModules any modules which override the modules referenced in the environment
   * such as to provide the actual JNDI context
   */
  public static Injector createInjector(final Map environment, Module... overridingModules)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    List<Module> modules = Lists.newArrayList();

    // lets bind the properties
    modules.add(new AbstractModule() {
      protected void configure() {
        Names.bindProperties(binder(), environment);
      }
    });

    Object moduleValue = environment.get(MODULE_CLASS_NAMES);
    if (moduleValue instanceof String) {
      String names = (String) moduleValue;
      StringTokenizer iter = new StringTokenizer(names);
      while (iter.hasMoreTokens()) {
        String moduleName = iter.nextToken();
        Module module = loadModule(moduleName);
        if (module != null) {
          modules.add(module);
        }
      }
    }
    Injector injector = Guice.createInjector(Modules.override(modules).with(overridingModules));
    return injector;
  }

  /**
   * Returns a collection of all instances of the given base type
   *
   * @param baseClass the base type of objects required
   * @param <T> the base type
   * @return a set of objects returned from this injector
   */
  public static <T> Set<T> getInstancesOf(Injector injector, Class<T> baseClass) {
    Set<T> answer = Sets.newHashSet();
    Set<Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
    for (Entry<Key<?>, Binding<?>> entry : entries) {
      Key<?> key = entry.getKey();
      Class<?> keyType = getKeyType(key);
      if (keyType != null && baseClass.isAssignableFrom(keyType)) {
        Binding<?> binding = entry.getValue();
        Object value = binding.getProvider().get();
        if (value != null) {
          T castValue = baseClass.cast(value);
          answer.add(castValue);
        }
      }
    }
    return answer;
  }

  /**
   * Returns a collection of all instances matching the given matcher
   *
   * @param matcher matches the types to return instances
   * @return a set of objects returned from this injector
   */
  public static <T> Set<T> getInstancesOf(Injector injector, Matcher<Class> matcher) {
    Set<T> answer = Sets.newHashSet();
    Set<Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
    for (Entry<Key<?>, Binding<?>> entry : entries) {
      Key<?> key = entry.getKey();
      Class<?> keyType = getKeyType(key);
      if (keyType != null && matcher.matches(keyType)) {
        Binding<?> binding = entry.getValue();
        Object value = binding.getProvider().get();
        answer.add((T) value);
      }
    }
    return answer;
  }

  /** Returns the key type of the given key */
  public static <T> Class<?> getKeyType(Key<?> key) {
    Class<?> keyType = null;
    TypeLiteral<?> typeLiteral = key.getTypeLiteral();
    Type type = typeLiteral.getType();
    if (type instanceof Class) {
      keyType = (Class<?>) type;
    }
    return keyType;
  }

  protected static Module loadModule(String moduleName)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    Class<?> type = Classes.loadClass(moduleName, GuiceInitialContextFactory.class.getClassLoader())
        ;
    return (Module) type.newInstance();
  }

  /**
   * Factory method to create a Guice Injector for some kind of test object <p/> The default
   * implementation will use the system property <code>org.guiceyfruit.modules</code> (see {@link
   * #MODULE_CLASS_NAMES} otherwise if that is not set it will look for the {@link .Configuration}
   * annotation and use the module defined on that otherwise it will try look for the inner class
   * called <code>ClassName$Configuration</code>
   */
  public static Injector createInjectorForTest(Object object) throws Exception {
    String modules = System.getProperty(MODULE_CLASS_NAMES);
    if (modules != null) {
      modules = modules.trim();
      if (modules.length() > 0) {
        System.out.println("Loading modules: " + modules);
        return createInjector(System.getProperties());
      }
    }
    Class<? extends Object> objectType = object.getClass();
    Class<? extends Module> moduleType = null;
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
    return Guice.createInjector(module);
  }
}
