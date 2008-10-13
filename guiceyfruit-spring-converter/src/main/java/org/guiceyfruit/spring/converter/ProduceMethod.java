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

import java.util.ArrayList;
import java.util.List;

/** @version $Revision: 1.1 $ */
public class ProduceMethod {
  private final String name;
  private final String className;
  private List<String> constructorExpressions = new ArrayList<String>();
  private List<Parameter> parameters = new ArrayList<Parameter>();
  private List<MethodCall> methodCalls = new ArrayList<MethodCall>();

  public ProduceMethod(String name, String className) {
    this.name = name;
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

  public String getName() {
    return name;
  }

  public List<String> getConstructorExpressions() {
    return constructorExpressions;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public List<MethodCall> getMethodCalls() {
    return methodCalls;
  }

  public void addConstructorExpression(String expression) {
    constructorExpressions.add(expression);
  }

  public void addParameter(String typeName, String beanRef) {
    parameters.add(new Parameter(typeName, beanRef));
  }

  public void addMethodCall(String object, String method, String expression) {
    methodCalls.add(new MethodCall(object, method, expression));
  }

  public static class Parameter {
    private final String type;
    private final String name;

    public Parameter(String type, String name) {
      this.type = type;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    @Override public String toString() {
      return type + " " + name;
    }
  }

  public static class MethodCall {
    private final String object;
    private final String method;
    private final String expression;

    public MethodCall(String object, String method, String expression) {
      this.object = object;
      this.method = method;
      this.expression = expression;
    }

    @Override public String toString() {
      return object + "." + method + "(" + expression + ");";
    }
  }
}
