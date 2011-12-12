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
package com.eightbitmage.moonscript.intentions.control;

import com.eightbitmage.moonscript.intentions.LuaIntentionsBundle;
import com.eightbitmage.moonscript.lang.lexer.LuaTokenTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaBinaryExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.intentions.base.IntentionUtils;
import com.eightbitmage.moonscript.intentions.base.MutablyNamedIntention;
import com.eightbitmage.moonscript.intentions.base.PsiElementPredicate;
import org.jetbrains.annotations.NotNull;


public class FlipConjunctionIntention extends MutablyNamedIntention {
  protected String getTextForElement(PsiElement element) {
    final LuaBinaryExpression binaryExpression =
        (LuaBinaryExpression) element;
    final IElementType tokenType = binaryExpression.getOperationTokenType();
    final String conjunction;
    assert tokenType != null;
    if (tokenType.equals(LuaTokenTypes.AND)) {
      conjunction = LuaTokenTypes.AND.toString();
    } else {
      conjunction =  LuaTokenTypes.OR.toString();
    }
    return LuaIntentionsBundle.message("flip.conjunction.intention.name", conjunction);
  }

  @NotNull
  public PsiElementPredicate getElementPredicate() {
    return new ConjunctionPredicate();
  }

  public void processIntention(@NotNull PsiElement element)
      throws IncorrectOperationException {
    final LuaBinaryExpression exp =
        (LuaBinaryExpression) element;
    final IElementType tokenType = exp.getOperationTokenType();

    final LuaExpression lhs = exp.getLeftOperand();
    final String lhsText = lhs.getText();

    final LuaExpression rhs = exp.getRightOperand();
    final String rhsText = rhs.getText();

    final String conjunction;
    if (tokenType.equals(LuaTokenTypes.AND)) {
      conjunction = LuaTokenTypes.AND.toString();
    } else {
      conjunction = LuaTokenTypes.OR.toString();
    }

    final String newExpression =
        rhsText + conjunction + lhsText;
    IntentionUtils.replaceExpression(newExpression, exp);
  }

}
