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

package org.guiceyfruit.ejb;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.Assert;
import junit.framework.TestCase;

import javax.ejb.EJB;

/**
 * @version $Revision: 1.1 $
 */
public class InjectionTest extends TestCase {

    public void testInjection() throws Exception {

        final SomeInterface service = new SomeInterface() {
            public String hello() {
                return "Hey!";
            }
        };

        Injector injector = Guice.createInjector(new EjbModule() {
            @Override
            protected void configure() {
                super.configure();

                bind(SomeInterface.class, "service").toInstance(service);
            }
        });

        TestDTO testDTO = injector.getInstance(TestDTO.class);
        Assert.assertSame("service", service, testDTO.getService());
    }

    public static class TestDTO {
        @EJB
        SomeInterface service;

        public SomeInterface getService() {
            return service;
        }
    }

}