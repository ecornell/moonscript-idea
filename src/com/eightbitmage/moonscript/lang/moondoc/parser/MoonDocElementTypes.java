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

package com.eightbitmage.moonscript.lang.moondoc.parser;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocElementType;
import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.psi.impl.MoonDocCommentImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.openapi.project.Project;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocElementTypeImpl;
import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocLexer;
import org.jetbrains.annotations.NotNull;







/**
 * @author ilyas
 */
public interface MoonDocElementTypes extends MoonDocTokenTypes {

  /**
   * LuaDoc comment
   */
  ILazyParseableElementType LUADOC_COMMENT = new ILazyParseableElementType("MoonDocComment") {
    @NotNull
    public Language getLanguage() {
      return MoonFileType.MOON_FILE_TYPE.getLanguage();
    }

    public ASTNode parseContents(ASTNode chameleon) {

      final PsiElement parentElement = chameleon.getTreeParent().getPsi();

      assert parentElement != null;
        
      final Project project = parentElement.getProject();

      final PsiBuilder builder = new PsiBuilderImpl(project, getLanguage(), new MoonDocLexer(), chameleon, chameleon.getText());
      final PsiParser parser = new MoonDocParser();

      return parser.parse(this, builder).getFirstChildNode();
    }

    @Override
    public ASTNode createNode(CharSequence text) {
      return new MoonDocCommentImpl(text);
    }
  };

  MoonDocElementType LDOC_TAG = new MoonDocElementTypeImpl("MoonDocTag");

  MoonDocElementType LDOC_REFERENCE_ELEMENT = new MoonDocElementTypeImpl("MoonDocReferenceElement");
  MoonDocElementType LDOC_PARAM_REF = new MoonDocElementTypeImpl("MoonDocParameterReference");
  MoonDocElementType LDOC_FIELD_REF = new MoonDocElementTypeImpl("MoonDocFieldReference");
  }
