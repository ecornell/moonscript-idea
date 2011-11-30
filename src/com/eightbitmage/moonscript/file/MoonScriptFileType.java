package com.eightbitmage.moonscript.file;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.eightbitmage.moonscript.MoonScriptIcons;
import com.eightbitmage.moonscript.MoonScriptLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * MoonScript file properties
 *
 * @author Michael kessler
 * @since 0.1.0
 */
public class MoonScriptFileType extends LanguageFileType {

  public static final MoonScriptFileType MOON_SCRIPT_FILE_TYPE = new MoonScriptFileType();
  public static final Language MOON_SCRIPT_LANGUAGE = MOON_SCRIPT_FILE_TYPE.getLanguage();

  @NonNls
  public static final String DEFAULT_EXTENSION = "moon";

  private MoonScriptFileType() {
    super(new MoonScriptLanguage());
  }

  @NotNull
  @NonNls
  public String getName() {
    return "MoonScript";
  }

  @NonNls
  @NotNull
  public String getDescription() {
    return "MoonScript Files";
  }

  @NotNull
  @NonNls
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @NotNull
   public Icon getIcon() {
    return MoonScriptIcons.FILE_TYPE;
  }

}
