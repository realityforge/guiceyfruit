/**
 * Copyright (C) 2006 Google Inc.
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

package org.guiceyfruit.jsr250;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.PostConstruct;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;

/**
 * Supports the {@link PostConstruct} annotation lifecycle from JSR250.
 * <p>
 * To install this interceptor call the {@link Jsr250#bind(com.google.inject.Binder)} method
 * from your module.
 *
 * @author james.strachan@gmail.com (James Strachan)
 * @version $Revision: 1.1 $
 */
public class PostConstructInterceptor implements ConstructorInterceptor {

  private AnnotatedMethodCache methodCache = new AnnotatedMethodCache(PostConstruct.class);

  public Object construct(ConstructorInvocation invocation) throws Throwable {
    Object value = invocation.proceed();
    if (value != null) {
      Class<?> type = value.getClass();

      Method method = methodCache.getMethod(type);

      if (method != null) {
        try {
          method.invoke(value);
        }
        catch (InvocationTargetException e) {
          throw e.getTargetException();
        }
      }
    }
    return value;
  }

}