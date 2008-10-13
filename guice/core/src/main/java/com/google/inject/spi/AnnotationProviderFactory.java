/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.spi;

import com.google.inject.Provider;
import java.lang.reflect.AnnotatedElement;

/**
 * A hook to allow frameworks to register an annotation based injection point as an alternative
 * injection point to {@link com.google.inject.Inject} such as using @Resource for JSR 250
 * or @PersistenceContext for JPA.
 * <p>
 * When creating classes implementing this interface you should annotate it with
 * {@link InjectionAnnotation} to mark the annotation which is used to define the injection point.
 *
 * @version $Revision: 1.1 $
 */
public interface AnnotationProviderFactory<T> {

  /**
   * Creates the provider for the injected values at the given member
   *
   * @param member the member to inject
   * @return the value to inject into this injection point
   */
  Provider<T> createProvider(AnnotatedElement member);

}
