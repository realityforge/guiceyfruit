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
package org.guiceyfruit.testing.junit3;

import junit.framework.TestCase;
import com.google.inject.Injector;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.Configuration;

/**
 * @version $Revision: 1.1 $
 */
public class GuiceyTestCase extends TestCase {
    private Injector injector;
    public static final String TEST_MODULES = "org.guiceyfruit.test.modules";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injector = createInjector();
        assertNotNull("Should have a Guice Injector created", injector);
        injector.injectMembers(this);
    }

    /**
     * Factory method to create the Guice Injector.
     * <p/>
     * The default implementation will use the system property
     * <code>org.guiceyfruit.modules</code> (see {@link Injectors#MODULE_CLASS_NAMES}
     * otherwise
     * if that is not set it will look for the {@link Configuration}
     * annotation and use the module defined on that otherwise it
     * will try look for the inner class called <code>TestClass$Configuration</code>
     *
     * @return
     */
    protected Injector createInjector() throws Exception {
        return Injectors.createInjectorForTest(this);
    }

    @Override
    protected void tearDown() throws Exception {
        if (injector != null) {
            injector.close();
            injector = null;
        }
        super.tearDown();

    }
}
