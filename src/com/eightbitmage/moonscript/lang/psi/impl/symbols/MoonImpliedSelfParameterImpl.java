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

package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 12, 2010
 * Time: 10:52:46 AM
 */
public class MoonImpliedSelfParameterImpl extends MoonParameterImpl
        implements MoonParameter {
    public MoonImpliedSelfParameterImpl(@NotNull ASTNode node) {
        super(node);
    }


    @Override
    public MoonFunctionDefinition getDeclaringFunction() {
        return (MoonFunctionDefinition) getNode().getTreeParent().getPsi();

    }

    public String getName() {
        return "self";
    }

    public String getText() { return "self"; }

    @Override
    public boolean isVarArgs() {
        return false;
    }

}
