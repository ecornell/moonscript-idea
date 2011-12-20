package com.eightbitmage.moonscript.compiler;

import com.eightbitmage.moonscript.MoonBundle;
import com.eightbitmage.moonscript.options.MoonSettings;
import com.eightbitmage.moonscript.settings.MoonProjectSettings;
import com.eightbitmage.moonscript.settings.MoonProjectSettingsConfigurable;
import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JFlexx wrapper to command line tool.
 *
 * @author Alexey Efimov
 */
public final class MoonScript {
    @NonNls
    private static final Pattern LINE_NUMBER_PATTERN = Pattern.compile(".*\\[(\\d+)\\].*?");

    @NonNls
    public static final String BIN_BASH = "/bin/bash";
    @NonNls
    public static final String HYPHEN_C = "-c";
    @NonNls
    public static final String SLASH_C = "/C";
    @NonNls
    private static final String COMMAND_COM = "command.com";
    @NonNls
    private static final String CMD_EXE = "cmd.exe";
    @NonNls
    private static final char QUOT = '"';
    @NonNls
    private static final char SPACE = ' ';

    @NotNull
    public static Map<CompilerMessageCategory, List<MoonScriptMessage>> compile(VirtualFile file) throws IOException, CantRunException {

        MoonSettings settings = MoonSettings.getInstance();

        StringBuilder command =  new StringBuilder();

        command.append(settings.MOON_HOME).append(SPACE);
        VirtualFile parent = file.getParent();
        command.append(SPACE).append(QUOT).append(file.getName()).append(QUOT);


        String shell = SystemInfo.isWindows ? SystemInfo.isWindows9x ? COMMAND_COM : CMD_EXE : BIN_BASH;
        String[] commands;
        if (SystemInfo.isWindows) {
            commands = new  String[]{shell, SLASH_C, QUOT + command.toString() + QUOT};
        } else {
            commands = new  String[]{shell, HYPHEN_C, command.toString()};
        }

        //Process process = Runtime.getRuntime().exec(commands, null, new File(settings.MOON_HOME));
        Process process = Runtime.getRuntime().exec(commands, null, new File(parent.getPath()));

        try {
            InputStream out = process.getInputStream();
            try {
                InputStream err = process.getErrorStream();
                try {
                    List<MoonScriptMessage> information = new ArrayList<MoonScriptMessage>();
                    List<MoonScriptMessage> error = new ArrayList<MoonScriptMessage>();
                    filter(StreamUtil.readText(out), information, error);
                    filter(StreamUtil.readText(err), information, error);
                    Map<CompilerMessageCategory, List<MoonScriptMessage>> messages = new HashMap<CompilerMessageCategory, List<MoonScriptMessage>>();
                    messages.put(CompilerMessageCategory.ERROR, error);
                    messages.put(CompilerMessageCategory.INFORMATION, information);
                    int code = 0;
                    try {
                        code = process.waitFor();
                    } catch (InterruptedException e) {
                        List<MoonScriptMessage> warnings = new ArrayList<MoonScriptMessage>();
                        warnings.add(new MoonScriptMessage("Interrupted while waiting for MoonScript to complete"));
                        messages.put(CompilerMessageCategory.WARNING, warnings);
                    }
                    if (code == 0) {
                        return messages;
                    } else {
                        if (messages.get(CompilerMessageCategory.ERROR).size() > 0) {
                            return messages;
                        }
                        throw new IOException(MoonBundle.message("command.0.execution.failed.with.exit.code.1", command, code));
                    }
                } finally {
                    err.close();
                }
            } finally {
                out.close();
            }
        } finally {
            process.destroy();
        }
    }

    private static void filter(@NonNls String output, @NotNull List<MoonScriptMessage> information, @NotNull List<MoonScriptMessage> error) {
        if (!StringUtil.isEmptyOrSpaces(output)) {
            String[] lines = output.split("[\\n\\r]+");
            for (int i = 0; i < lines.length; i++) {
                @NonNls String line = lines[i];
                if (line.startsWith(" [")) {
                    // Parse "error in file" message
                    // it look like:
                    // Error in file "MoonScript.flex" (line 72):
                    // Syntax error.
                    // <LEXI CAL_RULES> {
                    //       ^
                    String message = line;
                    int lineNumber = -1;
                    int columnNumber = 1;
                    Matcher matcher = LINE_NUMBER_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        try {
                            lineNumber = Integer.parseInt(matcher.group(1));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    // Skip line
//                    i++;
//                    char[] columnPointer = lines[++i].toCharArray();
//                    for (int j = 0; columnNumber == -1 && j < columnPointer.length; j++) {
//                        char c = columnPointer[j];
//                        if (c != ' ') {
//                            if (c == '^') {
//                                columnNumber = j + 1;
//                            } else {
//                                // It is invalid code pointer line
//                                // Rollback i to previous lines
//                                i -= 2;
//                                break;
//                            }
//                        }
//                    }
                    error.add(new MoonScriptMessage(message, lineNumber, columnNumber));
                }
            }
        }
    }

    public static boolean validateConfiguration(Project project) {
//        MoonSettings settings = MoonSettings.getInstance();
//        File home = new File(settings.MOON_HOME);
//        if (home.isDirectory() && home.exists()) {
//            if (!StringUtil.isEmptyOrSpaces(settings.SKELETON_PATH) && settings.COMMAND_LINE_OPTIONS.indexOf(OPTION_SKEL) == -1) {
//                File skel = new File(settings.SKELETON_PATH);
//                if (!skel.isFile() || !skel.exists()) {
//                    return showWarningMessageAndConfigure(project, JFlexBundle.message("jflex.skeleton.file.was.not.found"));
//                }
//            }
//        } else {
//            return showWarningMessageAndConfigure(project, JFlexBundle.message("jflex.home.path.is.invalid"));
//        }
        return true;
    }

    private static boolean showWarningMessageAndConfigure(final Project project, String message) {
        Messages.showWarningDialog(project, message, MoonBundle.message("moduletype.name"));
        // Show settings
        final Application application = ApplicationManager.getApplication();
        application.invokeLater(new Runnable() {
            public void run() {
                ShowSettingsUtil.getInstance().editConfigurable(project, application.getComponent(MoonProjectSettingsConfigurable.class));
            }
        });
        return false;
    }

    public static boolean isCompilationEnabled() {
        //return MoonProjectSettings.getInstance().ENABLED_COMPILATION;
        return true;
    }
}
