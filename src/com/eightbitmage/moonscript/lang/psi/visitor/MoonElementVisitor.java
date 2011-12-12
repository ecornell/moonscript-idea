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

package com.eightbitmage.moonscript.lang.psi.visitor;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.*;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiKeywordImpl;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiTokenImpl;
import com.eightbitmage.moonscript.lang.psi.impl.statements.MoonRepeatStatementImpl;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonCompoundReferenceElementImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElementVisitor;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocFieldReference;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocPsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 7:39:03 AM
 */

public class MoonElementVisitor extends PsiElementVisitor {
    public void visitElement(MoonPsiElement element) {
        ProgressManager.checkCanceled();
    }
//
//    public void visitFile(MoonPsiFile e) {
//        visitElement(e);
//    }

    public void visitFunctionDef(MoonFunctionDefinitionStatement e) {
        visitStatement(e);
    }

    public void visitAssignment(MoonAssignmentStatement e) {
        visitStatement(e);
    }

    public void visitIdentifier(MoonIdentifier e) {
        visitElement(e);
    }

    public void visitStatement(MoonStatementElement e) {
        visitElement(e);
    }

    public void visitNumericForStatement(MoonNumericForStatement e) {
        visitStatement(e);
    }

    public void visitBlock(MoonBlock e) {
        visitElement(e);
    }

    public void visitGenericForStatement(MoonGenericForStatement e) {
        visitStatement(e);
    }

    public void visitIfThenStatement(@NotNull MoonIfThenStatement e) {
        visitStatement(e);
    }

    public void visitWhileStatement(MoonWhileStatement e) {
        visitStatement(e);
    }

    public void visitParameter(MoonParameter e) {
        visitElement(e);
    }

    public void visitReturnStatement(MoonReturnStatement e) {
        visitStatement(e);
    }

    public void visitReferenceElement(MoonReferenceElement e) {
        visitElement(e);
    }

    public void visitKeyword(MoonPsiKeywordImpl e) {
        visitElement(e);
    }

    public void visitLuaToken(MoonPsiTokenImpl e) {
        visitElement(e);
    }

    public void visitDeclarationStatement(MoonDeclarationStatement e) {
        visitStatement(e);
    }

    public void visitDeclarationExpression(MoonDeclarationExpression e) {
        visitElement(e);
    }

    public void visitLiteralExpression(MoonLiteralExpression e) {
        visitElement(e);
    }

    public void visitTableConstructor(MoonTableConstructor e) {
        visitElement(e);
    }

    public void visitUnaryExpression(MoonUnaryExpression e) {
        visitElement(e);
    }

    public void visitBinaryExpression(MoonBinaryExpression e) {
        visitElement(e);
    }

    public void visitFunctionCall(MoonFunctionCallExpression e) {
        visitElement(e);
    }

    public void visitBreakStatement(MoonBreakStatement e) {
        visitStatement(e);
    }

    public void visitRepeatStatement(MoonRepeatStatementImpl e) {
        visitStatement(e);
    }

    public void visitFunctionCallStatement(MoonFunctionCallStatement e) {
        visitStatement(e);
    }

    public void visitCompoundIdentifier(MoonCompoundIdentifier e) {
        visitElement(e);
    }

    public void visitCompoundReference(MoonCompoundReferenceElementImpl e) {
        visitElement(e);
    }

    public void visitModuleStatement(MoonModuleStatement e) {
        visitFunctionCallStatement(e);
    }

    public void visitRequireExpression(MoonRequireExpression e) {
        visitElement(e);
    }

    public void visitRequireStatement(MoonRequireStatement e) {
        visitElement(e);
    }

    public void visitDocTag(MoonDocTag e) {
        visitDocComment(e);
    }

    public void visitDocFieldReference(MoonDocFieldReference e) {
        visitDocComment(e);
    }

    public void visitDoStatement(MoonDoStatement e) {
        visitElement(e);
    }

    public void visitDocComment(MoonDocPsiElement e) {
        visitElement(e);
    }

    public void visitAnonymousFunction(MoonAnonymousFunctionExpression e) {
        visitElement(e);
    }

    public void visitDocReference(MoonDocReferenceElement e) {
        visitDocComment(e);
    }
}




