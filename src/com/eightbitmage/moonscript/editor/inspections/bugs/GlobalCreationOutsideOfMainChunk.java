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

package com.eightbitmage.moonscript.editor.inspections.bugs;

import com.eightbitmage.moonscript.editor.inspections.AbstractInspection;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobal;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 7/4/11
 * Time: 10:11 AM
 */
public class GlobalCreationOutsideOfMainChunk extends AbstractInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
       return "Suspicious global creation";
    }

    @Override
    public String getStaticDescription() {
        return "Looks for creation of globals in scopes other than the main chunk";
    }

    @NotNull
    @Override
    public String getGroupDisplayName() {
        return PROBABLE_BUGS;
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MoonElementVisitor() {
            List<String> validGlobals = new ArrayList<String>();
            public void visitDeclarationExpression(MoonDeclarationExpression var) {
                super.visitDeclarationExpression(var);

                if (var instanceof MoonGlobal) {
                    MoonBlock block = PsiTreeUtil.getParentOfType(var, MoonBlock.class);
                    if (block == null) return;

                    if (block instanceof MoonPsiFile) {
                        validGlobals.add(var.getName());
                        return;
                    }

                    if (!validGlobals.contains(var.getName()))
                        holder.registerProblem(var, "Suspicious global creation ("+var.getName()+")", LocalQuickFix.EMPTY_ARRAY);
                }
            }
        };
    }
}
