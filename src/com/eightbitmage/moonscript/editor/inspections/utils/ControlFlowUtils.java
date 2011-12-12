/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *  
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  
 *   http://www.apache.org/licenses/LICENSE-2.0
 *  
 *   Unless required by applicable law or aLuaeed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.eightbitmage.moonscript.editor.inspections.utils;

import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonConditionalExpression;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings({"OverlyComplexClass"})
public class ControlFlowUtils {

  private ControlFlowUtils() {
    super();
  }

    public static boolean statementMayCompleteNormally(
            @Nullable MoonStatementElement statement) {
        if (statement == null) {
            return true;
        }
        if (statement instanceof MoonBreakStatement ||
                statement instanceof MoonReturnStatement) {
            return false;
        }

        if (statement instanceof MoonGenericForStatement || statement instanceof MoonNumericForStatement) {
            return forStatementMayReturnNormally(statement);
        }

        if (statement instanceof MoonWhileStatement) {
            return whileStatementMayReturnNormally(
                    (MoonWhileStatement) statement);
        }

        if (statement instanceof MoonDoStatement) {
            return blockMayCompleteNormally(((MoonDoStatement) statement).getBlock());
        }

        if (statement instanceof MoonBlock) {
            return blockMayCompleteNormally((MoonBlock) statement);
        }

        if (statement instanceof MoonIfThenStatement) {
            return ifStatementMayReturnNormally((MoonIfThenStatement) statement);
        }
        return true;
    }

    private static boolean whileStatementMayReturnNormally(
      @NotNull MoonWhileStatement loopStatement) {
    final MoonConditionalExpression test = loopStatement.getCondition();
    return !BoolUtils.isTrue(test)
        || statementIsBreakTarget(loopStatement);
  }

  private static boolean forStatementMayReturnNormally(
      @NotNull MoonStatementElement loopStatement) {
    return true;
  }


  private static boolean ifStatementMayReturnNormally(
      @NotNull MoonIfThenStatement ifStatement) {
    final MoonBlock thenBranch = ifStatement.getIfBlock();
    if (blockMayCompleteNormally(thenBranch)) {
      return true;
    }
    final MoonBlock elseBranch = ifStatement.getElseBlock();
    return elseBranch == null ||
        blockMayCompleteNormally(elseBranch);
  }

  public static boolean blockMayCompleteNormally(
      @Nullable MoonBlock block) {
    if (block == null) {
      return true;
    }

    final MoonStatementElement[] statements = block.getStatements();
    for (final MoonStatementElement statement : statements) {
      if (!statementMayCompleteNormally(statement)) {
        return false;
      }
    }
    return true;
  }


  private static boolean statementIsBreakTarget(
      @NotNull MoonStatementElement statement) {
    final BreakFinder breakFinder = new BreakFinder(statement);
    statement.accept(breakFinder);
    return breakFinder.breakFound();
  }

  public static boolean statementContainsReturn(
      @NotNull MoonStatementElement statement) {
    final ReturnFinder returnFinder = new ReturnFinder();
    statement.accept(returnFinder);
    return returnFinder.returnFound();
  }

  public static boolean isInLoop(@NotNull MoonPsiElement element) {
    final MoonConditionalLoop loop =
        PsiTreeUtil.getParentOfType(element, MoonConditionalLoop.class);
    if (loop == null) {
      return false;
    }
    final MoonBlock body = loop.getBody();
    return PsiTreeUtil.isAncestor(body, element, true);
  }



//  private static boolean isInWhileStatementBody(@NotNull MoonPsiElement element) {
//    final MoonWhileStatement whileStatement =
//        PsiTreeUtil.getParentOfType(element, MoonWhileStatement.class);
//    if (whileStatement == null) {
//      return false;
//    }
//    final MoonStatementElement body = whileStatement.getBody();
//    return PsiTreeUtil.isAncestor(body, element, true);
//  }

//  private static boolean isInForStatementBody(@NotNull MoonPsiElement element) {
//    final LuaForStatement forStatement =
//        PsiTreeUtil.getParentOfType(element, LuaForStatement.class);
//    if (forStatement == null) {
//      return false;
//    }
//    final MoonStatementElement body = forStatement.getBody();
//    return PsiTreeUtil.isAncestor(body, element, true);
//  }


//  public static MoonStatementElement stripBraces(@NotNull MoonStatementElement branch) {
//    if (branch instanceof LuaBlockStatement) {
//      final LuaBlockStatement block = (LuaBlockStatement) branch;
//      final LuaStatement[] statements = block.getBlock().getStatements();
//      if (statements.length == 1) {
//        return statements[0];
//      } else {
//        return block;
//      }
//    } else {
//      return branch;
//    }
//  }

  public static boolean statementCompletesWithStatement(
      @NotNull MoonStatementElement containingStatement,
      @NotNull MoonStatementElement statement) {
    MoonPsiElement statementToCheck = statement;
    while (true) {
      if (statementToCheck.equals(containingStatement)) {
        return true;
      }
      final MoonPsiElement container =
          getContainingStatement(statementToCheck);
      if (container == null) {
        return false;
      }
      if (container instanceof MoonBlock) {
        if (!statementIsLastInBlock((MoonBlock) container,
            (MoonStatementElement) statementToCheck)) {
          return false;
        }
      }
      if (isLoop(container)) {
        return false;
      }
      statementToCheck = container;
    }
  }

