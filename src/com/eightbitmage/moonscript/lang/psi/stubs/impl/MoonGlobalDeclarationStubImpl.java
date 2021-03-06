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

package com.eightbitmage.moonscript.lang.psi.stubs.impl;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonGlobalDeclarationStub;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalDeclaration;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:10 PM
 */
public class MoonGlobalDeclarationStubImpl extends StubBase<MoonGlobalDeclaration> implements MoonGlobalDeclarationStub {

    private final StringRef myName;
    private       StringRef myModule;


    public MoonGlobalDeclarationStubImpl(MoonGlobalDeclaration e) {
        super(null, MoonElementTypes.GLOBAL_NAME_DECL);
        myName = StringRef.fromString(e.getName());
        myModule = StringRef.fromString(e.getModuleName());
    }

    @Override
    public @Nullable String getModule() {
        if (myModule == null) return null;
        return myModule.getString();
    }

    public MoonGlobalDeclarationStubImpl(StubElement parent, StringRef name, StringRef module) {
        super(parent, MoonElementTypes.GLOBAL_NAME_DECL);
        myName = name;
        myModule = module;
    }

    @Override
    public String getName() {
        return myName.getString();
    }
}
