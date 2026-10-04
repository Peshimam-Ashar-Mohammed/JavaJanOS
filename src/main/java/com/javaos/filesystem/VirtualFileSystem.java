package com.javaos.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.DirectoryNotEmptyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class VirtualFileSystem {

    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    private final Path root;
    private final Path realRoot;
    private Path currentDirectory;

    public VirtualFileSystem() throws IOException {
        root = Paths.get("env").toAbsolutePath().normalize();
        Files.createDirectories(root);
        realRoot = root.toRealPath();
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

    public void printTree(String input) throws IOException {
        Path target = safeExistingPath(input);
        if (!Files.isDirectory(target)) {
            throw new IOException("Not a directory: " + input);
        }
        System.out.println(displayPath(target));
        printTreeChildren(target, "");
    }

    private void printTreeChildren(Path directory, String prefix) throws IOException {
        List<Path> children = safeChildren(directory);
        for (int i = 0; i < children.size(); i++) {
            Path child = children.get(i);
            boolean last = i == children.size() - 1;
            System.out.println(prefix + (last ? "└── " : "├── ") + child.getFileName()
                    + (Files.isDirectory(child, java.nio.file.LinkOption.NOFOLLOW_LINKS) ? "/" : ""));
            if (Files.isDirectory(child, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
                printTreeChildren(child, prefix + (last ? "    " : "│   "));
            }
        }
    }

    public void copyFile(String sourceInput, String destinationInput) throws IOException {
        Path source = safeExistingPath(sourceInput);
        if (!Files.isRegularFile(source, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Source is not a regular file: " + sourceInput);
        }
        Path destination = destinationPath(destinationInput, source.getFileName().toString());
        ensureDestinationParent(destination);
        if (Files.exists(destination, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Destination already exists: " + destinationInput);
        }
        Files.copy(source, destination);
        System.out.println("Copied " + sourceInput + " to " + destinationInput + ".");
    }

    public void move(String sourceInput, String destinationInput) throws IOException {
        Path source = safeExistingPath(sourceInput);
        if (source.equals(root)) {
            throw new IOException("Cannot move the virtual root directory.");
        }
        if (source.equals(currentDirectory)) {
            throw new IOException("Cannot move the current directory.");
        }
        Path destination = destinationPath(destinationInput, source.getFileName().toString());
        ensureDestinationParent(destination);
        if (Files.exists(destination, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Destination already exists: " + destinationInput);
        }
        if (Files.isDirectory(source, java.nio.file.LinkOption.NOFOLLOW_LINKS)
                && destination.startsWith(source)) {
            throw new IOException("Cannot move a directory into itself.");
        }
        Files.move(source, destination);
        System.out.println("Moved " + sourceInput + " to " + destinationInput + ".");
    }

    public List<String> find(String input, String filename) throws IOException {
        Path start = safeExistingPath(input);
        if (!Files.isDirectory(start)) {
            throw new IOException("Search path is not a directory: " + input);
        }
        List<String> matches = new ArrayList<>();
        findChildren(start, filename, matches);
        return matches;
    }

    private void findChildren(Path directory, String filename, List<String> matches) throws IOException {
        for (Path child : safeChildren(directory)) {
            if (child.getFileName().toString().equals(filename)) {
                matches.add(displayPath(child));
            }
            if (Files.isDirectory(child, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
                findChildren(child, filename, matches);
            }
        }
    }

    public long diskUsage(String input) throws IOException {
        Path target = safeExistingPath(input);
        return diskUsage(target, new HashSet<>());
    }

    private long diskUsage(Path path, Set<Path> visited) throws IOException {
        if (Files.isSymbolicLink(path) || !visited.add(path.toRealPath())) {
            return 0;
        }
        if (Files.isRegularFile(path, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            return Files.size(path);
        }
        if (!Files.isDirectory(path, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            return 0;
        }
        long total = 0;
        for (Path child : safeChildren(path)) {
            total += diskUsage(child, visited);
        }
        return total;
    }

    public String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        if (bytes < 1024L * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private Path safeExistingPath(String input) throws IOException {
        Path target = resolvePath(input);
        if (!Files.exists(target, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Path does not exist: " + input);
        }
        if (Files.isSymbolicLink(target)) {
            throw new IOException("Symbolic links are not supported: " + input);
        }
        if (!target.toRealPath().startsWith(realRoot)) {
            throw new IOException("Access denied: Outside virtual filesystem.");
        }
        return target;
    }

    private Path destinationPath(String input, String sourceName) {
        Path destination = resolvePath(input);
        if (Files.isSymbolicLink(destination)) {
            throw new IllegalArgumentException("Destination cannot be a symbolic link.");
        }
        if (Files.isDirectory(destination, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            destination = destination.resolve(sourceName);
        }
        return destination.normalize();
    }

    private void ensureDestinationParent(Path destination) throws IOException {
        if (!destination.startsWith(root)) {
            throw new IOException("Access denied: Outside virtual filesystem.");
        }
        Path parent = destination.getParent();
        if (parent == null || !Files.isDirectory(parent, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Destination directory does not exist.");
        }
        if (!parent.toRealPath().startsWith(realRoot)) {
            throw new IOException("Access denied: Outside virtual filesystem.");
        }
    }

    private List<Path> safeChildren(Path directory) throws IOException {
        List<Path> children = new ArrayList<>();
        try (var stream = Files.list(directory)) {
            stream.filter(path -> !Files.isSymbolicLink(path))
                    .forEach(children::add);
        } catch (SecurityException | DirectoryNotEmptyException e) {
            throw new IOException("Cannot access directory: " + displayPath(directory), e);
        }
        children.sort(Comparator
                .comparing((Path path) -> !Files.isDirectory(path, java.nio.file.LinkOption.NOFOLLOW_LINKS))
                .thenComparing(path -> path.getFileName().toString(), String.CASE_INSENSITIVE_ORDER));
        return children;
    }

    private String displayPath(Path path) {
        if (path.equals(root)) {
            return "/";
        }
        return "/" + root.relativize(path).toString().replace("\\", "/");
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
