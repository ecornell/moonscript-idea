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
import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;
import com.eightbitmage.moonscript.lang.psi.stubs.MoonStubElementType;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonCompoundIdentifierStub;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonGlobalDeclarationStub;
import com.eightbitmage.moonscript.lang.psi.stubs.elements.MoonFieldStubType;
import com.eightbitmage.moonscript.lang.psi.stubs.elements.MoonStubCompoundIdentifierType;
import com.eightbitmage.moonscript.lang.psi.stubs.elements.MoonStubGlobalDeclarationType;
import com.eightbitmage.moonscript.lang.psi.stubs.impl.MoonFieldStub;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalDeclaration;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 10, 2010
 * Time: 3:54:46 PM
 */
public interface MoonElementTypes extends MoonTokenTypes, MoonDocElementTypes {
    IElementType EMPTY_INPUT = new MoonElementType("empty input");

    

    IElementType FUNCTION_DEFINITION = new MoonElementType("Function Definition");

    IElementType LOCAL_NAME = new MoonElementType("local name");
    IElementType LOCAL_NAME_DECL = new MoonElementType("local name declaration");

    IElementType GLOBAL_NAME = new MoonElementType("global name");
  //  IElementType GLOBAL_NAME_DECL = new MoonElementType("global name declaration");
 // IElementType GETTABLE = new MoonElementType("get table");
//IElementType GETSELF = new MoonElementType("get self");
    MoonStubElementType<MoonGlobalDeclarationStub, MoonGlobalDeclaration> GLOBAL_NAME_DECL = new MoonStubGlobalDeclarationType();
    MoonStubElementType<MoonCompoundIdentifierStub, MoonCompoundIdentifier> GETTABLE = new MoonStubCompoundIdentifierType();
    //MoonStubElementType<MoonCompoundIdentifierStub, MoonCompoundIdentifier> GETSELF = new MoonStubCompoundIdentifierType();

    MoonStubElementType<MoonFieldStub, MoonFieldIdentifier> FIELD_NAME = new MoonFieldStubType();

    

    IElementType TABLE_INDEX = new MoonElementType("table index");
    IElementType KEY_ASSIGNMENT = new MoonElementType("keyed field initializer");
    IElementType IDX_ASSIGNMENT = new MoonElementType("indexed field initializer");

    IElementType REFERENCE = new MoonElementType("Reference");

    IElementType COMPOUND_REFERENCE = new MoonElementType("Compound Reference");
    IElementType IDENTIFIER_LIST = new MoonElementType("Identifier List");

    IElementType STATEMENT = new MoonElementType("Statment");
    IElementType LAST_STATEMENT = new MoonElementType("LastStatement");
    IElementType EXPR = new MoonElementType("Expression");
    IElementType EXPR_LIST = new MoonElementType("Expression List");

    IElementType LITERAL_EXPRESSION = new MoonElementType("Literal Expression");
    IElementType PARENTHEICAL_EXPRESSION = new MoonElementType("Parentheical Expression");

    IElementType TABLE_CONSTUCTOR = new MoonElementType("Table Constructor");
    IElementType FUNCTION_CALL_ARGS = new MoonElementType("Function Call Args");
    IElementType FUNCTION_CALL = new MoonElementType("Function Call Statement");
    IElementType FUNCTION_CALL_EXPR = new MoonElementType("Function Call Expression");
    IElementType ANONYMOUS_FUNCTION_EXPRESSION = new MoonElementType("Anonymous function expression");

    IElementType ASSIGN_STMT = new MoonElementType("Assignment Statement");
    IElementType CONDITIONAL_EXPR = new MoonElementType("Conditional Expression");

    IElementType LOCAL_DECL_WITH_ASSIGNMENT = new MoonElementType("Local Declaration With Assignment Statement");
    IElementType LOCAL_DECL = new MoonElementType("Local Declaration");

    IElementType SELF_PARAMETER = new MoonElementType("Implied parameter (self)");

    IElementType BLOCK = new MoonElementType("Block");

    IElementType UNARY_EXP = new MoonElementType("UnExp");
    IElementType BINARY_EXP = new MoonElementType("BinExp");
    IElementType UNARY_OP = new MoonElementType("UnOp");
    IElementType BINARY_OP = new MoonElementType("BinOp");

    IElementType DO_BLOCK = new MoonElementType("Do Block");

    IElementType WHILE_BLOCK = new MoonElementType("While Block");

    IElementType REPEAT_BLOCK = new MoonElementType("Repeat Block");
    IElementType GENERIC_FOR_BLOCK = new MoonElementType("Generic For Block");
    IElementType IF_THEN_BLOCK = new MoonElementType("If-Then Block");
    IElementType NUMERIC_FOR_BLOCK = new MoonElementType("Numeric For Block");

    TokenSet EXPRESSION_SET = TokenSet.create(LITERAL_EXPRESSION, BINARY_EXP, UNARY_EXP, EXPR);
    IElementType RETURN_STATEMENT = new MoonElementType("Return statement");
    IElementType RETURN_STATEMENT_WITH_TAIL_CALL = new MoonElementType("Tailcall Return statement");

    IElementType LOCAL_FUNCTION = new MoonElementType("local function def");

    TokenSet BLOCK_SET = TokenSet.create(FUNCTION_DEFINITION, LOCAL_FUNCTION, ANONYMOUS_FUNCTION_EXPRESSION,
            WHILE_BLOCK,
            GENERIC_FOR_BLOCK,
            IF_THEN_BLOCK,
            NUMERIC_FOR_BLOCK,
            REPEAT_BLOCK,
            DO_BLOCK);

    IElementType PARAMETER = new MoonElementType("function parameters");
    IElementType PARAMETER_LIST = new MoonElementType("function parameter");

    IElementType UPVAL_NAME = new MoonElementType("upvalue name");
}
