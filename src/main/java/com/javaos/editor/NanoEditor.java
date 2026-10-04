package com.javaos.editor;

import com.javaos.filesystem.VirtualFileSystem;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class NanoEditor {

    private final VirtualFileSystem fileSystem;
    private final Terminal terminal;
    private final LineReader lineReader;

    public NanoEditor(
            VirtualFileSystem fileSystem,
            Terminal terminal,
            LineReader lineReader
    ) {
        this.fileSystem = fileSystem;
        this.terminal = terminal;
        this.lineReader = lineReader;
    }

    public void open(String filename) throws IOException {

        String content = fileSystem.loadFileContent(filename);

        List<String> lines = new ArrayList<>(
                Arrays.asList(content.split("\\R", -1))
        );

        if (lines.size() == 1 && lines.get(0).isEmpty()) {
            lines.clear();
        }

        System.out.println();
        System.out.println("========== JavaOS Nano ==========");
        System.out.println("File: " + filename);

        printLines(lines);

        System.out.println("---------------------------------");
        System.out.println("Commands:");
        System.out.println(":e <line>  Edit a line");
        System.out.println(":i <line>  Insert before a line");
        System.out.println(":d <line>  Delete a line");
        System.out.println(":p         Print file");
        System.out.println(":w         Save");
        System.out.println(":q         Exit");
        System.out.println();

        while (true) {

            String input = lineReader.readLine("nano> ");

            if (input.equals(":w")) {

                String updatedContent = String.join(
                        System.lineSeparator(), lines
                );

                fileSystem.saveFileContent(filename, updatedContent);

                System.out.println("File saved.");
            }

            else if (input.equals(":q")) {
                System.out.println("Exiting Nano...");
                break;
            }

            else if (input.equals(":p")) {
                printLines(lines);
            }

            else if (input.startsWith(":e ")) {
                editLine(lines, input.substring(3).trim());
            }

            else if (input.startsWith(":i ")) {
                insertLine(lines, input.substring(3).trim());
            }

            else if (input.startsWith(":d ")) {
                deleteLine(lines, input.substring(3).trim());
            }

            else if (input.startsWith(":")) {
                System.out.println("Unknown editor command.");
            }

            else {
                lines.add(input);
                System.out.println("Line added.");
            }
        }
    }

    private void printLines(List<String> lines) {

        System.out.println("---------------------------------");

        for (int i = 0; i < lines.size(); i++) {
            System.out.printf("%3d | %s%n", i + 1, lines.get(i));
        }

        System.out.println("---------------------------------");
    }

    private void editLine(List<String> lines, String input) {

        try {
            int lineNumber = Integer.parseInt(input);

            if (lineNumber < 1 || lineNumber > lines.size()) {
                System.out.println("Invalid line number.");
                return;
            }

            String updatedLine = lineReader.readLine(
                    "New content for line " + lineNumber + "> "
            );

            lines.set(lineNumber - 1, updatedLine);

            System.out.println("Line updated.");

        } catch (NumberFormatException e) {
            System.out.println("Usage: :e <line>");
        }
    }

    private void insertLine(List<String> lines, String input) {

        try {
            int lineNumber = Integer.parseInt(input);

            if (lineNumber < 1 || lineNumber > lines.size() + 1) {
                System.out.println("Invalid line number.");
                return;
            }

            String newLine = lineReader.readLine("New line> ");

            lines.add(lineNumber - 1, newLine);

            System.out.println("Line inserted.");

        } catch (NumberFormatException e) {
            System.out.println("Usage: :i <line>");
        }
    }

    private void deleteLine(List<String> lines, String input) {

        try {
            int lineNumber = Integer.parseInt(input);

            if (lineNumber < 1 || lineNumber > lines.size()) {
                System.out.println("Invalid line number.");
                return;
            }

            lines.remove(lineNumber - 1);

            System.out.println("Line deleted.");

        } catch (NumberFormatException e) {
            System.out.println("Usage: :d <line>");
        }
    }
}