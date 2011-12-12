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

package com.eightbitmage.moonscript.lang.psi.stubs.elements;

import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonGlobalDeclarationImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.MoonStubElementType;
import com.eightbitmage.moonscript.lang.psi.stubs.MoonStubUtils;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonGlobalDeclarationStub;
import com.eightbitmage.moonscript.lang.psi.stubs.impl.MoonGlobalDeclarationStubImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonGlobalDeclarationIndex;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalDeclaration;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:01 PM
 */
public class MoonStubGlobalDeclarationType extends MoonStubElementType<MoonGlobalDeclarationStub, MoonGlobalDeclaration> {

    public MoonStubGlobalDeclarationType() {
        super("global stub name");
    }

    @Override
    public MoonGlobalDeclaration createPsi(MoonGlobalDeclarationStub stub) {
        return new MoonGlobalDeclarationImpl(stub);
    }

    @Override
    public MoonGlobalDeclarationStub createStub(MoonGlobalDeclaration psi, StubElement parentStub) {
        return new MoonGlobalDeclarationStubImpl(parentStub, StringRef.fromString(psi.getName()),
                StringRef.fromString(psi.getModuleName()));
    }

    @Override
    public void serialize(MoonGlobalDeclarationStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        MoonStubUtils.writeNullableString(dataStream, stub.getModule());

    }

    @Override
    public MoonGlobalDeclarationStub deserialize(StubInputStream dataStream, StubElement parentStub) throws
            IOException {
        StringRef ref = dataStream.readName();
        
        String module = MoonStubUtils.readNullableString(dataStream);

        return new MoonGlobalDeclarationStubImpl(parentStub, ref, StringRef.fromString(module));
    }

    @Override
    public String getExternalId() {
        return "lua.GLOBAL_DEF";
    }

    @Override
    public void indexStub(MoonGlobalDeclarationStub stub, IndexSink sink) {
        String name = stub.getName();

        if (name != null) {
            sink.occurrence(MoonGlobalDeclarationIndex.KEY, name);
        }
    }

    @Override
    public PsiElement createElement(ASTNode node) {
        return new MoonGlobalDeclarationImpl(node);
    }
}
