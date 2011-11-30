package com.eightbitmage.moonscript;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.eightbitmage.moonscript.file.MoonScriptFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Main application component that laods the MoonScript language support
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptLoader implements ApplicationComponent {

  public void initComponent() {
    ApplicationManager.getApplication().runWriteAction(
            new Runnable() {
              public void run() {
                FileTypeManager.getInstance().registerFileType(MoonScriptFileType.MOON_SCRIPT_FILE_TYPE, MoonScriptFileType.DEFAULT_EXTENSION);
              }
            }
    );
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return "moonscript.support.loader";
  }

}
