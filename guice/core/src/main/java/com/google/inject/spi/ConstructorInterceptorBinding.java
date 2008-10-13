/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.spi;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.matcher.Matcher;
import java.util.Arrays;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import org.aopalliance.intercept.ConstructorInterceptor;

/**
 * Registration of {@link ConstructorInterceptor} instances for matching classes. Instances are created
 * explicitly in a module using {@link com.google.inject.Binder#bindConstructorInterceptor(com.google.inject.matcher.Matcher, org.aopalliance.intercept.ConstructorInterceptor[])}
 * statements:
 * <pre>
 *     bindConstructorInterceptor(Matchers.subclassesOf(MyAction.class),
 *         new MyPostConstructionInterceptor());</pre>
 *
 * @author james.strachan@gmail.com (James Strachan)
 */
public final class ConstructorInterceptorBinding implements Element {
  private final Object source;
  private final Matcher<? super Class<?>> classMatcher;
  private final List<ConstructorInterceptor> interceptors;

  ConstructorInterceptorBinding(
      Object source,
      Matcher<? super Class<?>> classMatcher,
      ConstructorInterceptor[] interceptors) {
    this.source = checkNotNull(source, "source");
    this.classMatcher = checkNotNull(classMatcher, "classMatcher");
    this.interceptors = unmodifiableList(Arrays.asList(interceptors.clone()));
  }

  public Object getSource() {
    return source;
  }

  public Matcher<? super Class<?>> getClassMatcher() {
    return classMatcher;
  }

  public List<ConstructorInterceptor> getInterceptors() {
    return interceptors;
  }

  public <T> T acceptVisitor(ElementVisitor<T> visitor) {
    return visitor.visitConstructorInterceptorBinding(this);
  }
}
