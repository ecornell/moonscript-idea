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

package com.eightbitmage.moonscript.lang.moondoc.psi;

import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.eightbitmage.moonscript.lang.moondoc.parser.elements.MoonDocTagValueTokenType;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.eightbitmage.moonscript.lang.moondoc.psi.impl.*;

import static com.eightbitmage.moonscript.lang.moondoc.parser.elements.MoonDocTagValueTokenType.TagValueTokenType.REFERENCE_ELEMENT;


/**
 * @author ilyas
 */
public class MoonDocPsiCreator implements MoonDocElementTypes {

  public static PsiElement createElement(ASTNode node) {
    IElementType type = node.getElementType();

    if (type instanceof MoonDocTagValueTokenType) {
      MoonDocTagValueTokenType value = (MoonDocTagValueTokenType) type;
      MoonDocTagValueTokenType.TagValueTokenType valueType = value.getValueType(node);
      if (valueType == REFERENCE_ELEMENT) return new MoonDocSymbolReferenceElementImpl(node);

      return new MoonDocTagValueTokenImpl(node);
    }

    if (type == LDOC_TAG) return new MoonDocTagImpl(node);
    if (type == LDOC_FIELD_REF) return new MoonDocFieldReferenceImpl(node);
    if (type == LDOC_PARAM_REF) return new MoonDocParameterReferenceImpl(node);
    if (type == LDOC_REFERENCE_ELEMENT) return new MoonDocSymbolReferenceElementImpl(node);

    return new ASTWrapperPsiElement(node);
  }
}
