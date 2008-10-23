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

import com.google.inject.Inject;
import org.guiceyfruit.testing.UseModule;
import org.guiceyfruit.testing.junit4.example.Cheese;
import org.guiceyfruit.testing.junit4.example.EdamModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/** @version $Revision: 1.1 $ */
@RunWith(GuiceyJUnit4.class)
@UseModule(EdamModule.class)
public class AnnotationTest {
  @Inject
  Cheese cheese;

  @Test
  public void testSomething() {
    System.out.println("Running!");

    Assert.assertEquals("cheese.sayHello", "Edam James", cheese.sayHello("James"));
  }
}