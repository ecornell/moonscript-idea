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

package com.eightbitmage.moonscript.editor.completion;


import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.MoonPsiManager;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;
import com.eightbitmage.moonscript.lang.psi.impl.statements.MoonFunctionDefinitionStatementImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonGlobalDeclarationIndex;
import com.eightbitmage.moonscript.lang.psi.symbols.*;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonRecursiveElementVisitor;
import com.eightbitmage.moonscript.options.MoonApplicationSettings;
import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class MoonCompletionContributor extends DefaultCompletionContributor {
    private static final Logger log = Logger.getInstance("Lua.CompletionContributor");

    private static final ElementPattern<PsiElement> AFTER_SELF_DOT = psiElement().withParent(MoonCompoundIdentifier.class).afterSibling(psiElement().withName("self"));
    private static final ElementPattern<PsiElement> AFTER_DOT = psiElement().afterLeaf(".", ":");

    private static final ElementPattern<PsiElement> AFTER_FUNCTION = psiElement().afterLeafSkipping(psiElement().whitespace(), PlatformPatterns.string().matches("function"));
    
    private static final ElementPattern<PsiElement> NOT_AFTER_DOT = psiElement().withParent(MoonIdentifier.class).andNot(psiElement().afterLeaf(".", ":"));

    private static final Key<Collection<String>> PREFIX_FILTERED_GLOBALS_COLLECTION = new Key<Collection<String>>("moon.prefix.globals");

    private static final ElementPattern<PsiElement> AFTER_SELF =
            psiElement().withParent(MoonSymbol.class).afterLeaf(":",".");

    private Collection<String> getAllGlobals(@NotNull CompletionParameters parameters, ProcessingContext context) {
        return MoonPsiManager.getInstance(parameters.getOriginalFile().getProject()).getFilteredGlobalsCache();
    }

    private Collection<String> getPrefixFilteredGlobals(String prefix, @NotNull CompletionParameters parameters, ProcessingContext context) {
        Collection<String> names = context.get(PREFIX_FILTERED_GLOBALS_COLLECTION);
        if (names != null) return names;

        names = new ArrayList<String>();

        int prefixLen = prefix.length();
        for (String key : getAllGlobals(parameters, context)) {
            if (key.length() > prefixLen && key.startsWith(prefix))
                names.add(key);
        }

        context.put(PREFIX_FILTERED_GLOBALS_COLLECTION, names);
        return names;
    }

    public MoonCompletionContributor() {
        extend(CompletionType.BASIC, NOT_AFTER_DOT, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                for (String s : MoonKeywordsManager.getKeywords())
                    result.addElement(new MoonLookupElement(s));
            }
        });


