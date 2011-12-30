package com.eightbitmage.moonscript.options;

import com.eightbitmage.moonscript.MoonBundle;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Configurable for MoonScript.
 */
public final class MoonConfigurable implements Configurable {

    private MoonSettingsForm settingsForm;

    @Nls
    public String getDisplayName() {
        return MoonBundle.message("color.settings.name");
    }

    @Nullable
    public Icon getIcon() {
        return null;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (settingsForm == null) {
            settingsForm = new MoonSettingsForm(MoonSettings.getInstance());
        }
        return settingsForm.getFormComponent();
    }

    public boolean isModified() {
        return settingsForm != null && settingsForm.isModified(MoonSettings.getInstance());
    }

    public void apply() throws ConfigurationException {
        if (settingsForm != null) {
            MoonSettings.getInstance().loadState(settingsForm.getState());
        }
    }

    public void reset() {
        if (settingsForm != null) {
            settingsForm.loadState(MoonSettings.getInstance());
        }
    }

    public void disposeUIResources() {
        settingsForm = null;
    }
}
