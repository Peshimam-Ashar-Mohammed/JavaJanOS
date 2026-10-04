package com.javaos.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class VirtualFileSystem {

    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    private final Path root;
    private Path currentDirectory;

    public VirtualFileSystem() throws IOException {
        root = Paths.get("env").toAbsolutePath().normalize();
        Files.createDirectories(root.resolve("home"));
        Files.createDirectories(root.resolve("etc"));
        Files.createDirectories(root.resolve("tmp"));
        Files.createDirectories(root.resolve("root"));
        currentDirectory = root.resolve("home");
    }

    public String getCurrentPath() {
        return "/" + root.relativize(currentDirectory).toString().replace("\\", "/");
    }

    public void listFiles() throws IOException {
        try (var files = Files.list(currentDirectory)) {
            files.forEach(path -> {
                String name = path.getFileName().toString();
                System.out.println(Files.isDirectory(path) ? BLUE + name + "/" + RESET : name);
            });
        }
    }

    public Path resolvePath(String input) {
        Path target = input.startsWith("/")
                ? root.resolve(input.substring(1))
                : currentDirectory.resolve(input);
        target = target.normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Access denied: Outside virtual filesystem.");
        }
        return target;
    }

    public Path resolveForCommand(String input) {
        return resolvePath(input);
    }

    public void makeDirectory(String name) throws IOException {
        Path target = resolvePath(name);
        if (Files.exists(target)) {
            System.out.println("Directory or file already exists.");
            return;
        }
        Files.createDirectory(target);
        System.out.println("Directory created: " + name);
    }

    public void changeDirectory(String name) {
        Path target = resolvePath(name);
        if (!Files.exists(target)) {
            System.out.println("Directory does not exist.");
        } else if (!Files.isDirectory(target)) {
            System.out.println("Not a directory.");
        } else {
            currentDirectory = target;
        }
    }

    public void createFile(String name) throws IOException {
        Path target = resolvePath(name);
        if (Files.exists(target)) {
            System.out.println("File already exists.");
            return;
        }
        Files.createFile(target);
        System.out.println("File created: " + name);
    }

    public void readFile(String name) throws IOException {
        Path target = resolvePath(name);
        if (!Files.exists(target)) {
            System.out.println("File does not exist.");
        } else if (!Files.isRegularFile(target)) {
            System.out.println("Not a file.");
        } else {
            System.out.println(Files.readString(target));
        }
    }

    public void writeFile(String name, String content, boolean append) throws IOException {
        Path target = resolvePath(name);
        if (Files.isDirectory(target)) {
            System.out.println("Cannot write to a directory.");
            return;
        }
        if (append) {
            Files.writeString(target, content + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            Files.writeString(target, content + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        System.out.println("File updated: " + name);
    }

    public void removeFile(String name) throws IOException {
        Path target = resolvePath(name);
        if (!Files.exists(target)) {
            System.out.println("File does not exist.");
        } else if (Files.isDirectory(target)) {
            System.out.println("Cannot use rm on a directory. Use rmdir.");
        } else {
            Files.delete(target);
            System.out.println("File removed: " + name);
        }
    }

    public void removeDirectory(String name) throws IOException {
        Path target = resolvePath(name);
        if (!Files.exists(target)) {
            System.out.println("Directory does not exist.");
        } else if (!Files.isDirectory(target)) {
            System.out.println("That is a file, not a directory.");
        } else if (target.equals(root)) {
            System.out.println("Cannot remove the virtual root directory.");
        } else if (target.equals(currentDirectory)) {
            System.out.println("Cannot remove your current directory.");
        } else {
            try {
                Files.delete(target);
                System.out.println("Directory removed: " + name);
            } catch (java.nio.file.DirectoryNotEmptyException e) {
                System.out.println("Directory is not empty. Remove its contents first.");
            }
        }
    }


    public String loadFileContent(String name) throws IOException {
        Path target = resolvePath(name);

        if (Files.exists(target) && !Files.isRegularFile(target)) {
            throw new IOException("Not a regular file: " + name);
        }

        if (!Files.exists(target)) {
            return "";
        }

        return Files.readString(target);
    }

    public void saveFileContent(String name, String content) throws IOException {
        Path target = resolvePath(name);

        if (Files.isDirectory(target)) {
            throw new IOException("Cannot save to a directory.");
        }

        Files.writeString(target, content);
    }


}
