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

import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.psi.MoonNamedElement;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonLocalDeclaration;
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
import com.intellij.psi.util.PsiTreeUtil;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonLocalDefinitionStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 6, 2010
 * Time: 10:00:19 AM
 */
public class MoonLocalDefinitionStatementImpl extends MoonStatementElementImpl implements MoonLocalDefinitionStatement,
        MoonStatementElement, MoonAssignmentStatement {
    public MoonLocalDefinitionStatementImpl(ASTNode node) {
        super(node);

        MoonExpressionList exprs = getRightExprs();

        if (exprs != null) {
            List<MoonExpression> vals = exprs.getMoonExpressions();
            MoonLocalDeclaration[] defs = getDefinedAndAssignedSymbols();
            for (int i = 0; i < defs.length && i < vals.size(); i++) {
                MoonLocalDeclaration def = defs[i];
                MoonExpression expr = vals.get(i);

                if (expr instanceof MoonNamedElement) def.setAliasElement(expr);
            }
        }
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitDeclarationStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitDeclarationStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public MoonDeclarationExpression[] getDeclarations() {
        List<MoonDeclarationExpression> decls = new ArrayList<MoonDeclarationExpression>();
        MoonIdentifierList list = findChildByClass(MoonIdentifierList.class);

        assert list != null;
        for(MoonSymbol sym : list.getSymbols()) {
            if (sym instanceof MoonDeclarationExpression)
                decls.add((MoonDeclarationExpression) sym);

            if (sym instanceof MoonReferenceElement) {
                PsiElement e = ((MoonReferenceElement) sym).getElement();

                if (e instanceof MoonDeclarationExpression)
                    decls.add((MoonDeclarationExpression) e);
            }
        }

        return decls.toArray(new MoonDeclarationExpression[decls.size()]);
    }

    @Override
    public MoonExpression[] getExprs() {
        MoonExpressionList list = findChildByClass(MoonExpressionList.class);
        if (list == null) return new MoonExpression[0];

        return list.getMoonExpressions().toArray(new MoonExpression[list.count()]);
    }



    // locals are undefined within the statement, so  local a,b = b,a
    // should not resolve a to a or b to b. So to handle this we process
    // our declarations unless we are walking from a child of ourself.
    // in our case its, (localstat) <- (expr list) <- (expression) <- (variable) <- (reference )

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState,
                                       PsiElement lastParent, @NotNull PsiElement place) {
        // If we weren't found as a parent of the reference
        if (!PsiTreeUtil.isAncestor(this, place, true)) {
            final MoonDeclarationExpression[] decls = getDeclarations();
            for (int i = decls.length - 1; i >= 0; i--) {
                MoonDeclarationExpression decl = decls[i];
                if (!processor.execute(decl, resolveState)) return false;
            }
        }

        return true;
    }

    @Override
    public MoonIdentifierList getLeftExprs() {
        return findChildByClass(MoonIdentifierList.class);
    }

    @Override
    public MoonExpressionList getRightExprs() {
        return findChildByClass(MoonExpressionList.class);
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


    @Override
    public IElementType getOperationTokenType() {
        return MoonTokenTypes.ASSIGN;
    }

    @Override
    public PsiElement getOperatorElement() {
        return findChildByType(getOperationTokenType());
    }

    @Override
    public MoonSymbol[] getDefinedSymbols() {
        return getDeclarations();
    }

    @Override
    public MoonLocalDeclaration[] getDefinedAndAssignedSymbols() {
        MoonAssignment[] assignments = getAssignments();

        if (assignments.length == 0) return MoonLocalDeclaration.EMPTY_ARRAY;

        MoonLocalDeclaration[] syms = new MoonLocalDeclaration[assignments.length];
        for (int i = 0, assignmentsLength = assignments.length; i < assignmentsLength; i++) {
            MoonAssignment assign = assignments[i];
            syms[i] = (MoonLocalDeclaration) assign.getSymbol();
        }
        return syms;
    }

    @Override
    public MoonExpression[] getDefinedSymbolValues() {
        MoonExpressionList exprs = getRightExprs();

        if (exprs == null) return MoonExpression.EMPTY_ARRAY;

        List<MoonExpression> vals = exprs.getMoonExpressions();

        return vals.toArray(new MoonExpression[vals.size()]);
    }
}
