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

import com.google.inject.Binder;
import com.google.inject.matcher.Matchers;
import javax.annotation.PostConstruct;
import javax.naming.Context;

/**
 * Helper method for installing the JSR 250 lifecycle to a binder
 *
 * @version $Revision: 1.1 $
 */
public final class Jsr250 {

  /**
   * Binds the JSR 250 lifecycles along with the resource injection to the current binder
   *
   * @param binder the binder to bind to
   */
  public static void bind(Binder binder) {
    bindLifecycles(binder);
    bindResourceInjection(binder);
  }

  /**
   * Binds the lifecycle support
   *
   * @param binder the binder to bind to
   */
  public static void bindLifecycles(Binder binder) {
    binder.bindConstructorInterceptor(Matchers.methodAnnotatedWith(PostConstruct.class),
        new PostConstructInterceptor());

    binder.bind(PreDestroyCloser.class);
  }

  /**
   * Binds the Resource injection support
   *
   * @param binder the binder to bind to
   */
  public static void bindResourceInjection(Binder binder) {
    binder.bind(ResourceProviderFactory.class);
    bindJndi(binder);
  }

  /**
   * Binds the default JNDI provider
   *
   * @param binder the binder to bind to
   */
  public static void bindJndi(Binder binder) {
    // lets bind the default JNDI context
    binder.bind(Context.class).toProvider(ContextProvider.class).asEagerSingleton();
  }
}
