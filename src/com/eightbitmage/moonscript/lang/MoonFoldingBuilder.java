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

package com.eightbitmage.moonscript.lang;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonTableConstructor;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes.LONGCOMMENT;
import static com.eightbitmage.moonscript.lang.parser.MoonElementTypes.*;

public class MoonFoldingBuilder implements FoldingBuilder, DumbAware {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        final List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        final ASTNode fnode = node;
        final Document fdoc = document;

        appendDescriptors(fnode, fdoc, descriptors);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void appendDescriptors(final ASTNode node, final Document document, final List<FoldingDescriptor> descriptors) {
        if (node == null) return;

        ProgressManager.checkCanceled();

        try {
            if (isFoldableNode(node)) {
                final PsiElement psiElement = node.getPsi();

                if (psiElement instanceof MoonFunctionDefinition) {
                    MoonFunctionDefinition stmt = (MoonFunctionDefinition) psiElement;

                    if (stmt.getText().indexOf('\n')>0 && stmt.getBlock().getTextLength()>3)
                    descriptors.add(new FoldingDescriptor(node,
                            new TextRange(stmt.getParameters().getTextRange().getEndOffset() + 1,
                                    node.getTextRange().getEndOffset())));
                }

                if (psiElement instanceof MoonTableConstructor) {
                    MoonTableConstructor stmt = (MoonTableConstructor) psiElement;

                    if (stmt.getText().indexOf('\n')>0 && stmt.getTextLength()>3)
                        descriptors.add(new FoldingDescriptor(node,
                                new TextRange(stmt.getTextRange().getStartOffset() + 1,
                                        node.getTextRange().getEndOffset() - 1)));
                }

                if (psiElement instanceof MoonDocComment) {
//                    MoonDocComment stmt = (MoonDocComment) psiElement;
//
//                        if (stmt.getText().indexOf('\n')>0 && stmt.getTextLength()>3)
//                            descriptors.add(new FoldingDescriptor(node,
//                                    new TextRange(stmt.getTextRange().getStartOffset() + stmt.getText().indexOf('\n'),
//                                            node.getTextRange().getEndOffset())));

                   descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
                }
            }

            if (node.getElementType() == LONGCOMMENT && node.getTextLength() > 2) {
                descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            }



            ASTNode child = node.getFirstChildNode();
            while (child != null) {
                appendDescriptors(child, document, descriptors);
                child = child.getTreeNext();
            }
        } catch (Exception ignored) {
        }
    }

    private boolean isFoldableNode(ASTNode node) {
        final IElementType elementType = node.getElementType();
        return elementType == FUNCTION_DEFINITION ||
                elementType == LOCAL_FUNCTION ||
                elementType == ANONYMOUS_FUNCTION_EXPRESSION ||
                elementType == TABLE_CONSTUCTOR ||
                elementType == LUADOC_COMMENT;
    }

    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        if (node.getElementType() == LONGCOMMENT)
            return "comment";

        if (node.getElementType() == LUADOC_COMMENT) {
            ASTNode data = node.findChildByType(LDOC_COMMENT_DATA);

            if (data != null)
                return data.getText();
            
            return " doc comment";
        }

        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
