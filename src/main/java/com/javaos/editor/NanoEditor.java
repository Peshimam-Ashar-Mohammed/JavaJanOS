package com.javaos.editor;

import com.javaos.filesystem.VirtualFileSystem;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple line-command editor. It intentionally uses the normal JLine reader
 * instead of taking ownership of the terminal or interpreting key codes.
 */
public final class NanoEditor {

    private final VirtualFileSystem fileSystem;
    private final Terminal terminal;
    private final LineReader lineReader;
    private final List<String> lines = new ArrayList<>();

    private String filename;
    private boolean creatingFile;

    public NanoEditor(
            VirtualFileSystem fileSystem,
            Terminal terminal,
            LineReader lineReader) {
        this.fileSystem = fileSystem;
        this.terminal = terminal;
        this.lineReader = lineReader;
    }

    public void open(String filename) throws IOException {
        this.filename = filename;
        creatingFile = !Files.exists(fileSystem.resolvePath(filename));
        load();

        if (creatingFile) {
            editNewFile();
            return;
        }

        while (true) {
            printFileAndMenu();
            String input = lineReader.readLine("nano> ").trim();

            switch (input.toLowerCase()) {
                case "a", "add" -> appendLine();
                case "i", "insert" -> insertLine();
                case "e", "edit" -> editLine();
                case "d", "delete" -> deleteLine();
                case "p", "print" -> {
                    // The file is printed at the start of every menu cycle.
                }
                case "w", "write", ":w" -> save();
                case "q", "quit", ":q" -> {
                    return;
                }
                default -> handleLegacyCommand(input);
            }
        }
    }

    private void editNewFile() throws IOException {
            println("");
            println("Creating new file: " + filename);
            println("Type each line and press Enter to add it.");
            println(":p prints the file, :w saves, and :q exits.");

            while (true) {
                String input = lineReader.readLine("nano> ");
                switch (input) {
                    case ":p", "p", "print" -> printLines();
                    case ":w", "w", "write" -> save();
                    case ":wq", "wq" -> {
                        save();
                        return;
                    }
                    case ":q", "q", "quit" -> {
                        return;
                    }
                    default -> lines.add(input);
                }
            }
    }

    private void load() throws IOException {
        lines.clear();
        String content = fileSystem.loadFileContent(filename);

        if (!content.isEmpty()) {
            lines.addAll(Arrays.asList(content.split("\\R", -1)));
        }
    }

    private void printFileAndMenu() {
        println("");
        println("========== " + filename + " ==========");
        printLines();
        println("----------------------------------------");
        println("a/add     Append a new line");
        println("i/insert  Insert a line before a line number");
        println("e/edit    Edit an existing line");
        println("d/delete  Delete an entire line");
        println("p/print   Print the file again");
        println("w/write   Save the file");
        println("q/quit    Exit the editor");
    }

    private void printLines() {
        if (lines.isEmpty()) {
            println("(empty file)");
            return;
        }

        for (int index = 0; index < lines.size(); index++) {
            println((index + 1) + "  " + lines.get(index));
        }
    }

    private void appendLine() {
        lines.add(lineReader.readLine("new line> "));
    }

    private void editLine() {
        Integer lineNumber = readLineNumber("Line to edit (1-" + lines.size() + ")> ", false);
        if (lineNumber == null) {
            return;
        }

        String replacement = lineReader.readLine("replacement for line " + lineNumber + "> ");
        lines.set(lineNumber - 1, replacement);
    }

    private void insertLine() {
        Integer lineNumber = readLineNumber(
                "Insert before line (1-" + Math.max(1, lines.size()) + ")> ", true);
        if (lineNumber == null) {
            return;
        }

        String inserted = lineReader.readLine("insert before line " + lineNumber + "> ");
        lines.add(lineNumber - 1, inserted);
    }

    private void deleteLine() {
        Integer lineNumber = readLineNumber("Line to delete (1-" + lines.size() + ")> ", false);
        if (lineNumber == null) {
            return;
        }

        lines.remove(lineNumber - 1);
        println("Deleted line " + lineNumber + ".");
    }

    private Integer readLineNumber(String prompt, boolean allowEnd) {
        String input = lineReader.readLine(prompt).trim();
        try {
            int maximum = allowEnd ? lines.size() + 1 : lines.size();
            int lineNumber = Integer.parseInt(input);
            if (lineNumber < 1 || lineNumber > maximum) {
                println("Line must be between 1 and " + maximum + ".");
                return null;
            }
            return lineNumber;
        } catch (NumberFormatException exception) {
            println("Please enter a valid line number.");
            return null;
        }
    }

    private void handleLegacyCommand(String input) throws IOException {
        if (input.equals(":p")) {
            printLines();
        } else if (input.startsWith(":e")) {
            editLineFromCommand(input);
        } else if (input.startsWith(":i")) {
            insertLineFromCommand(input);
        } else if (input.startsWith(":d")) {
            deleteLineFromCommand(input);
        } else if (!input.isEmpty()) {
            println("Unknown option. Choose a command from the menu.");
        }
    }

    private void editLineFromCommand(String command) {
        Integer lineNumber = parseLegacyLineNumber(command, ":e <line>", false);
        if (lineNumber != null) {
            lines.set(lineNumber - 1,
                    lineReader.readLine("replacement for line " + lineNumber + "> "));
        }
    }

    private void insertLineFromCommand(String command) {
        Integer lineNumber = parseLegacyLineNumber(command, ":i <line>", true);
        if (lineNumber != null) {
            lines.add(lineNumber - 1,
                    lineReader.readLine("insert before line " + lineNumber + "> "));
        }
    }

    private void deleteLineFromCommand(String command) {
        Integer lineNumber = parseLegacyLineNumber(command, ":d <line>", false);
        if (lineNumber != null) {
            lines.remove(lineNumber - 1);
            println("Deleted line " + lineNumber + ".");
        }
    }

    private Integer parseLegacyLineNumber(String command, String usage, boolean allowEnd) {
        String argument = command.substring(2).trim();
        if (argument.isEmpty()) {
            println("Usage: " + usage);
            return null;
        }
        try {
            int maximum = allowEnd ? lines.size() + 1 : lines.size();
            int lineNumber = Integer.parseInt(argument);
            if (lineNumber < 1 || lineNumber > maximum) {
                println("Line must be between 1 and " + maximum + ".");
                return null;
            }
            return lineNumber;
        } catch (NumberFormatException exception) {
            println("Usage: " + usage);
            return null;
        }
    }

    private void save() throws IOException {
        fileSystem.saveFileContent(filename, String.join(System.lineSeparator(), lines));
        println("Saved " + filename + ".");
    }

    private void println(String message) {
        terminal.writer().println(message);
        terminal.writer().flush();
    }
}
