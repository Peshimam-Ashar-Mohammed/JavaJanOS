package com.javaos.networking;

import com.javaos.runtime.ProcessManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Enumeration;

public final class NetworkService {

    private static final int[] COMMON_PORTS =
            {21, 22, 23, 25, 53, 80, 110, 143, 443, 445, 3306, 3389, 5432, 8080, 8443};

    private final ProcessManager processManager;

    public NetworkService(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public void showIpAddresses() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            System.out.println("========== NETWORK INTERFACES ==========");
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                System.out.println();
                System.out.println(networkInterface.getName() + " (" + networkInterface.getDisplayName() + ")");
                System.out.println("  Status: " + (networkInterface.isUp() ? "UP" : "DOWN"));
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    System.out.println("  Address: " + addresses.nextElement().getHostAddress());
                }
            }
            System.out.println("\n========================================");
        } catch (SocketException e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    public void lookup(String hostname) {
        try {
            System.out.println("DNS lookup for: " + hostname);
            for (InetAddress address : InetAddress.getAllByName(hostname)) {
                System.out.println("Address: " + address.getHostAddress());
            }
        } catch (UnknownHostException e) {
            System.out.println("DNS lookup failed: Unknown host");
        }
    }

    public void ping(String hostname) {
        try {
            InetAddress address = InetAddress.getByName(hostname);
            System.out.println("PING " + hostname + " (" + address.getHostAddress() + ")");
            long start = System.nanoTime();
            boolean reachable = address.isReachable(3000);
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            if (reachable) {
                System.out.println("Host is reachable");
                System.out.println("Response time: " + elapsed + " ms");
            } else {
                System.out.println("No response within 3000 ms");
            }
        } catch (UnknownHostException e) {
            System.out.println("Ping failed: Unknown host");
        } catch (IOException e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    public void curl(String value) {
        try {
            String url = value.startsWith("http://") || value.startsWith("https://")
                    ? value : "https://" + value;
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Status: " + response.statusCode());
            System.out.println("URL: " + response.uri());
            System.out.println("----- RESPONSE BODY -----");
            System.out.println(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Request interrupted.");
        } catch (Exception e) {
            System.out.println("HTTP request failed: " + e.getMessage());
        }
    }

    public void showRoutes() {
        if (isWindows()) {
            processManager.runAndPrint("route", "print");
        } else {
            processManager.runAndPrint("ip", "route");
        }
    }

    public void showNetstat() {
        if (isWindows()) {
            processManager.runAndPrint("netstat", "-ano");
        } else {
            processManager.runAndPrint("netstat", "-an");
        }
    }

    public void traceRoute(String hostname) {
        if (isWindows()) {
            processManager.runAndPrint("tracert", "-d", hostname);
        } else {
            processManager.runAndPrint("traceroute", "-n", hostname);
        }
    }

    public void portScan(String hostname) {
        int open = 0;
        int closed = 0;
        int filtered = 0;
        int errors = 0;
        long start = System.currentTimeMillis();
        try {
            InetAddress address = InetAddress.getByName(hostname);
            System.out.println("\n======================================");
            System.out.println("       JAVAOS NETWORK SCANNER");
            System.out.println("======================================");
            System.out.println("Target   : " + hostname);
            System.out.println("IP       : " + address.getHostAddress());
            System.out.println("Protocol : TCP\n");
            System.out.printf("%-12s %-12s %s%n", "PORT", "STATE", "SERVICE");
            System.out.println("--------------------------------------");
            for (int port : COMMON_PORTS) {
                String state;
                try (Socket socket = new Socket()) {
                    socket.connect(new java.net.InetSocketAddress(address, port), 500);
                    state = "OPEN";
                    open++;
                } catch (java.net.ConnectException e) {
                    state = "CLOSED";
                    closed++;
                } catch (java.net.SocketTimeoutException e) {
                    state = "FILTERED";
                    filtered++;
                } catch (IOException e) {
                    state = "ERROR";
                    errors++;
                }
                System.out.printf("%-12s %-12s %s%n", port + "/tcp", state, serviceName(port));
            }
            System.out.println("--------------------------------------");
            System.out.println("Scan Statistics:");
            System.out.println("Open ports     : " + open);
            System.out.println("Closed ports   : " + closed);
            System.out.println("Filtered/Other : " + filtered);
            System.out.println("Ports scanned  : " + COMMON_PORTS.length);
            System.out.println("Duration       : " + (System.currentTimeMillis() - start) + " ms");
            System.out.println("Scan completed.");
            System.out.println("Errors         : " + errors);
        } catch (UnknownHostException e) {
            System.out.println("Unable to resolve host: " + hostname);
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private String serviceName(int port) {
        return switch (port) {
            case 21 -> "FTP";
            case 22 -> "SSH";
            case 23 -> "Telnet";
            case 25 -> "SMTP";
            case 53 -> "DNS";
            case 80 -> "HTTP";
            case 110 -> "POP3";
            case 143 -> "IMAP";
            case 443 -> "HTTPS";
            case 445 -> "SMB";
            case 3306 -> "MySQL";
            case 3389 -> "RDP";
            case 5432 -> "PostgreSQL";
            case 8080 -> "HTTP-Proxy";
            case 8443 -> "HTTPS-Alt";
            default -> "Unknown";
        };
    }
}
