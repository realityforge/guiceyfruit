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

package org.guiceyfruit.spring.converter.example;

/** @version $Revision: 1.1 $ */
public class MyBean {
  String name;
  int size;
  AnotherBean foo;
  AnotherBean bar;

  public MyBean() {
  }

  public MyBean(String name) {
    this.name = name;
  }

  public AnotherBean getFoo() {
    return foo;
  }

  public void setFoo(AnotherBean foo) {
    this.foo = foo;
  }

  public AnotherBean getBar() {
    return bar;
  }

  public void setBar(AnotherBean bar) {
    this.bar = bar;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
