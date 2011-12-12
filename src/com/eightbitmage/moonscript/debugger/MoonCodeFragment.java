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

package com.eightbitmage.moonscript.debugger;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiFileImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightVirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/15/11
 * Time: 3:04 AM
 */
public class MoonCodeFragment extends MoonPsiFileImpl {
    private GlobalSearchScope myResolveScope;

    public MoonCodeFragment(Project project, CharSequence text) {
        super(new SingleRootFileViewProvider(PsiManager.getInstance(project),
                new LightVirtualFile("DebugExpression.lua", MoonFileType.MOON_FILE_TYPE, text), true));
        ((SingleRootFileViewProvider) getViewProvider()).forceCachedPsi(this);
    }
}
