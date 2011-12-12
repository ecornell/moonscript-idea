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

package com.eightbitmage.moonscript.lang.parser;

import com.eightbitmage.moonscript.lang.lexer.MoonElementType;
import com.eightbitmage.moonscript.lang.moondoc.lexer.IMoonDocElementType;
import com.eightbitmage.moonscript.lang.moondoc.psi.MoonDocPsiCreator;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonLiteralExpression;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobal;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.psi.impl.expressions.*;
import com.eightbitmage.moonscript.lang.psi.impl.statements.*;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.*;

import static com.eightbitmage.moonscript.lang.parser.MoonElementTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 14, 2010
 * Time: 6:56:50 PM
 */
public class MoonPsiCreator {

    public static PsiElement createElement(ASTNode node) {
        IElementType elem = node.getElementType();

        if (elem instanceof MoonElementType.PsiCreator) {
            return ((MoonElementType.PsiCreator) elem).createPsi(node);
        }

        if (elem instanceof IMoonDocElementType) {
          return MoonDocPsiCreator.createElement(node);
        }

        if (elem == EXPR)
            return new MoonExpressionImpl(node);

        if (elem == FUNCTION_CALL_EXPR) {
            MoonFunctionCallExpressionImpl e = new MoonFunctionCallExpressionImpl(node);

            final String nameRaw = e.getNameRaw();
            if (nameRaw != null && nameRaw.equals("require"))
                return new MoonRequireExpressionImpl(node);

            return e;
        }

        if (elem == ANONYMOUS_FUNCTION_EXPRESSION)
            return new MoonAnonymousFunctionExpressionImpl(node);

        if (elem == CONDITIONAL_EXPR)
            return new MoonConditionalExpressionImpl(node);

        if (elem == REFERENCE)
//          assert false;
            return new MoonWrapperReferenceElementImpl(node);

        if (elem == COMPOUND_REFERENCE)
            return new MoonCompoundReferenceElementImpl(node);

        if (elem == TABLE_CONSTUCTOR)
            return new MoonTableConstructorImpl(node);

        if (elem == IDX_ASSIGNMENT)
            return new MoonExpressionImpl(node);

        if (elem == KEY_ASSIGNMENT)
            return new MoonKeyValueInitializerImpl(node);

        if (elem == BLOCK)
            return new MoonBlockImpl(node);

        if (elem == REPEAT_BLOCK)
            return new MoonRepeatStatementImpl(node);

        if (elem == LOCAL_DECL)
            return new MoonLocalDefinitionStatementImpl(node);

        if (elem == LOCAL_DECL_WITH_ASSIGNMENT)
            return new MoonLocalDefinitionStatementImpl(node);

        if (elem == EXPR_LIST)
            return new MoonExpressionListImpl(node);

        if (elem == IDENTIFIER_LIST)
            return new MoonIdentifierListImpl(node);

        if (elem == LITERAL_EXPRESSION) {
            MoonLiteralExpression lit = new MoonLiteralExpressionImpl(node);

            if (lit.getLuaType() == MoonType.STRING)
                return new MoonStringLiteralExpressionImpl(node);

            return lit;
        }

        if (elem == BINARY_EXP)
            return new MoonBinaryExpressionImpl(node);

        if (elem == UNARY_EXP)
            return new MoonUnaryExpressionImpl(node);

        if (elem == FUNCTION_CALL) {
            MoonFunctionCallStatementImpl e = new MoonFunctionCallStatementImpl(node);

            MoonReferenceElement name = e.getInvokedExpression().getFunctionNameElement();
            if (name == null)
                return e;

            MoonIdentifier id = (MoonIdentifier) name.getElement();
            
            if (id instanceof MoonGlobal) {
                if (id.getText().equals("module")) return new MoonModuleStatementImpl(node);
                if (id.getText().equals("require")) return new MoonRequireStatementImpl(node);
            }
            return e;
        }


        if (elem == RETURN_STATEMENT ||
                elem == RETURN_STATEMENT_WITH_TAIL_CALL)
            return new MoonReturnStatementImpl(node);

        if (elem == NUMERIC_FOR_BLOCK)
            return new MoonNumericForStatementImpl(node);

        if (elem == PARENTHEICAL_EXPRESSION)
            return new MoonParenthesizedExpressionImpl(node);

        if (elem == GENERIC_FOR_BLOCK)
            return new MoonGenericForStatementImpl(node);

        if (elem == WHILE_BLOCK)
            return new MoonWhileStatementImpl(node);

        if (elem == ASSIGN_STMT)
            return new MoonAssignmentStatementImpl(node);

        if (elem == DO_BLOCK)
            return new MoonDoStatementImpl(node);

        if (elem == IF_THEN_BLOCK)
            return new MoonIfThenStatementImpl(node);

        if (elem == SELF_PARAMETER)
            return new MoonImpliedSelfParameterImpl(node);

        if (elem == GLOBAL_NAME)
            return new MoonGlobalUsageImpl(node);

        if (elem == GLOBAL_NAME_DECL)
            return new MoonGlobalDeclarationImpl(node);

        if (elem == LOCAL_NAME_DECL)
            return new MoonLocalDeclarationImpl(node);

        if (elem == LOCAL_NAME)
            return new MoonLocalIdentifierImpl(node);

        if (elem == FIELD_NAME)
            return new MoonFieldIdentifierImpl(node);

        if (elem == UPVAL_NAME)
            return new MoonUpvalueIdentifierImpl(node);

        if (elem == FUNCTION_DEFINITION)
            return new MoonFunctionDefinitionStatementImpl(node);

        if (elem == LOCAL_FUNCTION)
            return new MoonLocalFunctionDefinitionStatementImpl(node);

        if (elem == MoonElementTypes.PARAMETER_LIST)
            return new MoonParameterListImpl(node);

        if (elem == MoonElementTypes.PARAMETER)
            return new MoonParameterImpl(node);

        if (elem == MoonElementTypes.FUNCTION_CALL_ARGS)
            return new MoonFunctionArgumentsImpl(node);

        if (elem == MoonElementTypes.GETTABLE)
            return new MoonCompoundIdentifierImpl(node);

//        if (elem == MoonElementTypes.GETSELF)
//            return new LuaCompoundSelfIdentifierImpl(node);


        return new MoonPsiElementImpl(node);
    }

}
