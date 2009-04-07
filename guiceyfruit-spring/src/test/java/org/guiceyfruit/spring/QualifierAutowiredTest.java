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

import com.google.inject.BindingAnnotation;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.name.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import junit.framework.TestCase;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.support.GuiceyFruitModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This class reuses the test beans and test code from Spring
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @version $Revision: 1.1 $
 */
public class QualifierAutowiredTest extends TestCase {

  private static final String JUERGEN = "juergen";

  private static final String MARK = "mark";

  public void testAutowiredFieldResolvesWithBaseQualifierAndDefaultValue() {
    // Using a @Named annotation is kinda like adding a @Qualifier in Spring
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {

      @Provides
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @Named(MARK)
      public Person createMark() {
        return new Person(MARK);
      }
    });

    QualifiedFieldWithBaseQualifierDefaultValueTestBean bean = injector
        .getInstance(QualifiedFieldWithBaseQualifierDefaultValueTestBean.class);
    assertEquals(MARK, bean.getPerson().getName());

    Person person = Injectors.getInstance(injector, Person.class, MARK);
    assertEquals(MARK, person.getName());

  }

  public void testAutowiredFieldResolvesWithBaseQualifierAndNonDefaultValue() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {

      @Provides
      @Named("juergen")
      public Person createJuergen() {
        return new Person("the real juergen");
      }

      @Provides
      @Named("not really juergen")
      public Person createImposter() {
        return new Person("juergen imposter");
      }

      // TODO note due to the constraint we cannot use constructors without @Inject
      // in Guice, we have to create a provider method

      @Provides
      public QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean createJuergen(
          @Named("juergen") Person person) {
        return new QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean(person);
      }
    });

    QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean bean = injector
        .getInstance(QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean.class);
    assertEquals("the real juergen", bean.getPerson().getName());
  }

  public void testAutowiredFieldDoesNotResolveWithBaseQualifierAndNonDefaultValueAndMultipleMatchingCandidates() {
    try {

      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {

        @Provides
        @Named("juergen")
        public Person createJuergen() {
          return new Person("the real juergen");
        }

        @Provides
        @Named("juergen")
        public Person createImposter() {
          return new Person("juergen imposter");
        }

        // TODO note due to the constraint we cannot use constructors without @Inject
        // in Guice, we have to create a provider method

        @Provides
        public QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean createJuergen(
            @Named("juergen") Person person) {
          return new QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean(person);
        }
      });

      QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean bean = injector
          .getInstance(QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean.class);
      fail("expected exception creating " + bean);
    }
    catch (CreationException e) {
      // expected
    }
  }

  public void testAutowiredFieldWithSingleNonQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      public Person createPerson() {
        return new Person(JUERGEN);
      }
    });
    try {
      QualifiedFieldTestBean bean = injector.getInstance(QualifiedFieldTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
  }

  public void testAutowiredMethodParameterWithSingleNonQualifiedCandidate() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        public Person createPerson() {
          return new Person(JUERGEN);
        }
      });

      QualifiedMethodParameterTestBean bean = injector
          .getInstance(QualifiedMethodParameterTestBean.class);
      fail("expected exception creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
  }

  public void testAutowiredConstructorArgumentWithSingleNonQualifiedCandidate() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        public Person createPerson() {
          return new Person(JUERGEN);
        }


        // We cannot support constructor injection using Spring annotations due to
        // current restrictions in Guice, so we must create a provider
        @Provides
        public QualifiedConstructorArgumentTestBean newBean(@TestQualifier Person person) {
          return new QualifiedConstructorArgumentTestBean(person);
        }
      });

      QualifiedConstructorArgumentTestBean bean = injector
          .getInstance(QualifiedConstructorArgumentTestBean.class);
      fail("expected exception creating " + bean);
    }
    catch (CreationException e) {
      // expected
    }
  }

