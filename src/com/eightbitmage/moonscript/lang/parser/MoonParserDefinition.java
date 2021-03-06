package com.eightbitmage.moonscript.lang.parser;

import com.eightbitmage.moonscript.lang.lexer.MoonLexer;
import com.eightbitmage.moonscript.lang.lexer.MoonParsingLexerMergingAdapter;
import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.parser.moon.MoonParser;
import com.eightbitmage.moonscript.lang.parser.moon.MoonParserOld;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiFileImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.elements.MoonStubFileElementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageUtil;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import static com.eightbitmage.moonscript.lang.parser.MoonElementTypes.*;

public class MoonParserDefinition implements ParserDefinition {
    public static final IStubFileElementType MOON_FILE = new MoonStubFileElementType();

    @NotNull
    public Lexer createLexer(Project project) {
        return new MoonParsingLexerMergingAdapter(new MoonLexer());
        //return new MoonLexer();
    }

    public PsiParser createParser(Project project) {
        return new MoonParser(project);
    }

    public IFileElementType getFileNodeType() {
        return MOON_FILE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES_SET;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENT_SET;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING_LITERAL_SET;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return MoonPsiCreator.createElement(node);
    }

    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new MoonPsiFileImpl(fileViewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        Lexer lexer=new MoonLexer();

        if (left.getElementType() == MoonTokenTypes.SHORTCOMMENT) return SpaceRequirements.MUST_LINE_BREAK;
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, lexer);
    }
}
