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

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonLongComment;
import com.intellij.psi.PsiComment;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 28, 2010
 * Time: 5:07:42 AM
 */
public class MoonLongCommentImpl extends CompositePsiElement implements MoonLongComment {
    public MoonLongCommentImpl() {
        super(MoonElementTypes.LONGCOMMENT);
    }

    @Override
    public PsiComment getOpen() {
        return (PsiComment) findChildByType(MoonElementTypes.LONGCOMMENT_BEGIN);
    }

    @Override
    public PsiComment getBody() {
         return (PsiComment) findChildByType(MoonElementTypes.LONGCOMMENT);
    }

    @Override
    public PsiComment getClose() {
        return (PsiComment) findChildByType(MoonElementTypes.LONGCOMMENT_END);
    }

    @Override
    public IElementType getTokenType() {
        return MoonElementTypes.LONGCOMMENT;
    }
}
