package com.eightbitmage.moonscript.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import static com.eightbitmage.moonscript.file.MoonScriptFileType.*;

/**
 * A MoonScript file
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptFile extends PsiFileBase {

  public MoonScriptFile(FileViewProvider viewProvider) {
    super(viewProvider, MOON_SCRIPT_FILE_TYPE.getLanguage());
  }

  @NotNull
  public FileType getFileType() {
    return MOON_SCRIPT_FILE_TYPE;
  }

  public String toString() {
    return "MoonScript File: " + getName();
  }

}
