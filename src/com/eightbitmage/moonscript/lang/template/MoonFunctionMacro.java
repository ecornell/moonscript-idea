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

package com.eightbitmage.moonscript.lang.template;

import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonAnonymousFunctionExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.*;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/25/11
 * Time: 1:35 PM
 */
public class MoonFunctionMacro extends Macro {
    @Override
    public String getName() {
        return "currentMoonFunction";
    }

    @Override
    public String getPresentableName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
//    public String getDescription() {
//        return "Substitutes the name of the current MoonScript function";
//    }

    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public Result calculateResult(@NotNull Expression[] expressions, ExpressionContext expressionContext) {
        PsiFile file = PsiDocumentManager.getInstance(expressionContext.getProject()).getPsiFile(
                expressionContext.getEditor().getDocument());

        if (file instanceof MoonPsiFile) {
            PsiElement e = file.findElementAt(expressionContext.getTemplateStartOffset());

            while (e != null) {
                if (e instanceof MoonFunctionDefinition) break;

                e = e.getContext();

            }

            if (e == null) return null;

            if (e instanceof MoonFunctionDefinitionStatement) {
                String name = ((MoonFunctionDefinitionStatement) e).getIdentifier().getName();

                if (name != null) return new TextResult(name);
            }

            if (e instanceof MoonAnonymousFunctionExpression) return new TextResult("anon");
        }

        return null;
    }

    @Override
    public Result calculateQuickResult(@NotNull Expression[] expressions, ExpressionContext expressionContext) {
        return null;
    }

    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression[] expressions,
                                                ExpressionContext expressionContext) {
        return new LookupElement[0];
    }
}
