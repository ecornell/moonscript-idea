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

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/21/11
 * Time: 8:31 PM
 */
public class MoonLibraryPresentationProvider extends LibraryPresentationProvider<MoonLibraryProperties> {

    protected MoonLibraryPresentationProvider() {
        super(new LibraryKind<MoonLibraryProperties>(MoonLibraryType.MOON_LIBRARY_TYPE_ID));
    }

    @Override
    public Icon getIcon() {
        return MoonIcons.MOON_ICON;
    }

    @Override
    public MoonLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        for (VirtualFile vf : classesRoots) {
            if (!vf.isDirectory())
                return null;

            for(VirtualFile file : vf.getChildren()) {
                String fileExtension = file.getExtension();
                if (fileExtension != null)
                    if (fileExtension.equals(MoonFileType.DEFAULT_EXTENSION))
                        return new MoonLibraryProperties();
            }
        }

        return null;
    }
}
