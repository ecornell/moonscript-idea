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

import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;

import org.jetbrains.annotations.NotNull;


public class MoonOverlyLongMethodInspection extends MoonMethodMetricInspection {

  @NotNull
  public String getDisplayName() {
    return "Overly long method";
  }

  @NotNull
  public String getGroupDisplayName() {
    return METHOD_METRICS;
  }

  protected int getDefaultLimit() {
    return 30;
  }

  protected String getConfigurationLabel() {
    return "Maximum statements per method:";
  }

  public String buildErrorString(Object... args) {
    return "Method '#ref' is too long ( statement count =" + args[0] + '>' + args[1] + ')';
  }

  public MoonElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new MoonElementVisitor() {
         public void visitFunctionDef(MoonFunctionDefinitionStatement func) {
              super.visitFunctionDef(func);

              final int limit = getLimit();
              final StatementCountVisitor visitor = new StatementCountVisitor();
              final MoonBlock block = func.getBlock();
              if (block == null) return;
              block.acceptChildren(visitor);
              final int statementCount = visitor.getStatementCount();
              if (statementCount <= limit) {
                return;
              }
              holder.registerProblem(func.getIdentifier(), buildErrorString(statementCount, limit), LocalQuickFix.EMPTY_ARRAY);
         }
    };
  }



//  public BaseInspectionVisitor buildVisitor() {
//    return new Visitor();
//  }
//
//  private class Visitor extends BaseInspectionVisitor {
//    public void visitMethod(GrMethod method) {
//      super.visitMethod(method);
//      final int limit = getLimit();
//      final StatementCountVisitor visitor = new StatementCountVisitor();
//      final GrOpenBlock block = method.getBlock();
//      if (block == null) return;
//      block.accept(visitor);
//      final int statementCount = visitor.getStatementCount();
//      if (statementCount <= limit) {
//        return;
//      }
//      registerMethodError(method, statementCount, limit);
//    }
//  }
}