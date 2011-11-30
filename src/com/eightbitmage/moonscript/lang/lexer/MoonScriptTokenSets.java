package com.eightbitmage.moonscript.lang.lexer;

import com.intellij.psi.tree.TokenSet;

/**
 * Group the lexer lexer
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public abstract class MoonScriptTokenSets implements MoonScriptTokenTypes {

  public static TokenSet WHITESPACE_TOKEN_SET = TokenSet.create(
          WHITE_SPACE
  );

  public static TokenSet COMMENTS_TOKEN_SET = TokenSet.create(
          LINE_COMMENT,
          BLOCK_COMMENT
  );

  public static TokenSet STRING_TOKEN_SET = TokenSet.create(
          STRING,
          STRING_LITERAL
  );

}
