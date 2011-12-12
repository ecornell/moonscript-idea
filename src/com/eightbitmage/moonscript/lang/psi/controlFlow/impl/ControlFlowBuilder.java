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
package com.eightbitmage.moonscript.lang.psi.controlFlow.impl;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonCompoundReferenceElementImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonRecursiveElementVisitor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.controlFlow.AfterCallInstruction;
import com.eightbitmage.moonscript.lang.psi.controlFlow.CallEnvironment;
import com.eightbitmage.moonscript.lang.psi.controlFlow.CallInstruction;
import com.eightbitmage.moonscript.lang.psi.controlFlow.Instruction;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


/**
 * @author ven
 */
public class ControlFlowBuilder extends MoonRecursiveElementVisitor {
  private static final Logger log = Logger.getInstance("Lua.ControlFlowBuilder");

  private List<InstructionImpl> myInstructions;

  private Stack<InstructionImpl> myProcessingStack;
  //private final PsiConstantEvaluationHelper myConstantEvaluator;

    public ControlFlowBuilder(Project project) {
        // myConstantEvaluator = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();

    }

    private InstructionImpl myHead;
    private boolean myNegate;
    private boolean myAssertionsOnly;
    private MoonPsiElement myLastInScope;

    private List<Pair<InstructionImpl, MoonPsiElement>> myPending;

    private int myInstructionNumber;

    public void visitBlock(MoonBlock block) {
        final PsiElement parent = block.getParent();
        if (parent instanceof MoonFunctionDefinition) {
            final MoonParameter[] parameters = ((MoonFunctionDefinition) parent).getParameters().getLuaParameters();
            for (MoonParameter parameter : parameters) {
                addNode(new ReadWriteVariableInstructionImpl(parameter, myInstructionNumber++));
            }
        }
        super.visitBlock(block);

        handlePossibleReturn(block);
    }

    private void handlePossibleReturn(MoonBlock block) {
        final MoonStatementElement[] statements = block.getStatements();
        for(int i=statements.length; i<0; i--)
            handlePossibleReturn(statements[i]);
    }

    private void handlePossibleReturn(MoonStatementElement last) {
        if (PsiTreeUtil.isAncestor(myLastInScope, last, false)) {
            final MaybeReturnInstruction instruction = new MaybeReturnInstruction((MoonExpression) last, myInstructionNumber++);
            checkPending(instruction);
            addNode(instruction);
        }
    }

    final Object lock = new Object();
    
    public Instruction[] buildControlFlow(MoonPsiElement scope) {
        myInstructions = new ArrayList<InstructionImpl>();
        myProcessingStack = new Stack<InstructionImpl>();
        myPending = new ArrayList<Pair<InstructionImpl, MoonPsiElement>>();
        myInstructionNumber = 0;

        myLastInScope = null;

        if (scope instanceof MoonPsiFile) {
            MoonStatementElement[] statements = ((MoonPsiFile) scope).getStatements();
            if (statements.length > 0) {
                myLastInScope = statements[statements.length - 1];
            }
        } else if (scope instanceof MoonBlock) {
            MoonStatementElement[] statements = ((MoonBlock) scope).getStatements();
            if (statements.length > 0) {
                myLastInScope = statements[statements.length - 1];
            }
        }

        log.info("Scope: " + scope + " parent: " + scope.getParent());

        startNode(null);
//    if (scope instanceof LuaClosableBlock) {
//      buildFlowForClosure((LuaClosableBlock)scope);
//    }
        scope.accept(this);

        final InstructionImpl end = startNode(null);
        checkPending(end); //collect return edges

        synchronized (lock) {
        for(Instruction i : myInstructions)
            log.info(i.toString());
        }
        return myInstructions.toArray(new Instruction[myInstructions.size()]);
    }

//  private void buildFlowForClosure(final LuaClosableBlock closure) {
//    for (MoonParameter parameter : closure.getParameters()) {
//      addNode(new ReadWriteVariableInstructionImpl(parameter, myInstructionNumber++));
//    }
//
//    final Set<String> names = new LinkedHashSet<String>();
//
//    closure.accept(new MoonRecursiveElementVisitor() {
//      public void visitReferenceExpression(LuaReferenceExpression refExpr) {
//        super.visitReferenceExpression(refExpr);
//        if (refExpr.getQualifierExpression() == null && !PsiUtil.isLValue(refExpr)) {
//          if (!(refExpr.getParent() instanceof LuaCall)) {
//            final String refName = refExpr.getReferenceName();
//            if (!hasDeclaredVariable(refName, closure, refExpr)) {
//              names.add(refName);
//            }
//          }
//        }
//      }
//    });
//
//    names.add("owner");
//
//    for (String name : names) {
//      addNode(new ReadWriteVariableInstructionImpl(name, closure.getLBrace(), myInstructionNumber++, true));
//    }
//
//    PsiElement child = closure.getFirstChild();
//    while (child != null) {
//      if (child instanceof MoonPsiElement) {
//        ((MoonPsiElement)child).accept(this);
//      }
//      child = child.getNextSibling();
//    }
//
//    final LuaStatement[] statements = closure.getStatements();
//    if (statements.length > 0) {
//      handlePossibleReturn(statements[statements.length - 1]);
//    }
//  }