/*


  @Test
  public void testAutowiredFieldWithSingleQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs = new ConstructorArgumentValues();
    cavs.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person = new RootBeanDefinition(Person.class, cavs, null);
    person.addQualifier(new AutowireCandidateQualifier(TestQualifier.class));
    context.registerBeanDefinition(JUERGEN, person);
    context
        .registerBeanDefinition("autowired", new RootBeanDefinition(QualifiedFieldTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedFieldTestBean bean = (QualifiedFieldTestBean) context.getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredMethodParameterWithSingleQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs = new ConstructorArgumentValues();
    cavs.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person = new RootBeanDefinition(Person.class, cavs, null);
    person.addQualifier(new AutowireCandidateQualifier(TestQualifier.class));
    context.registerBeanDefinition(JUERGEN, person);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedMethodParameterTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedMethodParameterTestBean bean = (QualifiedMethodParameterTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredMethodParameterWithStaticallyQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs = new ConstructorArgumentValues();
    cavs.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person = new RootBeanDefinition(QualifiedPerson.class, cavs, null);
    context.registerBeanDefinition(JUERGEN, person);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedMethodParameterTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedMethodParameterTestBean bean = (QualifiedMethodParameterTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredConstructorArgumentWithSingleQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs = new ConstructorArgumentValues();
    cavs.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person = new RootBeanDefinition(Person.class, cavs, null);
    person.addQualifier(new AutowireCandidateQualifier(TestQualifier.class));
    context.registerBeanDefinition(JUERGEN, person);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedConstructorArgumentTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedConstructorArgumentTestBean bean = (QualifiedConstructorArgumentTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredFieldWithMultipleNonQualifiedCandidates() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context
        .registerBeanDefinition("autowired", new RootBeanDefinition(QualifiedFieldTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    try {
      context.refresh();
      fail("expected BeanCreationException");
    }
    catch (BeanCreationException e) {
      assertTrue(e.getRootCause() instanceof NoSuchBeanDefinitionException);
      assertEquals("autowired", e.getBeanName());
    }
  }

  @Test
  public void testAutowiredMethodParameterWithMultipleNonQualifiedCandidates() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedMethodParameterTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    try {
      context.refresh();
      fail("expected BeanCreationException");
    }
    catch (BeanCreationException e) {
      assertTrue(e.getRootCause() instanceof NoSuchBeanDefinitionException);
      assertEquals("autowired", e.getBeanName());
    }
  }

  @Test
  public void testAutowiredConstructorArgumentWithMultipleNonQualifiedCandidates() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedConstructorArgumentTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    try {
      context.refresh();
      fail("expected BeanCreationException");
    }
    catch (BeanCreationException e) {
      assertTrue(e instanceof UnsatisfiedDependencyException);
      assertEquals("autowired", e.getBeanName());
    }
  }

  @Test
  public void testAutowiredFieldResolvesQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    person1.addQualifier(new AutowireCandidateQualifier(TestQualifier.class));
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context
        .registerBeanDefinition("autowired", new RootBeanDefinition(QualifiedFieldTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedFieldTestBean bean = (QualifiedFieldTestBean) context.getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredMethodParameterResolvesQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    person1.addQualifier(new AutowireCandidateQualifier(TestQualifier.class));
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedMethodParameterTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedMethodParameterTestBean bean = (QualifiedMethodParameterTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredConstructorArgumentResolvesQualifiedCandidate() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    person1.addQualifier(new AutowireCandidateQualifier(TestQualifier.class));
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedConstructorArgumentTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedConstructorArgumentTestBean bean = (QualifiedConstructorArgumentTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredFieldResolvesQualifiedCandidateWithDefaultValueAndNoValueOnBeanDefinition() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    // qualifier added, but includes no value
    person1.addQualifier(new AutowireCandidateQualifier(TestQualifierWithDefaultValue.class));
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithDefaultValueTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedFieldWithDefaultValueTestBean bean = (QualifiedFieldWithDefaultValueTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredFieldDoesNotResolveCandidateWithDefaultValueAndConflictingValueOnBeanDefinition() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    // qualifier added, and non-default value specified
    person1.addQualifier(
        new AutowireCandidateQualifier(TestQualifierWithDefaultValue.class, "not the default"));
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithDefaultValueTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    try {
      context.refresh();
      fail("expected BeanCreationException");
    }
    catch (BeanCreationException e) {
      assertTrue(e.getRootCause() instanceof NoSuchBeanDefinitionException);
      assertEquals("autowired", e.getBeanName());
    }
  }

  @Test
  public void testAutowiredFieldResolvesWithDefaultValueAndExplicitDefaultValueOnBeanDefinition() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    // qualifier added, and value matches the default
    person1.addQualifier(
        new AutowireCandidateQualifier(TestQualifierWithDefaultValue.class, "default"));
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithDefaultValueTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedFieldWithDefaultValueTestBean bean = (QualifiedFieldWithDefaultValueTestBean) context
        .getBean("autowired");
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredFieldResolvesWithMultipleQualifierValues() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier.setAttribute("number", 456);
    person1.addQualifier(qualifier);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier2.setAttribute("number", 123);
    person2.addQualifier(qualifier2);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithMultipleAttributesTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedFieldWithMultipleAttributesTestBean bean
        = (QualifiedFieldWithMultipleAttributesTestBean) context.getBean("autowired");
    assertEquals(MARK, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredFieldDoesNotResolveWithMultipleQualifierValuesAndConflictingDefaultValue() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier.setAttribute("number", 456);
    person1.addQualifier(qualifier);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier2.setAttribute("number", 123);
    qualifier2.setAttribute("value", "not the default");
    person2.addQualifier(qualifier2);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithMultipleAttributesTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    try {
      context.refresh();
      fail("expected BeanCreationException");
    }
    catch (BeanCreationException e) {
      assertTrue(e.getRootCause() instanceof NoSuchBeanDefinitionException);
      assertEquals("autowired", e.getBeanName());
    }
  }

  @Test
  public void testAutowiredFieldResolvesWithMultipleQualifierValuesAndExplicitDefaultValue() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier.setAttribute("number", 456);
    person1.addQualifier(qualifier);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier2.setAttribute("number", 123);
    qualifier2.setAttribute("value", "default");
    person2.addQualifier(qualifier2);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithMultipleAttributesTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    QualifiedFieldWithMultipleAttributesTestBean bean
        = (QualifiedFieldWithMultipleAttributesTestBean) context.getBean("autowired");
    assertEquals(MARK, bean.getPerson().getName());
  }

  @Test
  public void testAutowiredFieldDoesNotResolveWithMultipleQualifierValuesAndMultipleMatchingCandidates() {
    GenericApplicationContext context = new GenericApplicationContext();
    ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
    cavs1.addGenericArgumentValue(JUERGEN);
    RootBeanDefinition person1 = new RootBeanDefinition(Person.class, cavs1, null);
    AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier.setAttribute("number", 123);
    person1.addQualifier(qualifier);
    ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
    cavs2.addGenericArgumentValue(MARK);
    RootBeanDefinition person2 = new RootBeanDefinition(Person.class, cavs2, null);
    AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(
        TestQualifierWithMultipleAttributes.class);
    qualifier2.setAttribute("number", 123);
    qualifier2.setAttribute("value", "default");
    person2.addQualifier(qualifier2);
    context.registerBeanDefinition(JUERGEN, person1);
    context.registerBeanDefinition(MARK, person2);
    context.registerBeanDefinition("autowired",
        new RootBeanDefinition(QualifiedFieldWithMultipleAttributesTestBean.class));
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    try {
      context.refresh();
      fail("expected BeanCreationException");
    }
    catch (BeanCreationException e) {
      assertTrue(e.getRootCause() instanceof NoSuchBeanDefinitionException);
      assertEquals("autowired", e.getBeanName());
    }
  }

  */

  private static class QualifiedFieldTestBean {

    @Autowired
    @TestQualifier
    private Person person;

    public Person getPerson() {
      return this.person;
    }
  }

  private static class QualifiedMethodParameterTestBean {

    private Person person;

    @Autowired
    public void setPerson(@TestQualifier Person person) {
      this.person = person;
    }

    public Person getPerson() {
      return this.person;
    }
  }

  private static class QualifiedConstructorArgumentTestBean {

    private Person person;

    @Autowired
    public QualifiedConstructorArgumentTestBean(@TestQualifier Person person) {
      this.person = person;
    }

    public Person getPerson() {
      return this.person;
    }

  }

  public static class QualifiedFieldWithDefaultValueTestBean {

    @Autowired
    @TestQualifierWithDefaultValue
    private Person person;

    public Person getPerson() {
      return this.person;
    }
  }

  public static class QualifiedFieldWithMultipleAttributesTestBean {

    @Autowired
    @TestQualifierWithMultipleAttributes(number = 123)
    private Person person;

    public Person getPerson() {
      return this.person;
    }
  }

  private static class QualifiedFieldWithBaseQualifierDefaultValueTestBean {

    @Autowired
    @Qualifier
    private Person person;

    public Person getPerson() {
      return this.person;
    }
  }

  public static class QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean {

    private Person person;

    @Autowired
    public QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean(
        @Qualifier("juergen") Person person) {
      this.person = person;
    }

    public Person getPerson() {
      return this.person;
    }
  }

  private static class Person {

    private String name;

    public Person(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

  @TestQualifier
  private static class QualifiedPerson extends Person {

    public QualifiedPerson(String name) {
      super(name);
    }
  }

  // Note you must annotate qualifier annotations with @BindingAnnotation
  //-------------------------------------------------------------------------

  @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  @BindingAnnotation
  public static @interface TestQualifier {
  }

  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  @BindingAnnotation
  public static @interface TestQualifierWithDefaultValue {

    String value() default "default";

  }

  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  @BindingAnnotation
  public static @interface TestQualifierWithMultipleAttributes {

    String value() default "default";

    int number();

  }

}
