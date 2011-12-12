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

import com.eightbitmage.moonscript.intentions.MoonIntentionsBundle;
import com.eightbitmage.moonscript.intentions.base.MutablyNamedIntention;
import com.eightbitmage.moonscript.intentions.base.PsiElementPredicate;
import com.eightbitmage.moonscript.intentions.utils.ComparisonUtils;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonBinaryExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;


public class NegateComparisonIntention extends MutablyNamedIntention {
  protected String getTextForElement(PsiElement element) {
    final MoonBinaryExpression binaryExpression =
        (MoonBinaryExpression) element;
    final IElementType tokenType = binaryExpression.getOperationTokenType();
    final String comparison = ComparisonUtils.getStringForComparison(tokenType);
    final String negatedComparison = ComparisonUtils.getNegatedComparison(tokenType);

    return MoonIntentionsBundle.message("negate.comparison.intention.name", comparison, negatedComparison);
  }

  @NotNull
  public PsiElementPredicate getElementPredicate() {
    return new ComparisonPredicate();
  }

  public void processIntention(@NotNull PsiElement element)
      throws IncorrectOperationException {
    final MoonBinaryExpression exp =
        (MoonBinaryExpression) element;
    final IElementType tokenType = exp.getOperationTokenType();

    final MoonExpression lhs = exp.getLeftOperand();
    final String lhsText = lhs.getText();

    final MoonExpression rhs = exp.getRightOperand();
    final String rhsText = rhs.getText();

    final String negatedComparison = ComparisonUtils.getNegatedComparison(tokenType);

    final String newExpression = lhsText + negatedComparison + rhsText;
    replaceExpressionWithNegatedExpressionString(newExpression, exp);
  }

}