  private void addNode(InstructionImpl instruction) {
    myInstructions.add(instruction);
    if (myHead != null) {
      addEdge(myHead, instruction);
    }
    myHead = instruction;
  }

  static void addEdge(InstructionImpl beg, InstructionImpl end) {
    if (!beg.mySucc.contains(end)) {
      beg.mySucc.add(end);
    }

    if (!end.myPred.contains(beg)) {
      end.myPred.add(beg);
    }
  }

  public void visitFunctionDef(MoonFunctionDefinitionStatement e) {
    //do not go into functions

      e.getIdentifier().accept(this);
      addNode(new ReadWriteVariableInstructionImpl(e.getIdentifier(), myInstructionNumber++));
  }

    @Override
    public void visitDeclarationStatement(MoonDeclarationStatement e) {
        super.visitDeclarationStatement(e);

        for (MoonSymbol s : e.getDefinedSymbols())
            addNode(new ReadWriteVariableInstructionImpl(s, myInstructionNumber++));
    }


//    @Override
//    public void visitDeclarationStatement(MoonDeclarationStatement e) {
//        e.getDefinedSymbols().accept(this);
//    }
//
//    @Override
//    public void visitDeclarationExpression(MoonDeclarationExpression e) {
//        addNode(new ReadWriteVariableInstructionImpl(e, myInstructionNumber++));
//    }

    @Override
    public void visitFile(PsiFile file) {
        visitBlock((MoonBlock) file);
    }

    @Override
    public void visitDoStatement(MoonDoStatement e) {
        final InstructionImpl instruction = startNode(e);
        final MoonBlock body = e.getBlock();
        if (body != null) {
            body.accept(this);
        }
        finishNode(instruction);
    }

    //
//  public void visitBreakStatement(MoonBreakStatement breakStatement) {
//    super.visitBreakStatement(breakStatement);
//    final MoonStatementElement target = breakStatement.findTargetStatement();
//    if (target != null && myHead != null) {
//      addPendingEdge(target, myHead);
//    }
//
//    flowAbrupted();
//  }
//

  public void visitReturnStatement(MoonReturnStatement returnStatement) {
    boolean isNodeNeeded = myHead == null || myHead.getElement() != returnStatement;
    final MoonExpression value = returnStatement.getReturnValue();
    if (value != null) value.accept(this);

    if (isNodeNeeded) {
      InstructionImpl retInsn = startNode(returnStatement);
      addPendingEdge(null, myHead);
      finishNode(retInsn);
    }
    else {
      addPendingEdge(null, myHead);
    }
    flowAbrupted();
  }
//
//  public void visitAssertStatement(LuaAssertStatement assertStatement) {
//    final MoonExpression assertion = assertStatement.getAssertion();
//    if (assertion != null) {
//      assertion.accept(this);
//      final InstructionImpl assertInstruction = startNode(assertStatement);
//      final PsiType type = TypesUtil.createTypeByFQClassName("java.lang.AssertionError", assertStatement);
//      ExceptionInfo info = findCatch(type);
//      if (info != null) {
//        info.myThrowers.add(assertInstruction);
//      }
//      else {
//        addPendingEdge(null, assertInstruction);
//      }
//      finishNode(assertInstruction);
//    }
//  }
//
//
  private void flowAbrupted() {
    myHead = null;
  }

