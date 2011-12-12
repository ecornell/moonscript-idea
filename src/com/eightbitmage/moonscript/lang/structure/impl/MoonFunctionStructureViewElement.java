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
package com.eightbitmage.moonscript.lang.structure.impl;

import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.structure.MoonStructureViewTreeElement;
import com.eightbitmage.moonscript.lang.structure.itemsPresentations.impl.MoonFunctionItemPresentation;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;

public class MoonFunctionStructureViewElement extends MoonStructureViewTreeElement {

  public MoonFunctionStructureViewElement(MoonPsiElement element) {
    super(element);
  }

  public ItemPresentation getPresentation() {
    return new MoonFunctionItemPresentation((MoonFunctionDefinition) myElement);
  }

  public TreeElement[] getChildren() {
    return StructureViewTreeElement.EMPTY_ARRAY;
  }


}
