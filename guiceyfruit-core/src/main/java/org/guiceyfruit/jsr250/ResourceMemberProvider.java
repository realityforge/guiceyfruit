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

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import java.lang.reflect.Member;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.guiceyfruit.support.AnnotationMemberProviderSupport;

/**
 * Injects fields or methods with the results of the {@link Resource} annotation
 *
 * @version $Revision: 1.1 $
 */
public class ResourceMemberProvider extends AnnotationMemberProviderSupport<Resource> {

  private Context context;

  public Context getContext() {
    return context;
  }

  @Inject(optional = true)
  public void setContext(Context context) {
    this.context = context;
  }

  protected Object provide(Resource resource, Member member, Class<?> requiredType) {
    String name = getJndiName(resource, member);

    try {
      if (context == null) {
        context = new InitialContext();
      }
      return context.lookup(name);
    }
    catch (NamingException e) {
      throw new ProvisionException("Failed to find name '" + name + "' in JNDI. Cause: " + e, e);
    }
  }

  /** Deduces the JNDI name from the resource and member according to the JSR 250 rules */
  String getJndiName(Resource resource, Member member) {
    String answer = resource.name();
    if (answer == null || answer.length() == 0) {
      answer = member.getName();
    }
    if (answer == null || answer.length() == 0) {
      throw new IllegalArgumentException("No name defined");
    }
    return answer;
  }
}
