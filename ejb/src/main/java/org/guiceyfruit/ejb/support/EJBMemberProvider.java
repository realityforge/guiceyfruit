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
package org.guiceyfruit.ejb.support;

import com.google.inject.TypeLiteral;
import org.guiceyfruit.jsr250.NamedProviderSupport;

import javax.ejb.EJB;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * @version $Revision: 1.1 $
 */
public class EJBMemberProvider extends NamedProviderSupport<EJB> {
    public boolean isNullParameterAllowed(EJB annotation, Method method, Class<?> parameterType, int parameterIndex) {
        return false;
    }

    protected Object provide(EJB annotation, Member member, TypeLiteral<?> requiredType, Class<?> memberType, Annotation[] annotations) {
        String name = getValueName(annotation.beanName(), member);
        return provideObjectFromNamedBindingOrJndi(requiredType, name);
    }
}
