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

package com.eightbitmage.moonscript.lang.psi.impl.statements;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.util.MoonAssignment;
import com.eightbitmage.moonscript.lang.psi.util.MoonAssignmentUtil;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class MoonAssignmentStatementImpl extends MoonStatementElementImpl implements MoonAssignmentStatement {
    public MoonAssignmentStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitAssignment(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitAssignment(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public MoonIdentifierList getLeftExprs() {
        return (MoonIdentifierList) findChildByType(MoonElementTypes.IDENTIFIER_LIST);
    }

    @Override
    public MoonExpressionList getRightExprs() {
        return (MoonExpressionList) findChildByType(MoonElementTypes.EXPR_LIST);
    }

    @NotNull
    @Override
    public MoonAssignment[] getAssignments() {
        return MoonAssignmentUtil.getAssignments(this);
    }

    @Override
    public MoonExpression getAssignedValue(MoonSymbol symbol) {
        return MoonAssignment.FindAssignmentForSymbol(getAssignments(), symbol);
    }

    @NotNull
    @Override
    public IElementType getOperationTokenType() {
        return MoonElementTypes.ASSIGN;
    }


    @Override
    public PsiElement getOperatorElement() {
        return findChildByType(getOperationTokenType());
    }

    @Override
    public MoonSymbol[] getDefinedAndAssignedSymbols() {
        MoonAssignment[] assignments = getAssignments();

        if (assignments.length == 0) return MoonSymbol.EMPTY_ARRAY;

        List<MoonSymbol> syms = new ArrayList<MoonSymbol>();
        for (int i = 0, assignmentsLength = assignments.length; i < assignmentsLength; i++) {
            MoonAssignment assign = assignments[i];

            MoonSymbol id = assign.getSymbol();
            if (id instanceof MoonDeclarationExpression || (id instanceof MoonReferenceElement &&
                                                           ((MoonReferenceElement) id)
                                                                   .getElement() instanceof MoonDeclarationExpression))
                syms.add(assign.getSymbol());
        }
        if (syms.size() == 0) return MoonSymbol.EMPTY_ARRAY;
        
        return syms.toArray(new MoonSymbol[syms.size()]);
    }

    @Override
    public MoonExpression[] getDefinedSymbolValues() {
        return MoonAssignmentUtil.getDefinedSymbolValues(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state,
                                       PsiElement lastParent, @NotNull PsiElement place) {
        MoonSymbol[] defs = getDefinedAndAssignedSymbols();
        for (MoonSymbol def : defs) {
            if (def instanceof MoonReferenceElement)
                def = (MoonSymbol) ((MoonReferenceElement) def).getElement();
            
            if (def instanceof MoonDeclarationExpression)
                if (!processor.execute(def, state)) return false;
        }

        return true; //MoonPsiUtils.processChildDeclarations(, processor, state, lastParent, place);
    }

    @Override
    public MoonSymbol[] getDefinedSymbols() {
        List<MoonSymbol> names = new ArrayList<MoonSymbol>();

        MoonIdentifierList leftExprs = getLeftExprs();
        if (leftExprs == null)
            return MoonSymbol.EMPTY_ARRAY;

        MoonSymbol[] lhs = leftExprs.getSymbols();
        for (MoonSymbol symbol : lhs) {
            if (symbol instanceof MoonDeclarationExpression)
                names.add(symbol);
        }

        return names.toArray(new MoonSymbol[names.size()]);
    }
}
