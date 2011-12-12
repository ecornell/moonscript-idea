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
package com.eightbitmage.moonscript.editor.annotator;

import com.eightbitmage.moonscript.editor.highlighter.MoonHighlightingData;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocReferenceElement;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonCompoundReferenceElementImpl;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonGlobalDeclarationImpl;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonGlobalUsageImpl;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonLocalDeclarationImpl;
import com.eightbitmage.moonscript.lang.psi.statements.MoonDeclarationStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonLocalDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonReturnStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.eightbitmage.moonscript.options.MoonApplicationSettings;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.symbols.*;
import org.jetbrains.annotations.NotNull;


/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 8, 2010
 * Time: 5:45:21 PM
 */
public class MoonAnnotator extends MoonElementVisitor implements Annotator {
    private AnnotationHolder myHolder = null;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof MoonPsiElement) {
            myHolder = holder;
            ((MoonPsiElement) element).accept(this);
            myHolder = null;
        }
    }

    public void visitReturnStatement(MoonReturnStatement stat) {
        if (stat.isTailCall()) {
            final Annotation a = myHolder.createInfoAnnotation(stat, null);
            a.setTextAttributes(MoonHighlightingData.TAIL_CALL);
        }
    }

    @Override
    public void visitCompoundReference(MoonCompoundReferenceElementImpl ref) {
        super.visitCompoundReference(ref);
    }


    @Override
    public void visitDocReference(MoonDocReferenceElement ref) {
        super.visitDocReference(ref);

        PsiElement e = ref.resolve();

        hilightReference(ref, e);
    }

    public void visitReferenceElement(MoonReferenceElement ref) {
        PsiElement e;

        if (ref.getFirstChild() instanceof MoonDeclarationExpression)
            return;

        if (MoonApplicationSettings.getInstance().RESOLVE_ALIASED_IDENTIFIERS &&
            ref.getElement() instanceof MoonLocalIdentifier)
            e = ref.resolveWithoutCaching(true);
        else
            e = ref.resolve();


        hilightReference(ref, e);
    }

    private void hilightReference(PsiReference ref, PsiElement e) {
        if (e instanceof MoonParameter) {
            final Annotation a = myHolder.createInfoAnnotation((PsiElement)ref, null);
            a.setTextAttributes(MoonHighlightingData.PARAMETER);
        } else if (e instanceof MoonIdentifier) {
            MoonIdentifier id = (MoonIdentifier) e;
            TextAttributesKey attributesKey = null;

            if (id instanceof MoonGlobal) {
                attributesKey = MoonHighlightingData.GLOBAL_VAR;
            } else if (id instanceof MoonLocal && !id.getText().equals("...")) {
                attributesKey = MoonHighlightingData.LOCAL_VAR;
            } else if (id instanceof MoonFieldIdentifier) {
                attributesKey = MoonHighlightingData.FIELD;
            }

            if (attributesKey != null) {
                final Annotation annotation = myHolder.createInfoAnnotation((PsiElement)ref, null);
                annotation.setTextAttributes(attributesKey);
            }
        }
    }

    @Override
    public void visitDeclarationStatement(MoonDeclarationStatement e) {
        super.visitDeclarationStatement(e);

        if (e instanceof MoonLocalDefinitionStatement) {
            MoonIdentifierList left = ((MoonLocalDefinitionStatement) e).getLeftExprs();
            MoonExpressionList right = ((MoonLocalDefinitionStatement) e).getRightExprs();

            if (right == null || right.count() == 0)
                return;

            boolean allNil = true;
            for (MoonExpression expr : right.getMoonExpressions())
                if (!expr.getText().equals("nil")) {
                    allNil = false;
                    break;
                }

            if (allNil) {
                int assignment = ((MoonLocalDefinitionStatement) e).getOperatorElement().getTextOffset();
                final Annotation annotation = myHolder.createInfoAnnotation(new TextRange(assignment,
                                                                                          right.getTextRange()
                                                                                               .getEndOffset()
                ), null
                                                                           );
                annotation.setTextAttributes(SyntaxHighlighterColors.LINE_COMMENT);
            }
        }
    }

    public void visitDeclarationExpression(MoonDeclarationExpression dec) {
        if (!(dec.getContext() instanceof MoonParameter)) {
            final Annotation a = myHolder.createInfoAnnotation(dec, null);

            if (dec instanceof MoonLocalDeclarationImpl)
                a.setTextAttributes(MoonHighlightingData.LOCAL_VAR);
            else if (dec instanceof MoonGlobalDeclarationImpl)
                a.setTextAttributes(MoonHighlightingData.GLOBAL_VAR);
        }
    }

    public void visitParameter(MoonParameter id) {
        if (id.getTextLength()==0)
            return;
        
        final Annotation a = myHolder.createInfoAnnotation(id, null);
        a.setTextAttributes(MoonHighlightingData.PARAMETER);
    }

    public void visitIdentifier(MoonIdentifier id) {
        if ((id != null) && id instanceof MoonGlobalUsageImpl) {
            final Annotation annotation = myHolder.createInfoAnnotation(id, null);
            annotation.setTextAttributes(MoonHighlightingData.GLOBAL_VAR);
            return;
        }
        if (id instanceof MoonFieldIdentifier) {
            final Annotation annotation = myHolder.createInfoAnnotation(id, null);
            annotation.setTextAttributes(MoonHighlightingData.FIELD);
            return;
        }
        if (id instanceof MoonUpvalueIdentifier) {
            final Annotation annotation = myHolder.createInfoAnnotation(id, null);
            annotation.setTextAttributes(MoonHighlightingData.UPVAL);
        }
        //        if (id instanceof MoonLocalIdentifier) {
        //            final Annotation annotation = myHolder.createInfoAnnotation(id, null);
        //            annotation.setTextAttributes(MoonHighlightingData.LOCAL_VAR);
        //        }

    }
}
