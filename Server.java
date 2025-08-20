import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 41007;

    private static String getSystemInfo() {
        String hostname = "Unknown";
        String osName = System.getProperty("os.name");
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostname + " - " + osName;
    }

    private static void handleClientCommand(String command, PrintWriter out) {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "Unknown";
        }

        String response;
        try {
            switch (command) {
                case "Echo":
                    response = getSystemInfo();
                    break;

                case "Restart":
                    response = hostname + " - Rebooting...";
                    executeSystemCommand("restart");
                    break;

                case "Shutdown":
                    response = hostname + " - Shutting down...";
                    executeSystemCommand("shutdown");
                    break;

                case "Restore":
                    response = hostname + " - Cancelling shutdown (if pending)";

                    break;

                default:
                    response = "Invalid command";
            }
        } catch (IOException e) {
            response = hostname + " - Failed to execute command: " + e.getMessage();
        }

        out.println(response);
        out.flush();
        System.out.println("Handled command: " + command + " -> " + response);
    }

    private static void executeSystemCommand(String action) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Windows commands
            switch (action) {
                case "shutdown":
                    Runtime.getRuntime().exec("shutdown /s /t 0");
                    break;
                case "restart":
                    Runtime.getRuntime().exec("shutdown /r /t 0");
                    break;
                case "restore":
                    Runtime.getRuntime().exec("shutdown /a");
                    break;
            }
        } else if (os.contains("linux") || os.contains("mac")) {

            switch (action) {
                case "shutdown":
                    Runtime.getRuntime().exec("shutdown -h now");
                    break;
                case "restart":
                    Runtime.getRuntime().exec("reboot");
                    break;
                case "restore":

                    System.out.println("Restore not supported on Linux/macOS");
                    break;
            }
        } else {
            throw new IOException("Unsupported OS for system command");
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT + "...");
            System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());

            int flag = 0;
            while (true) {
                try (
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    if (flag == 0) {
                        System.out.println("Client connected: " + clientSocket.getInetAddress());
                        flag = 1;
                    }

                    String command = in.readLine();
                    if (command == null) {
                        System.out.println("Client sent no data.");
                        continue;
                    }

                    command = command.trim();
                    handleClientCommand(command, out);

                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

