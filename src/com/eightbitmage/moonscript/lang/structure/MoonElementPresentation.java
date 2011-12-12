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
package com.eightbitmage.moonscript.lang.structure;

import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonParameterList;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.intellij.psi.PsiElement;

public class MoonElementPresentation {
  public static String getPresentableText(PsiElement element) {
    assert element != null;

    if (element instanceof MoonPsiFile) {
      return getFilePresentableText(((MoonPsiFile) element));

    } else if (element instanceof MoonFunctionDefinitionStatement) {
        return getFunctionPresentableText((MoonFunctionDefinition) element);
    }
    else {
      return element.getText();
    }
  }

  public static String getFunctionPresentableText(MoonFunctionDefinition function) {

    MoonParameterList o = function.getParameters();

    try {
    String s = function.getName();
    if (s == null) s = "";
    return s + "(" + (o!=null?o.getText():"")+ ")";
    } catch (Throwable e) {
        return e.getMessage()==null?"Anonymous":e.getMessage();
    }
  }

  public static String getFilePresentableText(MoonPsiFile file) {
    return file.getName();
  }

    public static String getFunctionLocationText(MoonFunctionDefinition function) {
        return "";//function.getIdentifier().getNameSpace();
    }
}