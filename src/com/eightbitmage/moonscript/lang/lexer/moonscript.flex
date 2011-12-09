package com.eightbitmage.moonscript.lang.lexer;


import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptTokenTypes;
import java.util.Stack;

/**
 * The MoonScript lexer is responsible for generating a token stream of any MoonScript source file.
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
%%


%debug

%unicode

%public
%class MoonScriptLexer
%implements FlexLexer
%type IElementType

%function advance

%{

  private IElementType characterClassType;

  private final Stack<Integer> stack = new Stack<Integer>();

  /**
   * Push the actual state on top of the stack
   */
  private void pushState() {
    stack.push(yystate());
  }

  /**
   * Push the actual state on top of the stack
   * and change into another state
   *
   * @param state The new state
   */
  private void pushStateAndBegin(int state) {
    stack.push(yystate());
    yybegin(state);
  }

  /**
   * Pop the last state from the stack and change to it.
   * If the stack is empty, go to YYINITIAL
   */
  private void popState() {
    if (!stack.empty()) {
      yybegin(stack.pop());
    } else {
      yybegin(YYINITIAL);
    }
  }

  /**
   * Push the stream back to the position before the text match
   *
   * @param text The text to match
   * @return true when matched
   */
  private boolean pushBackTo(String text) {
    final int position = yytext().toString().indexOf(text);

    if (position != -1) {
      yypushback(yylength() - position);
      return true;
    }

    return false;
  }

  /**
   * Push the stream back to the position before the text match
   * and change into the given state
   *
   * @param text The text to match
   * @param state The new state
   * @return true when matched
   */
  private boolean pushBackAndState(String text, int state) {
    final boolean success = pushBackTo(text);

    if (success) {
      pushStateAndBegin(state);
    }

    return success;
  }

%}

TERMINATOR      = [\n\r]|\\\n
WHITE_SPACE     = [ \t]+

IDENTIFIER      = [$_a-z][$_a-zA-Z0-9]*
CLASS_NAME      = [A-Z][$_a-zA-Z0-9]*
CONSTANT        = [A-Z][$_A-Z0-9]*
NUMBER          = (0(x|X)[0-9a-fA-F]+)|(-?[0-9]+(\.[0-9]+)?(e[+\-]?[0-9]+)?)
FUNCTION        = [_a-zA-Z]([$_a-zA-Z0-9])*?[:]([^\n\r])*?(->|=>)
OBJECT_KEY      = [_a-zA-Z]([$_a-zA-Z0-9])*[:][^:]

/*RESERVED        = and|break|do|else|elseif|end|false|for|function|if|in|local|nil|not|or|repeat|return|then|true|until|while*/
RESERVED        = zzz
LOGIC           = and|or|\^|not
COMPARE         = ==|\!=|\~=|<|>|<=|>=
COMPOUND_ASSIGN = -=|\+=|\/=|\*=|%=
BOOL            = true|false|nil
UNARY           = do|not
QUOTE           = xxx

%state YYIDENTIFIER, YYNUMBER, YYJAVASCRIPT
%state YYDOUBLEQUOTESTRING, YYSINGLEQUOTESTRING
%state YYINTERPOLATION, YYQUOTEPROPERTY, YYCLASSNAME

%%

/*************************************************************************************************/
/* The initial state recognizes keywords, most operators and characters that start another state */
/*************************************************************************************************/

