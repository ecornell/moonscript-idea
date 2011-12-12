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

package com.eightbitmage.moonscript.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Uses code from the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public class MoonRunConfigurationEditor extends SettingsEditor<MoonRunConfiguration> {
    private MoonRunConfigurationForm myForm;

    public MoonRunConfigurationEditor(MoonRunConfiguration batchRunConfiguration) {
        this.myForm = new MoonRunConfigurationForm(batchRunConfiguration);
    }

    @Override
    protected void resetEditorFrom(MoonRunConfiguration runConfiguration) {
        MoonRunConfiguration.copyParams(runConfiguration, myForm);
    }

    @Override
    protected void applyEditorTo(MoonRunConfiguration runConfiguration) throws ConfigurationException {
        MoonRunConfiguration.copyParams(myForm, runConfiguration);
    }

    @Override
    @NotNull
    protected JComponent createEditor() {
        return myForm.getRootPanel();
    }

    @Override
    protected void disposeEditor() {
        myForm = null;
    }
}