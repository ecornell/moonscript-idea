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

package com.eightbitmage.moonscript.refactoring.introduce;

import com.eightbitmage.moonscript.lang.psi.MoonPsiElementFactory;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFileBase;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonConditionalLoop;
import com.eightbitmage.moonscript.lang.psi.statements.MoonDeclarationStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.util.MoonStatementOwner;
import com.eightbitmage.moonscript.refactoring.MoonRefactoringUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author ilyas
 */
public abstract class MoonIntroduceVariableBase extends MoonIntroduceHandlerBase<MoonIntroduceVariableSettings> {

  private static final Logger log =
    Logger.getInstance("Lua.IntroduceVariableBase");
  protected static String REFACTORING_NAME = "Introduce Variable";
    public static final String IDLUAREFACTORTMP = "_______IDLUAREFACTORTMP";
    private PsiElement positionElement = null;

  @NotNull
  @Override
  protected PsiElement findScope(MoonExpression selectedExpr, MoonSymbol variable) {

    // Get container element
    final PsiElement scope = MoonRefactoringUtil.getEnclosingContainer(selectedExpr);
//    if (scope == null || !(scope instanceof :PsiElement)) {
//      throw new GrIntroduceRefactoringError(
//        GroovyRefactoringBundle.message("refactoring.is.not.supported.in.the.current.context", REFACTORING_NAME));
//    }
//    if (!GroovyRefactoringUtil.isAppropriateContainerForIntroduceVariable(scope)) {
//      throw new GrIntroduceRefactoringError(
//        GroovyRefactoringBundle.message("refactoring.is.not.supported.in.the.current.context", REFACTORING_NAME));
//    }
    return scope;
  }

  protected void checkExpression(MoonExpression selectedExpr) {
    // Cannot perform refactoring in parameter default values

    PsiElement parent = selectedExpr.getParent();
    while (!(parent == null || parent instanceof MoonPsiFileBase || parent instanceof MoonParameter)) {
      parent = parent.getParent();
    }

//    if (checkInFieldInitializer(selectedExpr)) {
//      throw new GrIntroduceRefactoringError(GroovyRefactoringBundle.message("refactoring.is.not.supported.in.the.current.context"));
//    }

    if (parent instanceof MoonParameter) {
      throw new MoonIntroduceRefactoringError("Refactoring is not supported in parameters");
    }
  }

  @Override
  protected void checkVariable(MoonSymbol variable) throws MoonIntroduceRefactoringError {
    if (variable instanceof MoonCompoundIdentifier && !((MoonCompoundIdentifier) variable).isCompoundDeclaration())
        return;

   throw new MoonIntroduceRefactoringError(null);
  }

  @Override
  protected void checkOccurrences(PsiElement[] occurrences) {
    //nothing to do
  }

//  private static boolean checkInFieldInitializer(MoonExpression expr) {
//    PsiElement parent = expr.getParent();
//    if (parent instanceof GrClosableBlock) {
//      return false;
//    }
//    if (parent instanceof GrField && expr == ((GrField)parent).getInitializerGroovy()) {
//      return true;
//    }
//    if (parent instanceof MoonExpression) {
//      return checkInFieldInitializer(((MoonExpression)parent));
//    }
//    return false;
//  }

