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

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFileBase;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.impl.PsiUtil;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.refactoring.MoonRefactoringUtil;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.eightbitmage.moonscript.lang.psi.statements.MoonGenericForStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonWhileStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim.Medvedev
 */
public abstract class MoonIntroduceHandlerBase<Settings extends MoonIntroduceSettings> implements RefactoringActionHandler {
  protected abstract String getRefactoringName();

  protected abstract String getHelpID();

  @NotNull
  protected abstract PsiElement findScope(MoonExpression expression, MoonSymbol variable);

  protected abstract void checkExpression(MoonExpression selectedExpr) throws MoonIntroduceRefactoringError;

  protected abstract void checkVariable(MoonSymbol variable) throws MoonIntroduceRefactoringError;

  protected abstract void checkOccurrences(PsiElement[] occurrences);

  protected abstract MoonIntroduceDialog<Settings> getDialog(MoonIntroduceContext context);

  @Nullable
  public abstract MoonSymbol runRefactoring(MoonIntroduceContext context, Settings settings);

  public static List<MoonExpression> collectExpressions(final PsiFile file, final Editor editor, final int offset) {
    int correctedOffset = correctOffset(editor, offset);
    final PsiElement elementAtCaret = file.findElementAt(correctedOffset);
    final List<MoonExpression> expressions = new ArrayList<MoonExpression>();

    for (MoonExpression expression = PsiTreeUtil.getParentOfType(elementAtCaret, MoonExpression.class);
         expression != null;
         expression = PsiTreeUtil.getParentOfType(expression, MoonExpression.class)) {
      if (expressions.contains(expression)) continue;
      if (expressionIsNotCorrect(expression)) continue;

      expressions.add(expression);
    }
    return expressions;
  }

  private static boolean expressionIsNotCorrect(MoonExpression expression) {
      if (expression instanceof MoonReferenceElement) return true;
      if (expression instanceof MoonExpressionList) return true;
      if (expression instanceof MoonIdentifierList) return true;
//    if (expression instanceof GrSuperReferenceExpression) return true;
//    if (expression.getType() == PsiType.VOID) return true;
//    if (expression instanceof GrAssignmentExpression) return true;
//    if (expression instanceof GrReferenceExpression && expression.getParent() instanceof GrCall) {
//      final PsiElement resolved = ((GrReferenceExpression)expression).resolve();
//      return resolved instanceof PsiMethod || resolved instanceof PsiClass;
//    }
//    if (expression instanceof GrApplicationStatement) {
//      return !PsiUtil.isExpressionStatement(expression);
//    }
//    if (expression instanceof GrClosableBlock && expression.getParent() instanceof GrStringInjection) return true;

    return false;
  }

  private static int correctOffset(Editor editor, int offset) {
    Document document = editor.getDocument();
    CharSequence text = document.getCharsSequence();
    int correctedOffset = offset;
    int textLength = document.getTextLength();
    if (offset >= textLength) {
      correctedOffset = textLength - 1;
    }
    else if (!Character.isJavaIdentifierPart(text.charAt(offset))) {
      correctedOffset--;
    }
    if (correctedOffset < 0) {
      correctedOffset = offset;
    }
    else if (!Character.isJavaIdentifierPart(text.charAt(correctedOffset))) {
      if (text.charAt(correctedOffset) == ';') {//initially caret on the end of line
        correctedOffset--;
      }
      if (text.charAt(correctedOffset) != ')') {
        correctedOffset = offset;
      }
    }
    return correctedOffset;
  }

  @Nullable
  private static MoonSymbol findVariableAtCaret(final PsiFile file, final Editor editor, final int offset) {
    final int correctOffset = correctOffset(editor, offset);
    final PsiElement elementAtCaret = file.findElementAt(correctOffset);
    final MoonSymbol variable = PsiTreeUtil.getParentOfType(elementAtCaret, MoonSymbol.class);
    if (variable != null && variable.getTextRange().contains(correctOffset)) return variable;
    return null;
  }

