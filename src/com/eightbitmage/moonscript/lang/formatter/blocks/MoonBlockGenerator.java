/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.eightbitmage.moonscript.lang.formatter.blocks;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.lang.formatter.processors.MoonIndentProcessor;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFileBase;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonBinaryExpression;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate myBlock hierarchy
 *
 * @author ilyas
 */
public class MoonBlockGenerator implements MoonElementTypes {
    public static final Logger LOG = Logger.getInstance("Lua.MoonBlockGenerator");
//  private static final TokenSet NESTED = TokenSet.create(REFERENCE_EXPRESSION,
//      PATH_INDEX_PROPERTY,
//      PATH_METHOD_CALL,
//      PATH_PROPERTY_REFERENCE);

    static int level = 0;
    public static List<Block> generateSubBlocks(ASTNode node,
                                                Alignment myAlignment,
                                                Wrap myWrap,
                                                CodeStyleSettings mySettings,
                                                MoonFormattingBlock formattingBlock) {
//        //For binary expressions
        PsiElement blockPsi = formattingBlock.getNode().getPsi();
        if (blockPsi instanceof MoonBinaryExpression &&
                !(blockPsi.getParent() instanceof MoonBinaryExpression)) {
            return generateForBinaryExpr(node, myWrap, mySettings);
        }

       // LOG.info(">> parent: " + blockPsi + ": " + node);
        // For other cases
        final ArrayList<Block> subBlocks = new ArrayList<Block>();
        ASTNode[] children = getLuaChildren(node);
        ASTNode prevChildNode = null;
        for (ASTNode childNode : children) {
            if (canBeCorrectBlock(childNode)) {
                final Indent indent = MoonIndentProcessor.getChildIndent(formattingBlock, prevChildNode, childNode);
              //  LOG.info("" + level + "     child: " + childNode + "indent " + indent);
                level++;
                subBlocks.add(
                        new MoonFormattingBlock(childNode,
                                blockPsi instanceof MoonFormattingBlock ? null : myAlignment,
                                indent, myWrap, mySettings));
                --level;
                prevChildNode = childNode;
            }
        }
     //   LOG.info("<< parent: " + blockPsi+ ": " + node);
        return subBlocks;
    }

    /**
     * @param node Tree node
     * @return true, if the current node can be myBlock node, else otherwise
     */
    private static boolean canBeCorrectBlock(final ASTNode node) {
        return (node.getText().trim().length() > 0);
    }

    private static ASTNode[] getLuaChildren(final ASTNode node) {
        PsiElement psi = node.getPsi();
        if (psi instanceof OuterLanguageElement) {
            TextRange range = node.getTextRange();
            ArrayList<ASTNode> childList = new ArrayList<ASTNode>();
            PsiFile LuaFile = psi.getContainingFile().getViewProvider().getPsi(MoonFileType.MOON_LANGUAGE);
            if (LuaFile instanceof MoonPsiFileBase) {
                addChildNodes(LuaFile, childList, range);
            }
            return childList.toArray(new ASTNode[childList.size()]);
        }
        return node.getChildren(null);
    }

    private static void addChildNodes(PsiElement elem, ArrayList<ASTNode> childNodes, TextRange range) {
        ASTNode node = elem.getNode();
        if (range.contains(elem.getTextRange()) && node != null) {
            childNodes.add(node);
        } else {
            for (PsiElement child : elem.getChildren()) {
                addChildNodes(child, childNodes, range);
            }
        }

    }

    /**
     * Generates blocks for binary expressions
     *
     * @param node
     * @return
     */
    private static List<Block> generateForBinaryExpr(final ASTNode node, Wrap myWrap, CodeStyleSettings mySettings) {
        final ArrayList<Block> subBlocks = new ArrayList<Block>();
        Alignment alignment = mySettings.ALIGN_MULTILINE_BINARY_OPERATION ? Alignment.createAlignment() : null;
        MoonBinaryExpression myExpr = (MoonBinaryExpression) node.getPsi();
        ASTNode[] children = node.getChildren(null);
        if (myExpr.getLeftExpression() instanceof MoonBinaryExpression) {
            addBinaryChildrenRecursively(myExpr.getLeftExpression(), subBlocks, Indent.getContinuationWithoutFirstIndent(), alignment, myWrap, mySettings);
        }
        for (ASTNode childNode : children) {
            if (canBeCorrectBlock(childNode) &&
                    !(childNode.getPsi() instanceof MoonBinaryExpression)) {
                subBlocks.add(new MoonFormattingBlock(childNode, alignment, Indent.getContinuationWithoutFirstIndent(), myWrap, mySettings));
            }
        }
        if (myExpr.getRightExpression() instanceof MoonBinaryExpression) {
            addBinaryChildrenRecursively(myExpr.getRightExpression(), subBlocks, Indent.getContinuationWithoutFirstIndent(), alignment, myWrap, mySettings);
        }
        return subBlocks;
    }

