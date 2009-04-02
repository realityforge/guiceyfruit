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

package org.guiceyfruit.support;

import static com.google.inject.internal.Preconditions.checkArgument;
import static com.google.inject.internal.Preconditions.checkNotNull;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.TypeLiteral;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/** @version $Revision: 1.1 $ */
public class Matchers {

  /**
   * Returns a matcher which matches classes which have a public method annotated with a given
   * annotation.
   */
  public static Matcher<? super TypeLiteral<?>> methodAnnotatedWith(final Annotation annotation) {
    return new MethodAnnotatedWith(annotation);
  }

  /**
   * Returns a matcher which matches classes which have a public method annotated with a given
   * annotation.
   */
  public static Matcher<? super TypeLiteral<?>> methodAnnotatedWith(
      final Class<? extends Annotation> annotationType) {
    return new MethodAnnotatedWith(annotationType);
  }

  private static class MethodAnnotatedWith<T extends TypeLiteral<?>> extends AbstractMatcher<T> implements Serializable {
    private final Class<? extends Annotation> annotationType;

    public MethodAnnotatedWith(Annotation annotation) {
      checkNotNull(annotation, "annotation");
      this.annotationType = annotation.annotationType();
      checkForRuntimeRetention(annotationType);
    }

    public MethodAnnotatedWith(Class<? extends Annotation> annotationType) {
      checkNotNull(annotationType, "annotationType");
      this.annotationType = annotationType;
      checkForRuntimeRetention(annotationType);
    }

    public boolean matches(T type) {
      if (matchesClass(type.getRawType())) {
        return true;
      }
      return false;
    }

    protected boolean matchesClass(Class<?> type) {
      Method[] methods = type.getDeclaredMethods();
      for (Method method : methods) {
        Annotation fromElement = method.getAnnotation(annotationType);
        if (fromElement != null) {
          return true;
        }
      }
      if (!Object.class.equals(type)) {
        Class superclass = type.getSuperclass();
        if (superclass != null) {
          return matchesClass(superclass);
        }
      }
      return false;
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof MethodAnnotatedWith && ((MethodAnnotatedWith) other).annotationType
          .equals(annotationType);
    }

    @Override
    public int hashCode() {
      return 37 * annotationType.hashCode();
    }

    @Override
    public String toString() {
      return "methodAnnotatedWith(" + annotationType + ")";
    }

    private static final long serialVersionUID = 0;
  }

  // TODO the following code is because we can't derive from the Guice class
  //-------------------------------------------------------------------------
  private static void checkForRuntimeRetention(Class<? extends Annotation> annotationType) {
    Retention retention = annotationType.getAnnotation(Retention.class);
    checkArgument(retention != null && retention.value() == RetentionPolicy.RUNTIME,
        "Annotation " + annotationType.getSimpleName() + " is missing RUNTIME retention");
  }

  public static Matcher<Object> any() {
    return com.google.inject.matcher.Matchers.any();
  }

  /** Inverts the given matcher. */
  public static <T> Matcher<T> not(final Matcher<? super T> p) {
    return com.google.inject.matcher.Matchers.not(p);
  }

  /** Returns a matcher which matches elements (methods, classes, etc.) with a given annotation. */
  public static Matcher<AnnotatedElement> annotatedWith(
      final Class<? extends Annotation> annotationType) {
    return com.google.inject.matcher.Matchers.annotatedWith(annotationType);
  }

  /** Returns a matcher which matches elements (methods, classes, etc.) with a given annotation. */
  public static Matcher<AnnotatedElement> annotatedWith(final Annotation annotation) {
    return com.google.inject.matcher.Matchers.annotatedWith(annotation);
  }

  /** Returns a matcher which matches subclasses of the given type (as well as the given type). */
  public static Matcher<Class> subclassesOf(final Class<?> superclass) {
    return com.google.inject.matcher.Matchers.subclassesOf(superclass);
  }

  /** Returns a matcher which matches objects equal to the given object. */
  public static Matcher<Object> only(Object value) {
    return com.google.inject.matcher.Matchers.only(value);
  }

  /** Returns a matcher which matches only the given object. */
  public static Matcher<Object> identicalTo(final Object value) {
    return com.google.inject.matcher.Matchers.identicalTo(value);
  }

  /**
   * Returns a matcher which matches classes in the given package. Packages are specific to their
   * classloader, so classes with the same package name may not have the same package at runtime.
   */
  public static Matcher<Class> inPackage(final Package targetPackage) {
    return com.google.inject.matcher.Matchers.inPackage(targetPackage);
  }

  /**
   * Returns a matcher which matches classes in the given package and its subpackages. Unlike {@link
   * #inPackage(Package) inPackage()}, this matches classes from any classloader.
   *
   * @since 2.0
   */
  public static Matcher<Class> inSubpackage(final String targetPackageName) {
    return com.google.inject.matcher.Matchers.inSubpackage(targetPackageName);
  }

  /** Returns a matcher which matches methods with matching return types. */
  public static Matcher<Method> returns(final Matcher<? super Class<?>> returnType) {
    return com.google.inject.matcher.Matchers.returns(returnType);
  }

}
