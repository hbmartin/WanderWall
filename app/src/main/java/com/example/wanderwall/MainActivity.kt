package com.example.wanderwall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        scheduleWallpaperUpdate(1)
                    }
                }
            }
        }
    }

    private fun scheduleWallpaperUpdate(hours: Int) {
        val wallpaperWorkRequest = PeriodicWorkRequestBuilder<WallpaperWorker>(hours.toLong(), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(wallpaperWorkRequest)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(bottom = 16.dp)
    )
    Button(onClick = onButtonClick) {
        Text(text = "Set Wallpaper")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WanderwallTheme {
        Greeting("Android", onButtonClick = {})
    }
}