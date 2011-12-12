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

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.impl.statements.MoonAssignmentStatementImpl;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 6/8/11
 * Time: 12:49 PM
 */
public class MoonAssignmentUtil {
    @NotNull
    public static MoonAssignment[] getAssignments(MoonAssignmentStatement assignmentStatement) {
        MoonExpressionList exprs = assignmentStatement.getRightExprs();

        if (exprs == null)
            return MoonAssignment.EMPTY_ARRAY;

        List<MoonExpression> vals = exprs.getMoonExpressions();

        if (vals.size() == 0)
            return MoonAssignment.EMPTY_ARRAY;

        MoonIdentifierList leftExprs = assignmentStatement.getLeftExprs();
        if (leftExprs == null)
            return MoonAssignment.EMPTY_ARRAY;

        MoonSymbol[] defs = leftExprs.getSymbols();

        MoonAssignment[] assignments = new MoonAssignment[Math.min(vals.size(), defs.length)];

        for(int i=0;i<assignments.length; i++)
            assignments[i]= new MoonAssignment(defs[i], vals.get(i));

        return assignments;
    }

    public static MoonExpression[] getDefinedSymbolValues(MoonAssignmentStatementImpl assignmentStmt) {
        MoonExpressionList exprs = assignmentStmt.getRightExprs();

        if (exprs == null) return MoonExpression.EMPTY_ARRAY;

        List<MoonExpression> vals = exprs.getMoonExpressions();

        return vals.toArray(new MoonExpression[vals.size()]);
    }

}
