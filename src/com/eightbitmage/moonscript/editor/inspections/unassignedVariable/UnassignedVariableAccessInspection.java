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
package com.eightbitmage.moonscript.editor.inspections.unassignedVariable;

import com.eightbitmage.moonscript.editor.inspections.AbstractInspection;
import com.eightbitmage.moonscript.lang.psi.MoonControlFlowOwner;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobal;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.controlFlow.ControlFlowUtil;
import com.eightbitmage.moonscript.lang.psi.controlFlow.Instruction;
import com.eightbitmage.moonscript.lang.psi.controlFlow.ReadWriteVariableInstruction;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author ven
 */
public class UnassignedVariableAccessInspection extends AbstractInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Variable not assigned";
    }

    @Override
    public String getStaticDescription() {
        return "Variable is read from without being assigned to.";
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

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MoonElementVisitor() {
            //            @Override
            //            public void visitBlock(MoonBlock e) {
            //                super.visitBlock(e);
            //
            //                check(e, holder);
            //            }

            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
                if (! (file instanceof MoonPsiFile))
                    return;

                check((MoonControlFlowOwner) file, holder);
            }
        };
    }


    protected void check(MoonControlFlowOwner owner, ProblemsHolder problemsHolder) {
        Instruction[] flow = owner.getControlFlow();
        ReadWriteVariableInstruction[] reads = ControlFlowUtil.getReadsWithoutPriorWrites(flow);
        for (ReadWriteVariableInstruction read : reads) {
            PsiElement element = read.getElement();
            if (element instanceof MoonReferenceElement) {
                if (((MoonReferenceElement) element).getElement() instanceof MoonGlobal)
                    if (((MoonReferenceElement) element).multiResolve(false).length == 0) {
                        problemsHolder.registerProblem(element, "Unassigned variable usage",
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
            }
        }
    }
}
