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
package com.eightbitmage.moonscript.intentions.utils;

import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;


public class BoolUtils {
  private BoolUtils() {
    super();
  }

  public static boolean isNegated(MoonExpression exp) {
    MoonExpression ancestor = exp;
    while (ancestor.getParent() instanceof MoonParenthesizedExpression) {
      ancestor = (MoonExpression) ancestor.getParent();
    }
    if (ancestor.getParent() instanceof MoonUnaryExpression) {
      final MoonUnaryExpression prefixAncestor =
          (MoonUnaryExpression) ancestor.getParent();
      final IElementType sign = prefixAncestor.getOperationTokenType();
      if (MoonTokenTypes.NOT.equals(sign)) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  public static MoonExpression findNegation(MoonExpression exp) {
    MoonExpression ancestor = exp;
    while (ancestor.getParent() instanceof MoonParenthesizedExpression) {
      ancestor = (MoonExpression) ancestor.getParent();
    }
    if (ancestor.getParent() instanceof MoonUnaryExpression) {
      final MoonUnaryExpression prefixAncestor =
          (MoonUnaryExpression) ancestor.getParent();
      final IElementType sign = prefixAncestor.getOperationTokenType();
      if (MoonTokenTypes.NOT.equals(sign)) {
        return prefixAncestor;
      }
    }
    return null;
  }

  public static boolean isNegation(MoonExpression exp) {
    if (!(exp instanceof MoonUnaryExpression)) {
      return false;
    }
    final MoonUnaryExpression prefixExp = (MoonUnaryExpression) exp;
    final IElementType sign = prefixExp.getOperationTokenType();
    return MoonTokenTypes.NOT.equals(sign);
  }

  public static MoonExpression getNegated(MoonExpression exp) {
    final MoonUnaryExpression prefixExp = (MoonUnaryExpression) exp;
    final MoonExpression operand = prefixExp.getOperand();
    return ParenthesesUtils.stripParentheses(operand);
  }

  public static boolean isBooleanLiteral(MoonExpression exp) {
    if (exp instanceof MoonLiteralExpression) {
      final MoonLiteralExpression expression = (MoonLiteralExpression) exp;
      @NonNls final String text = expression.getText();
      return MoonTokenTypes.TRUE.equals(text) || MoonTokenTypes.FALSE.equals(text);
    }
    return false;
  }

  public static String getNegatedExpressionText(MoonExpression condition) {
    if (isNegation(condition)) {
      final MoonExpression negated = getNegated(condition);
      return negated.getText();
    } else if (ComparisonUtils.isComparison(condition)) {
      final MoonBinaryExpression binaryExpression =
          (MoonBinaryExpression) condition;
      final IElementType sign = binaryExpression.getOperationTokenType();
      final String negatedComparison =
          ComparisonUtils.getNegatedComparison(sign);
      final MoonExpression lhs = binaryExpression.getLeftOperand();
      final MoonExpression rhs = binaryExpression.getRightOperand();
      assert rhs != null;
      return lhs.getText() + negatedComparison + rhs.getText();
    } else if (ParenthesesUtils.getPrecendence(condition) >
        ParenthesesUtils.PREFIX_PRECEDENCE) {
      return "not (" + condition.getText() + ')';
    } else {
      return "not " + condition.getText();
    }
  }
}

