package com.example.wanderwall

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.onyx.android.sdk.api.device.screensaver.ScreenResourceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WallpaperWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val DEFAULT_URL = "https://hacklog.de/test.png" // Replace with your image URL
    }


    private fun downloadPngToPictures(context: Context, url: String): File? {
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()


        val picturesDir = Environment.getExternalStorageDirectory()
        if (picturesDir == null || !picturesDir.exists()) {
            Log.e("Download", "Pictures directory does not exist")
            return null
        }


        // where some files are ending up /storage/self/primary/Pictures
        val timestamp = SimpleDateFormat("yyyyMMdd_HH", Locale.US).format(Date())
        val imageFile = File(picturesDir, "/Screensaver/$timestamp.png")

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            response.body?.let { responseBody ->
                val sink = imageFile.sink()
                val bufferedSink = sink.buffer()

                val source = responseBody.source()
                var totalBytesWritten = 0L
                var bytesRead: Long
                val buffer = okio.Buffer()
                val bufferSize = 8192L // 8KB buffer size

                sink.use {
                    while (source.read(buffer, bufferSize).also { bytesRead = it } != -1L) {
                        sink.write(buffer, bytesRead)
                        totalBytesWritten += bytesRead
                    }
                }
            }


        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to download file: ${e.message}")
        }

        Log.i("WanderWall", "Image download" + imageFile.absolutePath)
        return imageFile

    }

    override suspend fun doWork(): Result {
        enableWiFi(context)

        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val url = sharedPreferences.getString("url", Constants.DEFAULT_URL) ?: Constants.DEFAULT_URL

        return withContext(Dispatchers.IO) {
            val imageFile = downloadPngToPictures(context, url)
            if (imageFile != null && imageFile.exists()) {
                val successScreensaver = ScreenResourceManager.setScreensaver(context, imageFile.absolutePath, false)
                // No need to setup shutdown for now
                // val successShutdown = ScreenResourceManager.setShutdown(context, imageFile.absolutePath, true)

                if (successScreensaver) {
                    Result.success()
                } else {
                    Log.e("WallpaperWorker", "Failed to set screensaver or shutdown image")
                    Result.failure()
                }
            } else {
                Log.e("WallpaperWorker", "Image file does not exist")
                Result.failure()
            }
        }
    }
}


fun enableWiFi(context: Context) {
    try {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
            Log.i("WiFi", "Wi-Fi enabled")
        }
    } catch (e: Exception) {
        Log.e("WiFi", "Error enabling Wi-Fi", e)
    }
}