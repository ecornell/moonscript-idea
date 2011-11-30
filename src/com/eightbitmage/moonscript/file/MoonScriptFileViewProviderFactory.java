package com.eightbitmage.moonscript.file;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;

/**
 * @author Elijah Cornell
 * @since 0.1.5
 */
public class MoonScriptFileViewProviderFactory implements FileViewProviderFactory {

  public FileViewProvider createFileViewProvider(VirtualFile file, Language language, PsiManager manager, boolean physical) {
    return new MoonScriptViewProvider(manager, file, physical, language);
  }
}
