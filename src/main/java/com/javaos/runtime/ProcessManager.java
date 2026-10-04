package com.javaos.runtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class ProcessManager {

    public int runAndPrint(String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            int exitCode = process.waitFor();

            System.out.println(output);

            if (exitCode != 0) {
                System.out.println("Command exited with code: " + exitCode);
            }

            return exitCode;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Command interrupted.");
            return -1;

        } catch (IOException e) {
            System.out.println("Could not execute command: " + e.getMessage());
            return -1;
        }
    }


    public int runAndPrintInDirectory(Path workingDirectory, String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(workingDirectory.toFile())
                    .redirectErrorStream(true)
                    .start();

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            int exitCode = process.waitFor();

            if (!output.isBlank()) {
                System.out.print(output);
            }

            if (exitCode != 0) {
                System.out.println("Process exited with code: " + exitCode);
            }

            return exitCode;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Process interrupted.");
            return -1;

        } catch (IOException e) {
            System.out.println("Could not execute process: " + e.getMessage());
            return -1;
        }
    }



}