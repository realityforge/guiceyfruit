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

package org.guiceyfruit.spring.converter;

import com.google.inject.internal.Strings;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * Converts a regular Spring XML document into a Guice module Java source file.
 *
 * @version $Revision: 1.1 $
 */
public class SpringConverter {
  private final XmlBeanFactory beanFactory;
  private String packageName = "";
  private String className = "MyModule";
  private String outputDir;
  //private String outputDir = "target/src/";
  private SortedSet<String> imports = new TreeSet<String>();
  private Set<String> ignoreClasses = new HashSet<String>();
  private Map<String, String> shortClassNames = new HashMap<String, String>();

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: springXmlFile [outputDirectory] [outputClassName]");
    }
    else {
      String springFile = args[0];
      XmlBeanFactory beanFactory = null;
      try {
        beanFactory = new XmlBeanFactory(new FileSystemResource(springFile));
      }
      catch (BeansException e) {
        System.out.println("Failed to open: " + springFile + ". Reason: " + e);
        e.printStackTrace();
        return;
      }
      try {
        SpringConverter converter = new SpringConverter(beanFactory);
        converter.convert();
      }
      catch (Exception e) {
        System.out.println("Failed to file from: " + springFile);
        System.out.println(e);
        e.printStackTrace();
      }
    }
  }

  public SpringConverter(XmlBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
    ignoreClasses.add("org.springframework.beans.factory.config.PropertyPlaceholderConfigurer");
  }

  public void convert() throws Exception {
    addImport("com.google.inject.AbstractModule");
    addImport("com.google.inject.Provides");

    PrintWriter writer = createOutputFileWriter();
    try {
      ModuleGenerator generator = new ModuleGenerator(this, writer);
      String[] names = beanFactory.getBeanDefinitionNames();
      for (String name : names) {
        BeanDefinition definition = beanFactory.getBeanDefinition(name);
        String className = definition.getBeanClassName();
        if (ignoreClasses.contains(className)) {
          continue;
        }

        generateBeanDefinition(generator, name, definition, className);
      }
      generator.generate();
    }
    finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  protected void generateBeanDefinition(ModuleGenerator generator, String name,
      BeanDefinition definition, String className) {
    String shortClassName = addImport(className);
    ProduceMethod method = generator.startProvides(name, shortClassName);

    ConstructorArgumentValues constructors = definition.getConstructorArgumentValues();
    Map map = constructors.getIndexedArgumentValues();
    for (int i = 0, size = map.size(); i < size; i++) {
      ValueHolder valueHolder = (ValueHolder) map.get(i);
      if (valueHolder != null) {
        Object value = valueHolder.getValue();
        if (value instanceof TypedStringValue) {
          TypedStringValue stringValue = (TypedStringValue) value;
          String text = stringValue.getValue();
          System.out.printf("param %s=\"%s\"\n", i, text);
          String expression = null;
          String namedParameter = namedParameter(text);
          if (namedParameter != null) {
            expression = addParameter(method, "String", namedParameter);
          }
          else {
            expression = "\"" + text + "\"";
          }
          method.addConstructorExpression(expression);
        }
        else if (value instanceof BeanReference) {
          BeanReference reference = (BeanReference) value;
          String beanRef = reference.getBeanName();
          // TODO
          String typeName = "Object";
          String expression = addParameter(method, typeName, beanRef);
          method.addConstructorExpression(expression);
        }
      }
    }

    MutablePropertyValues propertyValues = definition.getPropertyValues();
    PropertyValue[] propertyArray = propertyValues.getPropertyValues();
    for (PropertyValue propertyValue : propertyArray) {
      String property = getSetterMethod(propertyValue);
      Object value = propertyValue.getConvertedValue();
      if (value == null) {
        value = propertyValue.getValue();
      }
      if (value instanceof BeanReference) {
        BeanReference reference = (BeanReference) value;
        String beanRef = reference.getBeanName();
        // TODO
        String typeName = "Object";
        String expression = addParameter(method, typeName, beanRef);
        method.addMethodCall("answer", getSetterMethod(propertyValue), expression);
      }
      else if (value instanceof BeanDefinitionHolder) {
        BeanDefinitionHolder beanDefinitionHolder = (BeanDefinitionHolder) value;
        addChildBeanDefinition(generator, method, name, propertyValue,
            beanDefinitionHolder.getBeanDefinition());
      }
      else if (value instanceof ChildBeanDefinition) {
        ChildBeanDefinition childBeanDefinition = (ChildBeanDefinition) value;
        addChildBeanDefinition(generator, method, name, propertyValue,
            childBeanDefinition);
      }
      else {
        if (value instanceof TypedStringValue) {
          TypedStringValue stringValue = (TypedStringValue) value;
          value = stringValue.getValue();

        }
        String valueType = (value == null) ? null : value.getClass().getName();
        System.out.printf("property %s=%s of type %s\n", property, value, valueType);

        String expression;
        if (value instanceof String) {
          String text = (String) value;
          String namedParameter = namedParameter(text);
          if (namedParameter != null) {
            expression = addParameter(method, "String", namedParameter);
          }
          else {
            expression = "\"" + value + "\"";
          }
        }
        else if (value == null) {
          expression = "null";
        }
        else {
          expression = value.toString();
        }
        method.addMethodCall("answer", getSetterMethod(propertyValue), expression);
      }
    }
  }

  /**
   * Returns "something" for the string "${something} otherwise returns null if its not a property
   * placeholder string
   */
  protected String namedParameter(String text) {
    if (text.startsWith("${") && text.endsWith("}")) {
      return text.substring(2, text.length() - 1);
    }
    return null;
  }

  protected void addChildBeanDefinition(ModuleGenerator generator, ProduceMethod method,
      String name, PropertyValue propertyValue, BeanDefinition beanDefinition) {
    String childBeanName = childBeanName(name, propertyValue.getName());
    String childType = addImport(beanDefinition.getBeanClassName());
    String expression = addParameter(method, childType, childBeanName);
    generateBeanDefinition(generator, childBeanName, beanDefinition, childType);
    method.addMethodCall("answer", getSetterMethod(propertyValue), expression);
  }

  protected String addParameter(ProduceMethod method, String typeName, String beanRef) {
    String identifier = ModuleGenerator.asJavaIdentifier(beanRef);
    method.addParameter(namedParameterType(typeName, beanRef), identifier);
    return identifier;
  }

  protected String namedParameterType(String typeName, String beanRef) {
    return String.format("@Named(\"%s\") %s", beanRef, typeName);
  }

  protected String childBeanName(String name, String property) {
    return name + "." + property;
  }

  protected String getSetterMethod(PropertyValue propertyValue) {
    return "set" + Strings.capitalize(propertyValue.getName());
  }

  protected String addImport(String className) {
    if (!className.contains(".")) {
      return className;
    }
    imports.add(className);

    String[] names = splitClassName(className);
    String shortName = names[1];
    String alias = shortClassNames.get(shortName);
    if (alias != null && !alias.equals(className)) {
      return className;
    }
    else {
      shortClassNames.put(shortName, className);
      return shortName;
    }
  }

  protected String[] splitClassName(String className) {
    int idx = className.lastIndexOf(".");
    if (idx <= 0) {
      return new String[] { "", className };
    }
    else {
      return new String[] { className.substring(0, idx), className.substring(idx + 1) };
    }
  }

  public boolean hasPackage() {
    return packageName != null && packageName.length() > 0;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public SortedSet<String> getImports() {
    return imports;
  }

  protected PrintWriter createOutputFileWriter() throws IOException {
    if (outputDir == null) {
      return new PrintWriter(new OutputStreamWriter(System.out));
    }
    File file = new File(getOutputFileName());
    file.getParentFile().mkdirs();
    return new PrintWriter(new FileWriter(file));
  }

  protected String getOutputFileName() {
    StringBuilder buffer = new StringBuilder(outputDir);
    if (hasPackage()) {
      buffer.append(packageName.replace('.', '/'));
      buffer.append("/");
    }
    buffer.append(className);
    buffer.append(".java");
    return buffer.toString();
  }
}
