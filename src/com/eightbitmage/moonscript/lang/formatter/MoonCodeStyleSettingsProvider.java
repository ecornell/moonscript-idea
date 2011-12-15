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

package com.eightbitmage.moonscript.lang.formatter;

import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 19, 2010
 * Time: 7:37:11 PM
 */
public class MoonCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  @NotNull
  public Configurable createSettingsPage(final CodeStyleSettings settings, final CodeStyleSettings originalSettings) {
    return new CodeStyleAbstractConfigurable(settings, originalSettings, "MoonScript"){
      protected CodeStyleAbstractPanel createPanel(final CodeStyleSettings settings) {
        return new CodeStyleMoonPanel(settings);
      }
      public Icon getIcon() {
        return MoonIcons.MOON_ICON;
      }

      public String getHelpTopic() {
        return null;
      }

        public JPanel createPanel() {
          return null;
        }
    };
  }

  @Override
  public String getConfigurableDisplayName() {
    return "MoonScript";
  }
}
