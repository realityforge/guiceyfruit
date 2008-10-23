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
package org.guiceyfruit.testing.junit4;

import org.junit.runners.JUnit4;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.FrameworkMethod;
import org.guiceyfruit.Injectors;
import com.google.inject.Injector;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version $Revision: 1.1 $
 */
public class GuiceyJUnit4 extends BlockJUnit4ClassRunner {

    private Map<Object, Injector> injectors = new ConcurrentHashMap<Object, Injector>();

    public GuiceyJUnit4(Class<?> aClass) throws InitializationError {
        super(aClass);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        // TODO lets create a class level injector and use a test scope
        return super.withBeforeClasses(statement);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        // TODO lets create a class level injector and use a test scope
        return super.withAfterClasses(statement);
    }

    @Override
    protected Statement withBefores(FrameworkMethod frameworkMethod, final Object test, Statement statement) {
        final Statement parent = super.withBefores(frameworkMethod, test, statement);
        return new Statement() {
            public void evaluate() throws Throwable {
                Injector injector = Injectors.createInjectorForTest(test);
                injector.injectMembers(test);
                injectors.put(test, injector);

                parent.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfters(FrameworkMethod frameworkMethod, final Object test, Statement statement) {
        final Statement parent = super.withBefores(frameworkMethod, test, statement);
        return new Statement() {
            public void evaluate() throws Throwable {
                parent.evaluate();

                Injector injector = injectors.remove(test);
                if (injector != null) {
                    injector.close();
                }
            }
        };
    }
}