<YYINITIAL> {
  {RESERVED}                  { return MoonScriptTokenTypes.ERROR_ELEMENT; }
  {QUOTE}:                    { yypushback(1);
                                return MoonScriptTokenTypes.IDENTIFIER; }

  "@"                         { return MoonScriptTokenTypes.SELF; }
  "self"                      { return MoonScriptTokenTypes.SELF; }

  "class"                     { return MoonScriptTokenTypes.CLASS; }
  "extends"                   { return MoonScriptTokenTypes.EXTENDS; }

  "if"                        { return MoonScriptTokenTypes.IF; }
  "then"                      { return MoonScriptTokenTypes.THEN; }
  "else"                      { return MoonScriptTokenTypes.ELSE; }
  "elseif"                    { return MoonScriptTokenTypes.ELSE; }

  "for"                       { return MoonScriptTokenTypes.FOR; }
  "in"                        { return MoonScriptTokenTypes.IN; }
  "when"                      { return MoonScriptTokenTypes.WHEN; }

  "while"                     { return MoonScriptTokenTypes.WHILE; }
  "until"                     { return MoonScriptTokenTypes.UNTIL; }
  "break"                     { return MoonScriptTokenTypes.BREAK; }
  "return"                    { return MoonScriptTokenTypes.RETURN; }

  {BOOL}                      { return MoonScriptTokenTypes.BOOL; }
  {LOGIC}                     { return MoonScriptTokenTypes.LOGIC; }
  {COMPARE}                   { return MoonScriptTokenTypes.COMPARE; }
  {COMPOUND_ASSIGN}           { return MoonScriptTokenTypes.COMPOUND_ASSIGN; }
  {UNARY}                     { return MoonScriptTokenTypes.UNARY; }

  \"                          { yybegin(YYDOUBLEQUOTESTRING);
                                return MoonScriptTokenTypes.STRING_LITERAL; }

  \'                          { yybegin(YYSINGLEQUOTESTRING);
                                return MoonScriptTokenTypes.STRING_LITERAL; }

  {IDENTIFIER}                { yybegin(YYIDENTIFIER);
                                return MoonScriptTokenTypes.IDENTIFIER; }

  {CONSTANT}                  { yybegin(YYIDENTIFIER);
                                return MoonScriptTokenTypes.CONSTANT; }

  {CLASS_NAME}                { yybegin(YYCLASSNAME);
                                return MoonScriptTokenTypes.CLASS_NAME; }

  {OBJECT_KEY}                { pushBackTo(":");
                                return MoonScriptTokenTypes.OBJECT_KEY; }

  {FUNCTION}                  { pushBackTo(":");
                                return MoonScriptTokenTypes.FUNCTION_NAME;
                              }

  {NUMBER}                    { yybegin(YYNUMBER);
                                return MoonScriptTokenTypes.NUMBER; }


  "->"                        { return MoonScriptTokenTypes.FUNCTION; }
  "!"                         { return MoonScriptTokenTypes.FUNCTION; }
  "=>"                        { return MoonScriptTokenTypes.FUNCTION_BIND; }

  "="                         { return MoonScriptTokenTypes.EQUAL; }

  "["                         { return MoonScriptTokenTypes.BRACKET_START; }
  "]"                         { return MoonScriptTokenTypes.BRACKET_END; }

  "("                         { return MoonScriptTokenTypes.PARENTHESIS_START; }
  ")"                         { return MoonScriptTokenTypes.PARENTHESIS_END; }

  /* Push the state because the braces are important for determining the interpolation */
  "{"                         { pushState();
                                return MoonScriptTokenTypes.BRACE_START; }

  "."                         { return MoonScriptTokenTypes.DOT; }
  "#"                         { return MoonScriptTokenTypes.DOT; }
  "\\"                        { return MoonScriptTokenTypes.FUNCTION; }
  ":"                         { return MoonScriptTokenTypes.COLON; }
  ";"                         { return MoonScriptTokenTypes.SEMICOLON; }
  ","                         { return MoonScriptTokenTypes.COMMA; }

  "+"                         { return MoonScriptTokenTypes.PLUS; }
  "-"                         { return MoonScriptTokenTypes.MINUS; }
  "*"                         { return MoonScriptTokenTypes.MATH; }
  "%"                         { return MoonScriptTokenTypes.MATH; }
  "/"                         { return MoonScriptTokenTypes.MATH; }

  --\[\[~\]\]                 { return MoonScriptTokenTypes.BLOCK_COMMENT; }
  (--)(.*)*[^\n\r]?          { return MoonScriptTokenTypes.LINE_COMMENT; }

  (#\!)(.*)*[^\n\r]?          { return MoonScriptTokenTypes.LINE_COMMENT; }

  {TERMINATOR}                { return MoonScriptTokenTypes.TERMINATOR; }
  {WHITE_SPACE}               { return MoonScriptTokenTypes.WHITE_SPACE; }
}

/*********************************************************************************************************************/
/* A closing brace pops a state from the stack. If this state is YYINITIAL, then it is a normal BRACE_END, otherwise */
/* push it back to the steram an let the specific state recognize the special brace type. */
/*********************************************************************************************************************/

<YYINITIAL, YYIDENTIFIER, YYNUMBER> {
  "}"                         { popState();
                                if (yystate() == YYINITIAL) {
                                  return MoonScriptTokenTypes.BRACE_END;
                                } else {
                                  yypushback(1);
                                }
                              }
}

