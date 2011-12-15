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
package com.eightbitmage.moonscript.editor.inspections.usage;

import com.eightbitmage.moonscript.editor.inspections.AbstractInspection;
import com.eightbitmage.moonscript.lang.psi.MoonControlFlowOwner;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.controlFlow.Instruction;
import com.eightbitmage.moonscript.lang.psi.controlFlow.ReadWriteVariableInstruction;
import com.eightbitmage.moonscript.lang.psi.dataFlow.DFAEngine;
import com.eightbitmage.moonscript.lang.psi.dataFlow.reachingDefs.ReachingDefinitionsDfaInstance;
import com.eightbitmage.moonscript.lang.psi.dataFlow.reachingDefs.ReachingDefinitionsSemilattice;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonLocal;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntProcedure;
import gnu.trove.TObjectProcedure;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;

/**
 & @author ven
 */
public class UnusedDefInspection extends AbstractInspection {
  private static final Logger log = Logger.getInstance("Lua.UnusedDefInspection");


    @Override
    public String getStaticDescription() {
        return "Variable is not used";
    }

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return "Data Flow Issues";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Unused Assignment";
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "MoonUnusedAssignment";
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

                check((MoonControlFlowOwner) file, holder);
            }
        };
    }    


  protected void check(final MoonControlFlowOwner owner, final ProblemsHolder problemsHolder) {
    final Instruction[] flow = owner.getControlFlow();
    final ReachingDefinitionsDfaInstance dfaInstance = new ReachingDefinitionsDfaInstance(flow);
    final ReachingDefinitionsSemilattice lattice = new ReachingDefinitionsSemilattice();
    final DFAEngine<TIntObjectHashMap<TIntHashSet>> engine = new DFAEngine<TIntObjectHashMap<TIntHashSet>>(flow, dfaInstance, lattice);
    final ArrayList<TIntObjectHashMap<TIntHashSet>> dfaResult = engine.performDFA();
    final TIntHashSet unusedDefs = new TIntHashSet();
    for (Instruction instruction : flow) {
      if (instruction instanceof ReadWriteVariableInstruction && ((ReadWriteVariableInstruction) instruction).isWrite()) {
        unusedDefs.add(instruction.num());
      }
    }

    for (int i = 0; i < dfaResult.size(); i++) {
      final Instruction instruction = flow[i];
      if (instruction instanceof ReadWriteVariableInstruction) {
        final ReadWriteVariableInstruction varInsn = (ReadWriteVariableInstruction) instruction;
        if (!varInsn.isWrite()) {
          final String varName = varInsn.getVariableName();
          TIntObjectHashMap<TIntHashSet> e = dfaResult.get(i);
          e.forEachValue(new TObjectProcedure<TIntHashSet>() {
            public boolean execute(TIntHashSet reaching) {
              reaching.forEach(new TIntProcedure() {
                public boolean execute(int defNum) {
                  final String defName = ((ReadWriteVariableInstruction) flow[defNum]).getVariableName();
                  if (varName.equals(defName)) {
                    unusedDefs.remove(defNum);
                  }
                  return true;
                }
              });
              return true;
            }
          });
        }
      }
    }

    unusedDefs.forEach(new TIntProcedure() {
      public boolean execute(int num) {
        final ReadWriteVariableInstruction instruction = (ReadWriteVariableInstruction)flow[num];
        final PsiElement element = instruction.getElement();
        if (element == null) return true;
        PsiElement toHighlight = null;
        if (isLocalAssignment(element)) {
          if (element instanceof MoonReferenceElement) {
            PsiElement parent = element.getParent();
            if (parent instanceof MoonReferenceElement) {
              toHighlight = ((MoonAssignmentStatement)parent).getLeftExprs();
            }
//            if (parent instanceof GrPostfixExpression) {
//              toHighlight = parent;
//            }
          }
          else if (element instanceof MoonSymbol) {
            toHighlight = ((MoonSymbol)element);//.getNamedElement();
          }
          if (toHighlight == null) toHighlight = element;
          problemsHolder.registerProblem(toHighlight, "Unused Assignment",
                                         ProblemHighlightType.LIKE_UNUSED_SYMBOL);
        }
        return true;
      }
    });
  }

  private boolean isUsedInToplevelFlowOnly(PsiElement element) {
    MoonSymbol var = null;
    if (element instanceof MoonSymbol) {
      var = (MoonSymbol) element;
    } else if (element instanceof MoonReferenceElement) {
      final PsiElement resolved = ((MoonReferenceElement) element).resolve();
      if (resolved instanceof MoonSymbol) var = (MoonSymbol) resolved;
    }

    if (var != null) {
      final MoonPsiElement scope = getScope(var);
      if (scope == null) {
        PsiFile file = var.getContainingFile();
        log.error(file == null ? "no file???" : DebugUtil.psiToString(file, true, false));
      }

      return ReferencesSearch.search(var, new LocalSearchScope(scope)).forEach(new Processor<PsiReference>() {
        public boolean process(PsiReference ref) {
          return getScope(ref.getElement()) == scope;
        }
      });
    }

    return true;
  }

  private MoonPsiElement getScope(PsiElement var) {
    return PsiTreeUtil.getParentOfType(var, MoonBlock.class, MoonPsiFile.class);
  }

  private boolean isLocalAssignment(PsiElement element) {
    if (element instanceof MoonSymbol) {
      return isLocalVariable((MoonSymbol) element, false);
    } else if (element instanceof MoonReferenceElement) {
      final PsiElement resolved = ((MoonReferenceElement) element).resolve();
      return resolved instanceof MoonSymbol && isLocalVariable((MoonSymbol) resolved, true);
    }

    return false;
  }

  private boolean isLocalVariable(MoonSymbol var, boolean parametersAllowed) {
    if (var instanceof MoonLocal) return true;
    else if (var instanceof MoonParameter && !parametersAllowed) return false;

    return false;
  }

  public boolean isEnabledByDefault() {
    return false;
  }
}
