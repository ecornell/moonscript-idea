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

package com.eightbitmage.moonscript.options;

import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiManager;
import com.eightbitmage.moonscript.MoonFileType;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 20, 2010
 * Time: 7:08:52 PM
 */
public class MoonOptionsPanel extends BaseConfigurable implements Configurable, ApplicationComponent {
    static final Logger log = Logger.getLogger(MoonOptionsPanel.class);

    public MoonOptionsPanel() {
        addAdditionalCompletionsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModified(isModified(MoonApplicationSettings.getInstance()));
            }
        });
        resolveUpvaluedIdentifiersCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModified(isModified(MoonApplicationSettings.getInstance()));
            }
        });
        checkBoxTailCalls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModified(isModified(MoonApplicationSettings.getInstance()));
            }
        });
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JCheckBox addAdditionalCompletionsCheckBox;
    private JCheckBox resolveUpvaluedIdentifiersCheckBox;
    private JCheckBox checkBoxTailCalls;

    @Override
    public JComponent createComponent() {
        return getMainPanel();
    }

    public void apply() {
        getData(MoonApplicationSettings.getInstance());
        setModified(false);
    }

    public void reset() {
        setData(MoonApplicationSettings.getInstance());
    }

    @Override
    public void disposeUIResources() {

    }

    @Nls
    @Override
    public String getDisplayName() {
        return MoonFileType.MOON;
    }

    @Override
    public Icon getIcon() {
        return MoonIcons.MOON_ICON;
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return MoonFileType.MOON;
    }

    @Override
    public void initComponent() {
        setData(MoonApplicationSettings.getInstance());
    }

    @Override
    public void disposeComponent() {

    }

    public void setData(MoonApplicationSettings data) {
        addAdditionalCompletionsCheckBox.setSelected(data.INCLUDE_ALL_FIELDS_IN_COMPLETIONS);
        resolveUpvaluedIdentifiersCheckBox.setSelected(data.RESOLVE_ALIASED_IDENTIFIERS);
        checkBoxTailCalls.setSelected(data.SHOW_TAIL_CALLS_IN_GUTTER);



    }

    public void getData(MoonApplicationSettings data) {
        if (checkBoxTailCalls.isSelected() != data.SHOW_TAIL_CALLS_IN_GUTTER) {
            data.SHOW_TAIL_CALLS_IN_GUTTER = checkBoxTailCalls.isSelected();

            for(Project project : ProjectManager.getInstance().getOpenProjects())
              DaemonCodeAnalyzer.getInstance(project).restart();
        }

        data.INCLUDE_ALL_FIELDS_IN_COMPLETIONS = addAdditionalCompletionsCheckBox.isSelected();
        data.RESOLVE_ALIASED_IDENTIFIERS = resolveUpvaluedIdentifiersCheckBox.isSelected();
        if (data.RESOLVE_ALIASED_IDENTIFIERS) {
            for (Project project : ProjectManager.getInstance().getOpenProjects())
                PsiManager.getInstance(project).dropResolveCaches();
        }
    }

    public boolean isModified(MoonApplicationSettings data) {
        if (addAdditionalCompletionsCheckBox.isSelected() != data.INCLUDE_ALL_FIELDS_IN_COMPLETIONS) return true;
        if (resolveUpvaluedIdentifiersCheckBox.isSelected() != data.RESOLVE_ALIASED_IDENTIFIERS) return true;
        if (checkBoxTailCalls.isSelected() != data.SHOW_TAIL_CALLS_IN_GUTTER) return true;

        return false;
    }
}
