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

package com.eightbitmage.moonscript.lang.psi.impl.expressions;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonParameterList;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementImpl;
import com.eightbitmage.moonscript.lang.psi.impl.PsiUtil;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes.COMMA;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 14, 2010
 * Time: 6:44:09 PM
 */
public class MoonParameterListImpl extends MoonPsiElementImpl implements MoonParameterList {
    public MoonParameterListImpl(@NotNull ASTNode node) {
        super(node);
    }

    public int count() {
        return findChildrenByClass(MoonParameter.class).length;
    }

    @Override
    public PsiElement getLeftParen() {
        return findChildByType(MoonElementTypes.LPAREN);
    }

    @Override
    public PsiElement getRightParen() {
        return findChildByType(MoonElementTypes.RPAREN);
    }

    public PsiElement getContext() {
        return getParent();
    }    

    public String toString() {
        return "Parameter List (Count " + count() + ")";
    }

    @NotNull
    public MoonParameter[] getLuaParameters() {
        return findChildrenByClass(MoonParameter.class);
    }

    public int getParameterIndex(MoonParameter parameter) {
        MoonParameter[] parameters = getLuaParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals(parameter)) return i;
        }

        return -1;
    }

    public int getParametersCount() {
        return getLuaParameters().length;
    }

    public void addParameterToEnd(MoonParameter parameter) {
        MoonParameter[] params = getLuaParameters();
        final ASTNode astNode = getNode();
        if (params.length == 0) {
            astNode.addChild(parameter.getNode());
        } else {
            MoonParameter last = params[params.length - 1];
            astNode.addChild(parameter.getNode(), last.getNode());
            astNode.addLeaf(MoonElementTypes.COMMA, ",", last.getNode());
        }
    }

    public void addParameterToHead(MoonParameter parameter) {
        MoonParameter[] params = getLuaParameters();
        final ASTNode astNode = getNode();
        final ASTNode paramNode = parameter.getNode();
        assert paramNode != null;
        if (params.length == 0) {
            astNode.addChild(paramNode);
        } else {
            MoonParameter first = params[0];
            astNode.addChild(paramNode, first.getNode());
            astNode.addLeaf(COMMA, ",", first.getNode());
        }
    }

    public int getParameterNumber(final MoonParameter parameter) {
        for (int i = 0; i < getLuaParameters().length; i++) {
            MoonParameter param = getLuaParameters()[i];
            if (param == parameter) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public PsiElement removeParameter(final MoonParameter toRemove) {
        final ASTNode astNode = getNode();
        for (MoonParameter param : getLuaParameters()) {
            if (param == toRemove) {
                final ASTNode paramNode = param.getNode();
                assert paramNode != null;
                PsiElement prevSibling = PsiUtil.getPrevNonSpace(param);
                astNode.removeChild(paramNode);
                if (prevSibling != null) {
                    final ASTNode prev = prevSibling.getNode();
                    if (prev != null && prev.getElementType() == COMMA) {
                        astNode.removeChild(prev);
                    }
                }
                return toRemove;
            }
        }
        return null;
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        MoonParameter[] params = getLuaParameters();
        final ASTNode astNode = getNode();

        if (params.length == 0) {
            astNode.addChild(element.getNode());
        } else {
            astNode.addLeaf(COMMA, ",", (element = super.addAfter(element, anchor)).getNode());
        }

        CodeStyleManager.getInstance(getManager().getProject()).reformat(this);
        return element;
    }

}
