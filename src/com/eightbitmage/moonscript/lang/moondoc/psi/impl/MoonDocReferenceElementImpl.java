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

package com.eightbitmage.moonscript.lang.moondoc.psi.impl;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocReferenceElement;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTagValueToken;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class MoonDocReferenceElementImpl extends MoonDocPsiElementImpl implements MoonDocReferenceElement {

    public MoonDocReferenceElementImpl(@NotNull ASTNode node) {
        super(node);
    }

  public void accept(MoonElementVisitor visitor) {
    visitor.visitDocReference(this);
  }

    public String toString() {
        return "MoonDocReferenceElement";
    }

    @Nullable
    public MoonDocReferenceElement getLuaReferenceElement() {
        return this;
    }

    @Nullable
    @Override
    public MoonDocTagValueToken getReferenceNameElement() {
        return findChildByClass(MoonDocTagValueToken.class);
    }
}
