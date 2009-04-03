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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ObjectUtils;

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
    final NestedTestBean ntb1 = new NestedTestBean();
    final NestedTestBean ntb2 = new NestedTestBean();

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
    assertNull(bean.getTestBean4());
    assertNull(bean.getNestedTestBeans());
  }

  public void testOptionalResourceInjectionWithNoDependencies() {
    Injector injector = createInjector(new AbstractModule() {
      protected void configure() {
      }
    });

    OptionalResourceInjectionBean bean = injector.getInstance(OptionalResourceInjectionBean.class);
    assertNull(bean.getTestBean());
    assertNull(bean.getTestBean2());
    assertNull(bean.getTestBean3());
    assertNull(bean.getTestBean4());
    assertNull(bean.getNestedTestBeans());
  }

  protected Injector createInjector(Module module) {
    return Guice.createInjector(new SpringModule(), module);
  }

  public interface ITestBean {

    int getAge();

    void setAge(int age);

    String getName();

    void setName(String name);

    ITestBean getSpouse();

    void setSpouse(ITestBean spouse);

    ITestBean[] getSpouses();

    String[] getStringArray();

    void setStringArray(String[] stringArray);

    /** Throws a given (non-null) exception. */
    void exceptional(Throwable t) throws Throwable;

    Object returnsThis();

    INestedTestBean getDoctor();

    INestedTestBean getLawyer();

    IndexedTestBean getNestedIndexedBean();

    /**
     * Increment the age by one.
     *
     * @return the previous age
     */
    int haveBirthday();

    void unreliableFileOperation() throws IOException;

  }

  public class TestBean implements ITestBean, Comparable {

    private String beanName;

    private String country;

    private BeanFactory beanFactory;

    private boolean postProcessed;

    private String name;

    private String sex;

    private int age;

    private boolean jedi;

    private ITestBean[] spouses;

    private String touchy;

    private String[] stringArray;

    private Integer[] someIntegerArray;

    private Date date = new Date();

    private Float myFloat = new Float(0.0);

    private Collection friends = new LinkedList();

    private Set someSet = new HashSet();

    private Map someMap = new HashMap();

    private List someList = new ArrayList();

    private Properties someProperties = new Properties();

    private INestedTestBean doctor = new NestedTestBean();

    private INestedTestBean lawyer = new NestedTestBean();

    private IndexedTestBean nestedIndexedBean;

    private boolean destroyed;

    private Number someNumber;

    private Boolean someBoolean;

    private List otherColours;

    private List pets;

    public TestBean() {
    }

    public TestBean(String name) {
      this.name = name;
    }

    public TestBean(ITestBean spouse) {
      this.spouses = new ITestBean[] { spouse };
    }

    public TestBean(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public TestBean(ITestBean spouse, Properties someProperties) {
      this.spouses = new ITestBean[] { spouse };
      this.someProperties = someProperties;
    }

    public TestBean(List someList) {
      this.someList = someList;
    }

    public TestBean(Set someSet) {
      this.someSet = someSet;
    }

    public TestBean(Map someMap) {
      this.someMap = someMap;
    }

    public TestBean(Properties someProperties) {
      this.someProperties = someProperties;
    }

    public void setBeanName(String beanName) {
      this.beanName = beanName;
    }

    public String getBeanName() {
      return beanName;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
      this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
      return beanFactory;
    }

    public void setPostProcessed(boolean postProcessed) {
      this.postProcessed = postProcessed;
    }

    public boolean isPostProcessed() {
      return postProcessed;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSex() {
      return sex;
    }

    public void setSex(String sex) {
      this.sex = sex;
      if (this.name == null) {
        this.name = sex;
      }
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

    public boolean isJedi() {
      return jedi;
    }

    public void setJedi(boolean jedi) {
      this.jedi = jedi;
    }

    public ITestBean getSpouse() {
      return (spouses != null ? spouses[0] : null);
    }

    public void setSpouse(ITestBean spouse) {
      this.spouses = new ITestBean[] { spouse };
    }

    public ITestBean[] getSpouses() {
      return spouses;
    }

    public String getTouchy() {
      return touchy;
    }

    public void setTouchy(String touchy) throws Exception {
      if (touchy.indexOf('.') != -1) {
        throw new Exception("Can't contain a .");
      }
      if (touchy.indexOf(',') != -1) {
        throw new NumberFormatException("Number format exception: contains a ,");
      }
      this.touchy = touchy;
    }

    public String getCountry() {
      return country;
    }

    public void setCountry(String country) {
      this.country = country;
    }

    public String[] getStringArray() {
      return stringArray;
    }

    public void setStringArray(String[] stringArray) {
      this.stringArray = stringArray;
    }

    public Integer[] getSomeIntegerArray() {
      return someIntegerArray;
    }

    public void setSomeIntegerArray(Integer[] someIntegerArray) {
      this.someIntegerArray = someIntegerArray;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }

    public Float getMyFloat() {
      return myFloat;
    }

    public void setMyFloat(Float myFloat) {
      this.myFloat = myFloat;
    }

    public Collection getFriends() {
      return friends;
    }

    public void setFriends(Collection friends) {
      this.friends = friends;
    }

    public Set getSomeSet() {
      return someSet;
    }

    public void setSomeSet(Set someSet) {
      this.someSet = someSet;
    }

    public Map getSomeMap() {
      return someMap;
    }

    public void setSomeMap(Map someMap) {
      this.someMap = someMap;
    }

    public List getSomeList() {
      return someList;
    }

    public void setSomeList(List someList) {
      this.someList = someList;
    }

    public Properties getSomeProperties() {
      return someProperties;
    }

    public void setSomeProperties(Properties someProperties) {
      this.someProperties = someProperties;
    }

    public INestedTestBean getDoctor() {
      return doctor;
    }

    public void setDoctor(INestedTestBean doctor) {
      this.doctor = doctor;
    }

    public INestedTestBean getLawyer() {
      return lawyer;
    }

    public void setLawyer(INestedTestBean lawyer) {
      this.lawyer = lawyer;
    }

    public Number getSomeNumber() {
      return someNumber;
    }

    public void setSomeNumber(Number someNumber) {
      this.someNumber = someNumber;
    }

    public Boolean getSomeBoolean() {
      return someBoolean;
    }

    public void setSomeBoolean(Boolean someBoolean) {
      this.someBoolean = someBoolean;
    }

    public IndexedTestBean getNestedIndexedBean() {
      return nestedIndexedBean;
    }

    public void setNestedIndexedBean(IndexedTestBean nestedIndexedBean) {
      this.nestedIndexedBean = nestedIndexedBean;
    }

    public List getOtherColours() {
      return otherColours;
    }

    public void setOtherColours(List otherColours) {
      this.otherColours = otherColours;
    }

    public List getPets() {
      return pets;
    }

    public void setPets(List pets) {
      this.pets = pets;
    }

    /** @see ITestBean#exceptional(Throwable) */
    public void exceptional(Throwable t) throws Throwable {
      if (t != null) {
        throw t;
      }
    }

    public void unreliableFileOperation() throws IOException {
      throw new IOException();
    }

    /** @see ITestBean#returnsThis() */
    public Object returnsThis() {
      return this;
    }

    public int haveBirthday() {
      return age++;
    }

    public void destroy() {
      this.destroyed = true;
    }

    public boolean wasDestroyed() {
      return destroyed;
    }

    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || !(other instanceof TestBean)) {
        return false;
      }
      TestBean tb2 = (TestBean) other;
      return (ObjectUtils.nullSafeEquals(this.name, tb2.name) && this.age == tb2.age);
    }

    public int hashCode() {
      return this.age;
    }

    public int compareTo(Object other) {
      if (this.name != null && other instanceof TestBean) {
        return this.name.compareTo(((TestBean) other).getName());
      }
      else {
        return 1;
      }
    }

    public String toString() {
      return this.name;
    }

  }

  public interface INestedTestBean {

    public String getCompany();

  }

  public class NestedTestBean implements INestedTestBean {

    private String company = "";

    public NestedTestBean() {
    }

    public NestedTestBean(String company) {
      setCompany(company);
    }

    public void setCompany(String company) {
      this.company = (company != null ? company : "");
    }

    public String getCompany() {
      return company;
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof NestedTestBean)) {
        return false;
      }
      NestedTestBean ntb = (NestedTestBean) obj;
      return this.company.equals(ntb.company);
    }

    public int hashCode() {
      return this.company.hashCode();
    }

    public String toString() {
      return "NestedTestBean: " + this.company;
    }

  }

  public class IndexedTestBean {

    private TestBean[] array;

    private Collection<?> collection;

    private List list;

    private Set<? super Object> set;

    private SortedSet<? super Object> sortedSet;

    private Map map;

    private SortedMap sortedMap;

    public IndexedTestBean() {
      this(true);
    }

    public IndexedTestBean(boolean populate) {
      if (populate) {
        populate();
      }
    }

    public void populate() {
      TestBean tb0 = new TestBean("name0", 0);
      TestBean tb1 = new TestBean("name1", 0);
      TestBean tb2 = new TestBean("name2", 0);
      TestBean tb3 = new TestBean("name3", 0);
      TestBean tb4 = new TestBean("name4", 0);
      TestBean tb5 = new TestBean("name5", 0);
      TestBean tb6 = new TestBean("name6", 0);
      TestBean tb7 = new TestBean("name7", 0);
      TestBean tbX = new TestBean("nameX", 0);
      TestBean tbY = new TestBean("nameY", 0);
      this.array = new TestBean[] { tb0, tb1 };
      this.list = new ArrayList<Object>();
      this.list.add(tb2);
      this.list.add(tb3);
      this.set = new TreeSet<Object>();
      this.set.add(tb6);
      this.set.add(tb7);
      this.map = new HashMap<Object, Object>();
      this.map.put("key1", tb4);
      this.map.put("key2", tb5);
      this.map.put("key.3", tb5);
      List list = new ArrayList();
      list.add(tbX);
      list.add(tbY);
      this.map.put("key4", list);
    }

    public TestBean[] getArray() {
      return array;
    }

    public void setArray(TestBean[] array) {
      this.array = array;
    }

    public Collection<?> getCollection() {
      return collection;
    }

    public void setCollection(Collection<?> collection) {
      this.collection = collection;
    }

    public List getList() {
      return list;
    }

    public void setList(List list) {
      this.list = list;
    }

    public Set<?> getSet() {
      return set;
    }

    public void setSet(Set<? super Object> set) {
      this.set = set;
    }

    public SortedSet<? super Object> getSortedSet() {
      return sortedSet;
    }

    public void setSortedSet(SortedSet<? super Object> sortedSet) {
      this.sortedSet = sortedSet;
    }

    public Map getMap() {
      return map;
    }

    public void setMap(Map map) {
      this.map = map;
    }

    public SortedMap getSortedMap() {
      return sortedMap;
    }

    public void setSortedMap(SortedMap sortedMap) {
      this.sortedMap = sortedMap;
    }

  }

  public static class ResourceInjectionBean {

    @Autowired(required = false)
    private TestBean testBean;

    private TestBean testBean2;

    @Autowired
    public void setTestBean2(TestBean testBean2) {
      if (this.testBean2 != null) {
        throw new IllegalStateException("Already called");
      }
      this.testBean2 = testBean2;
    }

    public TestBean getTestBean() {
      return this.testBean;
    }

    public TestBean getTestBean2() {
      return this.testBean2;
    }
  }

  public static class ExtendedResourceInjectionBean<T> extends ResourceInjectionBean {

    @Autowired
    protected ITestBean testBean3;

    private T nestedTestBean;

    private ITestBean testBean4;

    private BeanFactory beanFactory;

    public ExtendedResourceInjectionBean() {
    }

    @Autowired
    @Required
    public void setTestBean2(TestBean testBean2) {
      super.setTestBean2(testBean2);
    }

    @Autowired
    private void inject(ITestBean testBean4, T nestedTestBean) {
      this.testBean4 = testBean4;
      this.nestedTestBean = nestedTestBean;
    }

    @Autowired
    protected void initBeanFactory(BeanFactory beanFactory) {
      this.beanFactory = beanFactory;
    }

    public ITestBean getTestBean3() {
      return this.testBean3;
    }

    public ITestBean getTestBean4() {
      return this.testBean4;
    }

    public T getNestedTestBean() {
      return this.nestedTestBean;
    }

    public BeanFactory getBeanFactory() {
      return this.beanFactory;
    }
  }

  public static class TypedExtendedResourceInjectionBean
      extends ExtendedResourceInjectionBean<NestedTestBean> {

  }

  public static class OptionalResourceInjectionBean extends ResourceInjectionBean {

    @Autowired(required = false)
    protected ITestBean testBean3;

    private IndexedTestBean indexedTestBean;

    private NestedTestBean[] nestedTestBeans;

    @Autowired(required = false)
    public NestedTestBean[] nestedTestBeansField;

    private ITestBean testBean4;

    @Autowired(required = false)
    public void setTestBean2(TestBean testBean2) {
      super.setTestBean2(testBean2);
    }

    @Autowired(required = false)
    private void inject(ITestBean testBean4, NestedTestBean[] nestedTestBeans,
        IndexedTestBean indexedTestBean) {
      this.testBean4 = testBean4;
      this.indexedTestBean = indexedTestBean;
      this.nestedTestBeans = nestedTestBeans;
    }

    public ITestBean getTestBean3() {
      return this.testBean3;
    }

    public ITestBean getTestBean4() {
      return this.testBean4;
    }

    public IndexedTestBean getIndexedTestBean() {
      return this.indexedTestBean;
    }

    public NestedTestBean[] getNestedTestBeans() {
      return this.nestedTestBeans;
    }
  }

  public static class OptionalCollectionResourceInjectionBean extends ResourceInjectionBean {

    @Autowired(required = false)
    protected ITestBean testBean3;

    private IndexedTestBean indexedTestBean;

    private List<NestedTestBean> nestedTestBeans;

    public List<NestedTestBean> nestedTestBeansSetter;

    @Autowired(required = false)
    public List<NestedTestBean> nestedTestBeansField;

    private ITestBean testBean4;

    @Autowired(required = false)
    public void setTestBean2(TestBean testBean2) {
      super.setTestBean2(testBean2);
    }

    @Autowired(required = false)
    private void inject(ITestBean testBean4, List<NestedTestBean> nestedTestBeans,
        IndexedTestBean indexedTestBean) {
      this.testBean4 = testBean4;
      this.indexedTestBean = indexedTestBean;
      this.nestedTestBeans = nestedTestBeans;
    }

    @Autowired(required = false)
    public void setNestedTestBeans(List<NestedTestBean> nestedTestBeans) {
      this.nestedTestBeansSetter = nestedTestBeans;
    }

    public ITestBean getTestBean3() {
      return this.testBean3;
    }

    public ITestBean getTestBean4() {
      return this.testBean4;
    }

    public IndexedTestBean getIndexedTestBean() {
      return this.indexedTestBean;
    }

    public List<NestedTestBean> getNestedTestBeans() {
      return this.nestedTestBeans;
    }
  }

  public static class ConstructorResourceInjectionBean extends ResourceInjectionBean {

    @Autowired
    protected ITestBean testBean3;

    private ITestBean testBean4;

    private NestedTestBean nestedTestBean;

    private ConfigurableListableBeanFactory beanFactory;

    public ConstructorResourceInjectionBean() {
      throw new UnsupportedOperationException();
    }

    public ConstructorResourceInjectionBean(ITestBean testBean3) {
      throw new UnsupportedOperationException();
    }

    @Autowired
    public ConstructorResourceInjectionBean(ITestBean testBean4, NestedTestBean nestedTestBean,
        ConfigurableListableBeanFactory beanFactory) {
      this.testBean4 = testBean4;
      this.nestedTestBean = nestedTestBean;
      this.beanFactory = beanFactory;
    }

    public ConstructorResourceInjectionBean(NestedTestBean nestedTestBean) {
      throw new UnsupportedOperationException();
    }

    public ConstructorResourceInjectionBean(ITestBean testBean3, ITestBean testBean4,
        NestedTestBean nestedTestBean) {
      throw new UnsupportedOperationException();
    }

    @Autowired
    public void setTestBean2(TestBean testBean2) {
      super.setTestBean2(testBean2);
    }

    public ITestBean getTestBean3() {
      return this.testBean3;
    }

    public ITestBean getTestBean4() {
      return this.testBean4;
    }

    public NestedTestBean getNestedTestBean() {
      return this.nestedTestBean;
    }

    public ConfigurableListableBeanFactory getBeanFactory() {
      return this.beanFactory;
    }
  }

  public static class ConstructorsResourceInjectionBean {

    protected ITestBean testBean3;

    private ITestBean testBean4;

    private NestedTestBean[] nestedTestBeans;

    public ConstructorsResourceInjectionBean() {
    }

    @Autowired(required = false)
    public ConstructorsResourceInjectionBean(ITestBean testBean3) {
      this.testBean3 = testBean3;
    }

    @Autowired(required = false)
    public ConstructorsResourceInjectionBean(ITestBean testBean4,
        NestedTestBean[] nestedTestBeans) {
      this.testBean4 = testBean4;
      this.nestedTestBeans = nestedTestBeans;
    }

    public ConstructorsResourceInjectionBean(NestedTestBean nestedTestBean) {
      throw new UnsupportedOperationException();
    }

    public ConstructorsResourceInjectionBean(ITestBean testBean3, ITestBean testBean4,
        NestedTestBean nestedTestBean) {
      throw new UnsupportedOperationException();
    }

    public ITestBean getTestBean3() {
      return this.testBean3;
    }

    public ITestBean getTestBean4() {
      return this.testBean4;
    }

    public NestedTestBean[] getNestedTestBeans() {
      return this.nestedTestBeans;
    }
  }

  public static class ConstructorsCollectionResourceInjectionBean {

    protected ITestBean testBean3;

    private ITestBean testBean4;

    private List<NestedTestBean> nestedTestBeans;

    public ConstructorsCollectionResourceInjectionBean() {
    }

    @Autowired(required = false)
    public ConstructorsCollectionResourceInjectionBean(ITestBean testBean3) {
      this.testBean3 = testBean3;
    }

    @Autowired(required = false)
    public ConstructorsCollectionResourceInjectionBean(ITestBean testBean4,
        List<NestedTestBean> nestedTestBeans) {
      this.testBean4 = testBean4;
      this.nestedTestBeans = nestedTestBeans;
    }

    public ConstructorsCollectionResourceInjectionBean(NestedTestBean nestedTestBean) {
      throw new UnsupportedOperationException();
    }

    public ConstructorsCollectionResourceInjectionBean(ITestBean testBean3, ITestBean testBean4,
        NestedTestBean nestedTestBean) {
      throw new UnsupportedOperationException();
    }

    public ITestBean getTestBean3() {
      return this.testBean3;
    }

    public ITestBean getTestBean4() {
      return this.testBean4;
    }

    public List<NestedTestBean> getNestedTestBeans() {
      return this.nestedTestBeans;
    }
  }

  public static class MapConstructorInjectionBean {

    private Map<String, TestBean> testBeanMap;

    @Autowired
    public MapConstructorInjectionBean(Map<String, TestBean> testBeanMap) {
      this.testBeanMap = testBeanMap;
    }

    public Map<String, TestBean> getTestBeanMap() {
      return this.testBeanMap;
    }
  }

  public static class MapFieldInjectionBean {

    @Autowired
    private Map<String, TestBean> testBeanMap;

    public Map<String, TestBean> getTestBeanMap() {
      return this.testBeanMap;
    }
  }

  public static class MapMethodInjectionBean {

    private TestBean testBean;

    private Map<String, TestBean> testBeanMap;

    @Autowired(required = false)
    public void setTestBeanMap(TestBean testBean, Map<String, TestBean> testBeanMap) {
      this.testBean = testBean;
      this.testBeanMap = testBeanMap;
    }

    public TestBean getTestBean() {
      return this.testBean;
    }

    public Map<String, TestBean> getTestBeanMap() {
      return this.testBeanMap;
    }
  }

}
