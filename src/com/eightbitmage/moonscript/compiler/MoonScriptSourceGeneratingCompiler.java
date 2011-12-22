package com.eightbitmage.moonscript.compiler;

import com.eightbitmage.moonscript.MoonBundle;
import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiFileImpl;
import com.intellij.compiler.CompilerConfiguration;
import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.execution.CantRunException;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The source generating compiler for *.moon files.
 */
public class MoonScriptSourceGeneratingCompiler implements SourceGeneratingCompiler, ProjectComponent {
    @NonNls
    private static final String MOON_FILE_NAME_PATTERN = "{0}.lua";

    private final Project project;
    private CompilerManager compilerManager;

    public MoonScriptSourceGeneratingCompiler(Project project, CompilerManager compilerManager) {
        this.project = project;
        this.compilerManager = compilerManager;
    }

    public void projectOpened() {

    }

    public void projectClosed() {

    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return "MoonScriptCompiler";
    }

    public void initComponent() {
        compilerManager.addCompilableFileType(MoonFileType.MOON_FILE_TYPE);
        compilerManager.addCompiler(this);
    }

    public void disposeComponent() {
        compilerManager.removeCompiler(this);
        compilerManager.removeCompilableFileType(MoonFileType.MOON_FILE_TYPE);

    }

    private static final GenerationItem[] EMPTY_GENERATION_ITEM_ARRAY = new GenerationItem[]{};

    public GenerationItem[] getGenerationItems(CompileContext context) {
        if (MoonScript.isCompilationEnabled()) {
            Module[] affectedModules = context.getCompileScope().getAffectedModules();
            if (affectedModules.length > 0) {
                Application application = ApplicationManager.getApplication();
                return application.runReadAction(new PrepareAction(context));
            }
        }
        return EMPTY_GENERATION_ITEM_ARRAY;
    }

    public GenerationItem[] generate(CompileContext context, GenerationItem[] items, VirtualFile outputRootDirectory) {
        if (MoonScript.isCompilationEnabled()) {
            if (items != null && items.length > 0) {
                Application application = ApplicationManager.getApplication();
                GenerationItem[] generationItems = application.runReadAction(new GenerateAction(context, items, outputRootDirectory));
                for (GenerationItem item : generationItems) {
                    CompilerUtil.refreshIOFile(((MoonGenerationItem) item).getGeneratedFile());
                }
                return generationItems;
            }
        }
        return EMPTY_GENERATION_ITEM_ARRAY;
    }

    @NotNull
    public String getDescription() {
        return "moonscript";
    }

    public boolean validateConfiguration(CompileScope scope) {
        if (MoonScript.isCompilationEnabled()) {
            Module[] affectedModules = scope.getAffectedModules();
            if (affectedModules.length > 0) {
                Project project = affectedModules[0].getProject();
                VirtualFile[] files = scope.getFiles(MoonFileType.MOON_FILE_TYPE, false);
                if (files.length > 0) {
                    return MoonScript.validateConfiguration(project);
                }
            }
        }
        return true;
    }

    public ValidityState createValidityState(DataInput in) throws IOException {
        return TimestampValidityState.load(in);
    }

