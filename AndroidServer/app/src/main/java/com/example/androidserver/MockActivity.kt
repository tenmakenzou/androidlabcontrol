package com.example.androidserver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidserver.ui.theme.AndroidserverTheme

class MockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidserverTheme {
                MockScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockScreen() {
    val dictionary = remember { generateMockComputers() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MOCK - Υπολογιστές") })
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(dictionary.entries.toList()) { (ip, os) ->
                ListItem(
                    headlineContent = { Text(ip) },
                    supportingContent = { Text(os) }
                )
                Divider(thickness = 0.5.dp)
            }
        }
    }
}

fun generateMockComputers(): Map<String, String> {
    val dictionary = mutableMapOf<String, String>()
    val osList = listOf("windows", "linux")
    for (i in 2..28) {
        val ip = "192.168.1.$i"
        val os = osList.random()
        dictionary[ip] = os
    }
    return dictionary
}
