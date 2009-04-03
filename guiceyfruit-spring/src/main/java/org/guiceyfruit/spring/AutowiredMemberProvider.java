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

package org.guiceyfruit.spring;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.internal.Iterables;
import com.google.inject.internal.Preconditions;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Set;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.support.AnnotationMemberProviderSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Creates a value for an {@link Autowired} member with an optional {@link Qualifier} annotation
 *
 * @version $Revision: 1.1 $
 */
public class AutowiredMemberProvider extends AnnotationMemberProviderSupport<Autowired> {

  private final Injector injector;

  @Inject
  public AutowiredMemberProvider(Injector injector) {
    Preconditions.checkNotNull(injector, "injector");
    this.injector = injector;
  }

  protected Object provide(Autowired annotation, Member member, Class<?> type) {
    Qualifier qualifier = null;
    if (member instanceof AnnotatedElement) {
      AnnotatedElement annotatedElement = (AnnotatedElement) member;
      qualifier = annotatedElement.getAnnotation(Qualifier.class);
    }

    if (qualifier != null) {
      Key<?> key = Key.get(type, qualifier);
      return injector.getInstance(key);
    }
    else {
      if (type.isArray()) {
        Class<?> componentType = type.getComponentType();
        Set<Binding<?>> set = Injectors.getBindingsOf(injector, componentType);
        // TODO should we return an empty array when no matches?
        // FWIW Spring seems to return null
        if (set.isEmpty()) {
          return null;
        }
        Object array = Array.newInstance(componentType, set.size());
        int index = 0;
        for (Binding<?> binding : set) {
          Object value = binding.getProvider().get();
          Array.set(array, index++, value);
        }
        return array;
      }
      else {
        Set<Binding<?>> set = Injectors.getBindingsOf(injector, type);
        int size = set.size();
        if (size == 1) {
          Binding<?> binding = Iterables.getOnlyElement(set);
          return binding.getProvider().get();
        }
        else if (size == 0) {
          // should we at least try and create one
          try {
            return injector.getInstance(type);
          }
          catch (Exception e) {
            // TODO should we log the warning that we can't resolve this?
            if (annotation.required()) {
              if (e instanceof ProvisionException) {
                throw (ProvisionException) e;
              }
              throw new ProvisionException("Could not resolve type " + type.getCanonicalName() + ": " + e, e);
            }
            return null;
          }
          //throw new ProvisionException("No binding could be found for " + type.getCanonicalName());
        }
        else {
          throw new ProvisionException(
              "Too many bindings " + size + " found for " + type.getCanonicalName());
        }
      }
    }
  }

}
