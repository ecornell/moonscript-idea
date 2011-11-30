package com.eightbitmage.moonscript.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Parser implementation for MoonScript language support
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptParser implements PsiParser {

  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder) {

    builder.setDebugMode(true);

    final PsiBuilder.Marker rootMarker = builder.mark();

    while (!builder.eof()) {
      IElementType token = builder.getTokenType();

      if (token != null) {
        builder.mark().done(token);
      }

      builder.advanceLexer();
    }

    rootMarker.done(root);
    return builder.getTreeBuilt();
  }

}
