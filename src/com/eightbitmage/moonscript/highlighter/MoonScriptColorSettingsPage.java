package com.eightbitmage.moonscript.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.eightbitmage.moonscript.MoonScriptIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

/**
 * MoonScript color settings page
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptColorSettingsPage implements ColorSettingsPage {

  private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
          new AttributesDescriptor("Bad character", MoonScriptSyntaxHighlighter.BAD_CHARACTER),
          new AttributesDescriptor("Dot", MoonScriptSyntaxHighlighter.DOT),
          new AttributesDescriptor("Colon", MoonScriptSyntaxHighlighter.COLON),
          new AttributesDescriptor("Comma", MoonScriptSyntaxHighlighter.COMMA),
          new AttributesDescriptor("Semicolon", MoonScriptSyntaxHighlighter.SEMICOLON),
          new AttributesDescriptor("Parenthesis", MoonScriptSyntaxHighlighter.PARENTHESIS),
          new AttributesDescriptor("Brackets", MoonScriptSyntaxHighlighter.BRACKETS),
          new AttributesDescriptor("Braces", MoonScriptSyntaxHighlighter.BRACES),
          new AttributesDescriptor("Line comment", MoonScriptSyntaxHighlighter.LINE_COMMENT),
          new AttributesDescriptor("Block comment", MoonScriptSyntaxHighlighter.BLOCK_COMMENT),
          new AttributesDescriptor("Identifier", MoonScriptSyntaxHighlighter.IDENTIFIER),
          new AttributesDescriptor("Class", MoonScriptSyntaxHighlighter.CLASS_NAME),
          new AttributesDescriptor("Function name", MoonScriptSyntaxHighlighter.FUNCTION_NAME),
          new AttributesDescriptor("Function", MoonScriptSyntaxHighlighter.FUNCTION),
          new AttributesDescriptor("Function binding", MoonScriptSyntaxHighlighter.FUNCTION_BINDING),
          new AttributesDescriptor("Object key", MoonScriptSyntaxHighlighter.OBJECT_KEY),
          new AttributesDescriptor("Constant", MoonScriptSyntaxHighlighter.CONSTANT),
          new AttributesDescriptor("Number", MoonScriptSyntaxHighlighter.NUMBER),
          new AttributesDescriptor("Boolean", MoonScriptSyntaxHighlighter.BOOLEAN),
          new AttributesDescriptor("String literal", MoonScriptSyntaxHighlighter.STRING_LITERAL),
          new AttributesDescriptor("String", MoonScriptSyntaxHighlighter.STRING),
          new AttributesDescriptor("Expression substitution mark", MoonScriptSyntaxHighlighter.EXPRESSIONS_SUBSTITUTION_MARK),
          new AttributesDescriptor("Escape sequence", MoonScriptSyntaxHighlighter.ESCAPE_SEQUENCE),
          new AttributesDescriptor("This references", MoonScriptSyntaxHighlighter.THIS),
          new AttributesDescriptor("Prototype", MoonScriptSyntaxHighlighter.PROTOTYPE),
          new AttributesDescriptor("Operations", MoonScriptSyntaxHighlighter.OPERATIONS),
          new AttributesDescriptor("Existential operator", MoonScriptSyntaxHighlighter.EXISTENTIAL),
          new AttributesDescriptor("Keyword", MoonScriptSyntaxHighlighter.KEYWORD),
          new AttributesDescriptor("Range", MoonScriptSyntaxHighlighter.RANGE),
          new AttributesDescriptor("Splat", MoonScriptSyntaxHighlighter.SPLAT),
          new AttributesDescriptor("Regular expression id", MoonScriptSyntaxHighlighter.REGULAR_EXPRESSION_ID),
          new AttributesDescriptor("Regular expression content", MoonScriptSyntaxHighlighter.REGULAR_EXPRESSION_CONTENT),
          new AttributesDescriptor("Regular expression flag", MoonScriptSyntaxHighlighter.REGULAR_EXPRESSION_FLAG),
          new AttributesDescriptor("Heredoc id", MoonScriptSyntaxHighlighter.HEREDOC_ID),
          new AttributesDescriptor("Heredoc content", MoonScriptSyntaxHighlighter.HEREDOC_CONTENT),
          new AttributesDescriptor("Heregex id", MoonScriptSyntaxHighlighter.HEREGEX_ID),
          new AttributesDescriptor("Heregex content", MoonScriptSyntaxHighlighter.HEREGEX_CONTENT),
          new AttributesDescriptor("Javascript id", MoonScriptSyntaxHighlighter.JAVASCRIPT_ID),
          new AttributesDescriptor("Javascript content", MoonScriptSyntaxHighlighter.JAVASCRIPT_CONTENT),
  };

  @NotNull
  public String getDisplayName() {
    return "MoonScript";
  }

  public Icon getIcon() {
    return MoonScriptIcons.FILE_TYPE;
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTRS;
  }

  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  public MoonScriptSyntaxHighlighter getHighlighter() {
    return new MoonScriptSyntaxHighlighter();
  }

  @NotNull
  public String getDemoText() {
    return "###\n" +
            "Some tests\n" +
            "###\n" +
            "class Animal\n" +
            "  constructor: (@name) -> \n" +
            "  move: (meters) -> alert @name + \" moved \" + meters + \"m.\"\n" +
            "\n" +
            "class Snake extends Animal\n" +
            "  move: -> \n" +
            "    alert \'Slithering...\'\n" +
            "    super 5\n" +
            "\n" +
            "number   = 42; opposite = true\n" +
            "\n" +
            "/^a\\/\\\\[a-Z/\\n]\\u00A3b$/.test 'a//b'\n" +
            "\n" +
            "square = (x) -> x * x\n" +
            "\n" +
            "list = [1...5]\n" +
            "\n" +
            "math =\n" +
            "  root:   Math.sqrt\n" +
            "  cube:   (x) => x * square x\n" +
            "\n" +
            "race = (winner, runners...) ->\n" +
            "  print winner, runners\n" +
            "\n" +
            "alert \"I knew it!\" if elvis?\n" +
            "\n" +
            "cubes = math.cube num for num in list\n" +
            "\n" +
            "text = \"\"\"\n" +
            " Result \n" +
            "    is #{ @number }\"\"\"\n" +
            "\n" +
            "html = ''' " +
            "  <body></body>" +
            "'''\n" +
            "let me = 0 # let is reserved\n" +
            "\n" +
            "String::dasherize = ->\n" +
            "  this.replace /_/g, \"-\"" +
            "\n" +
            "SINGERS = {Jagger: \"Rock\", Elvis: \"Roll\"}\n" +
            "\n" +
            "t = ///\n" +
            "#{ something }[a-z]\n" +
            "///igm\n" +
            "\n" +
            "$('.shopping_cart').bind 'click', (event) =>\n" +
            "    @customer.purchase @cart\n" +
            "\n" +
            "hi = `function() {\n" +
            "  return [document.title, \"Hello JavaScript\"].join(\": \");\n" +
            "}`";
  }

  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

}
