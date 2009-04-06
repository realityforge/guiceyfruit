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
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Iterables;
import com.google.inject.internal.Preconditions;
import com.google.inject.name.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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

  public boolean isNullParameterAllowed(Autowired annotation, Method method, Class<?> parameterType,
      int parameterIndex) {
    return !annotation.required();
  }

  protected Object provide(Autowired annotation, Member member, TypeLiteral<?> typeLiteral,
      Class<?> memberType) {
    Qualifier qualifier = null;
    if (member instanceof AnnotatedElement) {
      AnnotatedElement annotatedElement = (AnnotatedElement) member;
      qualifier = annotatedElement.getAnnotation(Qualifier.class);
    }
    if (qualifier != null) {
      Key<?> key = Key.get(typeLiteral, qualifier);
      return injector.getInstance(key);
    }
    else {
      Class<?> type = typeLiteral.getRawType();
      if (type.isArray()) {
        return provideArrayValue(member, typeLiteral, memberType);
      }
      else if (Collection.class.isAssignableFrom(type)) {
        Collection collection = createCollection(type);
        return provideCollectionValues(collection, member, typeLiteral);
      }
      else {
        return provideSingleValue(member, type, annotation);
      }
    }
  }

  protected Collection createCollection(Class<?> type) {
    if (!Modifier.isAbstract(type.getModifiers()) && !type.isInterface()) {
      // if its a concrete class with no args make one
      Constructor<?> constructor = null;
      try {
        constructor = type.getConstructor();
      }
      catch (NoSuchMethodException e) {
        // ignore
      }
      if (constructor != null) {
        try {
          return (Collection) constructor.newInstance();
        }
        catch (InstantiationException e) {
          throw new ProvisionException("Failed to instantiate " + constructor, e);
        }
        catch (IllegalAccessException e) {
          throw new ProvisionException("Failed to instantiate " + constructor, e);
        }
        catch (InvocationTargetException ie) {
          Throwable e = ie.getTargetException();
          throw new ProvisionException("Failed to instantiate " + constructor, e);
        }
      }
    }
    if (type.isAssignableFrom(SortedSet.class)) {
      return new TreeSet();
    }
    else if (type.isAssignableFrom(Set.class)) {
      return new HashSet();
    }
    return new ArrayList();
  }

  protected Object provideSingleValue(Member member, Class<?> type, Autowired annotation) {
    Set<Binding<?>> set = getSortedBindings(type);
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
          throw new ProvisionException(
              "Could not resolve type " + type.getCanonicalName() + " when injecting " + member
                  + ": " + e, e);
        }
        return null;
      }
      //throw new ProvisionException("No binding could be found for " + type.getCanonicalName());
    }
    else {
      throw new ProvisionException(
          "Too many bindings " + size + " found for " + type.getCanonicalName() + " when injecting "
              + member);
    }
  }

  protected Object provideArrayValue(Member member, TypeLiteral<?> type, Class<?> memberType) {
    Class<?> componentType = memberType.getComponentType();
    Set<Binding<?>> set = getSortedBindings(componentType);
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

  private Collection provideCollectionValues(Collection collection, Member member, TypeLiteral<?> type) {
    Type typeInstance = type.getType();
    if (typeInstance instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) typeInstance;
      Type[] arguments = parameterizedType.getActualTypeArguments();
      if (arguments.length == 1) {
        Type argument = arguments[0];
        if (argument instanceof Class) {
          Class<?> componentType = (Class<?>) argument;
          if (componentType != Object.class) {
            Set<Binding<?>> set = getSortedBindings(componentType);
            for (Binding<?> binding : set) {
              Object value = binding.getProvider().get();
              collection.add(value);
            }
            return collection;
          }
        }
      }
    }
/*
    TypeVariable<? extends Class<?>>[] typeVariables = type.getRawType().getTypeParameters();
    if (typeVariables != null && typeVariables.length == 1) {
      TypeVariable<? extends Class<?>> typeVariable = typeVariables[0];
      Class<?> componentType = typeVariable.getGenericDeclaration();
      if (componentType != Object.class) {
        Set<Binding<?>> set = getSortedBindings(componentType);
        for (Binding<?> binding : set) {
          Object value = binding.getProvider().get();
          collection.add(value);
        }
        return collection;
      }
    }
*/
    return null;
  }

  protected Set<Binding<?>> getSortedBindings(Class<?> type) {
    SortedSet<Binding<?>> answer = new TreeSet<Binding<?>>(new Comparator<Binding<?>>() {
      public int compare(Binding<?> b1, Binding<?> b2) {

        int answer = typeName(b1).compareTo(typeName(b2));
        if (answer == 0) {
          // TODO would be nice to use google colletions here but its excluded from guice
          String n1 = annotationName(b1);
          String n2 = annotationName(b2);
          if (n1 != null || n2 != null) {
            if (n1 == null) {
              return -1;
            }
            if (n2 == null) {
              return 1;
            }
            return n1.compareTo(n2);
          }
        }
        return answer;
      }
    });
    answer.addAll(Injectors.getBindingsOf(injector, type));
    return answer;
  }

  private String annotationName(Binding<?> binding) {
    Annotation annotation = binding.getKey().getAnnotation();
    if (annotation instanceof Named) {
      Named named = (Named) annotation;
      return named.value();
    }
    if (annotation instanceof Qualifier) {
      Qualifier qualifier = (Qualifier) annotation;
      return qualifier.value();
    }
    return null;
  }

  protected static String typeName(Binding<?> bindings) {
    return bindings.getKey().getTypeLiteral().getRawType().getName();
  }

}
