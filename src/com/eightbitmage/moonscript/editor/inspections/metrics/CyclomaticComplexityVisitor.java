/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.eightbitmage.moonscript.editor.inspections.metrics;

import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import org.jetbrains.annotations.NotNull;


class CyclomaticComplexityVisitor extends MoonElementVisitor {
  private int complexity = 1;

  public void visitElement(MoonPsiElement grElement) {
    int oldComplexity = 0;
    if (grElement instanceof MoonFunctionDefinitionStatement) {
      oldComplexity = complexity;
    }
    super.visitElement(grElement);

    if (grElement instanceof MoonFunctionDefinitionStatement) {
      complexity = oldComplexity;
    }
  }

  public void visitNumericForStatement(@NotNull MoonNumericForStatement statement) {
    super.visitNumericForStatement(statement);
    complexity++;
  }

  public void visitGenericForStatement(@NotNull MoonGenericForStatement statement) {
    super.visitGenericForStatement(statement);
    complexity++;
  }
    
  public void visitIfThenStatement(@NotNull MoonIfThenStatement statement) {
    super.visitIfThenStatement(statement);
    complexity++;

    complexity += statement.getElseIfConditions().length;
  }

//  public void visitConditionalExpression(MoonConditionalExpression expression) {
//    super.visitConditionalExpression(expression);
//    complexity++;
//  }

  public void visitWhileStatement(@NotNull MoonWhileStatement statement) {
    super.visitWhileStatement(statement);
    complexity++;
  }

  public int getComplexity() {
    return complexity;
  }
}