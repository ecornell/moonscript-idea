/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang.psi.resolve;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonGlobalDeclarationIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author ilyas
 */
public abstract class ResolveUtil {

  public static boolean treeWalkUp(PsiElement place, PsiScopeProcessor processor) {
    PsiElement lastParent = null;
    PsiElement run = place;
    while (run != null) {
      if (!run.processDeclarations(processor, ResolveState.initial(), lastParent, place)) return false;
      lastParent = run;
      run = run.getContext(); //same as getParent
    }

    return true;
  }

  public static boolean processChildren(PsiElement element, PsiScopeProcessor processor,
                                        ResolveState substitutor, PsiElement lastParent, PsiElement place) {
    PsiElement run = lastParent == null ? element.getLastChild() : lastParent.getPrevSibling();
    while (run != null) {
      if (PsiTreeUtil.findCommonParent(place, run) != run && !run.processDeclarations(processor, substitutor, lastParent, place)) return false;
      run = run.getPrevSibling();
    }

    return true;
  }

//  public static boolean processElement(PsiScopeProcessor processor, PsiNamedElement namedElement) {
//    if (namedElement == null) return true;
//    NameHint nameHint = processor.getHint(NameHint.KEY);
//    String name = nameHint == null ? null : nameHint.getName(ResolveState.initial());
//    if (name == null || name.equals(namedElement.getName())) {
//      return processor.execute(namedElement, ResolveState.initial());
//    }
//    return true;
//  }

  public static PsiElement[] mapToElements(MoonResolveResult[] candidates) {
    PsiElement[] elements = new PsiElement[candidates.length];
    for (int i = 0; i < elements.length; i++) {
      elements[i] = candidates[i].getElement();
    }

    return elements;
  }


    public static Collection<String> getFilteredGlobals(Project project, GlobalSearchScope scope) {
        MoonGlobalDeclarationIndex index = MoonGlobalDeclarationIndex.getInstance();
        Collection<String> names = index.getAllKeys(project);
        Collection<String> rejects = new LinkedList<String>();
        for (String name : names) {
            Collection<MoonDeclarationExpression> elems = index.get(name, project, scope);
            if (elems.size() == 0)
                rejects.add(name);
        }

        names.removeAll(rejects);
        return names;
    }

}
