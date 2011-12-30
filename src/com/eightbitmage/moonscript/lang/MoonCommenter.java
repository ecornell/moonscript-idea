/*
 * Copyright 2009 Max Ishchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eightbitmage.moonscript.lang;

import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.intellij.lang.ASTNode;
import com.intellij.lang.CodeDocumentationAwareCommenterEx;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

public class MoonCommenter implements CodeDocumentationAwareCommenterEx {

    public String getLineCommentPrefix() {
        return "--";
    }

    public String getBlockCommentPrefix() {
        return "--[[";
    }

    public String getBlockCommentSuffix() {
        return "]]";

    }

    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }


  public boolean isDocumentationCommentText(final PsiElement element) {
    if (element == null) return false;
    final ASTNode node = element.getNode();
    return node != null && node.getElementType() == MoonDocTokenTypes.LDOC_COMMENT_DATA;
  }

    @Override
    public IElementType getLineCommentTokenType() {
        return MoonTokenTypes.SHORTCOMMENT;
    }

    @Override
    public IElementType getBlockCommentTokenType() {
        return MoonTokenTypes.LONGCOMMENT;
    }

    @Override
    public IElementType getDocumentationCommentTokenType() {
        return MoonTokenTypes.LUADOC_COMMENT;
    }

    @Override
    public String getDocumentationCommentPrefix() {
        return "---";
    }

    @Override
    public String getDocumentationCommentLinePrefix() {
        return "--";
    }

    @Override
    public String getDocumentationCommentSuffix() {
        return null;
    }

    @Override
    public boolean isDocumentationComment(PsiComment element) {
        return element instanceof MoonDocComment;
    }


}
