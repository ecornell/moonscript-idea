package com.eightbitmage.moonscript.lang.psi.resolve;

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.impl.statements.MoonFunctionDefinitionStatementImpl;
import com.eightbitmage.moonscript.lang.psi.resolve.processors.ResolveProcessor;
import com.eightbitmage.moonscript.lang.psi.resolve.processors.SymbolResolveProcessor;
import com.eightbitmage.moonscript.lang.psi.statements.MoonLocalDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonGlobalDeclarationIndex;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonLocal;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.options.MoonApplicationSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MoonResolver implements ResolveCache.PolyVariantResolver<MoonReferenceElement> {
    public static final Logger log = Logger.getInstance("Lua.MoonResolver");

    boolean ignoreAliasing = false;
    public void setIgnoreAliasing(boolean b) { ignoreAliasing=b; }
    public boolean getIgnoreAliasing() { return ignoreAliasing; }

    static Collection<MoonDeclarationExpression> filteredGlobalsCache = null;

    @Nullable
    public MoonResolveResult[] resolve(MoonReferenceElement reference, boolean incompleteCode) {
        if (reference.getText() == null) return MoonResolveResult.EMPTY_ARRAY;
        final MoonResolveResult[] results = _resolve(reference, reference.getManager(), incompleteCode, ignoreAliasing);
        return results;
    }

    private static MoonResolveResult[] _resolve(MoonReferenceElement ref,
                                               PsiManager manager, boolean incompleteCode, boolean ignoreAliasing) {


        PsiElement element = ref.getElement();

        String prefix = null, postfix = null;
        MoonResolveResult[] selfResolves = null;
        if (element.getText().startsWith("self.") || element.getText().startsWith("self:")) {
            postfix = element.getText().substring(5);
            prefix = findSelfPrefix(element);
            ResolveProcessor processor = new SymbolResolveProcessor(prefix + postfix, ref, incompleteCode);
            ResolveUtil.treeWalkUp(ref, processor);
            if (processor.hasCandidates()) {
                selfResolves = processor.getCandidates();
            }
        }

        final String refName = ref.getName();
        if (refName == null) {
            return MoonResolveResult.EMPTY_ARRAY;
        }
        ResolveProcessor processor = new SymbolResolveProcessor(refName, ref, incompleteCode);
        if (selfResolves != null)
            for(MoonResolveResult result : selfResolves)
                processor.addCandidate(result);

        ResolveUtil.treeWalkUp(ref, processor);

        if (/*processor.hasCandidates() || */ref.getElement() instanceof MoonLocal) {
            if (!processor.hasCandidates())
                return MoonResolveResult.EMPTY_ARRAY;

            return new MoonResolveResult[]{processor.getCandidates()[0]};
        }

        // Search the Project Files
        final Project project = manager.getProject();
        final GlobalSearchScope sc = ref.getResolveScope();
        final MoonPsiFile currentFile = (MoonPsiFile) ref.getContainingFile();

        MoonGlobalDeclarationIndex index = MoonGlobalDeclarationIndex.getInstance();
        Collection<MoonDeclarationExpression> names = index.get(refName, project, sc);
        for (MoonDeclarationExpression name : names) {
            name.processDeclarations(processor, ResolveState.initial(), ref, ref);
        }

        if (processor.hasCandidates()) {
            return processor.getCandidates();
        }

        return MoonResolveResult.EMPTY_ARRAY;
    }



    private static String findSelfPrefix(PsiElement element) {
        while (!(element instanceof MoonFunctionDefinitionStatementImpl) && element != null)
            element = element.getContext();

        // Must be inside a function
        if (element == null) return null;

        MoonFunctionDefinitionStatementImpl func = (MoonFunctionDefinitionStatementImpl) element;

        MoonSymbol symbol = func.getIdentifier();

        int colonIdx = symbol.getText().lastIndexOf(':');
        int dotIdx = symbol.getText().lastIndexOf('.');
        if (colonIdx < 0 && dotIdx < 0) return null;

        int idx = Math.max(colonIdx, dotIdx);

        String prefix = symbol.getText().substring(0, idx + 1);
        return prefix;
    }

    public static MoonReferenceElement resolveAlias(MoonReferenceElement ref, @NotNull PsiElement resolved) {

        if (!MoonApplicationSettings.getInstance().RESOLVE_ALIASED_IDENTIFIERS)
            return null;

        if (resolved instanceof MoonLocal && resolved.getContext().getContext() instanceof MoonLocalDefinitionStatement) {
            MoonLocalDefinitionStatement stat = (MoonLocalDefinitionStatement) resolved.getContext().getContext();

            MoonDeclarationExpression[] decls = stat.getDeclarations();
            MoonExpression[] exprs = stat.getExprs();

            if (exprs != null && exprs.length > 0) {
                MoonExpression aliasedExpression = null;
                for (int i = 0; i < decls.length; i++) {
                    if (decls[i] == resolved && exprs.length > i)
                        aliasedExpression = exprs[i];
                }

                if (aliasedExpression instanceof MoonReferenceElement) {
                    return (MoonReferenceElement) aliasedExpression;
                }
            }
        }

        return null;
    }
}