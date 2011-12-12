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

package com.eightbitmage.moonscript.util;

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.module.MoonModuleType;
import com.eightbitmage.moonscript.sdk.MoonSdkType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim.Manuylov
 *         Date: 29.04.2010
 */
public class MoonModuleUtil {
    public static boolean isLuaSdk(@Nullable final Sdk sdk) {
        return sdk != null && sdk.getSdkType() instanceof MoonSdkType;
    }

    public static boolean isLuaModule(@Nullable final Module module) {
        return module != null && MoonModuleType.ID.equals(module.getModuleType().getId());
    }

    public static void checkForSdkFile(final MoonPsiFile file, Project project) {
        ModuleManager mm = ModuleManager.getInstance(project);
        boolean isSdkFile = false;

        for (final Module module : mm.getModules()) {
            ModuleRootManager mrm = ModuleRootManager.getInstance(module);
            Sdk sdk = mrm.getSdk();

            if (sdk != null) {
                VirtualFile[] vf = sdk.getRootProvider().getFiles(OrderRootType.CLASSES);

                for (VirtualFile libraryFile : vf)
                    MoonFileUtil.iterateRecursively(libraryFile, new ContentIterator() {
                        @Override
                        public boolean processFile(VirtualFile virtualFile) {
                            if (file.getVirtualFile() == virtualFile) {
                                file.setSdkFile(true);
                                return false;
                            }
                            return true;
                        }
                    });
            }
        }
    }
}
