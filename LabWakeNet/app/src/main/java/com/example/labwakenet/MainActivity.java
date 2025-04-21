package com.example.labwakenet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner pcs;

    Spinner commands;

    Button checkPcsButton;
    Button sendButton, wolButton;
    TextView resultTextView;

    String[] commandsArray = {"Echo", "Restart", "Shutdown", "Restore"};
    String[] pcsArray = new String[27];


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < pcsArray.length; i++) {
            pcsArray[i] = String.format("RPC%02d", i + 1);
        }
        commands = findViewById(R.id.commands);
        pcs = findViewById(R.id.pcs);
        sendButton = findViewById(R.id.sendButton);
        wolButton = findViewById(R.id.wolButton);
        checkPcsButton = findViewById(R.id.checkPcsButton);
        resultTextView = findViewById(R.id.resultTextView);
        checkPcsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckPcsActivity.class);
            startActivity(intent);
        });
        ArrayAdapter<String> adapterCommands = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, commandsArray);
        commands.setAdapter(adapterCommands);

        ArrayAdapter<String> adapterPcs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pcsArray);
        pcs.setAdapter(adapterPcs);

        sendButton.setOnClickListener(v -> sendCommand());
        wolButton.setOnClickListener(v -> sendWOL());
    }

    @SuppressLint("SetTextI18n")
    private void sendCommand() {
        String command = commands.getSelectedItem().toString();
        String target =  pcs.getSelectedItem().toString();

        new Thread(() -> {
            try {
                String result = TcpClient.sendCommand(target, 41007, command);
                runOnUiThread(() -> resultTextView.setText(result));
            } catch (Exception e) {
                runOnUiThread(() -> resultTextView.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void sendWOL() {
        String target =   pcs.getSelectedItem().toString();
        String macAddress = "00:11:22:33:44:55"; // Εδώ δίνεις το MAC του PRPCXX

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
