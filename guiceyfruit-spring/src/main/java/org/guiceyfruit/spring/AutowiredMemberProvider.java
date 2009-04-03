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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.Preconditions;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import org.guiceyfruit.support.AnnotationMemberProvider;
import org.guiceyfruit.support.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Creates a value for an {@link Autowired} member with an optional {@link Qualifier} annotation
 *
 * @version $Revision: 1.1 $
 */
public class AutowiredMemberProvider implements AnnotationMemberProvider<Autowired> {

  private final Injector injector;

  @Inject
  public AutowiredMemberProvider(Injector injector) {
    Preconditions.checkNotNull(injector, "injector");
    this.injector = injector;
  }

  public Object provide(Autowired annotation, Member member) {
    Class<?> type = Members.getInjectionValueType(member);
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
      return injector.getInstance(type);
    }
  }
}
