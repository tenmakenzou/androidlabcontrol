package com.example.labwakenet;


import java.net.*;
import java.util.*;

public class WakeOnLan {

    public static void sendWOL(String macStr, String broadcastIP) throws Exception {
        byte[] macBytes = getMacBytes(macStr);
        byte[] bytes = new byte[6 + 16 * macBytes.length];
        Arrays.fill(bytes, 0, 6, (byte) 0xFF);
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        InetAddress address = InetAddress.getByName(broadcastIP);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6)
            throw new IllegalArgumentException("Invalid MAC address.");
        for (int i = 0; i < 6; i++)
            bytes[i] = (byte) Integer.parseInt(hex[i], 16);
        return bytes;
    }
}