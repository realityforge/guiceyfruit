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

package org.guiceyfruit.spring;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import junit.framework.TestCase;
import org.guiceyfruit.Configures;
import org.guiceyfruit.spring.testbeans.IndexedTestBean;
import org.guiceyfruit.spring.testbeans.NestedTestBean;
import org.guiceyfruit.spring.testbeans.OptionalCollectionResourceInjectionBean;
import org.guiceyfruit.spring.testbeans.OptionalResourceInjectionBean;
import org.guiceyfruit.spring.testbeans.ResourceInjectionBean;
import org.guiceyfruit.spring.testbeans.TestBean;
import org.guiceyfruit.spring.testbeans.TypedExtendedResourceInjectionBean;
import org.guiceyfruit.support.GuiceyFruitModule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * This class reuses the test beans from Spring
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Sam Brannen
 * @author Chris Beams
 * @author James Strachan
 * @version $Revision: 1.1 $
 */

public class AutowiredTest extends TestCase {

  public void testAutowiredInjection() throws Exception {
    final TestBean tb = new TestBean();

    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
      }
    });

    ResourceInjectionBean bean = injector.getInstance(ResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());

    bean = injector.getInstance(ResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());

  }

  public void testOptionalResourceInjection() {
    final TestBean tb = new TestBean();
    final IndexedTestBean itb = new IndexedTestBean();
    final NestedTestBean ntb1 = new NestedTestBean("ntb1");
    final NestedTestBean ntb2 = new NestedTestBean("ntb2");

    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
        bind(IndexedTestBean.class).toInstance(itb);

        bind(Key.get(NestedTestBean.class, Names.named("nestedTestBean1"))).toInstance(ntb1);
        bind(Key.get(NestedTestBean.class, Names.named("nestedTestBean2"))).toInstance(ntb2);
      }
    });
    OptionalResourceInjectionBean bean = injector.getInstance(OptionalResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(itb, bean.getIndexedTestBean());
    assertEquals(2, bean.getNestedTestBeans().length);
    assertSame(ntb1, bean.getNestedTestBeans()[0]);
    assertSame(ntb2, bean.getNestedTestBeans()[1]);
    assertEquals(2, bean.nestedTestBeansField.length);
    assertSame(ntb1, bean.nestedTestBeansField[0]);
    assertSame(ntb2, bean.nestedTestBeansField[1]);
  }

  public void testOptionalResourceInjectionWithIncompleteDependencies() {
    final TestBean tb = new TestBean();

    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
      }
    });
    OptionalResourceInjectionBean bean = injector.getInstance(OptionalResourceInjectionBean.class);

    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertNull(bean.getNestedTestBeans());
/*
    // TODO difference from Spring as we can inject these values
    assertNull(bean.getTestBean4());
    assertNull(bean.getNestedTestBeans());
*/
  }

  public void testOptionalResourceInjectionWithNoDependencies() {
    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
      }
    });

    OptionalResourceInjectionBean bean = injector.getInstance(OptionalResourceInjectionBean.class);

    // TODO semantics here differ from Spring as
    // Guice is capable of knowing how to create a TestBean
