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

package com.eightbitmage.moonscript.lang.psi.util;

import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.intellij.openapi.util.text.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 6/8/11
 * Time: 12:16 PM
 */
public class MoonAssignment {
    public static final MoonAssignment[] EMPTY_ARRAY = new MoonAssignment[0];
    
    public MoonSymbol getSymbol() {
        return mySymbol;
    }

    public MoonExpression getValue() {
        return myValue;
    }


    @Override
    public String toString() {
        return StringUtil.notNullize(mySymbol.toString()) + " = " + StringUtil.notNullize(myValue.toString());
    }

    MoonSymbol mySymbol;
    MoonExpression myValue;

    public MoonAssignment(MoonSymbol mySymbol, MoonExpression myValue) {
        if (mySymbol instanceof MoonReferenceElement)
            this.mySymbol = (MoonSymbol) ((MoonReferenceElement) mySymbol).getElement();
        else
            this.mySymbol = mySymbol;
        this.myValue = myValue;
    }

    public static MoonExpression FindAssignmentForSymbol(MoonAssignment[] assignments, MoonSymbol symbol) {
        for (MoonAssignment assignment : assignments) {
            if (assignment.getSymbol() == symbol)
                return assignment.getValue();

            if (assignment.getSymbol() instanceof MoonReferenceElement)
                if (((MoonReferenceElement) assignment.getSymbol()).getElement() == symbol)
                    return assignment.getValue();
        }

        return null;
    }
}
