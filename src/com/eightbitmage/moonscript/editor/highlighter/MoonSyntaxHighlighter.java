/*
 * Copyright 2009 Max Ishchenko
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

package com.eightbitmage.moonscript.editor.highlighter;

import com.eightbitmage.moonscript.lang.lexer.MoonLexer;
import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 06.07.2009
 * Time: 16:40:05
 */
public class MoonSyntaxHighlighter extends SyntaxHighlighterBase {

    private final TextAttributesKey[] BAD_CHARACTER_KEYS = new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};

    private final Map<IElementType, TextAttributesKey> colors = new HashMap<IElementType, TextAttributesKey>();

    public MoonSyntaxHighlighter() {
        colors.put(MoonTokenTypes.LONGCOMMENT, MoonHighlightingData.LONGCOMMENT);
        colors.put(MoonTokenTypes.LONGCOMMENT_BEGIN, MoonHighlightingData.LONGCOMMENT_BRACES);
        colors.put(MoonTokenTypes.LONGCOMMENT_END, MoonHighlightingData.LONGCOMMENT_BRACES);
        colors.put(MoonTokenTypes.SHORTCOMMENT, MoonHighlightingData.COMMENT);
        colors.put(MoonTokenTypes.SHEBANG, MoonHighlightingData.COMMENT);

        colors.put(MoonTokenTypes.STRING, MoonHighlightingData.STRING);
        colors.put(MoonTokenTypes.LONGSTRING, MoonHighlightingData.LONGSTRING);
        colors.put(MoonTokenTypes.LONGSTRING_BEGIN, MoonHighlightingData.LONGSTRING_BRACES);
        colors.put(MoonTokenTypes.LONGSTRING_END, MoonHighlightingData.LONGSTRING_BRACES);

        fillMap(colors, MoonTokenTypes.KEYWORDS, MoonHighlightingData.KEYWORD);
        fillMap(colors, MoonTokenTypes.PARENS, MoonHighlightingData.PARENTHS);
        fillMap(colors, MoonTokenTypes.BRACES, MoonHighlightingData.BRACES);
        fillMap(colors, MoonTokenTypes.BRACKS, MoonHighlightingData.BRACKETS);

        fillMap(colors, MoonTokenTypes.BAD_INPUT, MoonHighlightingData.BAD_CHARACTER);
        fillMap(colors, MoonTokenTypes.DEFINED_CONSTANTS, MoonHighlightingData.DEFINED_CONSTANTS);
        colors.put(MoonTokenTypes.COMMA, MoonHighlightingData.COMMA);
        colors.put(MoonTokenTypes.NUMBER, MoonHighlightingData.NUMBER);

        fillMap(colors, MoonTokenTypes.OP_SET, MoonHighlightingData.OPERATION_SIGN);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new MoonLexer();
    }

    @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(colors.get(tokenType));
  }




}
