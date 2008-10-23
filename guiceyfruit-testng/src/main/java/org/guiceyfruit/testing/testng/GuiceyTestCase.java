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

package org.guiceyfruit.testing.testng;

import org.guiceyfruit.testing.InjectorManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/** @version $Revision: 1.1 $ */
public class GuiceyTestCase {
  protected static InjectorManager injectorManager;

  @BeforeClass(alwaysRun = true)
  protected void setUp() throws Exception {
    synchronized (GuiceyTestCase.class) {
      if (injectorManager == null) {
        injectorManager = new InjectorManager();
        injectorManager.beforeClasses();
      }
    }
  }

  @BeforeMethod(alwaysRun = true)
  protected void startTestScope() throws Exception {
    injectorManager.beforeTest(this);
  }

  @AfterMethod(alwaysRun = true)
  protected void tearDownTestScope() {
  }

  @AfterClass(alwaysRun = true)
  protected void tearDown() throws Exception {
    if (injectorManager != null) {
      injectorManager.afterClasses();
    }
  }
}