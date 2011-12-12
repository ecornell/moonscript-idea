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

package com.eightbitmage.moonscript.lang.psi.statements;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.util.MoonAssignment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MoonAssignmentStatement extends MoonMaybeDeclarationAssignmentStatement, MoonStatementElement, MoonDeclarationStatement {
    public MoonIdentifierList getLeftExprs();
    public MoonExpressionList getRightExprs();

    @NotNull
    public MoonAssignment[] getAssignments();

    @Nullable
    public MoonExpression getAssignedValue(MoonSymbol symbol);
    
    public IElementType getOperationTokenType();
    public PsiElement getOperatorElement();
}
