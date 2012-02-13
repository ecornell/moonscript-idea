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

package com.eightbitmage.moonscript.lang.lexer;

import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;


/**
 * Interface that contains all tokens returned by MoonLexer
 *
 * @author sylvanaar
 */
public interface MoonTokenTypes extends MoonDocElementTypes {
    //IFileElementType FILE = new IFileElementType(Language.findInstance(MoonLanguage.class));
    /**
     * Wrong token. Use for debugger needs
     */
    IElementType WRONG = TokenType.BAD_CHARACTER;


    /* **************************************************************************************************
   *  Whitespaces & NewLines
   * ****************************************************************************************************/

    IElementType INDENT = new MoonElementType("indent");
    IElementType UNINDENT = new MoonElementType("unindent");

    IElementType NL_BEFORE_LONGSTRING = new MoonElementType("newline after longstring start bracket");
    IElementType WS = TokenType.WHITE_SPACE;
    IElementType NEWLINE = new MoonElementType("new line");

    TokenSet WHITE_SPACES_SET = TokenSet.create(WS, NEWLINE, TokenType.WHITE_SPACE, LDOC_WHITESPACE, NL_BEFORE_LONGSTRING);

    /* **************************************************************************************************
   *  Comments
   * ****************************************************************************************************/

    IElementType SHEBANG = new MoonElementType("shebang - should ignore");

    IElementType LONGCOMMENT = new MoonElementType("long comment");
    IElementType SHORTCOMMENT = new MoonElementType("short comment");

    IElementType LONGCOMMENT_BEGIN = new MoonElementType("long comment start bracket");
    IElementType LONGCOMMENT_END = new MoonElementType("long comment end bracket");

    TokenSet COMMENT_SET = TokenSet.create(SHORTCOMMENT, LONGCOMMENT,  SHEBANG, LUADOC_COMMENT, LONGCOMMENT_BEGIN,
            LONGCOMMENT_END);
   
    /* **************************************************************************************************
   *  Identifiers
   * ****************************************************************************************************/

    IElementType NAME = new MoonElementType("identifier");

    /* **************************************************************************************************
   *  Integers & floats
   * ****************************************************************************************************/

    IElementType NUMBER = new MoonElementType("number");

    /* **************************************************************************************************
   *  Strings & regular expressions
   * ****************************************************************************************************/

    IElementType STRING = new MoonElementType("string");
    IElementType LONGSTRING = new MoonElementType("long string");

    IElementType LONGSTRING_BEGIN = new MoonElementType("long string start bracket");
    IElementType LONGSTRING_END = new MoonElementType("long string end bracket");



    TokenSet STRING_LITERAL_SET = TokenSet.create(STRING, LONGSTRING, LONGSTRING_BEGIN, LONGSTRING_END);


    IElementType UNTERMINATED_STRING = new MoonElementType("unterminated string");


    /* **************************************************************************************************
   *  Common tokens: operators, braces etc.
   * ****************************************************************************************************/


    IElementType DIV = new MoonElementType("/");
    IElementType MULT = new MoonElementType("*");
    IElementType LPAREN = new MoonElementType("(");
    IElementType RPAREN = new MoonElementType(")");
    IElementType LBRACK = new MoonElementType("[");
    IElementType RBRACK = new MoonElementType("]");
    IElementType LCURLY = new MoonElementType("{");
    IElementType RCURLY = new MoonElementType("}");
    IElementType COLON = new MoonElementType(":");
    IElementType COMMA = new MoonElementType(",");
    IElementType DOT = new MoonElementType(".");
    IElementType ASSIGN = new MoonElementType("=");
    IElementType SEMI = new MoonElementType(";");
    IElementType EQ = new MoonElementType("==");
    IElementType NE = new MoonElementType("~=");
    IElementType PLUS = new MoonElementType("+");
    IElementType MINUS = new MoonElementType("-");
    IElementType GE = new MoonElementType(">=");
    IElementType GT = new MoonElementType(">");
    IElementType EXP = new MoonElementType("^");
    IElementType LE = new MoonElementType("<=");
    IElementType LT = new MoonElementType("<");
    IElementType ELLIPSIS = new MoonElementType("...");
    IElementType CONCAT = new MoonElementType("..");
    IElementType GETN = new MoonElementType("#");
    IElementType MOD = new MoonElementType("%");

