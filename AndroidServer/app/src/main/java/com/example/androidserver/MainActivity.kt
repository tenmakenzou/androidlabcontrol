package com.example.androidserver

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidserver.ui.theme.AndroidserverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidserverTheme {
                MainScreen(
                    onButtonClick = { buttonName ->
                        val intent = Intent(this, MockActivity::class.java)
                        intent.putExtra("command", buttonName)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(onButtonClick: (String) -> Unit) {
    val options = listOf("Πάρε κατάσταση", "Άλλη εντολή")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box {

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4 κουμπιά
        Button(
            onClick = { onButtonClick("Πάρε κατάσταση") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Πάρε κατάσταση")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onButtonClick("Έλεγχος") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Έλεγχος")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onButtonClick("Επανεκκίνηση") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Επανεκκίνηση")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onButtonClick("Σβήσιμο") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Σβήσιμο")
        }
    }
}
