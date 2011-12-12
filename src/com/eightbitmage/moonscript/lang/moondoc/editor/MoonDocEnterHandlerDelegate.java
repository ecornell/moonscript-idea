/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang.moondoc.editor;

import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocCommentOwner;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonKeyValueInitializer;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/2/11
 * Time: 6:54 PM
 */
public class MoonDocEnterHandlerDelegate implements EnterHandlerDelegate {
    @Override
    public Result preprocessEnter(PsiFile file, Editor editor, Ref<Integer> caretOffset, Ref<Integer> caretAdvance,
                                  DataContext dataContext, EditorActionHandler originalHandler) {
        if (! (file instanceof MoonPsiFile))
            return Result.Continue;
        
        Document document = editor.getDocument();
        int caret = caretOffset.get();

        PsiElement e1 = file.findElementAt(caret - 1);

        if (e1 != null && e1.getNode().getElementType() == MoonDocElementTypes.LDOC_COMMENT_START) {
            // The user has typed the doc comment start and hit enter.
            // we want to autogenerate the luadocs
            String indent = CodeStyleManager
                    .getInstance(file.getProject()).getLineIndent(file, editor.getCaretModel().getOffset());

            StringBuilder luadoc = new StringBuilder().append(" \n");

            PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);

            e1 = file.findElementAt(caret - 1);
            if (e1 != null && e1.getContext() instanceof MoonDocComment) {
                MoonDocComment comment = (MoonDocComment) e1.getContext();

                assert comment != null;

                MoonDocCommentOwner owner = comment.getOwner();
                if (owner != null) {
                    if (owner instanceof MoonFunctionDefinitionStatement) {
                        MoonParameter[] parms = ((MoonFunctionDefinitionStatement) owner).getParameters()
                                .getLuaParameters();

                        for (MoonParameter p : parms)
                            luadoc.append(indent).append("-- @param ").append(p.getName()).append(" \n");

                        luadoc.append(indent).append("--");
                    } else if (owner instanceof MoonTableConstructor) {
                        MoonExpression[] initalizers = ((MoonTableConstructor) owner).getInitializers();

                        for (MoonExpression expression : initalizers)
                            if (expression instanceof MoonKeyValueInitializer) {
                                MoonExpression key = ((MoonKeyValueInitializer) expression).getFieldKey();
                                if (key instanceof MoonFieldIdentifier)
                                    luadoc.append(indent).append("-- @field ").append(((MoonFieldIdentifier) key).getName()).append(" \n");
                            }
                            
                        luadoc.append(indent).append("--");
                    }

                    document.insertString(caret, luadoc);

                    editor.getCaretModel().moveCaretRelatively(1, 0, false, false, false);

                    return Result.Stop;
                }
            }
        }

        return Result.Continue;
    }
}
