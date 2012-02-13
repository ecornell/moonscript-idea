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

package com.eightbitmage.moonscript.actions;

import com.eightbitmage.moonscript.MoonFileType;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;

import java.util.Properties;

public class MoonTemplatesFactory {
    public static final String NEW_SCRIPT_FILE_NAME = "MoonScript.moon";
    public static final String MOON_HEADER_NAME = "Moon Script File Header.moon";

    //private final FileTemplateGroupDescriptor templateGroup;
    private static final Logger log = Logger.getInstance("Moon.TemplatesFactory");

//    public MoonTemplatesFactory() {
//        templateGroup =
//                new FileTemplateGroupDescriptor(MoonBundle.message("file.template.group.title.moon"), MoonIcons.MOON_ICON);
//        templateGroup.addTemplate(NEW_SCRIPT_FILE_NAME);
//        templateGroup.addTemplate(MOON_HEADER_NAME);
//    }
//
//    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
//        return templateGroup;
//    }

    public static PsiFile createFromTemplate(final PsiDirectory directory, final String name,
                                             String fileName, String templateName,
                                           @NonNls String... parameters) throws IncorrectOperationException {
        log.debug("createFromTemplate: dir:" + directory + ", filename: " + fileName);

        final FileTemplate template = FileTemplateManager.getInstance().getTemplate(templateName);

        Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties());

        String text;

        try {
            text = template.getText(properties);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load template for " +
                                       FileTemplateManager.getInstance().internalTemplateToSubject(templateName), e);
        }

        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());

        log.debug("Create file from text");
        final PsiFile file = factory.createFileFromText(fileName, MoonFileType.MOON_FILE_TYPE, text);

        log.debug("Adding file to directory");
        return (PsiFile) directory.add(file);
    }

}