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

package org.guiceyfruit.jpa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import junit.framework.TestCase;

/** @version $Revision: 1.1 $ */
public class InjectionTest extends TestCase {

  public void testInjection() throws Exception {
    final EntityManager stubEntityManager = createStubEntityManager();

    Injector injector = Guice.createInjector(new JpaModule() {
      @Override
      protected void configure() {
        super.configure();

        bind(EntityManager.class).toInstance(stubEntityManager);
      }
    });

    TestDTO testDTO = injector.getInstance(TestDTO.class);
    assertEquals("EntityManager", stubEntityManager, testDTO.getEntityManager());
  }

  protected EntityManager createStubEntityManager() {
    return new EntityManager() {
      public void persist(Object o) {
        // TODO

      }

      public <T> T merge(T t) {
        // TODO
        return null;
      }

      public void remove(Object o) {
        // TODO

      }

      public <T> T find(Class<T> tClass, Object o) {
        // TODO
        return null;
      }

      public <T> T getReference(Class<T> tClass, Object o) {
        // TODO
        return null;
      }

      public void flush() {
        // TODO

      }

      public void setFlushMode(FlushModeType flushModeType) {
        // TODO

      }

      public FlushModeType getFlushMode() {
        // TODO
        return null;
      }

      public void lock(Object o, LockModeType lockModeType) {
        // TODO

      }

      public void refresh(Object o) {
        // TODO

      }

      public void clear() {
        // TODO

      }

      public boolean contains(Object o) {
        // TODO
        return false;
      }

      public Query createQuery(String s) {
        // TODO
        return null;
      }

      public Query createNamedQuery(String s) {
        // TODO
        return null;
      }

      public Query createNativeQuery(String s) {
        // TODO
        return null;
      }

      public Query createNativeQuery(String s, Class aClass) {
        // TODO
        return null;
      }

      public Query createNativeQuery(String s, String s1) {
        // TODO
        return null;
      }

      public void close() {
        // TODO

      }

      public boolean isOpen() {
        // TODO
        return false;
      }

      public EntityTransaction getTransaction() {
        // TODO
        return null;
      }

      public void joinTransaction() {
        // TODO

      }

      public Object getDelegate() {
        // TODO
        return null;
      }
    };
  }

  public static class TestDTO {
    @PersistenceContext
    EntityManager entityManager;

    public EntityManager getEntityManager() {
      return entityManager;
    }
  }
}
