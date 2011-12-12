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
import com.eightbitmage.moonscript.lang.psi.expressions.MoonBinaryExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.Map;

public class ComparisonUtils {
  private static final Map<IElementType, String> s_comparisonStrings = new HashMap<IElementType, String>(6);
  private static final Map<IElementType, String> s_swappedComparisons = new HashMap<IElementType, String>(6);
  private static final Map<IElementType, String> s_invertedComparisons = new HashMap<IElementType, String>(6);

  private ComparisonUtils() {
    super();
  }

  static {
    s_comparisonStrings.put(MoonTokenTypes.EQ, "==");
    s_comparisonStrings.put(MoonTokenTypes.NE, "~=");
    s_comparisonStrings.put(MoonTokenTypes.GT, ">");
    s_comparisonStrings.put(MoonTokenTypes.LT, "<");
    s_comparisonStrings.put(MoonTokenTypes.GE, ">=");
    s_comparisonStrings.put(MoonTokenTypes.LE, "<=");

    s_swappedComparisons.put(MoonTokenTypes.EQ, "==");
    s_swappedComparisons.put(MoonTokenTypes.NE, "~=");
    s_swappedComparisons.put(MoonTokenTypes.GT, "<");
    s_swappedComparisons.put(MoonTokenTypes.LT, ">");
    s_swappedComparisons.put(MoonTokenTypes.GE, "<=");
    s_swappedComparisons.put(MoonTokenTypes.LE, ">=");

    s_invertedComparisons.put(MoonTokenTypes.EQ, "~=");
    s_invertedComparisons.put(MoonTokenTypes.NE, "==");
    s_invertedComparisons.put(MoonTokenTypes.GT, "<=");
    s_invertedComparisons.put(MoonTokenTypes.LT, ">=");
    s_invertedComparisons.put(MoonTokenTypes.GE, "<");
    s_invertedComparisons.put(MoonTokenTypes.LE, ">");
  }

  public static boolean isComparison(MoonExpression exp) {
    if (!(exp instanceof MoonBinaryExpression)) {
      return false;
    }
    final MoonBinaryExpression binaryExpression = (MoonBinaryExpression) exp;
    final IElementType sign = binaryExpression.getOperationTokenType();
    return s_comparisonStrings.containsKey(sign);
  }

  public static String getStringForComparison(IElementType str) {
    return s_comparisonStrings.get(str);
  }

  public static String getFlippedComparison(IElementType str) {
    return s_swappedComparisons.get(str);
  }

  public static String getNegatedComparison(IElementType str) {
    return s_invertedComparisons.get(str);
  }
}
