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

package com.eightbitmage.moonscript.lang.psi.symbols;

import com.eightbitmage.moonscript.lang.psi.MoonNamedElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.intellij.navigation.NavigationItem;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/26/11
 * Time: 6:06 PM
 */
public interface MoonSymbol extends MoonExpression, MoonNamedElement, NavigationItem {
    static final MoonSymbol[] EMPTY_ARRAY = new MoonSymbol[0];
    
    public boolean isSameKind(MoonSymbol symbol);

    public boolean isAssignedTo();
}
