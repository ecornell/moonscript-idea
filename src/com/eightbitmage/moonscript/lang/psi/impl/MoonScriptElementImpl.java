package com.eightbitmage.moonscript.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.eightbitmage.moonscript.lang.psi.MoonScriptElement;

/**
 * @author Elijah Cornell
 * @since 0.1.8
 */
public class MoonScriptElementImpl extends ASTWrapperPsiElement implements MoonScriptElement {

  private final ASTNode node;

  public MoonScriptElementImpl(final ASTNode node) {
    super(node);
    this.node = node;
  }

  public String toString() {
    return "CS:" + node.getElementType().toString();
  }

}
