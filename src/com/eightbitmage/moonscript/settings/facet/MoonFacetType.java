/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: LuaFacetType.java, Class: LuaFacetType
 * Last modified: 2010-02-11
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eightbitmage.moonscript.settings.facet;


import com.eightbitmage.moonscript.MoonIcons;
import com.eightbitmage.moonscript.module.MoonModuleType;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MoonFacetType extends FacetType<MoonFacet, MoonFacetConfiguration> {
    public static final FacetTypeId<MoonFacet> ID = new FacetTypeId<MoonFacet>("Lua");
    public static final MoonFacetType INSTANCE = new MoonFacetType();

    public MoonFacetType() {
        super(ID, "Lua", "Lua");
    }

    @Override
    public MoonFacetConfiguration createDefaultConfiguration() {
        return new MoonFacetConfiguration();
    }

    @Override
    public MoonFacet createFacet(@NotNull Module module, String name,
                                 @NotNull MoonFacetConfiguration configuration,
                                 @Nullable Facet underlyingFacet) {
        return new MoonFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
         return !(moduleType instanceof MoonModuleType);
    }

    @Override
    public Icon getIcon() {
        return MoonIcons.MOON_ICON;
    }
}