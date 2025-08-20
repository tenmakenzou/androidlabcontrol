package com.example.labwakenet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ListView pcsListView;
    Spinner commands;
    Button checkPcsButton, sendButton, wolButton;
    TextView resultTextView;

    String[] commandsArray = {"Echo", "Restart", "Shutdown", "Restore"};
    String[] pcsArray = new String[27];
    Map<String, String> pcIpMap = new HashMap<>();

    // Thread pool (reused threads instead of creating new ones each click)
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build PC names (PC01 … PC27)
        for (int i = 0; i < pcsArray.length; i++) {
            pcsArray[i] = String.format("PC%02d", i + 1);
        }

        // Build IP addresses automatically (192.168.1.2 … 192.168.1.28)
        String[] ips = new String[27];
        for (int i = 0; i < ips.length; i++) {
            ips[i] = "192.168.1." + (i + 2);
        }

        // Map PCs to IPs
        for (int i = 0; i < pcsArray.length; i++) {
            pcIpMap.put(pcsArray[i], ips[i]);
        }

        pcsListView = findViewById(R.id.pcsListView);
        commands = findViewById(R.id.commands);
        sendButton = findViewById(R.id.sendButton);
        wolButton = findViewById(R.id.wolButton);
        checkPcsButton = findViewById(R.id.checkPcsButton);
        resultTextView = findViewById(R.id.resultTextView);

        ArrayAdapter<String> pcsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, pcsArray);
        pcsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        pcsListView.setAdapter(pcsAdapter);

        commands.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, commandsArray));

        sendButton.setOnClickListener(v -> sendCommand());
        wolButton.setOnClickListener(v -> sendWOL());
        checkPcsButton.setOnClickListener(v ->
                startActivity(new Intent(this, CheckPcsActivity.class)));
    }

    @SuppressLint("SetTextI18n")
    private void sendCommand() {
        SparseBooleanArray checked = pcsListView.getCheckedItemPositions();
        String command = commands.getSelectedItem().toString();
        resultTextView.setText("");

        if (checked.size() == 0) {
            resultTextView.setText("Please select at least one PC.");
            return;
        }

        for (int i = 0; i < pcsArray.length; i++) {
            if (checked.get(i)) {
                String target = pcsArray[i];
                String ip = pcIpMap.get(target);

                if (ip == null) {
                    runOnUiThread(() -> resultTextView.append("\nUnknown PC: " + target));
                    continue;
                }

                executor.execute(() -> {
                    try {
                        String result = TcpClient.sendCommand(ip, 41007, command);
                        runOnUiThread(() -> resultTextView.append("\n[" + target + "] " + result));
                    } catch (UnknownHostException e) {
                        runOnUiThread(() -> resultTextView.append("\n[" + target + "] Host Error"));
                    } catch (Exception e) {
                        runOnUiThread(() -> resultTextView.append("\n[" + target + "] Error: " + e.getMessage()));
                    }
                });
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void sendWOL() {
        Map<String, String> macMap = new HashMap<>() {{
            put("PC01", "macaddress"); // example entry, extend this map with real MACs
        }};

        SparseBooleanArray checked = pcsListView.getCheckedItemPositions();
        resultTextView.setText("");

        if (checked.size() == 0) {
            resultTextView.setText("Please select at least one PC.");
            return;
        }

        for (int i = 0; i < pcsArray.length; i++) {
            if (checked.get(i)) {
                String target = pcsArray[i];
                String macAddress = macMap.get(target);

                if (macAddress == null) {
                    runOnUiThread(() -> resultTextView.append("\n[" + target + "] MAC not found"));
                    continue;
                }

                executor.execute(() -> {
                    try {
                        WakeOnLan.sendWOL(macAddress, "255.255.255.255");
                        runOnUiThread(() -> resultTextView.append("\nWOL sent to " + target));
                    } catch (Exception e) {
                        runOnUiThread(() -> resultTextView.append("\nWOL Error on " + target + ": " + e.getMessage()));
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
