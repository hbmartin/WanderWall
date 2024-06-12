package com.example.wanderwall

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.wanderwall.ui.theme.WanderwallTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanderwallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreen(
                        modifier = Modifier.padding(innerPadding)
                    ) { interval, url ->
                        scheduleWallpaperUpdate(interval, url)
                    }
                }
            }
        }
    }

    private fun scheduleWallpaperUpdate(interval: Int, url: String) {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("interval", interval).putString("url", url).apply()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WallpaperWorker>(interval.toLong(), TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WallpaperWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onApplySettings: (Int, String) -> Unit) {
    var interval by remember { mutableStateOf(24) }
    var url by remember { mutableStateOf("https://hacklog.de/test.png") }
    var inputInterval by remember { mutableStateOf(interval.toString()) }
    var inputUrl by remember { mutableStateOf(url) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "WanderWall Settings", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = inputInterval,
            onValueChange = { inputInterval = it },
            label = { Text("Interval (hours)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = inputUrl,
            onValueChange = { inputUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Button(onClick = {
            interval = inputInterval.toIntOrNull() ?: 1
            url = inputUrl
            onApplySettings(interval, url)
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Apply")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WanderwallTheme {
        SettingsScreen(onApplySettings = { _, _ -> })
    }
}