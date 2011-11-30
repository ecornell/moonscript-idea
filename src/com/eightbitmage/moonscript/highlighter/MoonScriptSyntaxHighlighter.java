package com.eightbitmage.moonscript.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptFlexLexer;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * MoonScript syntax highlighter
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptSyntaxHighlighter extends SyntaxHighlighterBase {

  private static final Map<IElementType, TextAttributesKey> TOKENS_TO_STYLES;

  @NotNull
  public Lexer getHighlightingLexer() {
    return new MoonScriptFlexLexer();
  }

  static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.BAD_CHARACTER",
          HighlighterColors.BAD_CHARACTER.getDefaultAttributes()
  );

  static final TextAttributesKey SEMICOLON = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.SEMICOLON",
          SyntaxHighlighterColors.JAVA_SEMICOLON.getDefaultAttributes()
  );

  static final TextAttributesKey COMMA = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.COMMA",
          SyntaxHighlighterColors.COMMA.getDefaultAttributes()
  );

  static final TextAttributesKey DOT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.DOT",
          SyntaxHighlighterColors.DOT.getDefaultAttributes()
  );

  static final TextAttributesKey CLASS_NAME = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.CLASS_NAME",
          HighlighterColors.TEXT.getDefaultAttributes()
  );

  static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.IDENTIFIER",
          HighlighterColors.TEXT.getDefaultAttributes()
  );

  static final TextAttributesKey CONSTANT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.CONSTANT",
          HighlighterColors.TEXT.getDefaultAttributes()
  );

  static final TextAttributesKey FUNCTION_NAME = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.FUNCTION_NAME",
          HighlighterColors.TEXT.getDefaultAttributes()
  );

  static final TextAttributesKey OBJECT_KEY = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.OBJECT_KEY",
          HighlighterColors.TEXT.getDefaultAttributes()
  );

  static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.NUMBER",
          SyntaxHighlighterColors.NUMBER.getDefaultAttributes()
  );

  static final TextAttributesKey BOOLEAN = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.BOOLEAN",
          SyntaxHighlighterColors.NUMBER.getDefaultAttributes()
  );

  static final TextAttributesKey STRING_LITERAL = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.STRING_LITERAL",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.STRING",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey HEREDOC_ID = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.HEREDOC_ID",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey HEREDOC_CONTENT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.HEREDOC_CONTENT",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey HEREGEX_ID = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.HEREGEX_ID",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey HEREGEX_CONTENT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.HEREGEX_CONTENT",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey JAVASCRIPT_ID = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.JAVASCRIPT_ID",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey JAVASCRIPT_CONTENT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.JAVASCRIPT_CONTENT",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey EXPRESSIONS_SUBSTITUTION_MARK = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.EXPRESSIONS_SUBSTITUTION_MARK",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.LINE_COMMENT",
          SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes()
  );

  static final TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.BLOCK_COMMENT",
          SyntaxHighlighterColors.JAVA_BLOCK_COMMENT.getDefaultAttributes()
  );

  static final TextAttributesKey PARENTHESIS = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.PARENTHESIS",
          SyntaxHighlighterColors.PARENTHS.getDefaultAttributes()
  );

  static final TextAttributesKey BRACKETS = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.BRACKET",
          SyntaxHighlighterColors.BRACKETS.getDefaultAttributes()
  );

  static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.BRACE",
          SyntaxHighlighterColors.BRACES.getDefaultAttributes()
  );

  static final TextAttributesKey OPERATIONS = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.OPERATIONS",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey EXISTENTIAL = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.EXISTENTIAL",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.KEYWORD",
          SyntaxHighlighterColors.KEYWORD.getDefaultAttributes()
  );

  static final TextAttributesKey RANGE = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.RANGE",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey SPLAT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.SPLAT",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey THIS = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.THIS",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey COLON = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.COLON",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey PROTOTYPE = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.PROTOTYPE",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey FUNCTION = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.FUNCTION",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey FUNCTION_BINDING = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.FUNCTION_BINDING",
          SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
  );

  static final TextAttributesKey REGULAR_EXPRESSION_ID = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.REGULAR_EXPRESSION_ID",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey REGULAR_EXPRESSION_CONTENT = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.REGULAR_EXPRESSION_CONTENT",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey REGULAR_EXPRESSION_FLAG = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.REGULAR_EXPRESSION_FLAG",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static final TextAttributesKey ESCAPE_SEQUENCE = TextAttributesKey.createTextAttributesKey(
          "MOONSCRIPT.ESCAPE_SEQUENCE",
          SyntaxHighlighterColors.STRING.getDefaultAttributes()
  );

  static {
    TOKENS_TO_STYLES = new HashMap<IElementType, TextAttributesKey>();
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BAD_CHARACTER, BAD_CHARACTER);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.ERROR_ELEMENT, BAD_CHARACTER);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.WHITE_SPACE, HighlighterColors.TEXT);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.TERMINATOR, HighlighterColors.TEXT);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.DOT, DOT);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.COMMA, COMMA);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.COLON, COLON);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.SEMICOLON, SEMICOLON);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.IDENTIFIER, IDENTIFIER);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.CLASS_NAME, CLASS_NAME);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.CONSTANT, CONSTANT);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.FUNCTION_NAME, FUNCTION_NAME);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.OBJECT_KEY, OBJECT_KEY);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.NUMBER, NUMBER);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BOOL, BOOLEAN);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.ESCAPE_SEQUENCE, ESCAPE_SEQUENCE);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.STRING_LITERAL, STRING_LITERAL);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.STRING, STRING);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.LINE_COMMENT, LINE_COMMENT);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BLOCK_COMMENT, BLOCK_COMMENT);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.PARENTHESIS_START, PARENTHESIS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.PARENTHESIS_END, PARENTHESIS);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BRACKET_START, BRACKETS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BRACKET_END, BRACKETS);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BRACE_START, BRACES);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BRACE_END, BRACES);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.EQUAL, OPERATIONS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.COMPOUND_ASSIGN, OPERATIONS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.COMPARE, OPERATIONS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.LOGIC, OPERATIONS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.PLUS, OPERATIONS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.MINUS, OPERATIONS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.MATH, OPERATIONS);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.EXIST, EXISTENTIAL);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.RANGE, RANGE);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.SPLAT, SPLAT);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.SELF, THIS);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.PROTOTYPE, PROTOTYPE);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.FUNCTION, FUNCTION);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.FUNCTION_BIND, FUNCTION_BINDING);

    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.UNARY, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.CLASS, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.EXTENDS, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.IF, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.ELSE, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.THEN, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.UNLESS, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.FOR, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.IN, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.OF, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BY, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.WHILE, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.UNTIL, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.SWITCH, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.WHEN, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.TRY, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.CATCH, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.THROW, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.FINALLY, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.BREAK, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.CONTINUE, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.RETURN, KEYWORD);
    TOKENS_TO_STYLES.put(MoonScriptTokenTypes.INSTANCE_OF, KEYWORD);
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    if (!TOKENS_TO_STYLES.containsKey(tokenType)) {
      throw new UnsupportedOperationException(tokenType.toString());
    }
    return pack(TOKENS_TO_STYLES.get(tokenType));
  }

}
