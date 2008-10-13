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

package com.google.inject.jsr250;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.spi.AnnotationProviderFactory;
import com.google.inject.spi.InjectionAnnotation;
import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Injects objects from a JNDI context using the {@link Resource} annotation
 * to define the annotation injection point
 *
 * @version $Revision: 1.1 $
 */
@InjectionAnnotation(Resource.class)
public class ResourceProviderFactory<T> implements AnnotationProviderFactory<T> {
  private final Context context;

  // TODO not sure why we absolutely must annotate this single constructor?
  // see http://groups.google.com/group/google-guice/browse_thread/thread/2a252b1a3f7b3779
  @Inject
  public ResourceProviderFactory(Context context) {
    this.context = context;
  }

  public Provider<T> createProvider(AnnotatedElement member) {
    Resource resource = member.getAnnotation(Resource.class);
    Objects.nonNull(resource, "@Resource is missing!");
    if (resource != null) {
      String name = getJndiName(resource, member);
      return new ResourceProvider<T>(name);
    }
    return null;
  }

  /** Deduces the JNDI name from the resource and member according to the JSR 250 rules */
  String getJndiName(Resource resource, AnnotatedElement member) {
    String answer = resource.name();
    if (answer == null || answer.length() == 0) {
      if (member instanceof Method) {
        Method method = (Method) member;
        answer = method.getName();
        if (answer.startsWith("set")) {
          // lets switch the setter to the bean property name
          answer = Introspector.decapitalize(answer.substring(3));
        }
      }
      else if (member instanceof Field) {
        Field field = (Field) member;
        answer = field.getName();
      }
    }
    if (answer == null || answer.length() == 0) {
      throw new IllegalArgumentException("No name defined");
    }
    return answer;
  }

  class ResourceProvider<T> implements Provider<T> {
    private final String name;

    public ResourceProvider(String name) {
      this.name = name;
    }

    public T get() {
      try {
        return (T) context.lookup(name);
      }
      catch (NamingException e) {
        throw new ProvisionException("Failed to find name '" + name + "' in JNDI. Cause: " + e, e);
      }
    }

    @Override public String toString() {
      return "ResourceProvider(" + name + ")";
    }
  }
}
