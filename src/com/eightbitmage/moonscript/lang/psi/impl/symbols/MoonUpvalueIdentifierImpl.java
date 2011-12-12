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

package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.intellij.lang.ASTNode;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonUpvalueIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/26/11
 * Time: 9:23 PM
 */
public class MoonUpvalueIdentifierImpl extends MoonLocalIdentifierImpl implements MoonUpvalueIdentifier {
    public MoonUpvalueIdentifierImpl(ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSameKind(MoonSymbol symbol) {
        return symbol instanceof MoonLocalDeclarationImpl || symbol instanceof MoonParameter;
    }

    @Override
    public String toString() {
        return "Upvalue: " + getText();
    }
}
