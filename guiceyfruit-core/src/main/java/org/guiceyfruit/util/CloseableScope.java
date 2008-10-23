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

package org.guiceyfruit.util;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.CloseErrorsImpl;
import com.google.inject.internal.CompositeCloser;
import com.google.inject.spi.CloseErrors;
import com.google.inject.spi.CloseFailedException;
import com.google.inject.spi.Closeable;
import com.google.inject.spi.Closer;
import com.google.inject.spi.Closers;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.guiceyfruit.Injectors;

/**
 * Represents a scope which caches objects around until the scope is closed.
 *
 * The scope can be closed as many times as required - there is no need to recreate
 * the scope instance each time a scope goes out of scope.
 *
 * @version $Revision: 1.1 $
 */
public class CloseableScope implements Scope, Closeable {

  private final Map<Key<?>, Object> map = Maps.newConcurrentHashMap();

  @Inject
  private Injector injector;

  @SuppressWarnings("unchecked")
  public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
    return new Provider<T>() {
      public T get() {
        Object o = map.get(key);
        if (o == null) {
          o = creator.get();
          map.put(key, o);
        }
        return (T) o;
      }
    };
  }

  /**
   * Closes all of the objects within this scope - though this object must have
   * been injected to ensure that it has access to the injector to provide
   * the available {@link Closer} implementations to use for closing.
   *
   * To use this method you should have invoked {@link  Binder#requestInjection(Object[])}
   * passing in this instance first.
   *
   * @see Binder#requestInjection(Object[])
   */
  public void close() throws CloseFailedException {
    Objects.nonNull(injector, "injector has not been injected! Please Binder.requestInjection(thisScope)");
    close(injector);
  }


  /**
   * Closes all of the objects within this scope using the given injector
   * to find the available {@link Closer} implementations to use
   */
  public void close(Injector injector) throws CloseFailedException {
    Objects.nonNull(injector, "injector");
    Set<Closer> closers = Injectors.getInstancesOf(injector, Closer.class);
    Closer closer = CompositeCloser.newInstance(closers);
    if (closer == null) {
      return;
    }
    CloseErrorsImpl errors = new CloseErrorsImpl(this);
    close(closer, errors);
    errors.throwIfNecessary();
  }

  public void close(Closer closer, CloseErrors errors) {
    Set<Entry<Key<?>,Object>> entries = map.entrySet();
    for (Entry<Key<?>, Object> entry : entries) {
      Key<?> key = entry.getKey();
      Object value = entry.getValue();
      Closers.close(key, value, closer, errors);
    }
    map.clear();
  }
}
