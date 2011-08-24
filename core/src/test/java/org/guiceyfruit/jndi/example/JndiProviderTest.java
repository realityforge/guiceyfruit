/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guiceyfruit.jndi.example;

import com.google.inject.Injector;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import junit.framework.TestCase;

/** @version $Revision: 1.1 $ */
public class JndiProviderTest extends TestCase {
  protected static final boolean verbose = false;

  public void testJndiProvider() throws Exception {
    InputStream in = getClass().getResourceAsStream("jndi-example.properties");
    assertNotNull("Cannot find jndi-example.properties on the classpath!", in);

    Properties properties = new Properties();
    properties.load(in);

    InitialContext context = new InitialContext(new Hashtable(properties));

    if (verbose) {
      NamingEnumeration<Binding> iter = context.listBindings("");
      while (iter.hasMore()) {
        Binding binding = iter.next();
        System.out.println("  " + binding.getName() + " -> " + binding.getObject());
      }
    }

    MyBean foo = assertLookup(context, "foo", MyBean.class);
    assertEquals("foo.name", "Foo", foo.getName());

    MyBean blah = assertLookup(context, "blah", MyBean.class);
    assertEquals("blah.name", "Blah", blah.getName());

    // lets check that Cheese has not been instantiated yet
    assertEquals("Cheese instance count", 0, Cheese.instanceCount);
    Cheese cheese = assertLookup(context, "cheese", Cheese.class);
    assertEquals("cheese.type", "Edam", cheese.getType());
    assertEquals("Cheese instance count", 1, Cheese.instanceCount);

    SomeBean someBean = assertLookup(context, "org.guiceyfruit.jndi.example.SomeBean",
        SomeBean.class);
    assertEquals("someBean.name", "James", someBean.getName());

    // lets test we can find the injector with the default name
    Injector injector = (Injector) context.lookup("com.google.inject.Injector");
    assertNotNull("Injector should not be null", injector);

    // lets try using the custom name defined in the properties file
    injector = (Injector) context.lookup("myInjector");
    assertNotNull("Injector should not be null", injector);
  }

  protected <T> T assertLookup(InitialContext context, String name, Class<T> type)
      throws NamingException {
    Object value = context.lookup(name);
    if (verbose) {
      System.out.println(name + " = " + value);
    }
    assertNotNull("Should have an entry for '" + name + "' in JNDI", value);
    assertTrue("Should be an instanceof " + type.getName(), type.isInstance(value));
    return type.cast(value);
  }

}
