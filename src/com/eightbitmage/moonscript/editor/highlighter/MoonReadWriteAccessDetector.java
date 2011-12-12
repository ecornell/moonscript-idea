/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.editor.highlighter;

import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.util.MoonAssignment;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/17/11
 * Time: 1:37 AM
 */
public class MoonReadWriteAccessDetector extends ReadWriteAccessDetector {
    @Override
    public boolean isReadWriteAccessible(PsiElement element) {
        return element instanceof MoonSymbol;
    }

    @Override
    public boolean isDeclarationWriteAccess(PsiElement element) {
        if (! (element instanceof MoonSymbol))
            return false;

        if (element instanceof MoonParameter) {
            return true;
        }

        MoonStatementElement stmt = PsiTreeUtil.getParentOfType(element, MoonStatementElement.class);
        if (stmt == null) return false;
        
        if (stmt instanceof MoonGenericForStatement)
            return true;

        if (stmt instanceof MoonNumericForStatement)
            return true;

        if (stmt instanceof MoonFunctionDefinitionStatement)
            return true;

        if (stmt instanceof MoonAssignmentStatement) {
            for(MoonAssignment a : ((MoonAssignmentStatement) stmt).getAssignments())
                if (a.getSymbol() == element) return true;
        }
                
        return false;
    }

  public Access getReferenceAccess(final PsiElement referencedElement, final PsiReference reference) {
      if (reference.getElement().getParent().getParent() instanceof MoonFunctionDefinition)
          return Access.Write;
      
      if (reference.getElement() instanceof MoonCompoundIdentifier) {
          if (((MoonCompoundIdentifier) reference.getElement()).isCompoundDeclaration()) return Access.Write;
      } else {
          if (reference.getElement() instanceof MoonDeclarationExpression) return Access.Write;
      }
      
      return MoonPsiUtils.isLValue((MoonPsiElement) reference) ? Access.Write : Access.Read;
  }

  public Access getExpressionAccess(final PsiElement expression) {


    return MoonPsiUtils.isLValue((MoonPsiElement) expression) ? Access.Write : Access.Read;
  }
}