  public void invoke(final @NotNull Project project, final Editor editor, final PsiFile file, final @Nullable DataContext dataContext) {
    final SelectionModel selectionModel = editor.getSelectionModel();
    if (!selectionModel.hasSelection()) {
      final int offset = editor.getCaretModel().getOffset();

      final List<MoonExpression> expressions = collectExpressions(file, editor, offset);
      if (expressions.isEmpty()) {
        final MoonSymbol variable = findVariableAtCaret(file, editor, offset);
        if (variable == null || variable instanceof MoonParameter) {
          selectionModel.selectLineAtCaret();
        }
        else {
          final TextRange textRange = variable.getTextRange();
          selectionModel.setSelection(textRange.getStartOffset(), textRange.getEndOffset());
        }
      }
      else if (expressions.size() == 1) {
        final TextRange textRange = expressions.get(0).getTextRange();
        selectionModel.setSelection(textRange.getStartOffset(), textRange.getEndOffset());
      }
      else {
        IntroduceTargetChooser.showChooser(editor, expressions,
                                           new Pass<MoonExpression>() {
                                             public void pass(final MoonExpression selectedValue) {
                                               invoke(project, editor, file, selectedValue.getTextRange().getStartOffset(),
                                                      selectedValue.getTextRange().getEndOffset());
                                             }
                                           },
                                           new Function<MoonExpression, String>() {
                                             @Override
                                             public String fun(MoonExpression moonExpression) {
                                               return moonExpression.getText();
                                             }
                                           });
        return;
      }
    }
    invoke(project, editor, file, selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
    // Does nothing
  }

  public MoonIntroduceContext getContext(Project project, Editor editor, MoonExpression expression, @Nullable MoonSymbol variable) {
    final PsiElement scope = findScope(expression, variable);

    if (variable == null) {
      final PsiElement[] occurences = findOccurences(expression, scope);
      return new MoonIntroduceContext(project, editor, expression, occurences, scope, variable);

    }
    else {
//      final List<PsiElement> list = Collections.synchronizedList(new ArrayList<PsiElement>());
//      ReferencesSearch.search(variable, new LocalSearchScope(scope)).forEach(new Processor<PsiReference>() {
//        @Override
//        public boolean process(PsiReference psiReference) {
//          final PsiElement element = psiReference.getElement();
//          if (element != null) {
//            list.add(element);
//          }
//          return true;
//        }
//      });
        final PsiElement[] occurences = findOccurences(variable, scope);
//      return new MoonIntroduceContext(project, editor, variable, list.toArray(new PsiElement[list.size()]), scope,
//                                    variable);
      return new MoonIntroduceContext(project, editor, variable, occurences, scope,
                                    variable);
    }
  }

    protected PsiElement[] findOccurences(MoonExpression expression, PsiElement scope) {
        final PsiElement expr = PsiUtil.skipParentheses(expression, false);
        assert expr != null;
        final PsiElement[] occurrences = MoonRefactoringUtil.getExpressionOccurrences(expr, scope);
        if (occurrences == null || occurrences.length == 0) {
            throw new MoonIntroduceRefactoringError("No occurances found");
        }
        return occurrences;
    }

  private boolean invoke(final Project project, final Editor editor, PsiFile file, int startOffset, int endOffset) {
    try {
      PsiDocumentManager.getInstance(project).commitAllDocuments();
      if (!(file instanceof MoonPsiFileBase)) {
        throw new MoonIntroduceRefactoringError("Only Lua files");
      }
      if (!CommonRefactoringUtil.checkReadOnlyStatus(project, file)) {
        throw new MoonIntroduceRefactoringError("Read-only occurances found");
      }

      MoonExpression selectedExpr = findExpression((MoonPsiFileBase)file, startOffset, endOffset);
      final MoonSymbol variable = findVariable((MoonPsiFile)file, startOffset, endOffset);
      if (variable != null) {
        checkVariable(variable);
      }
      else if (selectedExpr != null) {
        checkExpression(selectedExpr);
      }
      else {
        throw new MoonIntroduceRefactoringError(null);
      }

      final MoonIntroduceContext context = getContext(project, editor, selectedExpr, variable);
      checkOccurrences(context.occurrences);
      final Settings settings = showDialog(context);
      if (settings == null) return false;

      CommandProcessor.getInstance().executeCommand(context.project, new Runnable() {
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Computable<MoonSymbol>() {
          public MoonSymbol compute() {
            return runRefactoring(context, settings);
          }
        });
      }
    }, getRefactoringName(), null);

      return true;
    }
    catch (MoonIntroduceRefactoringError e) {
      CommonRefactoringUtil
        .showErrorHint(project, editor, RefactoringBundle.getCannotRefactorMessage(e.getMessage()), getRefactoringName(), getHelpID());
      return false;
    }
  }

  @Nullable
  private static MoonSymbol findVariable(MoonPsiFile file, int startOffset, int endOffset) {
    MoonSymbol var = MoonRefactoringUtil.findElementInRange(file, startOffset, endOffset, MoonSymbol.class);
    if (var == null) {
      final MoonDeclarationStatement variableDeclaration =
        MoonRefactoringUtil.findElementInRange(file, startOffset, endOffset, MoonDeclarationStatement.class);
      if (variableDeclaration == null) return null;
      final MoonSymbol[] variables = variableDeclaration.getDefinedSymbols();
      if (variables.length == 1) {
        var = variables[0];
      }
    }
    if (var instanceof MoonParameter) {
      return null;
    }
    return var;
  }

  @Nullable
  public static MoonExpression findExpression(MoonPsiFileBase file, int startOffset, int endOffset) {
    MoonExpression selectedExpr = MoonRefactoringUtil.findElementInRange(file, startOffset, endOffset, MoonExpression.class);
    if (selectedExpr == null) return null;
//    PsiType type = selectedExpr.getType();
//    if (type != null) type = TypeConversionUtil.erasure(type);
//
//    if (PsiType.VOID.equals(type)) {
//      throw new GrIntroduceRefactoringError(LuaRefactoringBundle.message("selected.expression.has.void.type"));
//    }
//
//    if (expressionIsNotCorrect(selectedExpr)) {
//      throw new GrIntroduceRefactoringError(LuaRefactoringBundle.message("selected.block.should.represent.an.expression"));
//    }

    return selectedExpr;
  }

  @Nullable
  private Settings showDialog(MoonIntroduceContext context) {

    // Add occurences highlighting
    ArrayList<RangeHighlighter> highlighters = new ArrayList<RangeHighlighter>();
    HighlightManager highlightManager = null;
    if (context.editor != null) {
      highlightManager = HighlightManager.getInstance(context.project);
      EditorColorsManager colorsManager = EditorColorsManager.getInstance();
      TextAttributes attributes = colorsManager.getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
      if (context.occurrences.length > 1) {
        highlightManager.addOccurrenceHighlights(context.editor, context.occurrences, attributes, true, highlighters);
      }
    }

    MoonIntroduceDialog<Settings> dialog = getDialog(context);

    dialog.show();
    if (dialog.isOK()) {
      if (context.editor != null) {
        assert highlightManager != null : "highlight manager is null";
        for (RangeHighlighter highlighter : highlighters) {
          highlightManager.removeSegmentHighlighter(context.editor, highlighter);
        }
      }
      return dialog.getSettings();
    }
    else {
      if (context.occurrences.length > 1) {
        WindowManager.getInstance().getStatusBar(context.project)
          .setInfo("Press escape to remove highlighting");
      }
    }
    return null;
  }

  @Nullable
  public static PsiElement findAnchor(MoonIntroduceContext context,
                                       MoonIntroduceSettings settings,
                                       PsiElement[] occurrences,
                                       final PsiElement container) {
    if (occurrences.length == 0) return null;
    PsiElement candidate;
    if (occurrences.length == 1 || !settings.replaceAllOccurrences()) {
      candidate = context.expression;
    }
    else {
      MoonRefactoringUtil.sortOccurrences(occurrences);
      candidate = occurrences[0];
    }
    while (candidate != null && !container.equals(candidate.getParent())) {
      candidate = candidate.getParent();
    }
    if (candidate == null) {
      return null;
    }
    if ((container instanceof MoonWhileStatement) &&
        candidate.equals(((MoonWhileStatement)container).getCondition())) {
      return container;
    }
    if ((container instanceof MoonIfThenStatement) &&
        candidate.equals(((MoonIfThenStatement)container).getIfCondition())) {
      return container;
    }
    if ((container instanceof MoonGenericForStatement) &&
        candidate.equals(((MoonGenericForStatement)container).getInClause())) {
      return container;
    }
    return candidate;
  }

