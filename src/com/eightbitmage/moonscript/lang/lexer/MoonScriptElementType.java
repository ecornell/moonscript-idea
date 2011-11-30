package com.eightbitmage.moonscript.lang.lexer;

import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.file.MoonScriptFileType;

/**
 * Custom MoonScript element types.
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptElementType extends IElementType {

  private String name = null;

  public MoonScriptElementType(String name) {
    super(name, MoonScriptFileType.MOON_SCRIPT_FILE_TYPE.getLanguage());

    this.name = name;
  }

  public String toString() {
    return name;
  }

}
