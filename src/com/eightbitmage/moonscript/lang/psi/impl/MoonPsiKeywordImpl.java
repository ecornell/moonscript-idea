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

package com.eightbitmage.moonscript.lang.psi.impl;

import com.eightbitmage.moonscript.lang.psi.MoonPsiKeyword;
import com.eightbitmage.moonscript.lang.psi.MoonPsiToken;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 22, 2010
 * Time: 2:21:32 AM
 */
public class MoonPsiKeywordImpl extends LeafPsiElement implements MoonPsiKeyword, MoonPsiToken {
  public MoonPsiKeywordImpl(IElementType type, CharSequence text) {
    super(type, text);
  }

  public IElementType getTokenType(){
    return getElementType();
  }

  public void accept(@NotNull PsiElementVisitor visitor){
    if (visitor instanceof MoonElementVisitor) {
      ((MoonElementVisitor)visitor).visitKeyword(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  public String toString(){
    return "MoonKeyword:" + getText();
  }

//  static {
//    for(Field field: PsiKeyword.class.getFields()) {
//      CharTableImpl.staticIntern(field.getName().toLowerCase());
//    }
//  }
}