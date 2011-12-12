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
package com.eightbitmage.moonscript.intentions.comments;

import com.eightbitmage.moonscript.intentions.base.PsiElementPredicate;
import com.eightbitmage.moonscript.intentions.utils.TreeUtil;
import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;


class CStyleCommentPredicate implements PsiElementPredicate {

  public boolean satisfiedBy(PsiElement element) {
    if (!(element instanceof PsiComment)) {
      return false;
    }
    if (element instanceof MoonDocComment) {
      return false;
    }
    final PsiComment comment = (PsiComment) element;
    final IElementType type = comment.getTokenType();
    if (!MoonTokenTypes.LONGCOMMENT.equals(type)) {
      return false;
    }
    final PsiElement sibling = TreeUtil.getNextLeaf(comment);
    if(sibling == null)
    {
      return true;
    }
    if (!(isWhitespace(sibling))) {
      return false;
    }
    final String whitespaceText = sibling.getText();
    return whitespaceText.indexOf((int) '\n') >= 0 ||
        whitespaceText.indexOf((int) '\r') >= 0;
  }

  private static boolean isWhitespace(PsiElement element) {
    return element.getText().replace("\n", "").trim().length() == 0;
  }
}
