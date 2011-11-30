package com.eightbitmage.moonscript.lang.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/**
 * Define the MoonScript tokens used within the MoonScriptLexer
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public interface MoonScriptTokenTypes {

  public static final IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
  public static final IElementType ERROR_ELEMENT = TokenType.ERROR_ELEMENT;

  public static final IElementType WHITE_SPACE = TokenType.WHITE_SPACE;

  public static final IElementType TERMINATOR = new MoonScriptElementType("TERMINATOR");

  public static final IElementType DOT = new MoonScriptElementType("DOT");
  public static final IElementType COMMA = new MoonScriptElementType("COMMA");
  public static final IElementType COLON = new MoonScriptElementType("COLON");
  public static final IElementType SEMICOLON = new MoonScriptElementType("SEMICOLON");

  public static final IElementType IDENTIFIER = new MoonScriptElementType("IDENTIFIER");
  public static final IElementType CLASS_NAME = new MoonScriptElementType("CLASS_NAME");
  public static final IElementType CONSTANT = new MoonScriptElementType("CONSTANT");
  public static final IElementType FUNCTION_NAME = new MoonScriptElementType("FUNCTION_NAME");
  public static final IElementType OBJECT_KEY = new MoonScriptElementType("OBJECT_KEY");

  public static final IElementType NUMBER = new MoonScriptElementType("NUMBER");
  public static final IElementType BOOL = new MoonScriptElementType("BOOL");

  public static final IElementType ESCAPE_SEQUENCE = new MoonScriptElementType("ESCAPE_SEQUENCE");

  public static final IElementType STRING_LITERAL = new MoonScriptElementType("STRING_LITERAL");
  public static final IElementType STRING = new MoonScriptElementType("STRING");

  /*
  public static final IElementType HEREDOC_START = new MoonScriptElementType("HEREDOC_START");
  public static final IElementType HEREDOC = new MoonScriptElementType("HEREDOC");
  public static final IElementType HEREDOC_END = new MoonScriptElementType("HEREDOC_END");

  public static final IElementType REGEX_START = new MoonScriptElementType("REGEX_START");
  public static final IElementType REGEX = new MoonScriptElementType("REGEX");
  public static final IElementType REGEX_BRACKET_START = new MoonScriptElementType("REGEX_BRACKET_START");
  public static final IElementType REGEX_BRACKET_END = new MoonScriptElementType("REGEX_BRACKET_END");
  public static final IElementType REGEX_PARENTHESIS_START = new MoonScriptElementType("REGEX_PARENTHESIS_START");
  public static final IElementType REGEX_PARENTHESIS_END = new MoonScriptElementType("REGEX_PARENTHESIS_END");
  public static final IElementType REGEX_BRACE_START = new MoonScriptElementType("REGEX_BRACE_START");
  public static final IElementType REGEX_BRACE_END = new MoonScriptElementType("REGEX_BRACE_END");
  public static final IElementType REGEX_END = new MoonScriptElementType("REGEX_END");
  public static final IElementType REGEX_FLAG = new MoonScriptElementType("REGEX_FLAG");

  public static final IElementType HEREGEX_START = new MoonScriptElementType("HEREGEX_START");
  public static final IElementType HEREGEX = new MoonScriptElementType("HEREGEX");
  public static final IElementType HEREGEX_END = new MoonScriptElementType("HEREGEX_END");

  public static final IElementType INTERPOLATION_START = new MoonScriptElementType("INTERPOLATION_START");
  public static final IElementType INTERPOLATION_END = new MoonScriptElementType("INTERPOLATION_END");

  public static final IElementType JAVASCRIPT_LITERAL = new MoonScriptElementType("JAVASCRIPT_LITERAL");
  public static final IElementType JAVASCRIPT = new MoonScriptElementType("JAVASCRIPT");
  */

  public static final IElementType LINE_COMMENT = new MoonScriptElementType("COMMENT");
  public static final IElementType BLOCK_COMMENT = new MoonScriptElementType("BLOCK_COMMENT");

  public static final IElementType PARENTHESIS_START = new MoonScriptElementType("PARENTHESIS_START");
  public static final IElementType PARENTHESIS_END = new MoonScriptElementType("PARENTHESIS_END");

  public static final IElementType BRACKET_START = new MoonScriptElementType("BRACKET_START");
  public static final IElementType BRACKET_END = new MoonScriptElementType("BRACKET_END");

  public static final IElementType BRACE_START = new MoonScriptElementType("BRACE_START");
  public static final IElementType BRACE_END = new MoonScriptElementType("BRACE_END");

  public static final IElementType EQUAL = new MoonScriptElementType("EQUAL");
  public static final IElementType COMPOUND_ASSIGN = new MoonScriptElementType("COMPOUND_ASSIGN");
  public static final IElementType COMPARE = new MoonScriptElementType("COMPARE");
  public static final IElementType LOGIC = new MoonScriptElementType("LOGIC");
  public static final IElementType RANGE = new MoonScriptElementType("RANGE");
  public static final IElementType SPLAT = new MoonScriptElementType("SPLAT");
  public static final IElementType SELF = new MoonScriptElementType("SELF");
  public static final IElementType PROTOTYPE = new MoonScriptElementType("PROTOTYPE");
  public static final IElementType FUNCTION = new MoonScriptElementType("FUNCTION");
  public static final IElementType FUNCTION_BIND = new MoonScriptElementType("FUNCTION_BIND");
  public static final IElementType EXIST = new MoonScriptElementType("EXIST");
  public static final IElementType PLUS = new MoonScriptElementType("PLUS");
  public static final IElementType MINUS = new MoonScriptElementType("MINUS");
  public static final IElementType MATH = new MoonScriptElementType("MATH");

  public static final IElementType UNARY = new MoonScriptElementType("UNARY");
  public static final IElementType CLASS = new MoonScriptElementType("CLASS");
  public static final IElementType EXTENDS = new MoonScriptElementType("EXTENDS");
  public static final IElementType IF = new MoonScriptElementType("IF");
  public static final IElementType ELSE = new MoonScriptElementType("ELSE");
  public static final IElementType THEN = new MoonScriptElementType("THEN");
  public static final IElementType UNLESS = new MoonScriptElementType("UNLESS");
  public static final IElementType FOR = new MoonScriptElementType("FOR");
  public static final IElementType IN = new MoonScriptElementType("IN");
  public static final IElementType OF = new MoonScriptElementType("OF");
  public static final IElementType BY = new MoonScriptElementType("BY");
  public static final IElementType WHILE = new MoonScriptElementType("WHILE");
  public static final IElementType UNTIL = new MoonScriptElementType("UNTIL");
  public static final IElementType SWITCH = new MoonScriptElementType("SWITCH");
  public static final IElementType WHEN = new MoonScriptElementType("WHEN");
  public static final IElementType TRY = new MoonScriptElementType("TRY");
  public static final IElementType CATCH = new MoonScriptElementType("CATCH");
  public static final IElementType THROW = new MoonScriptElementType("THROW");
  public static final IElementType FINALLY = new MoonScriptElementType("FINALLY");
  public static final IElementType BREAK = new MoonScriptElementType("BREAK");
  public static final IElementType CONTINUE = new MoonScriptElementType("CONTINUE");
  public static final IElementType RETURN = new MoonScriptElementType("RETURN");
  public static final IElementType INSTANCE_OF = new MoonScriptElementType("INSTANCE_OF");

}
