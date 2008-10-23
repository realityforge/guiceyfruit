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

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.spi.CloseErrors;
import com.google.inject.spi.Closeable;
import com.google.inject.spi.Closer;
import com.google.inject.spi.Closers;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a scope which caches objects around until the scope is closed
 *
 * @version $Revision: 1.1 $
 */
public class CloseableScope implements Scope, Closeable {

  private final Map<Key<?>, Object> map = new HashMap<Key<?>, Object>();

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

  public void close(Closer closer, CloseErrors errors) {
    Set<Entry<Key<?>, Object>> entries = map.entrySet();
    for (Entry<Key<?>, Object> entry : entries) {
      Key<?> key = entry.getKey();
      Object value = entry.getValue();
      Closers.close(key, value, closer, errors);
    }
  }
}
