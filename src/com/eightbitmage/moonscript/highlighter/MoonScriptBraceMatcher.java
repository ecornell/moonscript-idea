package com.eightbitmage.moonscript.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Brace matcher for the MoonScript language
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptBraceMatcher implements PairedBraceMatcher {

  private static final BracePair[] PAIRS = {
          new BracePair(MoonScriptTokenTypes.PARENTHESIS_START, MoonScriptTokenTypes.PARENTHESIS_END, false),
          new BracePair(MoonScriptTokenTypes.BRACKET_START, MoonScriptTokenTypes.BRACKET_END, false),
          new BracePair(MoonScriptTokenTypes.BRACE_START, MoonScriptTokenTypes.BRACE_END, false),
  };

  public BracePair[] getPairs() {
    return PAIRS;
  }

  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType ibraceType, @Nullable IElementType tokenType) {
    return true;
  }

  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }

}
