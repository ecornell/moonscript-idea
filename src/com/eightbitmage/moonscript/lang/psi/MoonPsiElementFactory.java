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

package com.eightbitmage.moonscript.lang.psi;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocParameterReference;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocReferenceElement;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 14, 2010
 * Time: 7:12:06 PM
 */
public abstract class MoonPsiElementFactory {
    public static MoonPsiElementFactory getInstance(Project project) {
        return ServiceManager.getService(project, MoonPsiElementFactory.class);
    }
    public abstract MoonSymbol createReferenceNameFromText(String newElementName);

    public abstract MoonIdentifier createLocalNameIdentifier(String name);

    public abstract MoonIdentifier createGlobalNameIdentifier(String name);

    public abstract MoonIdentifier createFieldNameIdentifier(String name);

    public abstract MoonExpression createExpressionFromText(String newExpression);

    public abstract MoonStatementElement createStatementFromText(String newStatement) ;

    public abstract PsiComment createCommentFromText(String s, PsiElement parent);

    public abstract PsiElement createWhiteSpaceFromText(String text);

    public abstract MoonDeclarationExpression createLocalNameIdentifierDecl(String s);

    public abstract MoonDeclarationExpression createGlobalNameIdentifierDecl(String name);

    public abstract MoonDeclarationExpression createParameterNameIdentifier(String name);

    public abstract MoonExpressionCodeFragment createExpressionCodeFragment(String text, MoonPsiElement context, boolean b);

    public abstract MoonDocComment createDocCommentFromText(String s);

    public abstract MoonDocReferenceElement createDocFieldReferenceNameFromText(String elementName);

    public abstract MoonDocParameterReference createParameterDocMemberReferenceNameFromText(String elementName);
}
