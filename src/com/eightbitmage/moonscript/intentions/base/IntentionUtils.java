/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eightbitmage.moonscript.intentions.base;

import com.eightbitmage.moonscript.lang.psi.MoonPsiElementFactory;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


/**
 * User: Dmitry.Krasilschikov
 * Date: 13.11.2007
 */
public class IntentionUtils {

    public static MoonExpression replaceExpression(@NotNull String newExpression,
                                         @NotNull MoonExpression expression) throws IncorrectOperationException {
        final MoonPsiElementFactory factory = MoonPsiElementFactory.getInstance(expression.getProject());
        final MoonExpression newCall = factory.createExpressionFromText(newExpression);
        return (MoonExpression) expression.replaceWithExpression(newCall, true);
    }

    public static MoonStatementElement replaceStatement(@NonNls @NotNull String newStatement,
                                                       @NonNls @NotNull MoonStatementElement statement) throws
            IncorrectOperationException {
        final MoonPsiElementFactory factory = MoonPsiElementFactory.getInstance(statement.getProject());
        final MoonStatementElement newCall = (MoonStatementElement) factory.createStatementFromText(newStatement);
        return statement.replaceWithStatement(newCall);
    }

//  public static void createTemplateForMethod(PsiType[] argTypes,
//                                             ChooseTypeExpression[] paramTypesExpressions,
//                                             MoonFunctionDefinitionStatement method,
//                                             GrMemberOwner owner,
//                                             TypeConstraint[] constraints, boolean isConstructor) {
//
//    Project project = owner.getProject();
//    GrTypeElement typeElement = method.getReturnTypeElementLua();
//    ChooseTypeExpression expr = new ChooseTypeExpression(constraints, PsiManager.getInstance(project));
//    TemplateBuilderImpl builder = new TemplateBuilderImpl(method);
//    if (!isConstructor) {
//      assert typeElement != null;
//      builder.replaceElement(typeElement, expr);
//    }
//    GrParameter[] parameters = method.getParameterList().getLuaParameters();
//    assert parameters.length == argTypes.length;
//    for (int i = 0; i < parameters.length; i++) {
//      GrParameter parameter = parameters[i];
//      GrTypeElement parameterTypeElement = parameter.getTypeElementLua();
//      builder.replaceElement(parameterTypeElement, paramTypesExpressions[i]);
//      builder.replaceElement(parameter.getNameIdentifierLua(), new ParameterNameExpression());
//    }
//    GrOpenBlock body = method.getBlock();
//    assert body != null;
//    PsiElement lbrace = body.getLBrace();
//    assert lbrace != null;
//    builder.setEndVariableAfter(lbrace);
//
//    method = CodeInsightUtilBase.forcePsiPostprocessAndRestoreElement(method);
//    Template template = builder.buildTemplate();
//
//    Editor newEditor = QuickfixUtil.positionCursor(project, owner.getContainingFile(), method);
//    TextRange range = method.getTextRange();
//    newEditor.getDocument().deleteString(range.getStartOffset(), range.getEndOffset());
//
//    TemplateManager manager = TemplateManager.getInstance(project);
//    manager.startTemplate(newEditor, template);
//  }
}
