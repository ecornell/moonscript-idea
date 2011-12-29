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

package com.eightbitmage.moonscript.module;

import com.eightbitmage.moonscript.MoonBundle;
import com.eightbitmage.moonscript.MoonIcons;
import com.eightbitmage.moonscript.util.MoonModuleUtil;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class MoonModuleType extends ModuleType<MoonModuleBuilder> {
    @NotNull
    public static final String ID = "MOON_MODULE";

    @NotNull
    public static MoonModuleType getInstance() {
        return (MoonModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(final WizardContext wizardContext,
                                                final MoonModuleBuilder moduleBuilder,
                                                final ModulesProvider modulesProvider) {
        final ArrayList<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();

        steps.add(new MoonSdkSelectStep(moduleBuilder, null, null, wizardContext.getProject()));

        return steps.toArray(new ModuleWizardStep[steps.size()]);
    }

    public MoonModuleType() {
        super(ID);
    }

    @NotNull
    public MoonModuleBuilder createModuleBuilder() {
        return new MoonModuleBuilder();
    }

    @NotNull
    public String getName() {
        return MoonBundle.message("moduletype.name");
    }

    @NotNull
    public String getDescription() {
        return MoonBundle.message("moduletype.description");
    }

    @NotNull
    public Icon getBigIcon() {
        return MoonIcons.MOON_IDEA_MODULE_ICON;
    }

    @NotNull
    public Icon getNodeIcon(final boolean isOpened) {
        return MoonIcons.MOON_ICON;
    }

    public boolean isValidSdk(final Module module, final Sdk projectSdk) {
        if (MoonModuleUtil.isMoonModule(module) && MoonModuleUtil.isMoonSdk(projectSdk))
            return true;

        return false;
    }
}