    /**
     * Adds all children of specified element to given list
     *
     * @param elem
     * @param list
     * @param indent
     * @param alignment
     */
    private static void addBinaryChildrenRecursively(PsiElement elem,
                                                     List<Block> list,
                                                     Indent indent,
                                                     Alignment alignment, Wrap myWrap, CodeStyleSettings mySettings) {
        if (elem == null) return;
        ASTNode[] children = elem.getNode().getChildren(null);
        // For binary expressions
        if ((elem instanceof MoonBinaryExpression)) {
            MoonBinaryExpression myExpr = ((MoonBinaryExpression) elem);
            if (myExpr.getLeftExpression() instanceof MoonBinaryExpression) {
                addBinaryChildrenRecursively(myExpr.getLeftExpression(), list, Indent.getContinuationWithoutFirstIndent(), alignment, myWrap, mySettings);
            }
            for (ASTNode childNode : children) {
                if (canBeCorrectBlock(childNode) &&
                        !(childNode.getPsi() instanceof MoonBinaryExpression)) {
                    list.add(new MoonFormattingBlock(childNode, alignment, indent, myWrap, mySettings));
                }
            }
            if (myExpr.getRightExpression() instanceof MoonBinaryExpression) {
                addBinaryChildrenRecursively(myExpr.getRightExpression(), list, Indent.getContinuationWithoutFirstIndent(), alignment, myWrap, mySettings);
            }
        }
    }


    /**
     * Generates blocks for nested expressions like a.b.c etc.
     *
     * @param node
     * @return
     */
    private static List<Block> generateForNestedExpr(final ASTNode node, Alignment myAlignment, Wrap myWrap, CodeStyleSettings mySettings) {
        final ArrayList<Block> subBlocks = new ArrayList<Block>();
        ASTNode children[] = node.getChildren(null);
        if (children.length > 0 && false /* NESTED.contains(children[0].getElementType()) */) {
            addNestedChildrenRecursively(children[0].getPsi(), subBlocks, myAlignment, myWrap, mySettings);
        } else if (canBeCorrectBlock(children[0])) {
            subBlocks.add(new MoonFormattingBlock(children[0], myAlignment, Indent.getContinuationWithoutFirstIndent(), myWrap, mySettings));
        }
        if (children.length > 1) {
            for (ASTNode childNode : children) {
                if (canBeCorrectBlock(childNode) &&
                        children[0] != childNode) {
                    subBlocks.add(new MoonFormattingBlock(childNode, myAlignment, Indent.getContinuationWithoutFirstIndent(), myWrap, mySettings));
                }
            }
        }
        return subBlocks;
    }

    /**
     * Adds nested children for paths
     *
     * @param elem
     * @param list
     * @param myAlignment
     * @param mySettings
     */
    private static void addNestedChildrenRecursively(PsiElement elem,
                                                     List<Block> list, Alignment myAlignment, Wrap myWrap, CodeStyleSettings mySettings) {
        ASTNode[] children = elem.getNode().getChildren(null);
        // For path expressions
        if (children.length > 0) {
            addNestedChildrenRecursively(children[0].getPsi(), list, myAlignment, myWrap, mySettings);
        } else if (canBeCorrectBlock(children[0])) {
            list.add(new MoonFormattingBlock(children[0], myAlignment, Indent.getContinuationWithoutFirstIndent(), myWrap, mySettings));
        }
        if (children.length > 1) {
            for (ASTNode childNode : children) {
                if (canBeCorrectBlock(childNode) &&
                        children[0] != childNode) {
                    if (elem.getNode() != null) {
                        list.add(new MoonFormattingBlock(childNode, myAlignment, Indent.getContinuationWithoutFirstIndent(), myWrap, mySettings));
                    } else {
                        list.add(new MoonFormattingBlock(childNode, myAlignment, Indent.getNoneIndent(), myWrap, mySettings));
                    }
                }
            }
        }
    }

}
