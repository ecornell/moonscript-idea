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

package com.eightbitmage.moonscript.lang.psi.stubs.index;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/3/11
 * Time: 5:05 AM
 */
public class MoonFieldIndex extends StringStubIndexExtension<MoonFieldIdentifier> {
    public static final StubIndexKey<String, MoonFieldIdentifier> KEY = StubIndexKey.createIndexKey("lua.field.name");

    private static final MoonFieldIndex OUR_INSTANCE = new MoonFieldIndex();

    public static MoonFieldIndex getInstance() {
        return OUR_INSTANCE;
    }


    public StubIndexKey<String, MoonFieldIdentifier> getKey() {
        return KEY;
    }
}
