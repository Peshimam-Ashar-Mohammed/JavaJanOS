package com.javaos.commands.filesystem;

import com.javaos.filesystem.VirtualFileSystem;
import com.javaos.shell.CommandRegistry;

import java.io.IOException;

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
    }
}
