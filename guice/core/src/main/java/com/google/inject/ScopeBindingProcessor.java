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

package com.google.inject;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.spi.ScopeBinding;
import java.lang.annotation.Annotation;

/**
 * Handles {@link Binder#bindScope} commands.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
class ScopeBindingProcessor extends AbstractProcessor {

  private final State state;

  ScopeBindingProcessor(Errors errors,
      State state) {
    super(errors);
    this.state = state;
  }

  @Override public Boolean visitScopeBinding(ScopeBinding command) {
    Scope scope = command.getScope();
    Class<? extends Annotation> annotationType = command.getAnnotationType();

    if (!Annotations.isScopeAnnotation(annotationType)) {
      errors.withSource(annotationType).missingScopeAnnotation();
      // Go ahead and bind anyway so we don't get collateral errors.
    }

    if (!Annotations.isRetainedAtRuntime(annotationType)) {
      errors.withSource(annotationType)
          .missingRuntimeRetention(command.getSource());
      // Go ahead and bind anyway so we don't get collateral errors.
    }

    Scope existing = state.getScope(checkNotNull(annotationType, "annotation type"));
    if (existing != null) {
      errors.duplicateScopes(existing, annotationType, scope);
    } else {
      state.putAnnotation(annotationType, checkNotNull(scope, "scope"));
    }

    return true;
  }
}