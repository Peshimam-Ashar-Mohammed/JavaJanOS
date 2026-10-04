package com.javaos.commands.java;

import com.javaos.filesystem.VirtualFileSystem;
import com.javaos.runtime.JavaCompilerService;
import com.javaos.shell.CommandRegistry;

import com.javaos.runtime.ProcessManager;

public final class JavaCommands {

    private JavaCommands() {
    }

    public static void register(CommandRegistry registry,VirtualFileSystem fileSystem) 
    {
        JavaCompilerService compiler = new JavaCompilerService();

        registry.register("javac", "Compile a Java source file", arguments -> {

            if (arguments.isBlank()) {
                System.out.println("Usage: javac <filename.java>");
                return;
            }

            var sourceFile = fileSystem.resolveForCommand(arguments.trim());

            compiler.compile(sourceFile);
        });

        ProcessManager processManager = new ProcessManager();

        registry.register("java", "Run a compiled Java class", arguments -> {

            if (arguments.isBlank()) {
                System.out.println("Usage: java <className>");
                return;
            }

            String className = arguments.trim();

            if (!className.matches("[A-Za-z_$][A-Za-z0-9_$]*(\\.[A-Za-z_$][A-Za-z0-9_$]*)*")) {
                System.out.println("Invalid Java class name.");
                return;
            }

            var workingDirectory = fileSystem.resolveForCommand(".");

            processManager.runAndPrintInDirectory(
                    workingDirectory,
                    "java",
                    "-cp",
                    workingDirectory.toString(),
                    className
            );
        });

    }
}