  /**
   * Inserts new variable declaratrions and replaces occurrences
   */
  public MoonSymbol runRefactoring(final MoonIntroduceContext context, final MoonIntroduceVariableSettings settings) {
    // Generating varibable declaration

    final MoonPsiElementFactory factory = MoonPsiElementFactory.getInstance(context.project);

    MoonDeclarationStatement varDecl = null;

    if (context.var == null) {
        if (settings.isLocal())
            varDecl = (MoonDeclarationStatement) factory.createStatementFromText(
                    "local " + settings.getName() + " = " + context.expression.getText());
        else
            varDecl = (MoonDeclarationStatement) factory.createStatementFromText(
                    settings.getName() + " = " + context.expression.getText());
    } else {
        varDecl = (MoonDeclarationStatement) factory.createStatementFromText((settings.isLocal() ? "local " : "") + settings.getName() + " = " + IDLUAREFACTORTMP);

    }
//      = factory.createVariableDeclaration(settings.isDeclareFinal() ? new String[]{PsiModifier.FINAL} : null,
//                                 (MoonExpression)PsiUtil.skipParentheses(context.expression, false), settings.getSelectedType(),
//                                 settings.getName());

    // Marker for caret posiotion
    try {
        MoonExpression firstOccurrence;

      if (context.var == null) {
          /* insert new variable */
          MoonRefactoringUtil.sortOccurrences(context.occurrences);
          if (context.occurrences.length == 0 || !(context.occurrences[0] instanceof MoonExpression)) {
            throw new IncorrectOperationException("Wrong expression occurrence");
          }


      }
        
      if (settings.replaceAllOccurrences()) {
        firstOccurrence = ((MoonExpression)context.occurrences[0]);
      }
      else {
        firstOccurrence = context.expression;
      }

      //resolveLocalConflicts(context.scope, varDecl.getVariables()[0].getName());
      // Replace at the place of first occurrence

      MoonSymbol insertedVar = replaceFirstAssignmentStatement(firstOccurrence, context, varDecl);
      boolean alreadyDefined = insertedVar != null;
      if (insertedVar == null) {
        // Insert before first occurrence

        if (context.var != null)
            substituteInitializerExpression((MoonExpression) context.var.copy(), varDecl);

        assert varDecl.getDefinedSymbols().length > 0;

        insertedVar = insertVariableDefinition(context, settings, varDecl);
      }

//      insertedVar.setType(settings.getSelectedType());

      //Replace other occurrences
      MoonSymbol refExpr = createReferenceSymbol(settings, factory);
      if (settings.replaceAllOccurrences()) {
        ArrayList<PsiElement> replaced = new ArrayList<PsiElement>();
        for (PsiElement occurrence : context.occurrences) {
          if (!(alreadyDefined && firstOccurrence.equals(occurrence))) {
            if (occurrence instanceof MoonExpression) {
              MoonExpression element = (MoonExpression)occurrence;
              if (element.getParent() instanceof MoonReferenceElement)
                  element = (MoonExpression) element.getParent();

              replaced.add(element.replaceWithExpression(refExpr, true));
              // For caret position
              if (occurrence.equals(context.expression)) {
                refreshPositionMarker(replaced.get(replaced.size() - 1));
              }
              refExpr = createReferenceSymbol(settings, factory);
            }
            else {
              throw new IncorrectOperationException("Expression occurrence to be replaced is not instance of GroovyPsiElement");
            }
          }
        }
        if (context.editor != null) {
          // todo implement it...
//              final PsiElement[] replacedOccurrences = replaced.toArray(new PsiElement[replaced.size()]);
//              highlightReplacedOccurrences(myProject, editor, replacedOccurrences);
        }
      }
      else {
        if (!alreadyDefined) {
          refreshPositionMarker(context.expression.replaceWithExpression(refExpr, true));
        }
      }


      // Setting caret to logical position
      if (context.editor != null && getPositionMarker() != null) {
        context.editor.getCaretModel().moveToOffset(getPositionMarker().getTextRange().getEndOffset());
        context.editor.getSelectionModel().removeSelection();
      }
      return insertedVar;
    }
    catch (IncorrectOperationException e) {
      log.error(e);
    }
    return null;
  }

    private void substituteInitializerExpression(MoonExpression expression, MoonDeclarationStatement varDecl) {
        int markerPos = varDecl.getText().indexOf(IDLUAREFACTORTMP);
        MoonExpression fakeInitializer = PsiTreeUtil
                .findElementOfClassAtOffset(varDecl.getContainingFile(), markerPos, MoonExpression.class, false);

        assert fakeInitializer.getText().equals(IDLUAREFACTORTMP);

        if (fakeInitializer instanceof MoonExpressionList)
            fakeInitializer = ((MoonExpressionList) fakeInitializer).getMoonExpressions().get(0);
        
        if (fakeInitializer.getParent() instanceof MoonReferenceElement)
            fakeInitializer = (MoonExpression) fakeInitializer.getParent();

        fakeInitializer.replace(expression);
    }

    private MoonSymbol createReferenceSymbol(MoonIntroduceVariableSettings settings, MoonPsiElementFactory factory) {
        MoonSymbol symbol = settings.isLocal() ? factory.createLocalNameIdentifier(settings.getName()) :
        factory.createGlobalNameIdentifier(settings.getName());

        if (! (symbol instanceof MoonReferenceElement))
            symbol = (MoonSymbol) symbol.getParent();

        return symbol;
    }

    private static void resolveLocalConflicts(PsiElement tempContainer, String varName) {
    for (PsiElement child : tempContainer.getChildren()) {
//      if (child instanceof MoonReferenceElement && !child.getText().contains(".")) {
//        PsiReference psiReference = child.getReference();
//        if (psiReference != null) {
//          final PsiElement resolved = psiReference.resolve();
//          if (resolved != null) {
//            String fieldName = getFieldName(resolved);
//            if (fieldName != null && varName.equals(fieldName)) {
//              MoonPsiElementFactory factory = MoonPsiElementFactory.getInstance(tempContainer.getProject());
//              ((MoonReferenceElement)child).replaceWithExpression(factory.createExpressionFromText("this." + child.getText()), true);
//            }
//          }
//        }
//      }
//      else {
        resolveLocalConflicts(child, varName);
//      }
    }
  }

//  @Nullable
//  private static String getFieldName(PsiElement element) {
//    if (element instanceof GrAccessorMethod) element = ((GrAccessorMethod)element).getProperty();
//    return element instanceof GrField ? ((GrField)element).getName() : null;
//  }

