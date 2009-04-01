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

package org.guiceyfruit.jsr250;

import com.google.inject.ProvisionException;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.Context;
import org.guiceyfruit.support.AbstractGuiceyFruitModule;
import org.guiceyfruit.support.Matchers;

/**
 * A module which installs JSR 250 lifecycle and injection using the {@link Resource} annotation.
 * <p> If you do not wish to use Resource injection you can disable it via the {@link
 * #setResourceInjection(boolean)} which avoids you having to bind a JNDI context.
 *
 * @version $Revision: 1.1 $
 */
public class Jsr250Module extends AbstractGuiceyFruitModule {
  private boolean resourceInjection = true;

  protected void configure() {
    if (isResourceInjection()) {
      try {
        bindJndiContext();
      }
      catch (Exception e) {
        throw new ProvisionException("Failed to bind JNDI Context: " + e, e);
      }
      bind(ResourceMemberProvider.class).in(Singleton.class);

      bindAnnotationMemberProvider(Resource.class, ResourceMemberProvider.class);
    }

    bindConstructorInterceptor(Matchers.methodAnnotatedWith(PostConstruct.class),
        new PostConstructInterceptor());

    bind(PreDestroyCloser.class);
  }

  public boolean isResourceInjection() {
    return resourceInjection;
  }

  /** Allows resource injection to be disabled which avoids having to bind a JNDI context */
  public void setResourceInjection(boolean resourceInjection) {
    this.resourceInjection = resourceInjection;
  }

  /**
   * A strategy method to bind a JNDI context which by default uses {@link
   * org.guiceyfruit.jsr250.ContextProvider} to use the InitialContext
   */
  protected void bindJndiContext() throws Exception {
    bind(Context.class).toProvider(ContextProvider.class).in(Scopes.SINGLETON);
  }
}
