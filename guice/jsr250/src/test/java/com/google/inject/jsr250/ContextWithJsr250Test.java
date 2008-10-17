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

package com.google.inject.jsr250;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import junit.framework.TestCase;
import org.guiceyfruit.jndi.internal.JndiContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;

/** @version $Revision: 1.1 $ */
public class ContextWithJsr250Test extends TestCase {
  protected static final boolean verbose = false;

  public void testContextIsReused() throws Exception {
    InputStream in = getClass().getResourceAsStream("jndi-example.properties");
    assertNotNull("Cannot find jndi-example.properties on the classpath!", in);

    Properties properties = new Properties();
    properties.load(in);

    InitialContext context = new InitialContext(new Hashtable(properties));

    Injector injector = (Injector) context.lookup(Injector.class.getName());
    assertNotNull("Should have an injector!", injector);

    Context actual = injector.getInstance(Context.class);

    if (verbose) {
      Set<Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
      for (Entry<Key<?>, Binding<?>> entry : entries) {
        System.out.println("key: " + entry.getKey() + " -> " + entry.getValue());
      }
      System.out.println("Context: " + actual);
      System.out.println("Context type: " + actual.getClass().getName());
    }

    MatcherAssert.assertThat(actual, Is.is(JndiContext.class));
  }

}
