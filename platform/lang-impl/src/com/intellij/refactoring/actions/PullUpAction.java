/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.intellij.refactoring.actions;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageRefactoringSupport;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.lang.ElementsHandler;
import org.jetbrains.annotations.NotNull;

public class PullUpAction extends BasePlatformRefactoringAction {

  public PullUpAction() {
    setInjectedContext(true);
  }

  public boolean isAvailableInEditorOnly() {
    return false;
  }

  public boolean isEnabledOnElements(PsiElement[] elements) {
    if (elements.length > 0) {
      final Language language = elements[0].getLanguage();
      final RefactoringActionHandler handler = LanguageRefactoringSupport.INSTANCE.forLanguage(language).getPullUpHandler();
      return handler instanceof ElementsHandler && ((ElementsHandler)handler).isEnabledOnElements(elements);
    }
    return false;
  }

  @Override
  protected RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
    return provider.getPullUpHandler();
  }
}