//        extend(CompletionType.BASIC, AFTER_FUNCTION, new CompletionProvider<CompletionParameters>() {
//            @Override
//            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context,
//                                          @NotNull CompletionResultSet result) {
//                String prefix = result.getPrefixMatcher().getPrefix();
//
//                for (String key : getPrefixFilteredGlobals(prefix, parameters, context)) {
//                    result.addElement(new MoonLookupElement(key));
//                }
//            }
//        });
        extend(CompletionType.BASIC, NOT_AFTER_DOT, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                if (!MoonApplicationSettings.getInstance().INCLUDE_ALL_FIELDS_IN_COMPLETIONS) return;


                MoonPsiFile file = (MoonPsiFile) parameters.getOriginalFile();

                globalUsageVisitor.reset();

                file.acceptChildren(globalUsageVisitor);
                String prefix = result.getPrefixMatcher().getPrefix();
                int prefixLen = prefix.length();
                for (String key : globalUsageVisitor.getResult()) {

                    if (key.length() > prefixLen && key.startsWith(prefix))
                        result.addElement(new MoonLookupElement(key));
                }
            }
        });

        extend(CompletionType.BASIC, AFTER_SELF, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement element = parameters.getPosition();

                try {
                    MoonCompoundIdentifier cid = (MoonCompoundIdentifier) element.getContext().getContext();

                    if (!cid.getLeftSymbol().equals("self"))
                        return;
                } catch (Exception e) {
                    return;
                }

                while (!(element instanceof MoonFunctionDefinitionStatementImpl) && element != null)
                    element = element.getContext();

                // Must be inside a function
                if (element == null) return;

                MoonFunctionDefinitionStatementImpl func = (MoonFunctionDefinitionStatementImpl) element;

                MoonSymbol symbol = func.getIdentifier();

                int colonIdx = symbol.getText().lastIndexOf(':');
                int dotIdx = symbol.getText().lastIndexOf('.');
                if (colonIdx < 0 && dotIdx < 0) return;

                int idx = Math.max(colonIdx, dotIdx);

                String prefix = symbol.getText().substring(0, idx+1);

                for(String key : MoonGlobalDeclarationIndex.getInstance().getAllKeys(element.getProject())) {
//                    System.out.println(key);

                    if (key.startsWith(prefix)) {
                        result.addElement(new MoonLookupElement("self:"+key.substring(prefix.length())));
                        result.addElement(new MoonLookupElement("self."+key.substring(prefix.length())));
                    }
                }

                fieldVisitor.reset();

                ((MoonPsiFile)parameters.getOriginalFile()).accept(fieldVisitor);

                for (String s : fieldVisitor.getResult()) {
                    if (s.startsWith(prefix)) {
                        result.addElement(new MoonLookupElement("self:"+s));
                        result.addElement(new MoonLookupElement("self."+s));
                    }
                }
                

            }
        });



        extend(CompletionType.BASIC, AFTER_DOT, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                if (!MoonApplicationSettings.getInstance().INCLUDE_ALL_FIELDS_IN_COMPLETIONS)
                    return;

                String prefix = result.getPrefixMatcher().getPrefix();
                String matchPrefix = null;

                for(int i=prefix.length()-1; i>=0; i--)
                    if (prefix.charAt(i) == '.' || prefix.charAt(i) == ':') {
                        matchPrefix = prefix.substring(i+1,  prefix.length());
                        prefix = prefix.substring(0, i+1);
                        break;
                    }

                for(String key : getAllGlobals(parameters, context)) {
                    if (matchPrefix != null && key.startsWith(matchPrefix))
                        result.addElement(new MoonLookupElement(prefix + key));
                }
            }
        });

    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        int end = context.getIdentifierEndOffset();
        int start = context.getStartOffset();
        String identifierToReplace = context.getEditor().getDocument().getText(new TextRange(start-1, end));

        if (identifierToReplace.charAt(0) == '.' || identifierToReplace.charAt(0) == ':')
            context.setReplacementOffset(start);

        super.beforeCompletion(context);
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);    //To change body of overridden methods use File | Settings | File Templates.
    }

    MoonFieldElementVisitor fieldVisitor = new MoonFieldElementVisitor();
    MoonGlobalUsageVisitor globalUsageVisitor = new MoonGlobalUsageVisitor();

    private class MoonFieldElementVisitor extends MoonRecursiveElementVisitor {
        Set<String> result = new HashSet<String>();

        @Override
        public void visitIdentifier(MoonIdentifier e) {
            super.visitIdentifier(e);

            if (e instanceof MoonFieldIdentifier && e.getTextLength() > 0 && e.getText().charAt(0) != '[' && e.getName() != null)
                result.add(e.getName());

        }

        public Set<String> getResult() {
            return result;
        }

        public void reset() { result.clear(); }
    }


    private class MoonGlobalUsageVisitor extends MoonRecursiveElementVisitor {
        Set<String> result = new HashSet<String>();

        @Override
        public void visitIdentifier(MoonIdentifier e) {
            super.visitIdentifier(e);

            if (e instanceof MoonGlobalIdentifier && e.getTextLength() > 0 && e.getName() != null)
                result.add(e.getName());
        }

        public Set<String> getResult() {
            return result;
        }

        public void reset() { result.clear(); }
    }
}