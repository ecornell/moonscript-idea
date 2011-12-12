package com.eightbitmage.moonscript.editor.highlighter;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.lang.moondoc.highlighter.MoonDocSyntaxHighlighter;
import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class MoonEditorHighlighter extends LayeredLexerEditorHighlighter {

    public MoonEditorHighlighter(EditorColorsScheme scheme, Project project, VirtualFile virtualFile) {
        super(MoonSyntaxHighlighterFactory.getSyntaxHighlighter(MoonFileType.MOON_LANGUAGE, project, virtualFile), scheme);
        registerMoondocHighlighter();
    }

    private void registerMoondocHighlighter() {
        SyntaxHighlighter moonDocHighlighter = new MoonDocSyntaxHighlighter();
        final LayerDescriptor luaDocLayer = new LayerDescriptor(moonDocHighlighter, "\n", null);
        registerLayer(MoonDocElementTypes.LUADOC_COMMENT, luaDocLayer);
    }
}
