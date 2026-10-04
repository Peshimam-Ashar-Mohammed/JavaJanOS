package com.javaos.commands.system;

import com.javaos.shell.CommandRegistry;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class SystemCommands {

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[93m";
    private static final String PINK = "\u001B[95m";
    private static final String HEART_RESET = "\u001B[0m";

    private SystemCommands() {
    }

    public static void register(CommandRegistry registry, List<String> history) {
        registry.register("date", "Show the current date and time",
                arguments -> System.out.println(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss"))));
        registry.register("time", "Show the current time",
                arguments -> System.out.println(LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss | hh:mm:ss a"))));
        registry.register("whoami", "Show the current user",
                arguments -> System.out.println(System.getProperty("user.name")));
        registry.register("hostname", "Show the system hostname",
                arguments -> System.out.println(System.getenv().getOrDefault("COMPUTERNAME", "Unknown")));
        registry.register("uname", "Show operating system information", arguments -> {
            if (arguments.equals("-a")) {
                System.out.println(System.getProperty("os.name") + " "
                        + System.getProperty("os.version") + " "
                        + System.getProperty("os.arch") + " "
                        + System.getProperty("user.name"));
            } else {
                System.out.println(System.getProperty("os.name"));
            }
        });
        registry.register("arch", "Show the system architecture",
                arguments -> System.out.println(System.getProperty("os.arch")));
        registry.register("sysinfo", "Show system information",
                arguments -> printSystemInfo());
        registry.register("history", "Show command history", arguments -> {
            for (int i = 0; i < history.size(); i++) {
                System.out.println((i + 1) + "  " + history.get(i));
            }
        });

        registry.register("Filza", "❤️", arguments -> printEasterEgg());
        registry.register("Jan", "❤️", arguments -> printEasterEgg());
        registry.register("Janu", "❤️", arguments -> printEasterEgg());


    }

    private static void printSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        System.out.println("========== SYSTEM INFORMATION ==========");
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("Username: " + System.getProperty("user.name"));
        System.out.println("Hostname: " + System.getenv().getOrDefault("COMPUTERNAME", "Unknown"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Available Processors: " + runtime.availableProcessors());
        System.out.println("JVM Used Memory: " + ((total - free) / (1024 * 1024)) + " MB");
        System.out.println("JVM Allocated Memory: " + (total / (1024 * 1024)) + " MB");
        System.out.println("JVM Maximum Memory: " + (runtime.maxMemory() / (1024 * 1024)) + " MB");
        System.out.println("========================================");
    }


    private static void printEasterEgg() {
        System.out.println(PINK);
        System.out.println("       *****       *****");
        System.out.println("    *********** ***********");
        System.out.println("  ***************************");
        System.out.println(" *****************************");
        System.out.println(" *****************************");
        System.out.println("  ***************************");
        System.out.println("    ***********************");
        System.out.println("      *******************");
        System.out.println("        *************");
        System.out.println("          *********");
        System.out.println("            *****");
        System.out.println("              *");
        System.out.println(HEART_RESET);

        System.out.println(PINK + "i Love u my pastry baby" + HEART_RESET);
    }


}
