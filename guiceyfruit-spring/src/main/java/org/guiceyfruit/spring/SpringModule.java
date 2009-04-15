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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Iterables;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeListener;
import com.google.inject.spi.TypeEncounter;
import java.util.Arrays;
import java.util.Collections;
import org.guiceyfruit.jsr250.Jsr250Module;
import org.guiceyfruit.spring.support.AutowiredMemberProvider;
import org.guiceyfruit.spring.support.DisposableBeanCloser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A module for injecting beans using the Spring annotations
 *
 * @version $Revision: 1.1 $
 */
public class SpringModule extends Jsr250Module {

  /**
   * Returns a new Injector with support for
   * <a href="http://code.google.com/p/guiceyfruit/wiki/Annotations">Spring annotations and
   * lifecycle support</a> along with JSR 250 support included.
   */
  public static Injector createInjector(Module... modules) {
    Iterable<? extends Module> iterable = Iterables.concat(
        Collections.singletonList(new SpringModule()), Arrays.asList(modules));

    return Guice.createInjector(iterable);
  }

  protected void configure() {
    super.configure();

    bindAnnotationInjector(Autowired.class, AutowiredMemberProvider.class);

    // TODO cannot use the matchers to perform subclass checks!
    bindListener(Matchers.any(), new TypeListener() {
      public <I> void hear(TypeLiteral<I> injectableType, TypeEncounter<I> encounter) {
        Class<? super I> type = injectableType.getRawType();
        if (InitializingBean.class.isAssignableFrom(type)) {
          encounter.register(new InjectionListener<I>() {
            public void afterInjection(I injectee) {
              if (injectee instanceof InitializingBean) {
                InitializingBean initializingBean = (InitializingBean) injectee;
                try {
                  initializingBean.afterPropertiesSet();
                }
                catch (Exception e) {
                  throw new ProvisionException("Failed to invoke afterPropertiesSet(): " + e, e);
                }
              }
            }
          });
        }
      }
    });

    bind(DisposableBeanCloser.class);
  }

}
