package com.javaos.shell;

import com.javaos.commands.filesystem.FilesystemCommands;
import com.javaos.commands.networking.NetworkingCommands;
import com.javaos.commands.system.SystemCommands;
import com.javaos.filesystem.VirtualFileSystem;
import com.javaos.networking.NetworkService;
import com.javaos.runtime.ProcessManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.javaos.editor.NanoEditor;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import com.javaos.commands.java.JavaCommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Shell {

    private static final String RESET = "\u001B[0m";
    private static final String PURPLE = "\u001B[35m";
    private static final String WHITE = "\u001B[97m";
    private static final String YELLOW = "\u001B[93m";
    private static final String RED = "\u001B[91m";

    private final Terminal terminal;
    private final LineReader lineReader;
    private final VirtualFileSystem fileSystem;
    private final CommandParser parser = new CommandParser();
    private final CommandRegistry registry = new CommandRegistry();
    private final List<String> commandHistory = new ArrayList<>();

    public Shell() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();
        lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        fileSystem = new VirtualFileSystem();
        registerCommands();
    }

    public void start() throws IOException {
        try {
            while (true) {
                String input;
                try {
                    input = lineReader.readLine(prompt()).trim();
                } catch (UserInterruptException e) {
                    System.out.println();
                    continue;
                } catch (EndOfFileException e) {
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }
                commandHistory.add(input);
                CommandParser.ParsedCommand parsed = parser.parse(input);
                if (parsed.name().equalsIgnoreCase("exit")) {
                    System.out.println(YELLOW + "Shutting down JavaOS..." + RESET);
                    break;
                }
                if (parsed.name().equalsIgnoreCase("help")) {
                    printHelp();
                    continue;
                }
                if (!registry.dispatch(parsed.name(), parsed.arguments())) {
                    System.out.println(RED + "Command not found: " + parsed.name() + RESET);
                }
            }
        } finally {
            terminal.close();
        }
    }

    private String prompt() {
        return PURPLE + "javaos:" + fileSystem.getCurrentPath() + "$ " + RESET;
    }

    private void registerCommands() {
        FilesystemCommands.register(registry, fileSystem);
        SystemCommands.register(registry, commandHistory);
        NetworkingCommands.register(registry, new NetworkService(new ProcessManager()));
        JavaCommands.register(registry, fileSystem);

        NanoEditor editor = new NanoEditor(fileSystem, terminal, lineReader);

        registry.register("nano", "Open the JavaOS text editor", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: nano <filename>");
                return;
            }

            editor.open(arguments.trim());
        });


    }

    private void printHelp() {
        System.out.println(WHITE + "Available commands" + RESET);
        printSection("SYSTEM COMMANDS", "date", "time", "whoami", "hostname", "uname", "uname -a", "arch", "sysinfo");
        printSection("FILE COMMANDS", "pwd", "ls", "cd <directory>", "mkdir <directory>", "touch <file>",
                "cat <file>", "rm <file>", "rmdir <directory>", "echo <text> > <file>", "echo <text> >> <file>");
        printSection("NETWORK COMMANDS", "ip", "ip addr", "ping <host>", "nslookup <domain>", "curl <URL>",
                "ip route", "netstat", "traceroute <host>", "tracert <host>", "portscan <host>");
        printSection("OTHER COMMANDS", "help", "history", "exit");
        printSection("JAVA COMMANDS", "javac <filename.java>");
    }

    private void printSection(String title, String... names) {
        Map<String, String> descriptions = registry.descriptions();
        System.out.println();
        System.out.println(YELLOW + title + RESET);
        for (String name : names) {
            String lookup = name.split("\\s+", 2)[0];
            System.out.printf("  %-24s %s%n", name, descriptions.getOrDefault(lookup, ""));
        }
    }
}
