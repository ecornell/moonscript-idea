/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.eightbitmage.moonscript.lang.formatter.blocks;

import com.eightbitmage.moonscript.lang.formatter.processors.MoonSpacingProcessorBasic;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonBinaryExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonParameterList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonTableConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MoonFormattingBlock implements Block {
    public static final Logger LOG = Logger.getInstance("Moon.MoonBlock");
  final protected ASTNode myNode;
  final protected Alignment myAlignment;
  final protected Indent myIndent;
  final protected Wrap myWrap;
  final protected CodeStyleSettings mySettings;

  protected List<Block> mySubBlocks = null;

  public MoonFormattingBlock(@NotNull final ASTNode node, @Nullable final Alignment alignment, @NotNull final Indent indent, @Nullable final Wrap wrap, final CodeStyleSettings settings) {
    myNode = node;
    myAlignment = alignment;
    myIndent = indent;
    myWrap = wrap;
    mySettings = settings;
  }

  @NotNull
  public ASTNode getNode() {
   // LOG.info("Block <"+myNode.getText()+"> " + myNode.getElementType());
    return myNode;
  }

//  @NotNull
//  public CodeStyleSettings getSettings() {
//    return mySettings;
//  }

  @NotNull
  public TextRange getTextRange() {
    return myNode.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks() {
    if (mySubBlocks == null) {
      mySubBlocks = MoonBlockGenerator.generateSubBlocks(myNode, myAlignment, myWrap, mySettings, this);
    }
    return mySubBlocks;
  }

  @Nullable
  public Wrap getWrap() {
    return myWrap;
  }

  @Nullable
  public Indent getIndent() {
    return myIndent;
  }

  @Nullable
  public Alignment getAlignment() {
    return myAlignment;
  }

  /**
   * Returns spacing between neighrbour elements
   *
   * @param child1 left element
   * @param child2 right element
   * @return
   */
  @Nullable
  public Spacing getSpacing(Block child1, Block child2) {
    if ((child1 instanceof MoonFormattingBlock) && (child2 instanceof MoonFormattingBlock)) {
         return MoonSpacingProcessorBasic.getSpacing(((MoonFormattingBlock) child1), ((MoonFormattingBlock) child2), mySettings);
    }
    return null;
  }


  public boolean isIncomplete() {
    return isIncomplete(myNode);
  }

  /**
   * @param node Tree node
   * @return true if node is incomplete
   */
  public boolean isIncomplete(@NotNull final ASTNode node) {
    if (node.getElementType() instanceof ILazyParseableElementType) return false;
    ASTNode lastChild = node.getLastChildNode();
    while (lastChild != null &&
        !(lastChild.getElementType() instanceof ILazyParseableElementType) &&
        (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
      lastChild = lastChild.getTreePrev();
    }
    return lastChild != null && (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
  }

    public boolean isLeaf() {
        return getNode().getFirstChildNode() == null;
    }

  @NotNull
  public ChildAttributes getChildAttributes(final int newChildIndex) {
    return getAttributesByParent();
  }

  private ChildAttributes getAttributesByParent() {
    ASTNode astNode = getNode();
    final PsiElement psiParent = astNode.getPsi();
    if (psiParent instanceof MoonPsiFile) {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
      if (psiParent instanceof MoonTableConstructor) {
        return new ChildAttributes(Indent.getNormalIndent(), null);
      }
    if (MoonElementTypes.BLOCK_SET.contains(astNode.getElementType())) {
      final Alignment align = Alignment.createAlignment();
      return new ChildAttributes(Indent.getNormalIndent(), align);
    }

    if (psiParent instanceof MoonBinaryExpression) {
      return new ChildAttributes(Indent.getContinuationWithoutFirstIndent(), null);
    }
    if (psiParent instanceof MoonParameterList) {
      return new ChildAttributes(this.getIndent(), this.getAlignment());
    }
    if (psiParent instanceof MoonIdentifierList) {
      return new ChildAttributes(Indent.getContinuationIndent(), null);
    }
    return new ChildAttributes(Indent.getNoneIndent(), this.getAlignment());
  }
}
