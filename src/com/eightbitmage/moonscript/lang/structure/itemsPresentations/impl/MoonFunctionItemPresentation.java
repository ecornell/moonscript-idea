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
package com.eightbitmage.moonscript.lang.structure.itemsPresentations.impl;

import com.eightbitmage.moonscript.MoonIcons;
import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.structure.MoonElementPresentation;
import com.eightbitmage.moonscript.lang.structure.itemsPresentations.MoonItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class MoonFunctionItemPresentation extends MoonItemPresentation {

    private final NotNullLazyValue<String> myPresentableText = new NotNullLazyValue<String>() {

        @NotNull
        @Override
        protected String compute() {
            return MoonElementPresentation.
                    getFunctionPresentableText(((MoonFunctionDefinition) myElement));
        }
    };

    private final NotNullLazyValue<String> myLocationText = new NotNullLazyValue<String>() {
        @NotNull
        @Override
        protected String compute() {
            return MoonElementPresentation.
                    getFunctionLocationText(((MoonFunctionDefinition) myElement));
        }
    };
    TextAttributesKey textKey =
            TextAttributesKey.createTextAttributesKey(MoonFunctionItemPresentation.class.toString());

    public MoonFunctionItemPresentation(MoonFunctionDefinition myElement) {
        super(myElement);

    }

    public String getPresentableText() {
        return myPresentableText.getValue();
    }

    @Nullable
    public String getLocationString() {
        return myLocationText.getValue();
    }

    @Nullable
    public Icon getIcon(boolean open) {
        return MoonIcons.MOON_FUNCTION;
    }

    @Nullable
    public TextAttributesKey getTextAttributesKey() {
        return textKey;
    }
}
