package com.eightbitmage.moonscript;

import com.intellij.lang.Language;

/**
 * All main properties for the MoonScript language
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptLanguage extends Language {

  public MoonScriptLanguage() {
    super("MoonScript", "text/moonscript");
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }

}
