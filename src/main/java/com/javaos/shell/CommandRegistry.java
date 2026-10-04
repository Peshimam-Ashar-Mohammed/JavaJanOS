package com.javaos.shell;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class CommandRegistry {

    private final Map<String, RegisteredCommand> commands = new LinkedHashMap<>();

    public void register(String name, String description, Command command) {
        commands.put(normalize(name), new RegisteredCommand(name, description, command));
    }

    public boolean dispatch(String name, String arguments) throws IOException {
        RegisteredCommand registered = commands.get(normalize(name));
        if (registered == null) {
            return false;
        }
        registered.command().execute(arguments);
        return true;
    }

    public Map<String, String> descriptions() {
        Map<String, String> descriptions = new LinkedHashMap<>();
        commands.values().forEach(command ->
                descriptions.put(command.name(), command.description()));
        return descriptions;
    }

    private String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private record RegisteredCommand(String name, String description, Command command) {
    }
}