/*****************************************************************/
/* Characters than can follow an identifier or a number directly */
/*****************************************************************/

<YYIDENTIFIER, YYNUMBER> {
  "."                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.DOT; }

  "\\"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.DOT; }

  ":"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.COLON; }

  ";"                         { return MoonScriptTokenTypes.SEMICOLON; }

  "::"                        { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.PROTOTYPE; }

  ","                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.COMMA; }

  "["                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.BRACKET_START; }

  "]"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.BRACKET_END; }

  ")"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.PARENTHESIS_END; }

  "+"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.PLUS; }

  "-"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.MINUS; }

  "*"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.MATH; }

  "%"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.MATH; }

  "/"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.MATH; }

  "="                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.EQUAL; }

  "\!"                        { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.FUNCTION; }

  {TERMINATOR}                { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.TERMINATOR; }

  {WHITE_SPACE}               { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.WHITE_SPACE; }

  \"                          { yybegin(YYDOUBLEQUOTESTRING);
                                return MoonScriptTokenTypes.STRING_LITERAL; }

  \'                          { yybegin(YYSINGLEQUOTESTRING);
                                return MoonScriptTokenTypes.STRING_LITERAL; }
}

/**********************************************************************/
/* An identifier has some more characters that can follow it directly */
/**********************************************************************/

<YYIDENTIFIER> {
  \.{QUOTE} / [^a-zA-Z0-9]    { yybegin(YYQUOTEPROPERTY);
                                yypushback(yylength()); }

  "?"                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.EXIST; }

  "..."                       { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.SPLAT; }

  "("                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.PARENTHESIS_START; }

  "\!"                        {  yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.FUNCTION; }

}

/*****************/
/* A class name  */
/*****************/

<YYCLASSNAME> {
  \.{QUOTE} / [^a-zA-Z0-9]    { yybegin(YYQUOTEPROPERTY);
                                yypushback(yylength()); }

  "."                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.DOT; }

  ","                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.COMMA; }

  "::"                        { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.PROTOTYPE; }

  "("                         { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.PARENTHESIS_START; }

  "\!"                        {  yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.FUNCTION; }

  {TERMINATOR}                { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.TERMINATOR; }

  {WHITE_SPACE}               { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.WHITE_SPACE; }
}

/*******************************************************************************/
/* Certain reserved words an keywords are allowed as property of an identifier */
/*******************************************************************************/

<YYQUOTEPROPERTY> {
  "."                         { return MoonScriptTokenTypes.DOT; }

  {QUOTE}                     { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.IDENTIFIER; }
}


/*****************************************************************/
/* A number has some more characters that can follow it directly */
/*****************************************************************/

<YYNUMBER> {
  ".."                        { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.RANGE; }

  "..."                       { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.RANGE; }
}

/********************/
/* Escape sequences */
/********************/

<YYSINGLEQUOTESTRING, YYDOUBLEQUOTESTRING> {
  [\\][^\n\r]                 |
  [\\][0-8]{1,3}              |
  [\\]x[0-9a-fA-F]{1,2}       |
  [\\]u[0-9a-fA-F]{1,4}       { return MoonScriptTokenTypes.ESCAPE_SEQUENCE; }
}

/*************************************/
/* Content of a single quoted string */
/*************************************/

<YYSINGLEQUOTESTRING> {
  \'                          { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.STRING_LITERAL; }

  [^\'\n\r\\]+                { return MoonScriptTokenTypes.STRING; }

  {TERMINATOR}                { return MoonScriptTokenTypes.TERMINATOR; }

  [^]                         { yypushback(yytext().length());
                                yybegin(YYINITIAL); }
}

/*************************************/
/* Content of a double quoted string */
/*************************************/

<YYDOUBLEQUOTESTRING> {
  \"                          { yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.STRING_LITERAL; }

  [^\"\n\r\\]+                { pushBackAndState("#{", YYINTERPOLATION);
                                if (yylength() != 0) {
                                  return MoonScriptTokenTypes.STRING;
                                }
                              }


  {TERMINATOR}                { return MoonScriptTokenTypes.TERMINATOR; }

  [^]                         { yypushback(yytext().length());
                                yybegin(YYINITIAL); }
}

/*******************/
/* Nothing matched */
/*******************/

.                             { stack.clear();
                                yybegin(YYINITIAL);
                                return MoonScriptTokenTypes.BAD_CHARACTER; }
