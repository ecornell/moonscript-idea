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

package com.eightbitmage.moonscript;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MoonIcons {
	final String PATH = "/icons/";

	final Icon MOON_ICON = IconLoader.findIcon(PATH + "Moon.png");
    final Icon MOON_FUNCTION = IconLoader.findIcon(PATH + "function.png");

    final Icon MOON_IDEA_MODULE_ICON = IconLoader.findIcon(PATH + "logo_24x24.png");
    final Icon TAIL_RECURSION = IconLoader.findIcon(PATH + "repeat-icon-16x16.png");
}