    /* **************************************************************************************************
   *  Keywords
   * ****************************************************************************************************/


    IElementType IF = new MoonElementType("if");
    IElementType ELSE = new MoonElementType("else");
    IElementType ELSEIF = new MoonElementType("elseif");
    IElementType WHILE = new MoonElementType("while");
    IElementType WITH = new MoonElementType("with");

    IElementType THEN = new MoonElementType("then");
    IElementType FOR = new MoonElementType("for");
    IElementType IN = new MoonElementType("in");
    IElementType RETURN = new MoonElementType("return");
    IElementType BREAK = new MoonElementType("break");

    IElementType CONTINUE = new MoonElementType("continue");
    IElementType TRUE = new MoonElementType("true");
    IElementType FALSE = new MoonElementType("false");
    IElementType NIL = new MoonElementType("nil");
    IElementType FUNCTION = new MoonElementType("function");

    IElementType DO = new MoonElementType("do");
    IElementType NOT = new MoonElementType("not");
    IElementType AND = new MoonElementType("and");
    IElementType OR = new MoonElementType("or");
    IElementType LOCAL = new MoonElementType("local");

    IElementType REPEAT = new MoonElementType("repeat");
    IElementType UNTIL = new MoonElementType("until");
    IElementType END = new MoonElementType("end");

    IElementType CLASS = new MoonElementType("class");
    IElementType EXTENDS = new MoonElementType("extends");
    IElementType IMPORT = new MoonElementType("import");
    IElementType EXPORT = new MoonElementType("export");

    IElementType SELF = new MoonElementType("@");
    IElementType SWITCH = new MoonElementType("switch");
    IElementType WHEN = new MoonElementType("switch");


    TokenSet KEYWORDS = TokenSet.create(DO, FUNCTION, NOT, AND, OR,
            WITH, IF, THEN, ELSEIF, THEN, ELSE,
            WHILE, FOR, IN, RETURN, BREAK,
            CONTINUE, LOCAL,
            REPEAT, UNTIL, END,
            CLASS, EXTENDS, IMPORT, EXPORT,
            SWITCH, WHEN);

    TokenSet BRACES = TokenSet.create(LCURLY, RCURLY);
    TokenSet PARENS = TokenSet.create(LPAREN, RPAREN);
    TokenSet BRACKS = TokenSet.create(LBRACK, RBRACK);

    TokenSet BAD_INPUT = TokenSet.create(WRONG, UNTERMINATED_STRING);
    
    TokenSet DEFINED_CONSTANTS = TokenSet.create(NIL, TRUE, FALSE);

    TokenSet UNARY_OP_SET = TokenSet.create(NOT, MINUS, GETN);

    TokenSet BINARY_OP_SET = TokenSet.create(AND, OR,
            EQ, GE, GT, LT, LE, NE, DOT, COLON,
            MINUS, PLUS, DIV, MULT, EXP, MOD);

    TokenSet OP_SET = TokenSet.create(EQ, GE, GT, LT, LE, NE, MINUS, PLUS, DIV, MULT, EXP, MOD);

    TokenSet TABLE_ACCESS = TokenSet.create(DOT, COLON, LBRACK);

    TokenSet LITERALS_SET = TokenSet.create(NUMBER, NIL, TRUE, FALSE, STRING, LONGSTRING, LONGSTRING_BEGIN, LONGSTRING_END);

    TokenSet IDENTIFIERS_SET = TokenSet.create(NAME);

    TokenSet WHITE_SPACES_OR_COMMENTS = TokenSet.orSet(WHITE_SPACES_SET, COMMENT_SET);
}
