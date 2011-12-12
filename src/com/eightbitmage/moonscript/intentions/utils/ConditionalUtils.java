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

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonReturnStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import org.jetbrains.annotations.NonNls;


public class ConditionalUtils {

  private ConditionalUtils() {
    super();
  }

  public static boolean isReturn(MoonStatementElement statement, @NonNls String value) {
    if (statement == null) {
      return false;
    }
    if (!(statement instanceof MoonReturnStatement)) {
      return false;
    }
    final MoonReturnStatement returnStatement =
        (MoonReturnStatement) statement;
    final MoonExpression returnValue = (MoonExpression) returnStatement.getReturnValue();
    if (returnValue == null) {
      return false;
    }
    final String returnValueText = returnValue.getText();
    return value.equals(returnValueText);
  }

  public static boolean isAssignment(MoonStatementElement statement, @NonNls String value) {
    if (statement == null) {
      return false;
    }
    if (!(statement instanceof MoonExpression)) {
      return false;
    }
    final MoonExpression expression = (MoonExpression) statement;
    if (!(expression instanceof MoonAssignmentStatement)) {
      return false;
    }
    final MoonAssignmentStatement assignment =
        (MoonAssignmentStatement) expression;
    final MoonExpression rhs = assignment.getRightExprs();
    if (rhs == null) {
      return false;
    }
    final String rhsText = rhs.getText();
    return value.equals(rhsText);
  }

  public static boolean isAssignment(MoonStatementElement statement) {
    return statement instanceof MoonAssignmentStatement;
  }
}
