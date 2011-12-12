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

import com.eightbitmage.moonscript.lang.psi.expressions.LuaExpression;
import com.eightbitmage.moonscript.lang.psi.impl.LuaPsiElementImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.LuaSymbol;
import com.eightbitmage.moonscript.lang.psi.types.LuaType;
import com.eightbitmage.moonscript.lang.psi.util.LuaPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.eightbitmage.moonscript.LuaIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:52 PM
 */
public abstract class LuaSymbolImpl extends LuaPsiElementImpl implements LuaSymbol {
    public LuaSymbolImpl(ASTNode node) {
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
    public LuaType getLuaType() {
        return LuaType.ANY;
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
                return LuaIcons.LUA_ICON;
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
    public PsiElement replaceWithExpression(LuaExpression newExpr, boolean removeUnnecessaryParentheses) {
        return LuaPsiUtils.replaceElement(this, newExpr);
    }


}
