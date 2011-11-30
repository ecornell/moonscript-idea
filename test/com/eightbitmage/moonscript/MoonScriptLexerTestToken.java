package com.eightbitmage.moonscript;

import com.intellij.psi.tree.IElementType;

/**
 * Internal testing class for token comparison
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
class MoonScriptLexerTestToken {

  private String token;

  /**
   * Construct a test lexer token form the plugin lexer token data
   *
   * @param element The token element type
   * @param content The token content
   */
  public MoonScriptLexerTestToken(IElementType element, CharSequence content) {
    this.token = "[" + element.toString() + " " + content.toString().replace("\n", "\\n").replace("\t", "\\t") + "]";
  }

  /**
   * Construct a test lexer token from the MoonScript lexer token
   *
   * @param token The token name
   */
  public MoonScriptLexerTestToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public boolean equals(Object other) {
    return (other instanceof MoonScriptLexerTestToken &&
            token.equals(((MoonScriptLexerTestToken) other).getToken()));
  }

  public int hashCode() {
    return token.hashCode();
  }

  public String toString() {
    return token;
  }

}
