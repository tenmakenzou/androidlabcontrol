package com.example.labwakenet;
import java.io.*;
import java.net.*;

public class TcpClient {

    public static String sendCommand(String hostname, int port, String command) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostname, port), 3000);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.write(command);
        writer.newLine();
        writer.flush();

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }

        socket.close();
        return response.toString().trim();
    }
}
