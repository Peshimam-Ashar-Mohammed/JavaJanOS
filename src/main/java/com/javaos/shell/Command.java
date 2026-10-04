package com.javaos.shell;

import java.io.IOException;

@FunctionalInterface
public interface Command {
    void execute(String arguments) throws IOException;
}
