package com.eightbitmage.moonscript.editor.highlighter;

import com.eightbitmage.moonscript.LuaFileType;
import com.eightbitmage.moonscript.lang.luadoc.highlighter.LuaDocSyntaxHighlighter;
import com.eightbitmage.moonscript.lang.luadoc.parser.LuaDocElementTypes;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class LuaEditorHighlighter extends LayeredLexerEditorHighlighter {

    public LuaEditorHighlighter(EditorColorsScheme scheme, Project project, VirtualFile virtualFile) {
        super(LuaSyntaxHighlighterFactory.getSyntaxHighlighter(LuaFileType.LUA_LANGUAGE, project, virtualFile), scheme);
        registerLuadocHighlighter();
    }

    private void registerLuadocHighlighter() {
        SyntaxHighlighter luaDocHighlighter = new LuaDocSyntaxHighlighter();
        final LayerDescriptor luaDocLayer = new LayerDescriptor(luaDocHighlighter, "\n", null);
        registerLayer(LuaDocElementTypes.LUADOC_COMMENT, luaDocLayer);
    }
}
