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

package com.google.inject.spring;

import com.google.inject.spi.Closer;
import org.springframework.beans.factory.DisposableBean;

/**
 * Invokes the {@link org.springframework.beans.factory.DisposableBean#destroy()} on any beans
 * which are registered in a singleton scope when the injector is closed.
 * <p>
 * To install this lifecycle call the {@link SpringIntegration#bindLifecycle(com.google.inject.Binder)} method
 * from your module.
 *
 * @see com.google.inject.Injector#close()
 *
 * @version $Revision: 1.1 $
 */
public class DisposableBeanCloser implements Closer {
  public void close(Object object) throws Throwable {
    if (object instanceof DisposableBean) {
      DisposableBean disposableBean = (DisposableBean) object;
      disposableBean.destroy();
    }
  }
}
