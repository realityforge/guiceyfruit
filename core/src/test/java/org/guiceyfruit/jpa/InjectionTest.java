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
import com.google.inject.Provides;
import javax.persistence.Cache;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import junit.framework.TestCase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;

/**
 * @version $Revision: 1.1 $
 */
public class InjectionTest extends TestCase {

    public void testInjection() throws Exception {
        final EntityManager stubEntityManager = createStubEntityManager();

        Injector injector = Guice.createInjector(new JpaModule() {

            @Override
            protected void configure() {
                super.configure();

                // TODO this should be the hibernate implementation
                bind(EntityManagerFactory.class).toInstance(
                        new EntityManagerFactory() {
                            boolean open = true;

                          public CriteriaBuilder getCriteriaBuilder()
                          {
                            return null;
                          }

                          public Metamodel getMetamodel()
                          {
                            return null;
                          }

                          public Map<String, Object> getProperties()
                          {
                            return null;
                          }

                          public Cache getCache()
                          {
                            return null;
                          }

                          public PersistenceUnitUtil getPersistenceUnitUtil()
                          {
                            return null;
                          }

                          public EntityManager createEntityManager() {
                                return stubEntityManager;
                            }

                            public EntityManager createEntityManager(Map map) {
                                return stubEntityManager;
                            }

                            public void close() {
                                open = false;
                            }

                            public boolean isOpen() {
                                return open;
                            }


                        }
                );
            }

            @Provides
            public EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
                return entityManagerFactory.createEntityManager();
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

          public <T> T find( final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties )
          {
            return null;
          }

          public <T> T find( final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode )
          {
            return null;
          }

          public <T> T find( final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode, final Map<String, Object> properties )
          {
            return null;
          }

          public void lock( final Object entity, final LockModeType lockMode, final Map<String, Object> properties )
          {
          
          }

          public void refresh( final Object entity, final Map<String, Object> properties )
          {
          
          }

          public void refresh( final Object entity, final LockModeType lockMode )
          {
          
          }

          public void refresh( final Object entity, final LockModeType lockMode, final Map<String, Object> properties )
          {
          
          }

          public void detach( final Object entity )
          {
          
          }

          public LockModeType getLockMode( final Object entity )
          {
            return null;
          }

          public void setProperty( final String propertyName, final Object value )
          {
          
          }

          public Map<String, Object> getProperties()
          {
            return null;
          }

          public <T> TypedQuery<T> createQuery( final CriteriaQuery<T> criteriaQuery )
          {
            return null;
          }

          public <T> TypedQuery<T> createQuery( final String qlString, final Class<T> resultClass )
          {
            return null;
          }

          public <T> TypedQuery<T> createNamedQuery( final String name, final Class<T> resultClass )
          {
            return null;
          }

          public <T> T unwrap( final Class<T> cls )
          {
            return null;
          }

          public EntityManagerFactory getEntityManagerFactory()
          {
            return null;
          }

          public CriteriaBuilder getCriteriaBuilder()
          {
            return null;
          }

          public Metamodel getMetamodel()
          {
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
