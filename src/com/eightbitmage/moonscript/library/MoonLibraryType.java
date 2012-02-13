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

package com.eightbitmage.moonscript.library;

import com.eightbitmage.moonscript.module.MoonModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryType;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.ui.configuration.FacetsProvider;
import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/21/11
 * Time: 8:54 PM
 */
public class MoonLibraryType extends LibraryType<MoonLibraryProperties> {

    public static final String MOON_LIBRARY_TYPE_ID = "Moon";

    public MoonLibraryType() {
        super(LibraryKind.<MoonLibraryProperties>create(MOON_LIBRARY_TYPE_ID));
    }

    @NotNull
    @Override
    public String getCreateActionName() {
        return "New MoonScript Library";
    }

    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent jComponent, @Nullable VirtualFile virtualFile, @NotNull Project project) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public MoonLibraryProperties createDefaultProperties() {
        return new MoonLibraryProperties();
    }

    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<MoonLibraryProperties>
                                                                      properties) {
        return null;
    }

    @Override
    public Icon getIcon() {
        return MoonIcons.MOON_ICON;
    }

    @Override
    public boolean isSuitableModule(@NotNull Module module, @NotNull FacetsProvider facetsProvider) {
        if (module instanceof MoonModuleType) return true;


        return false;
    }


}
