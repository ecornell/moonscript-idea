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
package com.eightbitmage.moonscript.lang.psi.impl;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.debugger.MoonCodeFragment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTag;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocParameterReference;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocReferenceElement;
import com.eightbitmage.moonscript.lang.psi.*;
import com.eightbitmage.moonscript.lang.psi.statements.*;


/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 14, 2010
 * Time: 7:16:01 PM
 */
public class MoonPsiElementFactoryImpl extends MoonPsiElementFactory {
    Project myProject;

    public MoonPsiElementFactoryImpl(Project project) {
        myProject = project;
    }

    @Override
    public MoonExpression createExpressionFromText(String newExpression) {
        MoonPsiFile file = createDummyFile("return " + newExpression);

        MoonReturnStatement ret = (MoonReturnStatement) file.getStatements()[0];

        MoonExpressionList exprs = (MoonExpressionList) ret.getReturnValue();

        return exprs.getMoonExpressions().get(0);
    }

    @Override
    public MoonStatementElement createStatementFromText(String newStatement) {
        MoonPsiFile file = createDummyFile(newStatement);

        return file.getStatements()[0];
    }

    @Override
    public PsiComment createCommentFromText(String s, PsiElement parent) {
        MoonPsiFile file = createDummyFile(s);

        return (PsiComment) file.getChildren()[0];
    }

    public PsiElement createWhiteSpaceFromText(String text) {
        MoonPsiFile file = createDummyFile(text);

        return file.getChildren()[0];
    }

    private MoonPsiFile createDummyFile(String s, boolean isPhisical) {
        return (MoonPsiFile) PsiFileFactory.getInstance(myProject)
                                          .createFileFromText("DUMMY__." +
            MoonFileType.MOON_FILE_TYPE.getDefaultExtension(),
            MoonFileType.MOON_FILE_TYPE, s, System.currentTimeMillis(), isPhisical);
    }

    private MoonPsiFile createDummyFile(String s) {
        return createDummyFile(s, false);
    }

    public PsiFile createLuaFile(String idText) {
        return createLuaFile(idText, false, null);
    }

    public MoonPsiFile createLuaFile(String idText, boolean isPhisical,
        PsiElement context) {
        MoonPsiFile file = createDummyFile(idText, isPhisical);

        //file.setContext(context);
        return file;
    }

    //    public static ASTNode createLocalNameIdentifier(Project project, String name) {
    //                return null;  //To change body of created methods use File | Settings | File Templates.
    //    }
    @Override
    public MoonSymbol createReferenceNameFromText(String newElementName) {
        MoonPsiFile file = createDummyFile(newElementName + " = nil");

        if (! (file.getFirstChild() instanceof MoonAssignmentStatement) ) return null;

        MoonAssignmentStatement assign = (MoonAssignmentStatement) file.getFirstChild();

        assert assign != null;
        if (assign.getLeftExprs().count()!=1) return null;

        MoonSymbol e = assign.getLeftExprs().getSymbols()[0];

        if (e.getText().equals(newElementName))
            return e;

        return null;
    }




    @Override
    public MoonDeclarationExpression createLocalNameIdentifierDecl(String name) {
        MoonPsiFile file = createDummyFile("local " + name);

        final MoonLocalDefinitionStatement expressionStatement = (MoonLocalDefinitionStatement) file.getFirstChild();
        final MoonDeclarationExpression declaration = expressionStatement.getDeclarations()[0];

        return declaration;
    }

    public MoonIdentifier createLocalNameIdentifier(String name) {
        int firstDot = name.indexOf('.');
        String prefix = name.substring(0, firstDot>0?firstDot:name.length());
        MoonPsiFile file = createDummyFile("local " + prefix + "; " + name +
                " = nil");

        final MoonAssignmentStatement expressionStatement = (MoonAssignmentStatement) file.getStatements()[1];
        final MoonReferenceElement ref = (MoonReferenceElement) expressionStatement.getLeftExprs().getFirstChild();

        return (MoonIdentifier) ref.getElement();
    }


    public MoonDeclarationExpression createGlobalNameIdentifierDecl(String name) {
        MoonPsiFile file = createDummyFile(name + "=true");

        final MoonAssignmentStatement expressionStatement = (MoonAssignmentStatement) file.getFirstChild();
        final MoonDeclarationExpression declaration =
                (MoonDeclarationExpression) expressionStatement.getLeftExprs().getFirstChild().getFirstChild();

        return declaration;
    }

    @Override
    public MoonDeclarationExpression createParameterNameIdentifier(String name) {
        MoonPsiFile file = createDummyFile("function a("+name+") end");

        final MoonFunctionDefinitionStatement functionDef = (MoonFunctionDefinitionStatement) file.getFirstChild();

        assert functionDef != null;

        return functionDef.getParameters().getLuaParameters()[0];
    }

    @Override
    public MoonExpressionCodeFragment createExpressionCodeFragment(String text, MoonPsiElement context, boolean b) {
        //MoonPsiFile file = createDummyFile(text);
        MoonCodeFragment file = new MoonCodeFragment(myProject, text);

        return (MoonExpressionCodeFragment) file;
    }

    @Override
    public MoonDocComment createDocCommentFromText(String s) {
        MoonPsiFile file = createDummyFile(s);

        PsiElement e = file.getFirstChild();

        assert e instanceof MoonDocComment;

        return (MoonDocComment) e;
    }

    @Override
    public MoonDocReferenceElement createDocFieldReferenceNameFromText(String elementName) {
        MoonPsiFile file = createDummyFile("--- @field " + elementName + "\nlocal a={" + elementName + "=true}");

        MoonDocComment comment = (MoonDocComment) file.getFirstChild();

        assert comment != null;
        MoonDocTag tag = comment.getTags()[0];
        
        return tag.getDocFieldReference();
    }

    @Override
    public MoonDocParameterReference createParameterDocMemberReferenceNameFromText(String elementName) {
        MoonPsiFile file = createDummyFile("--- @param " + elementName + "\nfunction(" + elementName + ")");

        MoonDocComment comment = (MoonDocComment) file.getFirstChild();

        assert comment != null;
        MoonDocTag tag = comment.getTags()[0];

        return tag.getDocParameterReference();
    }

    public MoonIdentifier createGlobalNameIdentifier(String name) {
        MoonPsiFile file = createDummyFile(name + "=true; nop=" + name);

        final MoonAssignmentStatement expressionStatement = (MoonAssignmentStatement) file.getStatements()[1];
        final MoonReferenceElement ref = (MoonReferenceElement) expressionStatement.getRightExprs().getFirstChild();

        return (MoonIdentifier) ref.getElement();
    }

    @Override
    public MoonIdentifier createFieldNameIdentifier(String name) {
        MoonPsiFile file = createDummyFile("a."+name+"=nil");

        MoonAssignmentStatement assign = (MoonAssignmentStatement) file.getFirstChild();

        assert assign != null;
        MoonReferenceElement element = assign.getLeftExprs().getReferenceExprs()[0];
        MoonCompoundIdentifier id = (MoonCompoundIdentifier) element.getElement();

        return (MoonIdentifier) id.getRightSymbol();
    }
}
