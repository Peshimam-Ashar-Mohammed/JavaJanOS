package com.javaos.commands.networking;

import com.javaos.networking.NetworkService;
import com.javaos.shell.CommandRegistry;

public final class NetworkingCommands {

    private NetworkingCommands() {
    }

    public static void register(CommandRegistry registry, NetworkService network) {
        registry.register("ip", "Show network interfaces and IP addresses", arguments -> {
            if (arguments.isBlank() || arguments.equalsIgnoreCase("addr")) {
                network.showIpAddresses();
            } else if (arguments.equalsIgnoreCase("route")) {
                network.showRoutes();
            }
        });
        registry.register("nslookup", "Resolve domain names using DNS", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: nslookup <domain>");
            } else {
                network.lookup(arguments.trim());
            }
        });
        registry.register("ping", "Test network reachability", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: ping <hostname>");
            } else {
                network.ping(arguments.trim());
            }
        });
        registry.register("curl", "Make an HTTP GET request", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: curl <URL>");
            } else {
                network.curl(arguments.trim());
            }
        });
        registry.register("netstat", "Display network connections and ports",
                arguments -> network.showNetstat());
        registry.register("traceroute", "Trace the route to a host", arguments -> trace(network, arguments));
        registry.register("tracert", "Trace the route to a host (Windows)", arguments -> trace(network, arguments));
        registry.register("portscan", "Scan ports on a host", arguments -> {
            if (arguments.isBlank()) {
                System.out.println("Usage: portscan <hostname>");
            } else {
                network.portScan(arguments.trim());
            }
        });
    }

    private static void trace(NetworkService network, String arguments) {
        if (arguments.isBlank()) {
            System.out.println("Usage: traceroute <hostname>");
        } else {
            network.traceRoute(arguments.trim());
        }
    }
}
