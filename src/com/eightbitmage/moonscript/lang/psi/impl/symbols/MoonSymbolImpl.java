/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.MoonIcons;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:52 PM
 */
public abstract class MoonSymbolImpl extends MoonPsiElementImpl implements MoonSymbol {
    public MoonSymbolImpl(ASTNode node) {
        super(node);
    }

    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.ANY;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return getPresentationText();
            }

            @Nullable
            public String getLocationString() {
                String name = getContainingFile().getName();
                return "(in " + name + ")";
            }

            @Nullable
            public Icon getIcon(boolean open) {
                return MoonIcons.MOON_ICON;
            }

            @Nullable
            public TextAttributesKey getTextAttributesKey() {
                return null;
            }
        };
    }

    private String getPresentationText() {
        return getName();
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newExpr, boolean removeUnnecessaryParentheses) {
        return MoonPsiUtils.replaceElement(this, newExpr);
    }


}