    @Override
    public VirtualFile getPresentableFile(CompileContext context, Module module, VirtualFile outputRoot, VirtualFile generatedFile) {
        //todo: imp this ?
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static class MoonGenerationItem implements GenerationItem {
        private final Module module;
        private final boolean testSource;
        private final VirtualFile file;
        private final String generatedClassName;
        private final File generatedFile;
        private String path;

        public MoonGenerationItem(Module module, VirtualFile file, boolean testSource) {
            this.file = file;
            this.module = module;
            this.testSource = testSource;
//            String generationName = null;
//            PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(file);

//            if (psiFile instanceof MoonPsiFile) {
//                MoonPsiFileImpl flexPsiFile = (MoonPsiFileImpl) psiFile;
//                ASTNode node = flexPsiFile.getNode();
//                if (node != null) {
//                    /* lazy-lazy parser won't actually parse a file it has not been opened in editor before.
//                     * let's persuade it pretending that we really need all the stuff in a file.*/
//                    node.getPsi().getChildren();
// TODO: Imp
//                    MoonPsiElement generateName = flexPsiFile. getClassname();
//                    if (generateName != null) {
//                        generationName = generateName.getText();
//                    }
//                }
//           }
            //this.generatedClassName = generationName;

            this.generatedClassName = file.getNameWithoutExtension();

            VirtualFile parent = file.getParent();
            this.generatedFile = parent != null ?
                new File(VfsUtil.virtualToIoFile(parent), MessageFormat.format(MOON_FILE_NAME_PATTERN, generatedClassName)) :
                null;
        }

        public VirtualFile getFile() {
            return file;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public ValidityState getValidityState() {
            return null;
        }

        public Module getModule() {
            return module;
        }

        @NotNull
        public File getGeneratedFile() {
            return generatedFile;
        }

        public boolean isTestSource() {
            return testSource;
        }
    }

    private final class PrepareAction implements Computable<GenerationItem[]> {
        private final CompileContext context;

        public PrepareAction(CompileContext context) {
            this.context = context;
        }

        public GenerationItem[] compute() {
            ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
            CompileScope compileScope = context.getCompileScope();
            VirtualFile[] files = compileScope.getFiles(MoonFileType.MOON_FILE_TYPE, false);
            List<GenerationItem> items = new ArrayList<GenerationItem>(files.length);
            CompilerConfiguration compilerConfiguration = CompilerConfiguration.getInstance(project);
            for (VirtualFile file : files) {
                if (context.isMake() && compilerConfiguration.isExcludedFromCompilation(file)) {
                    continue;
                }
                MoonGenerationItem generationItem = new MoonGenerationItem(context.getModuleByFile(file), file, fileIndex.isInTestSourceContent(file));
                if (context.isMake()) {
                    File generatedFile = generationItem.getGeneratedFile();
                    if (!generatedFile.exists() || generatedFile.lastModified() <= file.getTimeStamp()) {
                        items.add(generationItem);
                    }
                } else {
                    items.add(generationItem);
                }
            }
            return items.toArray(new GenerationItem[items.size()]);
        }
    }

    private static class GenerateAction implements Computable<GenerationItem[]> {
        private final CompileContext context;
        private final GenerationItem[] items;
        private final File outputDir;

        public GenerateAction(CompileContext context, GenerationItem[] items, VirtualFile outputRootDirectory) {
            this.context = context;
            this.items = items;
            outputDir = VfsUtil.virtualToIoFile(outputRootDirectory);
        }

        public GenerationItem[] compute() {
            List<GenerationItem> results = new ArrayList<GenerationItem>(items.length);
            for (GenerationItem item : items) {
                if (item instanceof MoonGenerationItem) {
                    MoonGenerationItem flexItem = (MoonGenerationItem) item;
                    VirtualFile file = flexItem.getFile();
                    if (file != null && file.isValid()) {
                        try {
                            Map<CompilerMessageCategory, List<MoonScriptMessage>> messages = MoonScript.compile(file);
                            addMessages(file, messages);
                            if (messages.get(CompilerMessageCategory.ERROR).isEmpty()) {
                                File outFile = flexItem.getGeneratedFile();
                                if (outFile.isFile() && outFile.exists()) {
                                    flexItem.setPath(FileUtil.getRelativePath(outputDir, outFile));
                                    results.add(flexItem);
                                } else {
                                    context.addMessage(CompilerMessageCategory.ERROR, MoonBundle.message("file.not.generated"), file.getUrl(), -1, -1);
                                }
                            }
                        } catch (IOException e) {
                            context.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), file.getUrl(), -1, -1);
                        } catch (CantRunException e) {
                            context.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), file.getUrl(), -1, -1);
                        }
                    }
                }
            }
            return results.toArray(new GenerationItem[results.size()]);
        }

        private void addMessages(VirtualFile file, Map<CompilerMessageCategory, List<MoonScriptMessage>> messages) {
            for (CompilerMessageCategory category : messages.keySet()) {
                List<MoonScriptMessage> messageList = messages.get(category);
                for (MoonScriptMessage message : messageList) {
                    context.addMessage(category, message.getMessage(), file.getUrl(), message.getLine(), message.getColumn());
                }
            }
        }
    }
}