//  protected static void deleteLocalVar(MoonIntroduceContext context) {
//    final MoonSymbol resolved = GrIntroduceFieldHandler.resolveLocalVar(context);
//    final PsiElement parent = resolved.getParent();
//    if (parent instanceof GrTupleDeclaration) {
//      if (((GrTupleDeclaration)parent).getVariables().length == 1) {
//        parent.getParent().delete();
//      }
//      else {
//        final MoonExpression initializerLua = resolved.getInitializerLua();
//        if (initializerLua != null) initializerLua.delete();
//        resolved.delete();
//      }
//    }
//    else {
//      if (((LuaSymbolDeclaration)parent).getVariables().length == 1) {
//        parent.delete();
//      }
//      else {
//        resolved.delete();
//      }
//    }
//  }

  protected static MoonSymbol resolveLocalVar(MoonIntroduceContext context) {
    if (context.var != null) return context.var;
    return (MoonSymbol)((MoonReferenceElement)context.expression).resolve();
  }

//  public static boolean hasLhs(final PsiElement[] occurrences) {
//    for (PsiElement element : occurrences) {
//      if (element instanceof GrReferenceExpression) {
//        if (PsiUtil.isLValue((MoonPsiElement)element)) return true;
//        if (ControlFlowUtils.isIncOrDecOperand((GrReferenceExpression)element)) return true;
//      }
//    }
//    return false;
//  }
//
//
  public interface Validator  {
    boolean isOK(MoonIntroduceDialog dialog);
  }
}
