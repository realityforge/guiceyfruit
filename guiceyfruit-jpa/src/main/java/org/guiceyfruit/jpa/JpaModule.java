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

package org.guiceyfruit.jpa;

import javax.persistence.PersistenceContext;
import org.guiceyfruit.jpa.support.PersistenceMemberProvider;
import org.guiceyfruit.jsr250.Jsr250Module;

/**
 * Allows the JPA persistence context to be injected via {@link javax.persistence.PersistenceContext}
 *
 * @version $Revision: 1.1 $
 */
public class JpaModule extends Jsr250Module {
  @Override
  protected void configure() {
    super.configure();

    bindAnnotationInjector(PersistenceContext.class, PersistenceMemberProvider.class);
  }
}
