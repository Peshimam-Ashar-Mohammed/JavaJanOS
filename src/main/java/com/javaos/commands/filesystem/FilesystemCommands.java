package com.javaos.commands.filesystem;

import com.javaos.filesystem.VirtualFileSystem;
import com.javaos.shell.CommandRegistry;

import java.io.IOException;
import java.util.List;

public final class FilesystemCommands {

    private FilesystemCommands() {
    }

    public static void register(CommandRegistry registry, VirtualFileSystem fileSystem) {
        registry.register("pwd", "Print the current working directory",
                arguments -> System.out.println(fileSystem.getCurrentPath()));
        registry.register("ls", "List files in the current directory",
                arguments -> fileSystem.listFiles());
        registry.register("mkdir", "Create a directory", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: mkdir <directory>");
            } else {
                fileSystem.makeDirectory(arguments);
            }
        });
        registry.register("cd", "Change the current directory", arguments ->
                fileSystem.changeDirectory(arguments.isBlank() ? "/home" : arguments));
        registry.register("touch", "Create a file", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: touch <filename>");
            } else {
                fileSystem.createFile(arguments);
            }
        });
        registry.register("cat", "Display file contents", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: cat <filename>");
            } else {
                fileSystem.readFile(arguments);
            }
        });
        registry.register("rm", "Remove a file", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: rm <filename>");
            } else {
                fileSystem.removeFile(arguments.trim());
            }
        });
        registry.register("rmdir", "Remove an empty directory", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: rmdir <directory>");
            } else {
                fileSystem.removeDirectory(arguments.trim());
            }
        });
        registry.register("echo", "Print text or write it to a file", arguments -> {
            if (arguments.contains(">>")) {
                String[] parts = arguments.split(">>", 2);
                fileSystem.writeFile(parts[1].trim(), parts[0].trim(), true);
            } else if (arguments.contains(">")) {
                String[] parts = arguments.split(">", 2);
                fileSystem.writeFile(parts[1].trim(), parts[0].trim(), false);
            } else {
                System.out.println(arguments);
            }
        });
        registry.register("tree", "Display a directory tree", arguments ->
                runSafely(() -> fileSystem.printTree(arguments.isBlank() ? "." : arguments.trim())));
        registry.register("cp", "Copy a file without overwriting", arguments ->
                runWithOperands(arguments, "cp <source> <destination>",
                        operands -> fileSystem.copyFile(operands.get(0), operands.get(1))));
        registry.register("mv", "Move or rename without overwriting", arguments ->
                runWithOperands(arguments, "mv <source> <destination>",
                        operands -> fileSystem.move(operands.get(0), operands.get(1))));
        registry.register("find", "Find matching filenames recursively", arguments ->
                runWithOperands(arguments, "find <directory> <filename>",
                        operands -> {
                            List<String> matches = fileSystem.find(operands.get(0), operands.get(1));
                            if (matches.isEmpty()) {
                                System.out.println("No matches found.");
                            } else {
                                matches.forEach(System.out::println);
                            }
                        }));
        registry.register("du", "Display file or directory size", arguments ->
                runSafely(() -> {
                    String path = arguments.isBlank() ? "." : arguments.trim();
                    long bytes = fileSystem.diskUsage(path);
                    System.out.println(fileSystem.formatSize(bytes) + "  " + path);
                }));
    }

    private static void runWithOperands(String arguments, String usage,
                                        OperandAction action) {
        List<String> operands = parseOperands(arguments);
        if (operands.size() != 2) {
            System.out.println("Usage: " + usage);
            return;
        }
        runSafely(() -> action.run(operands));
    }

    private static List<String> parseOperands(String arguments) {
        return List.of(arguments.trim().split("\\s+", 2));
    }

    private static void runSafely(IoAction action) {
        try {
            action.run();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Filesystem error: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface IoAction {
        void run() throws IOException;
    }

    @FunctionalInterface
    private interface OperandAction {
        void run(List<String> operands) throws IOException;
    }
}
