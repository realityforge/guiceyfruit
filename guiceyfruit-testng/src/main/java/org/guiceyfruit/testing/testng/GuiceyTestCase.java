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

import com.google.inject.Injector;
import org.guiceyfruit.Injectors;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/** @version $Revision: 1.1 $ */
public class GuiceyTestCase {
  private Injector injector;
  public static final String TEST_MODULES = "org.guiceyfruit.test.modules";

  @BeforeClass(alwaysRun = true)
  protected void setUp() throws Exception {
    injector = createInjector();
    Assert.assertNotNull(injector, "Should have a Guice Injector created");
  }

  @BeforeMethod(alwaysRun = true)
  protected void startTestScope() {
    injector.injectMembers(this);
  }

  @AfterMethod(alwaysRun = true)
  protected void tearDownTestScope() {
  }

  @AfterClass(alwaysRun = true)
  protected void tearDown() throws Exception {
    if (injector != null) {
      injector.close();
    }
    injector = null;
  }

  /**
   * Factory method to create the Guice Injector. <p/> The default implementation will use the
   * system property <code>org.guiceyfruit.modules</code> (see {@link
   * org.guiceyfruit.Injectors#MODULE_CLASS_NAMES} otherwise if that is not set it will look for the
   * {@link org.guiceyfruit.testing.Configuration} annotation and use the module defined on that
   * otherwise it will try look for the inner class called <code>TestClass$Configuration</code>
   */
  protected Injector createInjector() throws Exception {
    return Injectors.createInjectorForTest(this);
  }

}