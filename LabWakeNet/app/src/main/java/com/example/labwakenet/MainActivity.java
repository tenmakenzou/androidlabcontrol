package com.example.labwakenet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Spinner pcs;
    Spinner commands;
    Button checkPcsButton;
    Button sendButton, wolButton;
    TextView resultTextView;

    String[] commandsArray = {"Echo", "Restart", "Shutdown", "Restore"};
    String[] pcsArray = new String[27];
    Map<String, String> pcIpMap = new HashMap<>();

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create PC list
        for (int i = 0; i < pcsArray.length; i++) {
            pcsArray[i] = String.format("RPC%02d", i + 1);
        }

        // εδω βαζουμε τις IP των υπολογιστων (δουλευει με την ip του ΥΠ μου)
        pcIpMap.put("RPC01", "192.168.1.2");
        pcIpMap.put("RPC02", "192.168.1.101");
        pcIpMap.put("RPC03", "192.168.1.102");

        commands = findViewById(R.id.commands);
        pcs = findViewById(R.id.pcs);
        sendButton = findViewById(R.id.sendButton);
        wolButton = findViewById(R.id.wolButton);
        checkPcsButton = findViewById(R.id.checkPcsButton);
        resultTextView = findViewById(R.id.resultTextView);

        ArrayAdapter<String> adapterCommands = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, commandsArray);
        commands.setAdapter(adapterCommands);

        ArrayAdapter<String> adapterPcs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pcsArray);
        pcs.setAdapter(adapterPcs);

        checkPcsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckPcsActivity.class);
            startActivity(intent);
        });

        sendButton.setOnClickListener(v -> sendCommand());
        wolButton.setOnClickListener(v -> sendWOL());
    }

    @SuppressLint("SetTextI18n")
    private void sendCommand() {
        String command = commands.getSelectedItem().toString();
        String target = pcs.getSelectedItem().toString();

        String ip = pcIpMap.get(target);
        if (ip == null) {
            resultTextView.setText("Error: Unknown PC selected (" + target + ")");
            return;
        }

        //βοηθαει στο debug
        new Thread(() -> {
            try {
                String result = TcpClient.sendCommand(ip, 41007, command);
                runOnUiThread(() -> resultTextView.setText(result));
            } catch (UnknownHostException e) {
                runOnUiThread(() -> resultTextView.setText("Error: Unable to resolve host " + ip));
            } catch (java.net.ConnectException e) {
                runOnUiThread(() -> resultTextView.setText("Error: Unable to connect to " + ip + ":41007"));
            } catch (java.net.SocketTimeoutException e) {
                runOnUiThread(() -> resultTextView.setText("Error: Connection to " + ip + " timed out"));
            } catch (Exception e) {
                runOnUiThread(() -> resultTextView.setText("Unexpected Error: " + e.getMessage()));
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void sendWOL() {
        String target = pcs.getSelectedItem().toString();
        String macAddress = "00:11:22:33:44:55"; // βαζεις την mac του υπολογιστη
        new Thread(() -> {
            try {
                WakeOnLan.sendWOL(macAddress, "255.255.255.255");
                runOnUiThread(() -> resultTextView.setText("WOL sent to " + target));
            } catch (Exception e) {
                runOnUiThread(() -> resultTextView.setText("WOL Error: " + e.getMessage()));
            }
        }).start();
    }
}