/*
    assertNull(bean.getTestBean3());
    assertNull(bean.getTestBean4());
*/

    assertNotNull(bean.getTestBean());
    assertNotNull(bean.getTestBean2());
    assertNotSame(bean.getTestBean(), bean.getTestBean2());
    assertNull(bean.getNestedTestBeans());
  }

  public void testResourceInjection() {
    final TestBean tb = new TestBean();

    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
      }
    });
    ResourceInjectionBean bean = injector.getInstance(ResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());

    bean = injector.getInstance(ResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
  }

  public void testExtendedResourceInjection() {
    final TestBean tb = new TestBean();
    final NestedTestBean ntb = new NestedTestBean();
    final BeanFactory bf = new DefaultListableBeanFactory();

    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
        bind(NestedTestBean.class).toInstance(ntb);
        bind(BeanFactory.class).toInstance(bf);
      }
    });

    TypedExtendedResourceInjectionBean bean = injector
        .getInstance(TypedExtendedResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(ntb, bean.getNestedTestBean());
    assertSame(bf, bean.getBeanFactory());

    bean = injector.getInstance(TypedExtendedResourceInjectionBean.class);
    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(ntb, bean.getNestedTestBean());
    assertSame(bf, bean.getBeanFactory());
  }

  public void testExtendedResourceInjectionWithOverriding() {
    final TestBean tb = new TestBean("tb1");
    final TestBean tb2 = new TestBean("tb2");
    final NestedTestBean ntb = new NestedTestBean();
    final BeanFactory bf = new DefaultListableBeanFactory();

    Injector injector = createInjector(new GuiceyFruitModule() {
      protected void configure() {
        super.configure();

        bind(TestBean.class).toInstance(tb);
        bind(NestedTestBean.class).toInstance(ntb);
        bind(BeanFactory.class).toInstance(bf);
      }

      @Configures
      TypedExtendedResourceInjectionBean configure(TypedExtendedResourceInjectionBean bean) {
        bean.setTestBean2(tb2);
        return bean;
      }
    });

    TypedExtendedResourceInjectionBean bean = injector
        .getInstance(TypedExtendedResourceInjectionBean.class);

    assertSame(tb, bean.getTestBean());
    assertSame(tb2, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(ntb, bean.getNestedTestBean());
    assertSame(bf, bean.getBeanFactory());
  }

  public void testExtendedResourceInjectionWithAtRequired() {
    final TestBean tb = new TestBean("tb1");
    final NestedTestBean ntb = new NestedTestBean();
    final BeanFactory bf = new DefaultListableBeanFactory();

    Injector injector = createInjector(new GuiceyFruitModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
        bind(NestedTestBean.class).toInstance(ntb);
        bind(BeanFactory.class).toInstance(bf);
      }
    });

    TypedExtendedResourceInjectionBean bean = injector
        .getInstance(TypedExtendedResourceInjectionBean.class);

    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(ntb, bean.getNestedTestBean());
    assertSame(bf, bean.getBeanFactory());
  }

  public void testOptionalCollectionResourceInjection() {
    final TestBean tb = new TestBean("tb1");
    final IndexedTestBean itb = new IndexedTestBean();
    final NestedTestBean ntb1 = new NestedTestBean();
    final NestedTestBean ntb2 = new NestedTestBean();

    Injector injector = createInjector(new GuiceyFruitModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
        bind(IndexedTestBean.class).toInstance(itb);

        bind(Key.get(NestedTestBean.class, Names.named("nestedTestBean1"))).toInstance(ntb1);
        bind(Key.get(NestedTestBean.class, Names.named("nestedTestBean2"))).toInstance(ntb2);
      }
    });

    // Two calls to verify that caching doesn't break re-creation.
    OptionalCollectionResourceInjectionBean bean = injector
        .getInstance(OptionalCollectionResourceInjectionBean.class);
    assertNotNull(bean);
    bean = injector.getInstance(OptionalCollectionResourceInjectionBean.class);

    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(itb, bean.getIndexedTestBean());
    assertNotNull(bean.getNestedTestBeans());
    assertEquals(2, bean.getNestedTestBeans().size());
    assertSame(ntb1, bean.getNestedTestBeans().get(0));
    assertSame(ntb2, bean.getNestedTestBeans().get(1));
    assertEquals(2, bean.nestedTestBeansSetter.size());
    assertSame(ntb1, bean.nestedTestBeansSetter.get(0));
    assertSame(ntb2, bean.nestedTestBeansSetter.get(1));
    assertEquals(2, bean.nestedTestBeansField.size());
    assertSame(ntb1, bean.nestedTestBeansField.get(0));
    assertSame(ntb2, bean.nestedTestBeansField.get(1));
  }

  public void testOptionalCollectionResourceInjectionWithSingleElement() {
    final TestBean tb = new TestBean("tb1");
    final IndexedTestBean itb = new IndexedTestBean();
    final NestedTestBean ntb1 = new NestedTestBean();

    Injector injector = createInjector(new GuiceyFruitModule() {
      protected void configure() {
        bind(TestBean.class).toInstance(tb);
        bind(IndexedTestBean.class).toInstance(itb);

        bind(Key.get(NestedTestBean.class, Names.named("nestedTestBean1"))).toInstance(ntb1);
      }
    });

    // Two calls to verify that caching doesn't break re-creation.
    OptionalCollectionResourceInjectionBean bean = injector
        .getInstance(OptionalCollectionResourceInjectionBean.class);
    assertNotNull(bean);
    bean = injector.getInstance(OptionalCollectionResourceInjectionBean.class);

    assertSame(tb, bean.getTestBean());
    assertSame(tb, bean.getTestBean2());
    assertSame(tb, bean.getTestBean3());
    assertSame(tb, bean.getTestBean4());
    assertSame(itb, bean.getIndexedTestBean());
    assertEquals(1, bean.getNestedTestBeans().size());
    assertSame(ntb1, bean.getNestedTestBeans().get(0));
    assertEquals(1, bean.nestedTestBeansSetter.size());
    assertSame(ntb1, bean.nestedTestBeansSetter.get(0));
    assertEquals(1, bean.nestedTestBeansField.size());
    assertSame(ntb1, bean.nestedTestBeansField.get(0));
  }

  // Spring tests not yet ported...
  //-------------------------------------------------------------------------

