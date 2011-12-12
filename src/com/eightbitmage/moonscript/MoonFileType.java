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

package com.eightbitmage.moonscript;

import com.eightbitmage.moonscript.editor.highlighter.MoonEditorHighlighter;
import com.eightbitmage.moonscript.lang.MoonLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 04.07.2009
 * Time: 1:03:43
 */
public class MoonFileType extends LanguageFileType {
    public static final MoonFileType MOON_FILE_TYPE = new MoonFileType();

    public static final Language MOON_LANGUAGE = MOON_FILE_TYPE.getLanguage();
    // public static final Icon MOON_FILE_TYPE = MoonIcons.MOON_ICON_16x16;
    @NonNls
    public static final String DEFAULT_EXTENSION = "moon";
    public static final String MOON = "MoonScript";

    private MoonFileType() {
        super(new MoonLanguage());
    }

    /**
     * Creates a language file type for the specified language.
     *
     * @param language The language used in the files of the type.
     */
    protected MoonFileType(@NotNull Language language) {
        super(language);
    }

    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
        return new MoonEditorHighlighter(colors, project, virtualFile);
    }

    @NotNull
    public String getName() {
        return MOON;
    }

    @NotNull
    public String getDescription() {
        return "MoonScript source file";
    }

    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    public Icon getIcon() {
        return MoonIcons.MOON_ICON;
    }

}