  private void refreshPositionMarker(PsiElement position) {
    if (positionElement == null && position != null) {
      positionElement = position;
    }
  }

  private PsiElement getPositionMarker() {
    return positionElement;
  }

  private MoonSymbol insertVariableDefinition(MoonIntroduceContext context,
                                              MoonIntroduceVariableSettings settings,
                                              MoonDeclarationStatement varDecl) throws IncorrectOperationException {
    log.assertTrue(context.occurrences.length > 0);

    MoonStatementElement anchorElement = (MoonStatementElement)findAnchor(context, settings, context.occurrences, context.scope);
    log.assertTrue(anchorElement != null);
    PsiElement realContainer = anchorElement.getParent();

    assert MoonRefactoringUtil.isAppropriateContainerForIntroduceVariable(realContainer);

    if (!(realContainer instanceof MoonConditionalLoop)) {
      if (realContainer instanceof MoonStatementOwner) {
        MoonStatementOwner block = (MoonStatementOwner)realContainer;
        varDecl = (MoonDeclarationStatement)block.addStatementBefore(varDecl, anchorElement);

        block.addAfter(MoonPsiElementFactory.getInstance(context.project).createWhiteSpaceFromText("\n"), varDecl);
      }
    }
//    else {
//      // To replace branch body correctly
//      String refId = varDecl.getVariables()[0].getName();
//      GrBlockStatement newBody;
//      final GroovyPsiElementFactory factory = GroovyPsiElementFactory.getInstance(context.project);
//      if (anchorElement.equals(context.expression)) {
//        newBody = factory.createBlockStatement(varDecl);
//      }
//      else {
//        replaceExpressionOccurrencesInStatement(anchorElement, context.expression, refId, settings.replaceAllOccurrences());
//        newBody = factory.createBlockStatement(varDecl, anchorElement);
//      }
//
//      varDecl = (LuaSymbolDeclaration)newBody.getBlock().getStatements()[0];
//
//      GrCodeBlock tempBlock = ((GrBlockStatement)((GrLoopStatement)realContainer).replaceBody(newBody)).getBlock();
//      refreshPositionMarker(tempBlock.getStatements()[tempBlock.getStatements().length - 1]);
//    }

    return varDecl.getDefinedSymbols()[0];
  }

  private static void replaceExpressionOccurrencesInStatement(MoonStatementElement stmt,
                                                              MoonExpression expr,
                                                              String refText,
                                                              boolean replaceAllOccurrences)
    throws IncorrectOperationException {
    MoonPsiElementFactory factory = MoonPsiElementFactory.getInstance(stmt.getProject());
    MoonExpression refExpr = factory.createExpressionFromText(refText);
    if (!replaceAllOccurrences) {
      expr.replaceWithExpression(refExpr, true);
    }
    else {
      PsiElement[] occurrences = MoonRefactoringUtil.getExpressionOccurrences(expr, stmt);
      for (PsiElement occurrence : occurrences) {
        if (occurrence instanceof MoonExpression) {
          MoonExpression moonExpression = (MoonExpression)occurrence;
          moonExpression.replaceWithExpression(refExpr, true);
          refExpr = factory.createExpressionFromText(refText);
        }
        else {
          throw new IncorrectOperationException();
        }
      }
    }
  }

    /**
     * Replaces an expression occurrence by appropriate variable declaration
     */
    @Nullable
    private MoonSymbol replaceFirstAssignmentStatement(@NotNull MoonExpression expr, MoonIntroduceContext context,
                                                      @NotNull MoonDeclarationStatement definition) throws
            IncorrectOperationException {


        MoonAssignmentStatement assign =
                PsiTreeUtil.getParentOfType(expr, MoonAssignmentStatement.class, true, MoonStatementElement.class);
        if (assign != null) {
            MoonSymbol symbol = assign.getLeftExprs().getSymbols()[0];
            if (symbol instanceof MoonReferenceElement)
                symbol = (MoonSymbol) ((MoonReferenceElement) symbol).getElement();
            
            boolean isSymbolAssinedto = false;
            for (PsiElement element : context.occurrences)
                if (element.equals(symbol)) isSymbolAssinedto = true;

            if (isSymbolAssinedto) {
                // for now we only support single assignments when we are replacing an assignment with a variable
                // definition
                if (assign.getLeftExprs().count() != 1 && assign.getRightExprs().count() != 1) return null;

                substituteInitializerExpression(
                        (MoonExpression) assign.getRightExprs().getMoonExpressions().get(0).copy(), definition);

                definition = (MoonDeclarationStatement) assign.replaceWithStatement(definition);

                if (expr.equals(context.expression)) {
                    refreshPositionMarker(definition);
                }
                return definition.getDefinedSymbols()[0];
            }
        }
        return null;
    }
}
