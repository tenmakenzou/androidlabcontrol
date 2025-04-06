import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 41007;
    
    private static String getSystemInfo() {
        String hostname = "Unknown"; 
        String osName = System.getProperty("os.name");  //YYY OS υπολογιστή
        try {
            hostname = InetAddress.getLocalHost().getHostName(); //XXX Όνομα υπολογιστή 
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostname + " - " + osName;
    }
    
    private static void handleClientCommand(String command, PrintWriter out) {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();  //XXX Όνομα υπολογιστή 
        } catch (UnknownHostException e) {
            hostname = "Unknown";
        }
        
        String response;
        switch (command) {
            case "Echo":
                response = getSystemInfo();
                break;
            case "Restart":
                response = hostname + " - Rebooting...";
                // Runtime.getRuntime().exec("sudo shutdown -r now");  // Linux
                //Runtime.getRuntime().exec("shutdown -r");
                // Θα το τροποποιήσουμε να εκτέλει την εντόλη αναλόγος OS (Απο το response)
                break;
            case "Shutdown":
                response = hostname + " - Shutting down...";
                // Runtime.getRuntime().exec("sudo shutdown -h now"); // Linux
                //Runtime.getRuntime().exec("shutdown /s");
                // Θα το τροποποιήσουμε να εκτέλει την εντόλη αναλόγος OS (Απο το response)
                break;
            case "Restore":
                response = hostname + " - Restoring";
                out.println(response);
                out.flush();
                response = hostname + " - Restored";
                break;
            default:
                response = "Invalid command";
        }
        out.println(response);
        out.flush();
    }
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    
                    System.out.println("Connected by " + clientSocket.getInetAddress()); // Θα το δοκιμασουμε μετα σε client.java
                    String command = in.readLine().trim();
                    handleClientCommand(command, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
