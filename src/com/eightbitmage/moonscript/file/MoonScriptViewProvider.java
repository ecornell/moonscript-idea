package com.eightbitmage.moonscript.file;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * MoonScript view provider that disables incremental reparsing
 *
 * @author Elijah Cornell
 * @since 0.1.5
 */
public class MoonScriptViewProvider extends SingleRootFileViewProvider {

  public MoonScriptViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file) {
   super(manager, file);
  }

  public MoonScriptViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile virtualFile, final boolean physical) {
    super(manager, virtualFile, physical);
  }

  protected MoonScriptViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile virtualFile, final boolean physical, @NotNull Language language) {
    super(manager, virtualFile, physical, language);
  }

  public boolean supportsIncrementalReparse(final Language rootLanguage) {
    return false;
  }

}