  public static boolean blockCompletesWithStatement(
      @NotNull MoonBlock body,
      @NotNull MoonStatementElement statement) {
    MoonStatementElement statementToCheck = statement;
    while (true) {
      if (statementToCheck == null) {
        return false;
      }
      final MoonStatementElement container =
          getContainingStatement(statementToCheck);
      if (container == null) {
        return false;
      }
      if (isLoop(container)) {
        return false;
      }
      if (container instanceof MoonBlock) {
        if (!statementIsLastInBlock((MoonBlock) container,
            statementToCheck)) {
          return false;
        }
        if (container.equals(body)) {
          return true;
        }
        statementToCheck =
            PsiTreeUtil.getParentOfType(container,
                MoonStatementElement.class);
      } else {
        statementToCheck = container;
      }
    }
  }


  private static boolean isLoop(@NotNull MoonPsiElement element) {
    return element instanceof MoonConditionalLoop;
  }

  @Nullable
  private static MoonStatementElement getContainingStatement(
      @NotNull MoonPsiElement statement) {
    return PsiTreeUtil.getParentOfType(statement, MoonStatementElement.class);
  }

  @Nullable
  private static MoonPsiElement getContainingStatementOrBlock(
      @NotNull MoonPsiElement statement) {
    return PsiTreeUtil.getParentOfType(statement, MoonStatementElement.class, MoonBlock.class);
  }

  private static boolean statementIsLastInBlock(@NotNull MoonBlock block,
                                                @NotNull MoonStatementElement statement) {
    final MoonStatementElement[] statements = block.getStatements();
    for (int i = statements.length - 1; i >= 0; i--) {
      final MoonStatementElement childStatement = statements[i];
      if (statement.equals(childStatement)) {
        return true;
      }
      if (!(childStatement instanceof MoonReturnStatement)) {
        return false;
      }
    }
    return false;
  }

  private static boolean statementIsLastInCodeBlock(@NotNull MoonBlock block,
                                                    @NotNull MoonStatementElement statement) {
    final MoonStatementElement[] statements = block.getStatements();
    for (int i = statements.length - 1; i >= 0; i--) {
      final MoonStatementElement childStatement = statements[i];
      if (statement.equals(childStatement)) {
        return true;
      }
      if (!(childStatement instanceof MoonReturnStatement)) {
        return false;
      }
    }
    return false;
  }

  private static class ReturnFinder extends MoonElementVisitor {
    private boolean m_found = false;

    public boolean returnFound() {
      return m_found;
    }

    public void visitReturnStatement(
        @NotNull MoonReturnStatement returnStatement) {
      if (m_found) {
        return;
      }
      super.visitReturnStatement(returnStatement);
      m_found = true;
    }
  }

  private static class BreakFinder extends MoonElementVisitor {
    private boolean m_found = false;
    private final MoonStatementElement m_target;

    private BreakFinder(@NotNull MoonStatementElement target) {
      super();
      m_target = target;
    }

    public boolean breakFound() {
      return m_found;
    }

    public void visitBreakStatement(
        @NotNull MoonBreakStatement breakStatement) {
      if (m_found) {
        return;
      }
      super.visitBreakStatement(breakStatement);
    // TODO
      final MoonStatementElement exitedStatement = null; // TODO breakStatement.findTargetStatement();
      if (exitedStatement == null) {
        return;
      }
      if (PsiTreeUtil.isAncestor(exitedStatement, m_target, false)) {
        m_found = true;
      }
    }
  }


//  public static boolean isInExitStatement(@NotNull MoonExpression expression) {
//    return isInReturnStatementArgument(expression) ||
//        isInThrowStatementArgument(expression);
//  }

//  private static boolean isInReturnStatementArgument(
//      @NotNull MoonExpression expression) {
//    final MoonReturnStatement returnStatement =
//        PsiTreeUtil
//            .getParentOfType(expression, MoonReturnStatement.class);
//    return returnStatement != null;
//  }

//  private static boolean isInThrowStatementArgument(
//      @NotNull MoonExpression expression) {
//    final LuaThrowStatement throwStatement =
//        PsiTreeUtil
//            .getParentOfType(expression, LuaThrowStatement.class);
//    return throwStatement != null;
//  }


//  public interface ExitPointVisitor {
//    boolean visit(Instruction instruction);
//  }
//
//  public static void visitAllExitPoints(@Nullable MoonBlock block, ExitPointVisitor visitor) {
//    if (block == null) return;
//    final Instruction[] flow = block.getControlFlow();
//    boolean[] visited = new boolean[flow.length];
//    visitAllExitPointsInner(flow[flow.length - 1], flow[0], visited, visitor);
//  }
//
//  private static boolean visitAllExitPointsInner(Instruction last, Instruction first, boolean[] visited, ExitPointVisitor visitor) {
//    if (first == last) return true;
//    if (last instanceof MaybeReturnInstruction) {
//      return visitor.visit(last);
//    }
//
//    final PsiElement element = last.getElement();
//    if (element != null) {
//      return visitor.visit(last);
//    }
//    visited[last.num()] = true;
//    for (Instruction pred : last.allPred()) {
//      if (!visited[pred.num()]) {
//        if (!visitAllExitPointsInner(pred, first, visited, visitor)) return false;
//      }
//    }
//    return true;
//  }
}