/*

  @Test
  public void testFieldInjectionWithMap() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          RootBeanDefinition bd = new RootBeanDefinition(MapFieldInjectionBean.class);
          bd.setScope(RootBeanDefinition.SCOPE_PROTOTYPE);
          bf.registerBeanDefinition("annotatedBean", bd);
          TestBean tb1 = new TestBean();
          TestBean tb2 = new TestBean();
          bf.registerSingleton("testBean1", tb1);
          bf.registerSingleton("testBean2", tb1);

          MapFieldInjectionBean bean = (MapFieldInjectionBean) bf.getBean("annotatedBean");
          assertEquals(2, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
          assertTrue(bean.getTestBeanMap().values().contains(tb1));
          assertTrue(bean.getTestBeanMap().values().contains(tb2));

          bean = (MapFieldInjectionBean) bf.getBean("annotatedBean");
          assertEquals(2, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
          assertTrue(bean.getTestBeanMap().values().contains(tb1));
          assertTrue(bean.getTestBeanMap().values().contains(tb2));
  }

  @Test
  public void testMethodInjectionWithMap() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          RootBeanDefinition bd = new RootBeanDefinition(MapMethodInjectionBean.class);
          bd.setScope(RootBeanDefinition.SCOPE_PROTOTYPE);
          bf.registerBeanDefinition("annotatedBean", bd);
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);

          MapMethodInjectionBean bean = (MapMethodInjectionBean) bf.getBean("annotatedBean");
          assertEquals(1, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean"));
          assertTrue(bean.getTestBeanMap().values().contains(tb));
          assertSame(tb, bean.getTestBean());

          bean = (MapMethodInjectionBean) bf.getBean("annotatedBean");
          assertEquals(1, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean"));
          assertTrue(bean.getTestBeanMap().values().contains(tb));
          assertSame(tb, bean.getTestBean());
  }

  @Test
  public void testMethodInjectionWithMapAndMultipleMatches() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(MapMethodInjectionBean.class));
          bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
          bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));

          try {
                  bf.getBean("annotatedBean");
                  fail("should have failed, more than one bean of type");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testMethodInjectionWithMapAndMultipleMatchesButOnlyOneAutowireCandidate() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(MapMethodInjectionBean.class));
          bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
          RootBeanDefinition rbd2 = new RootBeanDefinition(TestBean.class);
          rbd2.setAutowireCandidate(false);
          bf.registerBeanDefinition("testBean2", rbd2);

          MapMethodInjectionBean bean = (MapMethodInjectionBean) bf.getBean("annotatedBean");
          TestBean tb = (TestBean) bf.getBean("testBean1");
          assertEquals(1, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
          assertTrue(bean.getTestBeanMap().values().contains(tb));
          assertSame(tb, bean.getTestBean());
          bf.destroySingletons();
  }

  @Test
  public void testMethodInjectionWithMapAndNoMatches() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(MapMethodInjectionBean.class));

          MapMethodInjectionBean bean = (MapMethodInjectionBean) bf.getBean("annotatedBean");
          assertNull(bean.getTestBeanMap());
          assertNull(bean.getTestBean());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationRequiredFieldResourceInjection() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationRequiredFieldResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);

          CustomAnnotationRequiredFieldResourceInjectionBean bean = (CustomAnnotationRequiredFieldResourceInjectionBean) bf.getBean("customBean");
          assertSame(tb, bean.getTestBean());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationRequiredFieldResourceInjectionFailsWhenNoDependencyFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationRequiredFieldResourceInjectionBean.class));

          try {
                  bf.getBean("customBean");
                  fail("expected BeanCreationException; no dependency available for required field");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationRequiredFieldResourceInjectionFailsWhenMultipleDependenciesFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationRequiredFieldResourceInjectionBean.class));
          TestBean tb1 = new TestBean();
          bf.registerSingleton("testBean1", tb1);
          TestBean tb2 = new TestBean();
          bf.registerSingleton("testBean2", tb2);

          try {
                  bf.getBean("customBean");
                  fail("expected BeanCreationException; multiple beans of dependency type available");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationRequiredMethodResourceInjection() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationRequiredMethodResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);

          CustomAnnotationRequiredMethodResourceInjectionBean bean = (CustomAnnotationRequiredMethodResourceInjectionBean) bf.getBean("customBean");
          assertSame(tb, bean.getTestBean());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationRequiredMethodResourceInjectionFailsWhenNoDependencyFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationRequiredMethodResourceInjectionBean.class));

          try {
                  bf.getBean("customBean");
                  fail("expected BeanCreationException; no dependency available for required method");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationRequiredMethodResourceInjectionFailsWhenMultipleDependenciesFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationRequiredMethodResourceInjectionBean.class));
          TestBean tb1 = new TestBean();
          bf.registerSingleton("testBean1", tb1);
          TestBean tb2 = new TestBean();
          bf.registerSingleton("testBean2", tb2);

          try {
                  bf.getBean("customBean");
                  fail("expected BeanCreationException; multiple beans of dependency type available");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationOptionalFieldResourceInjection() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationOptionalFieldResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);

          CustomAnnotationOptionalFieldResourceInjectionBean bean = (CustomAnnotationOptionalFieldResourceInjectionBean) bf.getBean("customBean");
          assertSame(tb, bean.getTestBean3());
          assertNull(bean.getTestBean());
          assertNull(bean.getTestBean2());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationOptionalFieldResourceInjectionWhenNoDependencyFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationOptionalFieldResourceInjectionBean.class));

          CustomAnnotationOptionalFieldResourceInjectionBean bean = (CustomAnnotationOptionalFieldResourceInjectionBean) bf.getBean("customBean");
          assertNull(bean.getTestBean3());
          assertNull(bean.getTestBean());
          assertNull(bean.getTestBean2());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationOptionalFieldResourceInjectionWhenMultipleDependenciesFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationOptionalFieldResourceInjectionBean.class));
          TestBean tb1 = new TestBean();
          bf.registerSingleton("testBean1", tb1);
          TestBean tb2 = new TestBean();
          bf.registerSingleton("testBean2", tb2);

          try {
                  bf.getBean("customBean");
                  fail("expected BeanCreationException; multiple beans of dependency type available");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationOptionalMethodResourceInjection() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationOptionalMethodResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);

          CustomAnnotationOptionalMethodResourceInjectionBean bean = (CustomAnnotationOptionalMethodResourceInjectionBean) bf.getBean("customBean");
          assertSame(tb, bean.getTestBean3());
          assertNull(bean.getTestBean());
          assertNull(bean.getTestBean2());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationOptionalMethodResourceInjectionWhenNoDependencyFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationOptionalMethodResourceInjectionBean.class));

          CustomAnnotationOptionalMethodResourceInjectionBean bean = (CustomAnnotationOptionalMethodResourceInjectionBean) bf.getBean("customBean");
          assertNull(bean.getTestBean3());
          assertNull(bean.getTestBean());
          assertNull(bean.getTestBean2());
          bf.destroySingletons();
  }

  @Test
  public void testCustomAnnotationOptionalMethodResourceInjectionWhenMultipleDependenciesFound() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setAutowiredAnnotationType(MyAutowired.class);
          bpp.setRequiredParameterName("optional");
          bpp.setRequiredParameterValue(false);
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("customBean", new RootBeanDefinition(
                          CustomAnnotationOptionalMethodResourceInjectionBean.class));
          TestBean tb1 = new TestBean();
          bf.registerSingleton("testBean1", tb1);
          TestBean tb2 = new TestBean();
          bf.registerSingleton("testBean2", tb2);

          try {
                  bf.getBean("customBean");
                  fail("expected BeanCreationException; multiple beans of dependency type available");
          }
          catch (BeanCreationException e) {
                  // expected
          }
          bf.destroySingletons();
  }

  @Test
  public void testBeanAutowiredWithFactoryBean() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("factoryBeanDependentBean", new RootBeanDefinition(FactoryBeanDependentBean.class));
          bf.registerSingleton("stringFactoryBean", new StringFactoryBean());

          final StringFactoryBean factoryBean = (StringFactoryBean) bf.getBean("&stringFactoryBean");
          final FactoryBeanDependentBean bean = (FactoryBeanDependentBean) bf.getBean("factoryBeanDependentBean");

          assertNotNull("The singleton StringFactoryBean should have been registered.", factoryBean);
          assertNotNull("The factoryBeanDependentBean should have been registered.", bean);
          assertEquals("The FactoryBeanDependentBean should have been autowired 'by type' with the StringFactoryBean.",
                          factoryBean, bean.getFactoryBean());

          bf.destroySingletons();
  }
*/




  // Purposely excluded tests so far
  //-------------------------------------------------------------------------

