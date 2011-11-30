package com.eightbitmage.moonscript.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

import com.eightbitmage.moonscript.file.MoonScriptFileType;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptFlexLexer;
import com.eightbitmage.moonscript.lang.lexer.MoonScriptTokenSets;
import com.eightbitmage.moonscript.lang.psi.MoonScriptFile;
import com.eightbitmage.moonscript.lang.psi.impl.MoonScriptElementImpl;
import org.jetbrains.annotations.NotNull;

public class MoonScriptParserDefinition implements ParserDefinition {

  private static final IFileElementType FILE_ELEMENT_TYPE = new IFileElementType(MoonScriptFileType.MOON_SCRIPT_LANGUAGE);

  @NotNull
  public Lexer createLexer(Project project) {
    return new MoonScriptFlexLexer();
  }

  public PsiParser createParser(Project project) {
    return new MoonScriptParser();
  }

  public IFileElementType getFileNodeType() {
    return FILE_ELEMENT_TYPE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return MoonScriptTokenSets.WHITESPACE_TOKEN_SET;
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return MoonScriptTokenSets.COMMENTS_TOKEN_SET;
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return MoonScriptTokenSets.STRING_TOKEN_SET;
  }

  @NotNull
  public PsiElement createElement(ASTNode node) {
    return new MoonScriptElementImpl(node);
  }

  public PsiFile createFile(FileViewProvider viewProvider) {
    return new MoonScriptFile(viewProvider);
  }

  public ParserDefinition.SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return ParserDefinition.SpaceRequirements.MAY;
  }

}
