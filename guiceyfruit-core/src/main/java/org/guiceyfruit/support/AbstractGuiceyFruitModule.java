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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectableType;
import com.google.inject.spi.InjectableType.Encounter;
import com.google.inject.spi.InjectableType.Listener;
import com.google.inject.spi.InjectionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.aopalliance.intercept.ConstructorInterceptor;

/**
 * Adds some new helper methods to the base Guice module
 *
 * @version $Revision: 1.1 $
 */
public abstract class AbstractGuiceyFruitModule extends AbstractModule {
  protected void bindConstructorInterceptor(Matcher<Class> classMatcher,
      ConstructorInterceptor constructorInterceptor) {

    // TODO

  }

  protected <A extends Annotation> void bindAnnotationMemberProvider(final Class<A> annotationType,
      final Key<? extends AnnotationMemberProvider> key) {
    bindAnnotationMemberProvider(annotationType, new EncounterProvider<AnnotationMemberProvider>() {
      public AnnotationMemberProvider get(Encounter<?> encounter) {
        Provider<? extends AnnotationMemberProvider> provider = encounter.getProvider(key);
        return provider.get();
      }
    });
  }

  protected <A extends Annotation> void bindAnnotationMemberProvider(final Class<A> annotationType,
      final Class<? extends AnnotationMemberProvider> type) {
    bindAnnotationMemberProvider(annotationType, new EncounterProvider<AnnotationMemberProvider>() {
      public AnnotationMemberProvider get(Encounter<?> encounter) {
        Provider<? extends AnnotationMemberProvider> provider = encounter.getProvider(type);
        return provider.get();
      }
    });
  }

  protected <A extends Annotation> void bindAnnotationMemberProvider(final Class<A> annotationType,
      final EncounterProvider<AnnotationMemberProvider> memberProviderProvider) {
    bindListener(Matchers.any(), new Listener() {
      public <I> void hear(InjectableType<I> injectableType, final Encounter<I> encounter) {
        System.out.println("Called on type: " + injectableType);

        Class<? super I> type = injectableType.getType().getRawType();
        Field[] fields = type.getDeclaredFields();

        for (final Field field : fields) {
          // TODO lets exclude fields with @Inject?
          final A annotation = field.getAnnotation(annotationType);
          if (annotation != null) {
            System.out.println("field: " + field);

            encounter.register(new InjectionListener<I>() {
              public void afterInjection(I injectee) {
                AnnotationMemberProvider provider = memberProviderProvider.get(encounter);
                Object value = provider.provide(annotation, field);
                if (checkInjectedValueType(value, field.getType(), encounter)) {
                  try {
                    field.setAccessible(true);
                    field.set(injectee, value);
                  }
                  catch (IllegalAccessException e) {
                    encounter.addError(e);
                  }
                }
              }
            });
          }
        }

        Method[] methods = type.getDeclaredMethods();
        for (final Method method : methods) {
          // TODO lets exclude methods with @Inject?
          final A annotation = method.getAnnotation(annotationType);
          if (annotation != null) {
            System.out.println("method: " + method);

            Class<?>[] classes = method.getParameterTypes();
            if (classes.length != 1) {
              encounter.addError("Method annotated with " + annotationType.getCanonicalName()
                  + " should only have 1 parameter but has " + classes.length);
              continue;
            }
            final Class<?> paramType = classes[0];

            encounter.register(new InjectionListener<I>() {
              public void afterInjection(I injectee) {
                AnnotationMemberProvider provider = memberProviderProvider.get(encounter);
                Object value = provider.provide(annotation, method);
                if (checkInjectedValueType(value, paramType, encounter)) {
                  try {
                    method.invoke(injectee, value);
                  }
                  catch (IllegalAccessException e) {
                    encounter.addError(e);
                  }
                  catch (InvocationTargetException e) {
                    encounter.addError(e.getTargetException());
                  }
                }
              }
            });
          }
        }
/*
        Set<InjectionPoint> pointSet = injectableType.getInjectableMembers();
        for (InjectionPoint injectionPoint : pointSet) {
          Member member = injectionPoint.getMember();

          System.out.println("member: " + member);
        }
*/
      }
    });

  }

  /**
   * Returns true if the value to be injected is of the correct type otherwise an error is raised on
   * the encounter and false is returned
   */
  protected <I> boolean checkInjectedValueType(Object value, Class<?> type,
      Encounter<I> encounter) {
    // TODO check the type
    return true;
  }
}