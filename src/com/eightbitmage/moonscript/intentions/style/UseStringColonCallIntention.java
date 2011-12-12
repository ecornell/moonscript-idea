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

package com.eightbitmage.moonscript.intentions.style;

import com.eightbitmage.moonscript.intentions.MoonIntentionsBundle;
import com.eightbitmage.moonscript.intentions.base.MutablyNamedIntention;
import com.eightbitmage.moonscript.intentions.base.PsiElementPredicate;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFunctionCallExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.impl.expressions.MoonStringLiteralExpressionImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/21/11
 * Time: 2:08 PM
 */
public class UseStringColonCallIntention extends MutablyNamedIntention {
    @Override
    protected String getTextForElement(PsiElement element) {
        return MoonIntentionsBundle.message("use.string.colon.call.intention.name");
    }

    @Override
    protected void processIntention(@NotNull PsiElement element) throws IncorrectOperationException {
        final MoonFunctionCallExpression call = (MoonFunctionCallExpression) element;

        final MoonReferenceElement stringfunc = call.getFunctionNameElement();

        final MoonExpressionList parameters = call.getArgumentList();

        if (parameters == null) return;

        final List<MoonExpression> moonExpressions = parameters.getMoonExpressions();

        if (moonExpressions.size() == 0) return;

        MoonExpression stringElem = moonExpressions.get(0);

        StringBuilder newCall = new StringBuilder();

        if (stringElem instanceof MoonStringLiteralExpressionImpl)
            newCall.append('(' ).append(stringElem.getText()).append(')' );
        else newCall.append(stringElem.getText());

        assert stringfunc.getName() != null;
                
        newCall.append(':').append(stringfunc.getName().substring(7)).append('(');

        for (int i = 1, len = moonExpressions.size(); i < len; i++) {
            if (i>1) newCall.append(',');
            
            newCall.append(moonExpressions.get(i).getText());
        }

        newCall.append(')');

        Document doc = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());

//        PsiElement newElement = IntentionUtils.replaceExpression(newCall.toString(), call);

        CharSequence newE = newCall.subSequence(0, newCall.length());

        assert doc != null;
        doc.replaceString(element.getTextOffset(), element.getTextOffset() + element.getTextLength(), newE);
    }

    @NotNull
    @Override
    protected PsiElementPredicate getElementPredicate() {
        return new StringLibraryCallPredicate();
    }
}
