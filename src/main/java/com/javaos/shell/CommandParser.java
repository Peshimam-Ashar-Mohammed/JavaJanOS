package com.javaos.shell;

public final class CommandParser {

    public ParsedCommand parse(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return new ParsedCommand("", "");
        }

        String[] parts = trimmed.split("\\s+", 2);
        return new ParsedCommand(parts[0], parts.length > 1 ? parts[1] : "");
    }

    public record ParsedCommand(String name, String arguments) {
    }
}
