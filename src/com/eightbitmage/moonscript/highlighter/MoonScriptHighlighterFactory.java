package com.eightbitmage.moonscript.highlighter;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;

/**
 * MoonScript Syntax highlighter factory
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {

  @NotNull
  protected MoonScriptSyntaxHighlighter createHighlighter() {
    return new MoonScriptSyntaxHighlighter();
  }

}
