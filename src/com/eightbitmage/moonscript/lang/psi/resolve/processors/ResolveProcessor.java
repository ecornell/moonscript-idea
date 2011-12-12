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

package com.eightbitmage.moonscript.lang.psi.resolve.processors;


import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResultImpl;
import com.intellij.psi.scope.PsiScopeProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ilyas
 */
public abstract class ResolveProcessor implements PsiScopeProcessor/*, NameHint, ElementClassHint*/ {
  protected static final MoonResolveResult[] EMPTY_SET = new MoonResolveResultImpl[0];
  protected Set<MoonResolveResult> myCandidates = new HashSet<MoonResolveResult>();
  protected final String myName;

  public ResolveProcessor(String myName) {
    this.myName = myName;
  }

  public MoonResolveResult[] getCandidates() {
    if (!hasCandidates())
        return EMPTY_SET;

    return myCandidates.toArray(new MoonResolveResult[myCandidates.size()]);
  }

  public void addCandidate(MoonResolveResult candidate) {
      myCandidates.add(candidate);
  }
//  public <T> T getHint(Class<T> hintClass) {
//    if (NameHint.class == hintClass && myName != null) {
//      return (T) this;
//    } else if (ElementClassHint.class == hintClass) {
//      return (T) this;
//    }
//
//    return null;
//  }

  public void handleEvent(Event event, Object o) {
  }

  public boolean hasCandidates() {
    return myCandidates.size() > 0;
  }

}