  public void visitAssignment(MoonAssignmentStatement expression) {
    MoonIdentifierList lValues = expression.getLeftExprs();
    MoonExpressionList rValues = expression.getRightExprs();
    if (rValues != null) {
      rValues.accept(this);
      lValues.accept(this);
    }
  }

//  @Override
//  public void visitParenthesizedExpression(MoonParenthesizedExpression expression) {
//    final MoonExpression operand = expression.getOperand();
//    if (operand != null) operand.accept(this);
//  }
//
  @Override
  public void visitUnaryExpression(MoonUnaryExpression expression) {
    final MoonExpression operand = expression.getOperand();
    if (operand != null) {
      final boolean negation = expression.getOperationTokenType() == MoonElementTypes.NOT;
      if (negation) {
        myNegate = !myNegate;
      }
      operand.accept(this);
      if (negation) {
        myNegate = !myNegate;
      }
    }
  }

    @Override
    public void visitCompoundReference(MoonCompoundReferenceElementImpl e) {
        visitReferenceElement(e);
    }

    

    public void visitReferenceElement(MoonReferenceElement referenceExpression) {
    super.visitReferenceElement(referenceExpression);

    final ReadWriteVariableInstructionImpl i =
      new ReadWriteVariableInstructionImpl(referenceExpression, myInstructionNumber++, !myAssertionsOnly && MoonPsiUtils.isLValue(referenceExpression));
    addNode(i);
    checkPending(i);
  }

  public void visitIfThenStatement(MoonIfThenStatement ifStatement) {
    InstructionImpl ifInstruction = startNode(ifStatement);
    final MoonExpression condition = ifStatement.getIfCondition();

    final InstructionImpl head = myHead;
    final MoonBlock thenBranch = ifStatement.getIfBlock();
    InstructionImpl thenEnd = null;
    if (thenBranch != null) {
      if (condition != null) {
        condition.accept(this);
      }
      thenBranch.accept(this);
      handlePossibleReturn(thenBranch);
      thenEnd = myHead;
    }

    myHead = head;
    final MoonBlock elseBranch = ifStatement.getElseBlock();
    InstructionImpl elseEnd = null;
    if (elseBranch != null) {
      if (condition != null) {
        myNegate = !myNegate;
        final boolean old = myAssertionsOnly;
        myAssertionsOnly = true;
        condition.accept(this);
        myNegate = !myNegate;
        myAssertionsOnly = old;
      }

      elseBranch.accept(this);
      handlePossibleReturn(elseBranch);
      elseEnd = myHead;
    }


    if (thenBranch != null || elseBranch != null) {
      final InstructionImpl end = new IfEndInstruction(ifStatement, myInstructionNumber++);
      addNode(end);
      if (thenEnd != null) addEdge(thenEnd, end);
      if (elseEnd != null) addEdge(elseEnd, end);
    }
    finishNode(ifInstruction);



    /*InstructionImpl ifInstruction = startNode(ifStatement);
    final LuaCondition condition = ifStatement.getCondition();

    final InstructionImpl head = myHead;
    final LuaStatement thenBranch = ifStatement.getThenBranch();
    if (thenBranch != null) {
      if (condition != null) {
        condition.accept(this);
      }
      thenBranch.accept(this);
      handlePossibleReturn(thenBranch);
      addPendingEdge(ifStatement, myHead);
    }

    myHead = head;
    if (condition != null) {
      myNegate = !myNegate;
      final boolean old = myAssertionsOnly;
      myAssertionsOnly = true;
      condition.accept(this);
      myNegate = !myNegate;
      myAssertionsOnly = old;
    }

    final LuaStatement elseBranch = ifStatement.getElseBranch();
    if (elseBranch != null) {
      elseBranch.accept(this);
      handlePossibleReturn(elseBranch);
      addPendingEdge(ifStatement, myHead);
    }

    finishNode(ifInstruction);*/
  }
//
//  public void visitForStatement(LuaForStatement forStatement) {
//    final LuaForClause clause = forStatement.getClause();
//    if (clause instanceof LuaTraditionalForClause) {
//      for (LuaCondition initializer : ((LuaTraditionalForClause)clause).getInitialization()) {
//        initializer.accept(this);
//      }
//    }
//    else if (clause instanceof LuaForInClause) {
//      final MoonExpression expression = ((LuaForInClause)clause).getIteratedExpression();
//      if (expression != null) {
//        expression.accept(this);
//      }
//      for (LuaVariable variable : clause.getDeclaredVariables()) {
//        ReadWriteVariableInstructionImpl writeInsn = new ReadWriteVariableInstructionImpl(variable, myInstructionNumber++);
//        checkPending(writeInsn);
//        addNode(writeInsn);
//      }
//    }
//
//    InstructionImpl instruction = startNode(forStatement);
//    if (clause instanceof LuaTraditionalForClause) {
//      final MoonExpression condition = ((LuaTraditionalForClause)clause).getCondition();
//      if (condition != null) {
//        condition.accept(this);
//        if (!alwaysTrue(condition)) {
//          addPendingEdge(forStatement, myHead); //break cycle
//        }
//      }
//    } else {
//      addPendingEdge(forStatement, myHead); //break cycle
//    }
//
//    final LuaStatement body = forStatement.getBody();
//    if (body != null) {
//      InstructionImpl bodyInstruction = startNode(body);
//      body.accept(this);
//      finishNode(bodyInstruction);
//    }
//    checkPending(instruction); //check for breaks targeted here
//
//    if (clause instanceof LuaTraditionalForClause) {
//      for (MoonExpression expression : ((LuaTraditionalForClause)clause).getUpdate()) {
//        expression.accept(this);
//      }
//    }
//    if (myHead != null) addEdge(myHead, instruction);  //loop
//    flowAbrupted();
//
//    finishNode(instruction);
//  }
//
  private void checkPending(InstructionImpl instruction) {
    final PsiElement element = instruction.getElement();
    if (element == null) {
      //add all
      for (Pair<InstructionImpl, MoonPsiElement> pair : myPending) {
        addEdge(pair.getFirst(), instruction);
      }
      myPending.clear();
    }
    else {
      for (int i = myPending.size() - 1; i >= 0; i--) {
        final Pair<InstructionImpl, MoonPsiElement> pair = myPending.get(i);
        final PsiElement scopeWhenToAdd = pair.getSecond();
        if (scopeWhenToAdd == null) continue;
        if (!PsiTreeUtil.isAncestor(scopeWhenToAdd, element, false)) {
          addEdge(pair.getFirst(), instruction);
          myPending.remove(i);
        }
        else {
          break;
        }
      }
    }
  }

