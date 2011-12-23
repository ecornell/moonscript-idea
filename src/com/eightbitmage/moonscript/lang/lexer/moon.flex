package com.eightbitmage.moonscript.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.*;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

%%

/*
* moon.l - flex lexer for MoonScript
*/

%class _MoonLexer
%implements FlexLexer, MoonTokenTypes

%unicode

%debug

%function advance
%type IElementType

//%eof{ return;
//%eof}

%{
    ExtendedSyntaxStrCommentHandler longCommentOrStringHandler = new ExtendedSyntaxStrCommentHandler();

    int current_line_indent = 0;
    int indent_level = 0;

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

%init{
%init}

//indent      =   ^[ \t]+
w           =   [ \t]+
nl          =   \r\n|\n|\r
nonl        =   [^\r\n]
nobrknl     =   [^\[\r\n]
name        =   [_a-zA-Z][_a-zA-Z0-9]*
n           =   [0-9]+
exp         =   [Ee][+-]?{n}
number      =   (0[xX][0-9a-fA-F]+|({n}|{n}[.]{n}){exp}?|[.]{n}|{n}[.])
sep         =   =*
luadoc      =   ---[^\r\n]*{nl}([ \t]*--({nobrknl}{nonl}*{nl}|{nonl}{nl}|{nl}))*


%x XLONGSTRING
%x XLONGSTRING_BEGIN
%x XSHORTCOMMENT
%x XLONGCOMMENT
%x XSTRINGQ
%x XSTRINGA
%x XINDENT

%s YYNORMAL
%s YYNAME
%s YYNUMBER

%%



/*************************************************************************************************/
/* The initial state recognizes keywords, most operators and characters that start another state */
/*************************************************************************************************/
<YYINITIAL> {
  .  { current_line_indent = 0; indent_level = 0; yypushback(yylength()); yybegin(XINDENT); }
}


<YYNORMAL> {
    {nl}     { current_line_indent = 0; yybegin(XINDENT); }

    /* Keywords */
    "and"          { return AND; }
    "break"        { return BREAK; }
    "do"           { return DO; }
    "else"         { return ELSE; }
    "elseif"       { return ELSEIF; }
    "end"          { return END; }
    "false"        { return FALSE; }
    "for"          { return FOR; }
    "function"     { return FUNCTION; }
    "->"           { return FUNCTION; }
    "=>"           { return FUNCTION; }
    "class"        { return CLASS; }
    "if"           { return IF; }
    "in"           { return IN; }
    "local"        { return LOCAL; }
    "nil"          { return NIL; }
    "not"          { return NOT; }
    "or"           { return OR; }
    "repeat"       { return REPEAT; }
    "return"       { return RETURN; }
    "then"         { return THEN; }
    "true"         { return TRUE; }
    "until"        { return UNTIL; }
    "while"        { return WHILE; }

    "export"       { return EXPORT; }
    "import"       { return IMPORT; }
    "with"         { return WITH; }
    "switch"       { return SWITCH; }

    //{number}       { return NUMBER; }

    {luadoc}       { yypushback(1); /* TODO: Only pushback a newline */  return LUADOC_COMMENT; }

    --\[{sep}\[    { longCommentOrStringHandler.setCurrentExtQuoteStart(yytext().toString()); yybegin( XLONGCOMMENT ); return LONGCOMMENT_BEGIN; }
    --+            { yypushback(yytext().length()); yybegin( XSHORTCOMMENT ); return advance(); }

    "["{sep}"["    { longCommentOrStringHandler.setCurrentExtQuoteStart(yytext().toString()); yybegin( XLONGSTRING_BEGIN ); return LONGSTRING_BEGIN; }

    "\""           { yybegin(XSTRINGQ);  return STRING; }
    '              { yybegin(XSTRINGA); return STRING; }

    "#!"          { yybegin( XSHORTCOMMENT ); return SHEBANG; }

    {name}        { yybegin(YYNAME);
                    return NAME; }

    {number}     { yybegin(YYNUMBER);
                   return NUMBER; }

    "..."        { return ELLIPSIS; }
    ".."         { return CONCAT; }
    "..="        { return CONCAT; }
    "=="         { return EQ; }
    ">="         { return GE; }
    "<="         { return LE; }
    "~="         { return NE; }
    "!="         { return NE; }
    "-"          { return MINUS; }
    "-="         { return MINUS; }
    "+"          { return PLUS;}
    "+="         { return PLUS;}
    "*"          { return MULT;}
    "*="         { return MULT;}
    "%"          { return MOD;}
    "%="         { return MOD;}
    "/"          { return DIV; }
    "/="         { return DIV; }
    "="          { return ASSIGN;}
    ">"          { return GT;}
    "<"          { return LT;}
    "("          { return LPAREN;}
    ")"          { return RPAREN;}
    "["          { return LBRACK;}
    "]"          { return RBRACK;}
    "{"          { return LCURLY;}
    "}"          { return RCURLY;}
    "#"          { return GETN;}
    ","          { return COMMA; }
    ";"          { return SEMI; }
    ":"          { return COLON; }
    "."          { return DOT;}
    "\\"         { return DOT;}
    "^"          { return EXP;}

    "@"          { return SELF; }

    //{nl}         { return NEWLINE; }
    {w}          { return WS; }

    .            { stack.clear();
                   yybegin(YYNORMAL);
                   return WRONG; }
}


<XINDENT>
{
  " "      { System.out.println("_"); current_line_indent++; }
  "\t"     { System.out.println("_"); current_line_indent++; }
  {nl}     { current_line_indent = 0; /*ignoring blank line */ }
  .        {
                   yypushback(yylength());
                   System.out.println(current_line_indent + " " + indent_level);
                   if (current_line_indent > indent_level) {
                       indent_level++;
                       return INDENT;
                   } else if (current_line_indent < indent_level) {
                       indent_level--;
                       return UNINDENT;
                   }
                   else {
                         yybegin(YYNORMAL);
                   }
           }
}

<XSTRINGQ>
{
  \"\"       {return STRING;}
  \"         { yybegin(YYNORMAL); return STRING; }
  \\[abfnrt] {return STRING;}
  \\\n       {return STRING;}
  \\\"       {return STRING; }
  \\'        {return STRING;}
  \\"["      {return STRING;}
  \\"]"      {return STRING;}
  \\\\       { return STRING; }
  {nl}       { yybegin(YYNORMAL); return WRONG; }
  .          {return STRING;}
}

<XSTRINGA>
{
  ''          { return STRING; }
  '           { yybegin(YYNORMAL); return STRING; }
  \\[abfnrt]  { return STRING; }
  \\\n        { return STRING; }
  \\\'        { return STRING; }
  \\'         { yybegin(YYNORMAL); return STRING; }
  \\"["       { return STRING; }
  \\"]"       { return STRING; }
  \\\\        { return STRING; }
  {nl}        { yybegin(YYNORMAL);return WRONG;  }
  .           { return STRING; }
}


<XLONGSTRING_BEGIN>
{
    {nl}     { return NL_BEFORE_LONGSTRING; }
    .        { yypushback(1); yybegin(XLONGSTRING); return advance(); }
}


<XLONGSTRING>
{
  "]"{sep}"]"     {
                    if (longCommentOrStringHandler.isCurrentExtQuoteStart(yytext())) {
                       yybegin(YYNORMAL); longCommentOrStringHandler.resetCurrentExtQuoteStart(); return LONGSTRING_END;
                    } else { yypushback(yytext().length()-1); }
                    return LONGSTRING;
                  }
                  
  {nl}     { return LONGSTRING; }
  .        { return LONGSTRING; }
}

<XSHORTCOMMENT>
{
  {nl}      {yybegin(YYNORMAL);  yypushback(yylength()); return NEWLINE; }
  .         { return SHORTCOMMENT;}
}

<XLONGCOMMENT>
{
  "]"{sep}"]"     {
                    if (longCommentOrStringHandler.isCurrentExtQuoteStart(yytext())) {
                       yybegin(YYINITIAL); longCommentOrStringHandler.resetCurrentExtQuoteStart(); return LONGCOMMENT_END;
                    }  else { yypushback(yytext().length()-1); }
                  return LONGCOMMENT;
                  }

  {nl}     { return LONGCOMMENT;}
  .        { return LONGCOMMENT;}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*****************************************************************/
/* Characters than can follow an identifier or a number directly */
/*****************************************************************/

<YYNAME, YYNUMBER> {
  "."          { yybegin(YYNORMAL); return DOT; }
  ":"          { yybegin(YYNORMAL); return COLON; }
  ","          { yybegin(YYNORMAL); return COMMA; }
  "["          { yybegin(YYNORMAL); return LBRACK; }
  "]"          { yybegin(YYNORMAL); return RBRACK; }
  ")"          { yybegin(YYNORMAL); return RPAREN; }
  "+"          { yybegin(YYNORMAL); return PLUS; }
  "-"          { yybegin(YYNORMAL); return MINUS; }
  "*"          { yybegin(YYNORMAL); return MULT; }
  "%"          { yybegin(YYNORMAL); return MOD; }
  "/"          { yybegin(YYNORMAL); return DIV; }
  "!"          { yybegin(YYNORMAL); return FUNCTION; }
  {nl}         { yybegin(YYNORMAL); yypushback(yylength()); return NEWLINE; }
  {w}          { yybegin(YYNORMAL); return WS; }
}


/**********************************************************************/
/* An identifier has some more characters that can follow it directly */
/**********************************************************************/

//<YYNAME> {
//  \.{QUOTE} / [^a-zA-Z0-9]    { yybegin(YYQUOTEPROPERTY);
//                                yypushback(yylength()); }
//
//  "?"                         { yybegin(YYINITIAL);
//                                return EXIST; }
//
//  "..."                       { yybegin(YYINITIAL);
//                                return SPLAT; }
//
//  "("                         { yybegin(YYINITIAL);
//                                return PARENTHESIS_START; }
//}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// Other ////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//.            { return WRONG; }


/*******************/
/* Nothing matched */
/*******************/

 .     { stack.clear();
         yybegin(YYNORMAL);
         return WRONG; }