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
 * 
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

  public void testAutowiredFieldWithSingleQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifier
      public Person createPerson() {
        return new Person(JUERGEN);
      }
    });

    QualifiedFieldTestBean bean = injector.getInstance(QualifiedFieldTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredMethodParameterWithSingleQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifier
      public Person createPerson() {
        return new Person(JUERGEN);
      }
    });

    QualifiedMethodParameterTestBean bean = injector
        .getInstance(QualifiedMethodParameterTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredMethodParameterWithStaticallyQualifiedCandidate() {
    // TODO for some reason this works in Spring - I don't understand why this is meant to work
    // yet testAutowiredFieldWithSingleNonQualifiedCandidate() is meant to fail!
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        public Person createPerson() {
          return new Person(JUERGEN);
        }
      });

      QualifiedMethodParameterTestBean bean = injector
          .getInstance(QualifiedMethodParameterTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
    //assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredConstructorArgumentWithSingleQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifier
      public Person createPerson() {
        return new Person(JUERGEN);
      }

      // TODO we cannot yet do constructor injection due to Guice limitation
      // so we must create a provider
      @Provides
      public QualifiedConstructorArgumentTestBean createBean(@TestQualifier Person person) {
        return new QualifiedConstructorArgumentTestBean(person);
      }
    });

    QualifiedConstructorArgumentTestBean bean = injector
        .getInstance(QualifiedConstructorArgumentTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredFieldWithMultipleNonQualifiedCandidates() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        @Named(JUERGEN)
        public Person createJuergen() {
          return new Person(JUERGEN);
        }

        @Provides
        @Named(MARK)
        public Person createMark() {
          return new Person(MARK);
        }
      });

      QualifiedFieldTestBean bean = injector.getInstance(QualifiedFieldTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
  }

  public void testAutowiredMethodParameterWithMultipleNonQualifiedCandidates() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        @Named(JUERGEN)
        public Person createJuergen() {
          return new Person(JUERGEN);
        }

        @Provides
        @Named(MARK)
        public Person createMark() {
          return new Person(MARK);
        }
      });

      QualifiedMethodParameterTestBean bean = injector
          .getInstance(QualifiedMethodParameterTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
  }

  public void testAutowiredConstructorArgumentWithMultipleNonQualifiedCandidates() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        @Named(JUERGEN)
        public Person createJuergen() {
          return new Person(JUERGEN);
        }

        @Provides
        @Named(MARK)
        public Person createMark() {
          return new Person(MARK);
        }

        // TODO we cannot yet do constructor injection due to Guice limitation
        // so we must create a provider
        @Provides
        public QualifiedConstructorArgumentTestBean createBean(@TestQualifier Person person) {
          return new QualifiedConstructorArgumentTestBean(person);
        }
      });

      QualifiedConstructorArgumentTestBean bean = injector
          .getInstance(QualifiedConstructorArgumentTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (CreationException e) {
      // expected
    }
  }

  public void testAutowiredFieldResolvesQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifier
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @Named(MARK)
      public Person createMark() {
        return new Person(MARK);
      }
    });

    QualifiedFieldTestBean bean = injector.getInstance(QualifiedFieldTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredMethodParameterResolvesQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifier
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @Named(MARK)
      public Person createMark() {
        return new Person(MARK);
      }
    });

    QualifiedMethodParameterTestBean bean = injector
        .getInstance(QualifiedMethodParameterTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());

  }

  public void testAutowiredConstructorArgumentResolvesQualifiedCandidate() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifier
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @Named(MARK)
      public Person createMark() {
        return new Person(MARK);
      }

      // TODO we cannot yet do constructor injection due to Guice limitation
      // so we must create a provider
      @Provides
      public QualifiedConstructorArgumentTestBean createBean(@TestQualifier Person person) {
        return new QualifiedConstructorArgumentTestBean(person);
      }
    });

    QualifiedConstructorArgumentTestBean bean = injector
        .getInstance(QualifiedConstructorArgumentTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredFieldResolvesQualifiedCandidateWithDefaultValueAndNoValueOnBeanDefinition() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifierWithDefaultValue
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @Named(MARK)
      public Person createMark() {
        return new Person(MARK);
      }
    });
    QualifiedFieldWithDefaultValueTestBean bean = injector
        .getInstance(QualifiedFieldWithDefaultValueTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredFieldDoesNotResolveCandidateWithDefaultValueAndConflictingValueOnBeanDefinition() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        @TestQualifierWithDefaultValue("not the default")
        public Person createJuergen() {
          return new Person(JUERGEN);
        }

        @Provides
        @Named(MARK)
        public Person createMark() {
          return new Person(MARK);
        }
      });
      QualifiedFieldWithDefaultValueTestBean bean = injector
          .getInstance(QualifiedFieldWithDefaultValueTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
  }
  public void testAutowiredFieldResolvesWithDefaultValueAndExplicitDefaultValueOnBeanDefinition() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifierWithDefaultValue("default")
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @Named(MARK)
      public Person createMark() {
        return new Person(MARK);
      }
    });
    QualifiedFieldWithDefaultValueTestBean bean = injector
        .getInstance(QualifiedFieldWithDefaultValueTestBean.class);
    assertEquals(JUERGEN, bean.getPerson().getName());
  }

  public void testAutowiredFieldResolvesWithMultipleQualifierValues() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifierWithMultipleAttributes(number = 456)
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @TestQualifierWithMultipleAttributes(number = 123)
      public Person createMark() {
        return new Person(MARK);
      }
    });
    QualifiedFieldWithMultipleAttributesTestBean bean = injector
        .getInstance(QualifiedFieldWithMultipleAttributesTestBean.class);
    assertEquals(MARK, bean.getPerson().getName());
  }

  public void testAutowiredFieldDoesNotResolveWithMultipleQualifierValuesAndConflictingDefaultValue() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        @TestQualifierWithMultipleAttributes(number = 456)
        public Person createJuergen() {
          return new Person(JUERGEN);
        }

        @Provides
        @TestQualifierWithMultipleAttributes(number = 123, value = "not the default")
        public Person createMark() {
          return new Person(MARK);
        }
      });
      QualifiedFieldWithMultipleAttributesTestBean bean = injector
          .getInstance(QualifiedFieldWithMultipleAttributesTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (ProvisionException e) {
      // expected
    }
  }

  public void testAutowiredFieldResolvesWithMultipleQualifierValuesAndExplicitDefaultValue() {
    Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
      @Provides
      @TestQualifierWithMultipleAttributes(number = 456)
      public Person createJuergen() {
        return new Person(JUERGEN);
      }

      @Provides
      @TestQualifierWithMultipleAttributes(number = 123, value = "default")
      public Person createMark() {
        return new Person(MARK);
      }
    });
    QualifiedFieldWithMultipleAttributesTestBean bean = injector
        .getInstance(QualifiedFieldWithMultipleAttributesTestBean.class);
    assertEquals(MARK, bean.getPerson().getName());
  }


  public void testAutowiredFieldDoesNotResolveWithMultipleQualifierValuesAndMultipleMatchingCandidates() {
    try {
      Injector injector = SpringModule.createInjector(new GuiceyFruitModule() {
        @Provides
        @TestQualifierWithMultipleAttributes(number = 123)
        public Person createJuergen() {
          return new Person(JUERGEN);
        }

        @Provides
        @TestQualifierWithMultipleAttributes(number = 123, value = "default")
        public Person createMark() {
          return new Person(MARK);
        }
      });
      QualifiedFieldWithMultipleAttributesTestBean bean = injector
          .getInstance(QualifiedFieldWithMultipleAttributesTestBean.class);
      fail("expected failure creating " + bean);
    }
    catch (CreationException e) {
      // expected
    }
  }


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

  @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  @BindingAnnotation
  public static @interface TestQualifier {
  }

  @Target({ ElementType.FIELD, ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  @BindingAnnotation
  public static @interface TestQualifierWithDefaultValue {

    String value() default "default";

  }

  @Target({ ElementType.FIELD, ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  @BindingAnnotation
  public static @interface TestQualifierWithMultipleAttributes {

    String value() default "default";

    int number();

  }

}
