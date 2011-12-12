/*
 * Copyright 2007-2008 Dave Griffith
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
package com.eightbitmage.moonscript.editor.inspections.utils;

import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonConditionalExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonUnaryExpression;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class BoolUtils {

  public static boolean isNegation(@NotNull MoonExpression exp) {
    if (!(exp instanceof MoonUnaryExpression)) {
      return false;
    }
    final MoonUnaryExpression prefixExp = (MoonUnaryExpression) exp;
    final IElementType sign = prefixExp.getOperationTokenType();
    return MoonTokenTypes.NOT.equals(sign);
  }

  public static boolean isTrue(MoonConditionalExpression condition) {
    if (condition == null) {
      return false;
    }
    return "true".equals(condition.getText());
  }

  public static boolean isFalse(MoonConditionalExpression condition) {
    if (condition == null) {
      return false;
    }
    return "false".equals(condition.getText());
  }

}
