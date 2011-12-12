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

package com.eightbitmage.moonscript.lang.psi.stubs.index;

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.search.MoonSourceFilterScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;

import java.util.Collection;


public class MoonFullScriptNameIndex extends IntStubIndexExtension<MoonPsiFile> {
    public static final StubIndexKey<Integer, MoonPsiFile> KEY = StubIndexKey.createIndexKey("lua.script.fqn");

    private static final MoonFullScriptNameIndex OUR_INSTANCE = new MoonFullScriptNameIndex();

    public static MoonFullScriptNameIndex getInstance() {
        return OUR_INSTANCE;
    }

    public StubIndexKey<Integer, MoonPsiFile> getKey() {
        return KEY;
    }

    public Collection<MoonPsiFile> get(Integer integer, Project project, GlobalSearchScope scope) {
        return super.get(integer, project, new MoonSourceFilterScope(scope, project));
    }
}