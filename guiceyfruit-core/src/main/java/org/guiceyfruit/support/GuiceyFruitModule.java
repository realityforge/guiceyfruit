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
import com.google.inject.ProvisionException;
import static com.google.inject.matcher.Matchers.any;
import com.google.inject.spi.InjectableType;
import com.google.inject.spi.InjectableType.Encounter;
import com.google.inject.spi.InjectableType.Listener;
import com.google.inject.spi.InjectionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.guiceyfruit.support.EncounterProvider.encounterProvider;

/**
 * Adds some new helper methods to the base Guice module
 *
 * @version $Revision: 1.1 $
 */
public abstract class GuiceyFruitModule extends AbstractModule {

  protected <A extends Annotation> void bindMethodHandler(final Class<A> annotationType,
      final MethodHandler methodHandler) {

    bindMethodHandler(annotationType, encounterProvider(methodHandler));
  }

  protected <A extends Annotation> void bindMethodHandler(final Class<A> annotationType,
      final Key<? extends MethodHandler> methodHandlerKey) {

    bindMethodHandler(annotationType, encounterProvider(methodHandlerKey));
  }

  protected <A extends Annotation> void bindMethodHandler(final Class<A> annotationType,
      final Class<? extends MethodHandler> methodHandlerType) {

    bindMethodHandler(annotationType, encounterProvider(methodHandlerType));
  }

  private <A extends Annotation> void bindMethodHandler(final Class<A> annotationType,
      final EncounterProvider<MethodHandler> encounterProvider) {

    bindListener(any(), new Listener() {
      public <I> void hear(InjectableType<I> injectableType, Encounter<I> encounter) {
        Class<? super I> type = injectableType.getType().getRawType();
        Method[] methods = type.getDeclaredMethods();
        for (final Method method : methods) {
          final A annotation = method.getAnnotation(annotationType);
          if (annotation != null) {
            final Provider<? extends MethodHandler> provider = encounterProvider.get(encounter);

            encounter.registerPostInjectListener(new InjectionListener<I>() {
              public void afterInjection(I injectee) {

                MethodHandler methodHandler = provider.get();
                try {
                  methodHandler.afterInjection(injectee, annotation, method);
                }
                catch (InvocationTargetException ie) {
                  Throwable e = ie.getTargetException();
                  throw new ProvisionException(e.getMessage(), e);
                }
                catch (IllegalAccessException e) {
                  throw new ProvisionException(e.getMessage(), e);
                }
              }
            });
          }
        }
      }
    });
  }

  /**
   * Binds a custom injection point for a given injection annotation to the annotation member
   * provider so that occurrences of the annotation on fields and methods with a single parameter
   * will be injected by Guice after the constructor and @Inject have been processed.
   *
   * @param annotationType the annotation class used to define the injection point
   * @param annotationMemberProviderKey the key of the annotation member provider which can be
   * instantiated and injected by guice
   * @param <A> the annotation type used as the injection point
   */
  protected <A extends Annotation> void bindAnnotationInjector(final Class<A> annotationType,
      final Key<? extends AnnotationMemberProvider> annotationMemberProviderKey) {

    bindAnnotationInjector(annotationType, encounterProvider(annotationMemberProviderKey));
  }

  /**
   * Binds a custom injection point for a given injection annotation to the annotation member
   * provider so that occurrences of the annotation on fields and methods with a single parameter
   * will be injected by Guice after the constructor and @Inject have been processed.
   *
   * @param annotationType the annotation class used to define the injection point
   * @param annotationMemberProviderType the type of the annotation member provider which can be
   * instantiated and injected by guice
   * @param <A> the annotation type used as the injection point
   */
  protected <A extends Annotation> void bindAnnotationInjector(final Class<A> annotationType,
      final Class<? extends AnnotationMemberProvider> annotationMemberProviderType) {

    bindAnnotationInjector(annotationType, encounterProvider(annotationMemberProviderType));
  }

  private <A extends Annotation> void bindAnnotationInjector(final Class<A> annotationType,
      final EncounterProvider<AnnotationMemberProvider> memberProviderProvider) {
    bindListener(any(), new Listener() {
      Provider<? extends AnnotationMemberProvider> providerProvider;

      public <I> void hear(InjectableType<I> injectableType, final Encounter<I> encounter) {
        Class<? super I> type = injectableType.getType().getRawType();

        for (Class<? super I> t = type; t != Object.class; t = t.getSuperclass()) {
          Field[] fields = t.getDeclaredFields();
          for (Field field : fields) {
            bindAnnotationInjectorToField(encounter, field);
          }
        }

        Method[] methods = type.getDeclaredMethods();
        for (final Method method : methods) {
          // TODO lets exclude methods with @Inject?
          final A annotation = method.getAnnotation(annotationType);
          if (annotation != null) {
            if (providerProvider == null) {
              providerProvider = memberProviderProvider.get(encounter);
            }

            encounter.register(new InjectionListener<I>() {
              public void afterInjection(I injectee) {
                AnnotationMemberProvider provider = providerProvider.get();

                Class<?>[] parameterTypes = method.getParameterTypes();
                int size = parameterTypes.length;
                Object[] values = new Object[size];
                for (int i = 0; i < size; i++) {
                  Class<?> paramType = parameterTypes[i];
                  Object value = provider.provide(annotation, method, paramType, i);
                  checkInjectedValueType(value, paramType, encounter);

                  // if we have a null value then assume the injection point cannot be satisfied
                  // which is the spring @Autowired way of doing things
                  if (value == null) {
                    return;
                  }
                  values[i] = value;
                }
                try {
                  method.setAccessible(true);
                  method.invoke(injectee, values);
                }
                catch (IllegalAccessException e) {
                  throw new ProvisionException(
                      "Failed to inject method " + method + ". Reason: " + e, e);
                }
                catch (InvocationTargetException ie) {
                  Throwable e = ie.getTargetException();
                  throw new ProvisionException(
                      "Failed to inject method " + method + ". Reason: " + e, e);
                }
              }
            });
          }
        }
      }

      protected <I> void bindAnnotationInjectorToField(final Encounter<I> encounter,
          final Field field) {
        // TODO lets exclude fields with @Inject?
        final A annotation = field.getAnnotation(annotationType);
        if (annotation != null) {
          if (providerProvider == null) {
            providerProvider = memberProviderProvider.get(encounter);
          }

          encounter.register(new InjectionListener<I>() {
            public void afterInjection(I injectee) {
              AnnotationMemberProvider provider = providerProvider.get();
              Object value = provider.provide(annotation, field);
              checkInjectedValueType(value, field.getType(), encounter);

              try {
                field.setAccessible(true);
                field.set(injectee, value);
              }
              catch (IllegalAccessException e) {
                throw new ProvisionException("Failed to inject field " + field + ". Reason: " + e,
                    e);
              }
            }
          });
        }
      }
    });
  }

/*
  protected void bindCloseHook() {
    bindListener(any(), new Listener() {
      public <I> void hear(InjectableType<I> injectableType, Encounter<I> encounter) {
        encounter.registerPostInjectListener(new InjectionListener<I>() {
          public void afterInjection(I injectee) {

          }
        });
      }
    });
  }
*/

  /**
   * Returns true if the value to be injected is of the correct type otherwise an error is raised on
   * the encounter and false is returned
   */
  protected <I> void checkInjectedValueType(Object value, Class<?> type, Encounter<I> encounter) {
    // TODO check the type
  }
}