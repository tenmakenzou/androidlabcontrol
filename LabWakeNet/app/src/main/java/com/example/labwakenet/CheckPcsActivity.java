package com.example.labwakenet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Map;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class CheckPcsActivity extends AppCompatActivity {

    ListView statusListView;
    List<String> statusList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    Map<String, String> pcIpMap = IntStream.rangeClosed(1, 20)
            .mapToObj(i -> Map.entry(
                    String.format("PC%02d", i),
                    "192.168.1." + (i + 1)))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_pcs);

        statusListView = findViewById(R.id.statusListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, statusList);
        statusListView.setAdapter(adapter);

        checkAllPcs();
    }

    private void checkAllPcs() {
        new Thread(() -> {
            List<String> sortedKeys = new ArrayList<>(pcIpMap.keySet());
            sortedKeys.sort(Comparator.comparing(k -> k.replaceAll("[^0-9]", ""), String::compareTo));

            for (String name : sortedKeys) {
                String ip = pcIpMap.get(name);
                String status = name + " (" + ip + "): " + getPcStatus(ip, 41007, 1000);

                runOnUiThread(() -> {
                    statusList.add(status);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private String getPcStatus(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Echo");

            String response = in.readLine();
            if (response != null && !response.trim().isEmpty()) {
                return "🟢 Online - " + response.trim();
            } else {
                return "🟢 Online";
            }

        } catch (Exception e) {
            return "🔴 Offline";
        }
    }
}
