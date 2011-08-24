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

package org.guiceyfruit.jsr250;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.guiceyfruit.Injectors;
import org.guiceyfruit.support.AnnotationMemberProviderSupport;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Injects fields or methods with the results of the {@link Resource} annotation
 *
 * @version $Revision: 1.1 $
 */
public class ResourceMemberProvider extends NamedProviderSupport<Resource> {

    public boolean isNullParameterAllowed(Resource annotation, Method method, Class<?> parameterType,
                                          int parameterIndex) {
        // TODO can a @Resource be optional?
        return false;
    }

    protected Object provide(Resource resource, Member member, TypeLiteral<?> requiredType,
                             Class<?> memberType, Annotation[] annotations) {
        String name = getValueName(resource.name(), member);
        return provideObjectFromNamedBindingOrJndi(requiredType, name);
    }

}
