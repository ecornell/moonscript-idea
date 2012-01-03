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

package com.eightbitmage.moonscript.lang.psi.stubs.elements;

import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonCompoundIdentifierImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.MoonStubElementType;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonCompoundIdentifierStub;
import com.eightbitmage.moonscript.lang.psi.stubs.impl.MoonCompoundIdentifierStubImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonGlobalDeclarationIndex;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
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
 * Date: 2/21/11
 * Time: 7:32 PM
 */
public class MoonStubCompoundIdentifierType
    extends MoonStubElementType<MoonCompoundIdentifierStub, MoonCompoundIdentifier> {
    public MoonStubCompoundIdentifierType() {
        super("compound id stub name");
    }

    @Override
    public PsiElement createElement(ASTNode node) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MoonCompoundIdentifier createPsi(MoonCompoundIdentifierStub stub) {
        return new MoonCompoundIdentifierImpl(stub);
    }

    @Override
    public MoonCompoundIdentifierStub createStub(MoonCompoundIdentifier psi, StubElement parentStub) {
        return new MoonCompoundIdentifierStubImpl(parentStub, psi);
    }

    @Override
    public String getExternalId() {
        return "moon.COMPOUND_ID";
    }

    @Override
    public void serialize(MoonCompoundIdentifierStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeBoolean(stub.isGlobalDeclaration());
    }

    @Override
    public MoonCompoundIdentifierStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        boolean isDeclaration = dataStream.readBoolean();
        return new MoonCompoundIdentifierStubImpl(parentStub, ref, isDeclaration);
    }

    @Override
    public void indexStub(MoonCompoundIdentifierStub stub, IndexSink sink) {
        String name = stub.getName();

        if (name != null && stub.isGlobalDeclaration()) {
//            System.out.println("indexed: " + name);
          sink.occurrence(MoonGlobalDeclarationIndex.KEY, name);
        }
    }
}
