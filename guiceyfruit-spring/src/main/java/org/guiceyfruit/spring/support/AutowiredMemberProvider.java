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

package org.guiceyfruit.spring.support;

import com.google.inject.Binding;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Iterables;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Preconditions;
import com.google.inject.internal.Sets;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.spring.NoAutowire;
import org.guiceyfruit.support.AnnotationMemberProviderSupport;
import org.guiceyfruit.support.Comparators;
import org.guiceyfruit.support.Predicate;
import org.guiceyfruit.support.Strings;
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
    Predicate<Binding> filter = createQualifierFilter(member);

    Class<?> type = typeLiteral.getRawType();
    if (type.isArray()) {
      return provideArrayValue(member, typeLiteral, memberType, filter);
    }
    else if (Collection.class.isAssignableFrom(type)) {
      Collection collection = createCollection(type);
      return provideCollectionValues(collection, member, typeLiteral, filter);
    }
    else if (Map.class.isAssignableFrom(type)) {
      Map map = createMap(type);
      return provideMapValues(map, member, typeLiteral, filter);
    }
    else {
      return provideSingleValue(member, type, annotation, filter);
    }
  }

  /**
   * Returns a new filter on the given member to respect the use of {@link Qualifier} annotations or
   * annotations annotated with {@link Qualifier}
   */
  protected Predicate<Binding> createQualifierFilter(Member member) {
    Predicate<Binding> filter = null;
    if (member instanceof AnnotatedElement) {
      AnnotatedElement annotatedElement = (AnnotatedElement) member;
      final Qualifier qualifier = annotatedElement.getAnnotation(Qualifier.class);
      if (qualifier != null) {
        final String expectedValue = qualifier.value();
        final boolean notEmptyValue = Strings.isNotEmpty(expectedValue);
        return new Predicate<Binding>() {
          public boolean matches(Binding binding) {
            String value = annotationName(binding);

            // we cannot use @Qualified as a binding annotation
            // so we can't test for just a @Qualified binding with no text
            // so lets just test for a non-empty string
            if (notEmptyValue) {
              return Comparators.equal(expectedValue, value);
            }
            else {
              return Strings.isNotEmpty(value);
            }
          }

          @Override
          public String toString() {
            return "@Autowired @Qualifier(" + expectedValue + ")";
          }
        };
      }

      // lets iterate through all of the annotations looking for a qualifier
      Set<Annotation> qualifiedAnnotations = Sets.newHashSet();
      Annotation[] annotations = annotatedElement.getAnnotations();
      for (Annotation annotation : annotations) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Qualifier qualified = annotationType.getAnnotation(Qualifier.class);
        if (qualified != null) {
          // we can only support qualified annotations which are also annotated with Guice's
          // @BindingAnnotation
          if (annotationType.getAnnotation(BindingAnnotation.class) != null) {
            qualifiedAnnotations.add(annotation);
          }
        }
      }
      int size = qualifiedAnnotations.size();
      if (size == 1) {
        final Annotation annotation = Iterables.getOnlyElement(qualifiedAnnotations);
        return new Predicate<Binding>() {
          public boolean matches(Binding binding) {
            Annotation actualAnnotation = binding.getKey().getAnnotation();
            return actualAnnotation != null && actualAnnotation.equals(annotation);
          }

          @Override
          public String toString() {
            return "@Autowired " + annotation;
          }
        };
      }
    }
    return new Predicate<Binding>() {
      public boolean matches(Binding binding) {
        return true;
      }

      @Override
      public String toString() {
        return "@Autowired";
      }
    };
  }

  protected Object provideSingleValue(Member member, Class<?> type, Autowired annotation,
      Predicate<Binding> filter) {
    Set<Binding<?>> set = getSortedBindings(type, filter);
    int size = set.size();
    if (size == 1) {
      Binding<?> binding = Iterables.getOnlyElement(set);
      return binding.getProvider().get();
    }
    else if (size == 0) {
      // should we at least try and create one
      try {
        Binding<?> binding = injector.getBinding(type);
        if (filter.matches(binding)) {
          return binding.getProvider().get();
        }
        else {
          if (annotation.required()) {
            throw new ProvisionException("Could not find required binding for " + filter + " when injecting " + member);
          }
          return null;
        }
      }
      catch (Exception e) {
        // TODO should we log the warning that we can't resolve this?
        if (annotation.required()) {
          if (e instanceof ProvisionException) {
            throw (ProvisionException) e;
          }
          throw new ProvisionException(
              "Could not resolve type " + type.getCanonicalName() + " with filter " + filter
                  + " when injecting " + member + ": " + e, e);
        }
        return null;
      }
      //throw new ProvisionException("No binding could be found for " + type.getCanonicalName());
    }
    else {
      throw new ProvisionException(
          "Too many bindings " + size + " found for " + type.getCanonicalName() + " with keys "
              + keys(set) + " when injecting " + member);
    }
  }

  /** Returns the keys used in the given bindings */
  public static List<Key<?>> keys(Iterable<Binding<?>> bindings) {
    List<Key<?>> answer = Lists.newArrayList();
    for (Binding<?> binding : bindings) {
      answer.add(binding.getKey());
    }
    return answer;
  }

  protected Object provideArrayValue(Member member, TypeLiteral<?> type, Class<?> memberType,
      Predicate<Binding> filter) {
    Class<?> componentType = memberType.getComponentType();
    Set<Binding<?>> set = getSortedBindings(componentType, filter);
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

  private Collection provideCollectionValues(Collection collection, Member member,
      TypeLiteral<?> type, Predicate<Binding> filter) {
    Type typeInstance = type.getType();
    if (typeInstance instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) typeInstance;
      Type[] arguments = parameterizedType.getActualTypeArguments();
      if (arguments.length == 1) {
        Type argument = arguments[0];
        if (argument instanceof Class) {
          Class<?> componentType = (Class<?>) argument;
          if (componentType != Object.class) {
            Set<Binding<?>> set = getSortedBindings(componentType, filter);
            if (set.isEmpty()) {
              // TODO return null or empty collection if nothing to inject?
              return null;
            }
            for (Binding<?> binding : set) {
              Object value = binding.getProvider().get();
              collection.add(value);
            }
            return collection;
          }
        }
      }
    }
    // TODO return null or empty collection if nothing to inject?
    return null;
  }

  protected Map provideMapValues(Map map, Member member, TypeLiteral<?> type,
      Predicate<Binding> filter) {
    Type typeInstance = type.getType();
    if (typeInstance instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) typeInstance;
      Type[] arguments = parameterizedType.getActualTypeArguments();
      if (arguments.length == 2) {
        Type key = arguments[0];
        if (key instanceof Class) {
          Class<?> keyType = (Class<?>) key;
          if (keyType != Object.class && keyType != String.class) {
            throw new ProvisionException(
                "Cannot inject Map instances with a key type of " + keyType.getName() + " for "
                    + member);
          }
          Type valueType = arguments[1];
          if (valueType instanceof Class) {
            Class<?> componentType = (Class<?>) valueType;
            if (componentType != Object.class) {
              Set<Binding<?>> set = getSortedBindings(componentType, filter);
              if (set.isEmpty()) {
                // TODO return null or empty collection if nothing to inject?
                return null;
              }
              for (Binding<?> binding : set) {
                Object keyValue = binding.getKey().toString();
                Object value = binding.getProvider().get();
                map.put(keyValue, value);
              }
              return map;
            }
          }
        }
      }
    }
    // TODO return null or empty collection if nothing to inject?
    return null;
  }

  protected Map createMap(Class<?> type) {
    Object answer = tryCreateInstance(type);
    if (answer instanceof Map) {
      return (Map) answer;
    }
    else if (SortedMap.class.isAssignableFrom(type)) {
      return new TreeMap();
    }
    return new HashMap();
  }

  protected Collection createCollection(Class<?> type) {
    Object answer = tryCreateInstance(type);
    if (answer instanceof Collection) {
      return (Collection) answer;
    }
    else if (SortedSet.class.isAssignableFrom(type)) {
      return new TreeSet();
    }
    else if (Set.class.isAssignableFrom(type)) {
      return new HashSet();
    }
    return new ArrayList();
  }

  /**
   * Returns a new instance of the given class if its a public non abstract class which has a public
   * zero argument constructor otherwise returns null
   */
  protected Object tryCreateInstance(Class<?> type) {
    Object answer = null;
    int modifiers = type.getModifiers();
    if (!Modifier.isAbstract(modifiers) && Modifier.isPublic(modifiers) && !type.isInterface()) {
      // if its a concrete class with no args make one
      Constructor<?> constructor = null;
      try {
        constructor = type.getConstructor();
      }
      catch (NoSuchMethodException e) {
        // ignore
      }
      if (constructor != null) {
        if (Modifier.isPublic(constructor.getModifiers())) {
          try {
            answer = constructor.newInstance();
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
    }
    return answer;
  }

  protected Set<Binding<?>> getSortedBindings(Class<?> type, Predicate<Binding> filter) {
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
    Set<Binding<?>> bindings = Injectors.getBindingsOf(injector, type);
    for (Binding<?> binding : bindings) {
      if (isValidAutowireBinding(binding) && filter.matches(binding)) {
        answer.add(binding);
      }
    }
    return answer;
  }

  protected boolean isValidAutowireBinding(Binding<?> binding) {
    Key<?> key = binding.getKey();
    Annotation annotation = key.getAnnotation();
    if (annotation instanceof NoAutowire) {
      return false;
    }
    Class<? extends Annotation> annotationType = key.getAnnotationType();
    if (annotationType != null && NoAutowire.class.isAssignableFrom(annotationType)) {
      return false;
    }
    return true;
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
