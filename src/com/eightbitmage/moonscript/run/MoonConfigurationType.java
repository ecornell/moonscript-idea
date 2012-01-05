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

import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MoonConfigurationType implements ConfigurationType {
  private final ConfigurationFactory myFactory;
  
  public MoonConfigurationType() {
    myFactory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new MoonRunConfiguration(new RunConfigurationModule(project), this, "");
            }
    };
  }
    public String getDisplayName() {
        return "MoonScript";
    }

    public String getConfigurationTypeDescription() {
        return "MoonScript run configuration";
    }

    public Icon getIcon() {
        return MoonIcons.MOON_ICON;
    }

    @NotNull
    public String getId() {
        return "#MoonConfigurationType";
    }

    public static MoonConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), MoonConfigurationType.class);
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }

//    private static class LuaConfigurationFactory extends ConfigurationFactory {
//        public LuaConfigurationFactory(ConfigurationType confType) {
//            super(confType);
//        }
//
//        @Override
//        public RunConfiguration createTemplateConfiguration(Project project) {
//         //   MoonInterpreterDetection LuaDetector = new MoonInterpreterDetection();
//
//            MoonRunConfiguration configuration = new MoonRunConfiguration(new RunConfigurationModule(project), this, "");
//           // configuration.setInterpreterPath(LuaDetector.findBestLocation());
//            configuration.setInterpreterPath("lua");
//            return configuration;
//        }
//    }
    
}