  //add edge when instruction.getElement() is not contained in scopeWhenAdded
  private void addPendingEdge(MoonPsiElement scopeWhenAdded, InstructionImpl instruction) {
    if (instruction == null) return;

    int i = 0;
    if (scopeWhenAdded != null) {
      for (; i < myPending.size(); i++) {
        Pair<InstructionImpl, MoonPsiElement> pair = myPending.get(i);

        final MoonPsiElement currScope = pair.getSecond();
        if (currScope == null) continue;
        if (!PsiTreeUtil.isAncestor(currScope, scopeWhenAdded, true)) break;
      }
    }
    myPending.add(i, new Pair<InstructionImpl, MoonPsiElement>(instruction, scopeWhenAdded));
  }

  public void visitWhileStatement(MoonWhileStatement whileStatement) {
    final InstructionImpl instruction = startNode(whileStatement);
    final MoonConditionalExpression condition = whileStatement.getCondition();
    if (condition != null) {
      condition.accept(this);
    }
    if (!alwaysTrue(condition)) {
      addPendingEdge(whileStatement, myHead); //break
    }
    final MoonBlock body = whileStatement.getBody();
    if (body != null) {
      body.accept(this);
    }
    checkPending(instruction); //check for breaks targeted here
    if (myHead != null) addEdge(myHead, instruction); //loop
    flowAbrupted();
    finishNode(instruction);
  }

  private boolean alwaysTrue(MoonExpression condition) {
      MoonType type = condition.getLuaType();

      if (type != MoonType.NIL && type != MoonType.BOOLEAN && type != MoonType.ANY)
          return true;

      return false;
  }


  private InstructionImpl startNode(@Nullable MoonPsiElement element) {
    return startNode(element, true);
  }

  private InstructionImpl startNode(MoonPsiElement element, boolean checkPending) {
    final InstructionImpl instruction = new InstructionImpl(element, myInstructionNumber++);
    addNode(instruction);
    if (checkPending) checkPending(instruction);
    return myProcessingStack.push(instruction);
  }