/*
  public void testIncompleteBeanDefinition() {
    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
      }
    });

    TestBean bean = injector.getInstance(TestBean.class);

    // this will work in Guice but fail in spring so not applicable!
  }


  // Constructor resource injection not supported yet

  @Test
  public void testConstructorResourceInjection() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          bf.registerResolvableDependency(BeanFactory.class, bf);
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          RootBeanDefinition bd = new RootBeanDefinition(ConstructorResourceInjectionBean.class);
          bd.setScope(RootBeanDefinition.SCOPE_PROTOTYPE);
          bf.registerBeanDefinition("annotatedBean", bd);
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);
          NestedTestBean ntb = new NestedTestBean();
          bf.registerSingleton("nestedTestBean", ntb);

          ConstructorResourceInjectionBean bean = (ConstructorResourceInjectionBean) bf.getBean("annotatedBean");
          assertSame(tb, bean.getTestBean());
          assertSame(tb, bean.getTestBean2());
          assertSame(tb, bean.getTestBean3());
          assertSame(tb, bean.getTestBean4());
          assertSame(ntb, bean.getNestedTestBean());
          assertSame(bf, bean.getBeanFactory());

          bean = (ConstructorResourceInjectionBean) bf.getBean("annotatedBean");
          assertSame(tb, bean.getTestBean());
          assertSame(tb, bean.getTestBean2());
          assertSame(tb, bean.getTestBean3());
          assertSame(tb, bean.getTestBean4());
          assertSame(ntb, bean.getNestedTestBean());
          assertSame(bf, bean.getBeanFactory());
  }

  @Test
  public void testConstructorResourceInjectionWithMultipleCandidates() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(ConstructorsResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);
          NestedTestBean ntb1 = new NestedTestBean();
          bf.registerSingleton("nestedTestBean1", ntb1);
          NestedTestBean ntb2 = new NestedTestBean();
          bf.registerSingleton("nestedTestBean2", ntb2);

          ConstructorsResourceInjectionBean bean = (ConstructorsResourceInjectionBean) bf.getBean("annotatedBean");
          assertNull(bean.getTestBean3());
          assertSame(tb, bean.getTestBean4());
          assertEquals(2, bean.getNestedTestBeans().length);
          assertSame(ntb1, bean.getNestedTestBeans()[0]);
          assertSame(ntb2, bean.getNestedTestBeans()[1]);
          bf.destroySingletons();
  }

  @Test
  public void testConstructorResourceInjectionWithMultipleCandidatesAsCollection() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(
                          ConstructorsCollectionResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);
          NestedTestBean ntb1 = new NestedTestBean();
          bf.registerSingleton("nestedTestBean1", ntb1);
          NestedTestBean ntb2 = new NestedTestBean();
          bf.registerSingleton("nestedTestBean2", ntb2);

          ConstructorsCollectionResourceInjectionBean bean = (ConstructorsCollectionResourceInjectionBean) bf.getBean("annotatedBean");
          assertNull(bean.getTestBean3());
          assertSame(tb, bean.getTestBean4());
          assertEquals(2, bean.getNestedTestBeans().size());
          assertSame(ntb1, bean.getNestedTestBeans().get(0));
          assertSame(ntb2, bean.getNestedTestBeans().get(1));
          bf.destroySingletons();
  }

  @Test
  public void testConstructorResourceInjectionWithMultipleCandidatesAndFallback() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(ConstructorsResourceInjectionBean.class));
          TestBean tb = new TestBean();
          bf.registerSingleton("testBean", tb);

          ConstructorsResourceInjectionBean bean = (ConstructorsResourceInjectionBean) bf.getBean("annotatedBean");
          assertSame(tb, bean.getTestBean3());
          assertNull(bean.getTestBean4());
          bf.destroySingletons();
  }

  @Test
  public void testConstructorResourceInjectionWithMultipleCandidatesAndDefaultFallback() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(ConstructorsResourceInjectionBean.class));

          ConstructorsResourceInjectionBean bean = (ConstructorsResourceInjectionBean) bf.getBean("annotatedBean");
          assertNull(bean.getTestBean3());
          assertNull(bean.getTestBean4());
          bf.destroySingletons();
  }

  @Test
  public void testConstructorInjectionWithMap() {
          DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
          AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
          bpp.setBeanFactory(bf);
          bf.addBeanPostProcessor(bpp);
          RootBeanDefinition bd = new RootBeanDefinition(MapConstructorInjectionBean.class);
          bd.setScope(RootBeanDefinition.SCOPE_PROTOTYPE);
          bf.registerBeanDefinition("annotatedBean", bd);
          TestBean tb1 = new TestBean();
          TestBean tb2 = new TestBean();
          bf.registerSingleton("testBean1", tb1);
          bf.registerSingleton("testBean2", tb1);

          MapConstructorInjectionBean bean = (MapConstructorInjectionBean) bf.getBean("annotatedBean");
          assertEquals(2, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
          assertTrue(bean.getTestBeanMap().values().contains(tb1));
          assertTrue(bean.getTestBeanMap().values().contains(tb2));

          bean = (MapConstructorInjectionBean) bf.getBean("annotatedBean");
          assertEquals(2, bean.getTestBeanMap().size());
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
          assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
          assertTrue(bean.getTestBeanMap().values().contains(tb1));
          assertTrue(bean.getTestBeanMap().values().contains(tb2));
  }
  }
*/

  // Implementation methods
  //-------------------------------------------------------------------------

  protected Injector createInjector(Module module) {
    return Guice.createInjector(new SpringModule(), module);
  }

}
