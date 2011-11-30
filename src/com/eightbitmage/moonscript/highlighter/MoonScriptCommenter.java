package com.eightbitmage.moonscript.highlighter;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptTokenTypes;
import org.jetbrains.annotations.Nullable;

/**
 * Commenting and uncommenting of MoonScript code blocks
 *
 * @author Elijah Cornell
 * @since 0.1.0
 */
public class MoonScriptCommenter implements CodeDocumentationAwareCommenter {

  public String getLineCommentPrefix() {
    return "#";
  }

  public String getBlockCommentPrefix() {
    return "###";
  }

  public String getBlockCommentSuffix() {
    return "###";
  }

  public String getCommentedBlockCommentPrefix() {
    return null;
  }

  public String getCommentedBlockCommentSuffix() {
    return null;
  }

  @Nullable
  public IElementType getLineCommentTokenType() {
    return MoonScriptTokenTypes.LINE_COMMENT;
  }

  @Nullable
  public IElementType getBlockCommentTokenType() {
    return MoonScriptTokenTypes.BLOCK_COMMENT;
  }

  @Nullable
  public IElementType getDocumentationCommentTokenType() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentPrefix() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentLinePrefix() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentSuffix() {
    return null;
  }

  public boolean isDocumentationComment(PsiComment element) {
    return false;
  }

}
