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

package com.eightbitmage.moonscript.editor.highlighter;

import com.eightbitmage.moonscript.MoonBundle;
import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Apr 3, 2010
 * Time: 1:52:31 AM
 */
public class MoonColorsPage implements ColorSettingsPage {
    final String DEMO_TEXT = "<global>a</global> = { <global>foo</global>.<field>bar</field>,  <global>foo</global>.<field>bar</field>(), <global>fx</global>(), <global>f</global> = <global>a</global>, 1,  <global>FOO</global> } -- url http://www.url.com \n" +
            "local <local>x</local>,<local>y</local> = 20,nil\n" +
            "for <local>i</local>=1,10 do\n" +
            "  local <local>y</local> = 0\n" +
            "  <global>a</global>[<local>i</local>] = function() <local>y</local>=<local>y</local>+1; return <local>x</local>+<local>y</local> end\n" +
            "end\n" +
            "\n" +
            "--[[ " +
            "  Multiline\n" +
            "  Comment\n" +
            "]]\n" +
            "\n" +
            "<moondoc>--- External Documentation URL (shift-F1)</moondoc>\n" +
            "<moondoc>-- This is called by shift-F1 on the symbol, or by the</moondoc>\n" +
            "<moondoc>-- external documentation button on the quick help panel</moondoc>\n" +
            "<moondoc>-- <moondoc-tag>@class</moondoc-tag> <moondoc-value>tag-name</moondoc-value> The name to get documentation for.</moondoc>\n" +
            "<moondoc>-- <moondoc-tag>@param</moondoc-tag> <parameter>name</parameter> The name to get documentation for.</moondoc>\n" +
            "<moondoc>-- <moondoc-tag>@return</moondoc-tag> the URL of the external documentation</moondoc>\n" +
            "function <global>getDocumentationUrl</global>(<parameter>name</parameter>) \n" +
            "  local <local>p1</local>, <local>p2</local> = <global>string</global>.<field>match</field>(<parameter>name</parameter>, \"(%a+)\\.?(%a*)\")\n" +
            "  local <local>url</local> = <global>BASE_URL</global> .. \"/docs/api/\" .. <local>p1</local> .. [[long string]]\n" +
            "\n" +
            "  if <local>p2</local> and true then <local>url</local> = <local>url</local> .. <local>p2</local> end\n" +
            "\n" +
            "  return <local>url</local>\n" +
            "end\n" +
            "\n" +
            "<global>a</global> = \"BAD\n";



    private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
            new AttributesDescriptor(MoonBundle.message("color.settings.number"), MoonHighlightingData.NUMBER),
            new AttributesDescriptor(MoonBundle.message("color.settings.string"), MoonHighlightingData.STRING),
            new AttributesDescriptor(MoonBundle.message("color.settings.longstring"), MoonHighlightingData.LONGSTRING),
            new AttributesDescriptor(MoonBundle.message("color.settings.keyword"), MoonHighlightingData.KEYWORD),
            new AttributesDescriptor(MoonBundle.message("color.settings.operation"), MoonHighlightingData.OPERATION_SIGN),
            new AttributesDescriptor(MoonBundle.message("color.settings.constant.keywords"), MoonHighlightingData.DEFINED_CONSTANTS),
            new AttributesDescriptor(MoonBundle.message("color.settings.globals"), MoonHighlightingData.GLOBAL_VAR),
            new AttributesDescriptor(MoonBundle.message("color.settings.locals"), MoonHighlightingData.LOCAL_VAR),
            new AttributesDescriptor(MoonBundle.message("color.settings.field"), MoonHighlightingData.FIELD),
            new AttributesDescriptor(MoonBundle.message("color.settings.parameter"), MoonHighlightingData.PARAMETER),
            new AttributesDescriptor(MoonBundle.message("color.settings.comment"), MoonHighlightingData.COMMENT),
            new AttributesDescriptor(MoonBundle.message("color.settings.longcomment"), MoonHighlightingData.LONGCOMMENT),
            new AttributesDescriptor(MoonBundle.message("color.settings.luadoc"), MoonHighlightingData.LUADOC),
            new AttributesDescriptor(MoonBundle.message("color.settings.luadoc.tag"), MoonHighlightingData.LUADOC_TAG),
            new AttributesDescriptor(MoonBundle.message("color.settings.luadoc.value"), MoonHighlightingData.LUADOC_VALUE),
            new AttributesDescriptor(MoonBundle.message("color.settings.longstring.braces"), MoonHighlightingData.LONGSTRING_BRACES),
            new AttributesDescriptor(MoonBundle.message("color.settings.longcomment.braces"), MoonHighlightingData.LONGCOMMENT_BRACES),
            new AttributesDescriptor(MoonBundle.message("color.settings.brackets"), MoonHighlightingData.BRACKETS),
            new AttributesDescriptor(MoonBundle.message("color.settings.parenths"), MoonHighlightingData.PARENTHS),
            new AttributesDescriptor(MoonBundle.message("color.settings.braces"), MoonHighlightingData.BRACES),
            new AttributesDescriptor(MoonBundle.message("color.settings.comma"), MoonHighlightingData.COMMA),
            new AttributesDescriptor(MoonBundle.message("color.settings.bad_character"), MoonHighlightingData.BAD_CHARACTER),
    };

    private static final Map<String, TextAttributesKey> ATTR_MAP = new HashMap<String, TextAttributesKey> ();

    static {
        ATTR_MAP.put("local", MoonHighlightingData.LOCAL_VAR);
        ATTR_MAP.put("global", MoonHighlightingData.GLOBAL_VAR);
        ATTR_MAP.put("field", MoonHighlightingData.FIELD);
        ATTR_MAP.put("parameter", MoonHighlightingData.PARAMETER);
        ATTR_MAP.put("moondoc", MoonHighlightingData.LUADOC);
        ATTR_MAP.put("moondoc-tag", MoonHighlightingData.LUADOC_TAG);
        ATTR_MAP.put("moondoc-value", MoonHighlightingData.LUADOC_VALUE);
    }

    @NotNull
	public String getDisplayName() {
		return MoonBundle.message("color.settings.name");
	}

	@Nullable
	public Icon getIcon() {
		return MoonIcons.MOON_ICON;
	}

	@NotNull
	public AttributesDescriptor[] getAttributeDescriptors() {
		return ATTRS;
	}

	@NotNull
	public ColorDescriptor[] getColorDescriptors() {
		return new ColorDescriptor[0];
	}

	@NotNull
	public SyntaxHighlighter getHighlighter() {
		return new MoonSyntaxHighlighter();
	}

	@NonNls
	@NotNull
	public String getDemoText() {
		return DEMO_TEXT;
	}

	@Nullable
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return ATTR_MAP;
	}

}
