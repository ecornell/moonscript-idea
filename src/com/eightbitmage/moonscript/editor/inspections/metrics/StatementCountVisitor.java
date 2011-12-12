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

import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import org.jetbrains.annotations.NotNull;


class StatementCountVisitor extends MoonElementVisitor {
  private int statementCount = 0;

//  public void visitElement(MoonPsiElement element) {
//    int oldCount = 0;
//    if (element instanceof MoonFunctionDefinitionStatement) {
//      oldCount = statementCount;
//    }
//    super.visitElement(element);
//
//    if (element instanceof MoonFunctionDefinitionStatement) {
//      statementCount = oldCount;
//    }
//  }


    public void visitFunctionDef(MoonFunctionDefinitionStatement e) {
        super.visitFunctionDef(e);

        statementCount = 0;
    }


  public void visitStatement(@NotNull MoonStatementElement statement) {
    statementCount++;
    int oldCount = 0;

    if (statement instanceof MoonFunctionDefinitionStatement) {
      oldCount = statementCount;
    }

    super.visitStatement(statement);

    if (statement instanceof MoonFunctionDefinitionStatement) {
      statementCount = oldCount;
    }
  }


  public int getStatementCount() {
    return statementCount;
  }
}