  private void finishNode(InstructionImpl instruction) {
    assert instruction.equals(myProcessingStack.pop());
/*    myHead = myProcessingStack.peek();*/
  }
//
//  public void visitField(LuaField field) {
//  }
//
//  public void visitParameter(MoonParameter parameter) {
//  }
//
//  public void visitMethod(LuaMethod method) {
//  }
//
//  public void visitTypeDefinition(LuaTypeDefinition typeDefinition) {
//    if (typeDefinition instanceof LuaAnonymousClassDefinition) {
//      super.visitTypeDefinition(typeDefinition);
//    }
//  }
//
//  public void visitVariable(LuaVariable variable) {
//    super.visitVariable(variable);
//    if (variable.getInitializerLua() != null ||
//        variable.getParent() instanceof LuaTupleDeclaration && ((LuaTupleDeclaration)variable.getParent()).getInitializerLua() != null) {
//      ReadWriteVariableInstructionImpl writeInsn = new ReadWriteVariableInstructionImpl(variable, myInstructionNumber++);
//      checkPending(writeInsn);
//      addNode(writeInsn);
//    }
//  }
//
  @Nullable
  private InstructionImpl findInstruction(PsiElement element) {
    for (int i = myProcessingStack.size() - 1; i >= 0; i--) {
      InstructionImpl instruction = myProcessingStack.get(i);
      if (element.equals(instruction.getElement())) return instruction;
    }
    return null;
  }

  static class CallInstructionImpl extends InstructionImpl implements CallInstruction {
    private final InstructionImpl myCallee;

    public String toString() {
      return super.toString() + " CALL " + myCallee.num();
    }

    public Iterable<? extends Instruction> succ(CallEnvironment env) {
      getStack(env, myCallee).push(this);
      return Collections.singletonList(myCallee);
    }

    public Iterable<? extends Instruction> allSucc() {
      return Collections.singletonList(myCallee);
    }

    protected String getElementPresentation() {
      return "";
    }

    CallInstructionImpl(int num, InstructionImpl callee) {
      super(null, num);
      myCallee = callee;
    }
  }

  static class PostCallInstructionImpl extends InstructionImpl implements AfterCallInstruction {
    private final CallInstructionImpl myCall;
    private RetInstruction myReturnInsn;

    public String toString() {
      return super.toString() + "AFTER CALL " + myCall.num();
    }

    public Iterable<? extends Instruction> allPred() {
      return Collections.singletonList(myReturnInsn);
    }

    public Iterable<? extends Instruction> pred(CallEnvironment env) {
      getStack(env, myReturnInsn).push(myCall);
      return Collections.singletonList(myReturnInsn);
    }

    protected String getElementPresentation() {
      return "";
    }

    PostCallInstructionImpl(int num, CallInstructionImpl call) {
      super(null, num);
      myCall = call;
    }

    public void setReturnInstruction(RetInstruction retInstruction) {
      myReturnInsn = retInstruction;
    }
  }

  static class RetInstruction extends InstructionImpl {
    RetInstruction(int num) {
      super(null, num);
    }

    public String toString() {
      return super.toString() + " RETURN";
    }

    protected String getElementPresentation() {
      return "";
    }

    public Iterable<? extends Instruction> succ(CallEnvironment env) {
      final Stack<CallInstruction> callStack = getStack(env, this);
      if (callStack.isEmpty()) return Collections.emptyList();     //can be true in case env was not populated (e.g. by DFA)

      final CallInstruction callInstruction = callStack.peek();
      final List<InstructionImpl> succ = ((CallInstructionImpl)callInstruction).mySucc;
      final Stack<CallInstruction> copy = (Stack<CallInstruction>)callStack.clone();
      copy.pop();
      for (InstructionImpl instruction : succ) {
        env.update(copy, instruction);
      }

      return succ;
    }
  }

  private static boolean hasDeclaredVariable(String name, MoonBlock scope, PsiElement place) {
    PsiElement prev = null;
    while (place != null) {
      if (place instanceof MoonBlock) {
        MoonStatementElement[] statements = ((MoonBlock)place).getStatements();
        for (MoonStatementElement statement : statements) {
          if (statement == prev) break;
          if (statement instanceof MoonDeclarationStatement) {
            MoonSymbol[] variables = ((MoonDeclarationStatement)statement).getDefinedSymbols();
            for (MoonSymbol variable : variables) {
              if (name.equals(variable.getName())) return true;
            }
          }
        }
      }

      if (place == scope) {
        break;
      }
      else {
        prev = place;
        place = place.getParent();
      }
    }

    return false;
  }


}
