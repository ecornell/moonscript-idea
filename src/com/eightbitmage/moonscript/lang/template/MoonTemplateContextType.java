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

import com.eightbitmage.moonscript.editor.highlighter.MoonSyntaxHighlighter;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiFile;
import com.eightbitmage.moonscript.MoonFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/22/11
 * Time: 10:45 PM
 */
public class MoonTemplateContextType extends TemplateContextType {
    protected MoonTemplateContextType() {
        super("LUA", "Lua");
    }

    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return file instanceof MoonPsiFile;
    }

    public boolean isInContext(@NotNull FileType fileType) {
        return fileType instanceof MoonFileType;
    }

    @Override
    public SyntaxHighlighter createHighlighter() {
        return new MoonSyntaxHighlighter();
    }
}

