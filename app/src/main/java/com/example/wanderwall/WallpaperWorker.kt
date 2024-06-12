package com.example.wanderwall

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.onyx.android.sdk.api.device.screensaver.ScreenResourceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.Okio
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class WallpaperWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val DEFAULT_URL = "https://hacklog.de/test.png" // Replace with your image URL
    }


    fun downloadPngToPictures(context: Context, url: String): File? {
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()


        val picturesDir = Environment.getExternalStorageDirectory()
        if (picturesDir == null || !picturesDir.exists()) {
            Log.e("Download", "Pictures directory does not exist")
            return null
        }


        val imageFile = File(picturesDir, "/Screensaver/hacklog.png")


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

                sink.flush()
                sink.close()


                println("File downloaded successfully to ${imageFile.absolutePath}")
                println("Total bytes written: $totalBytesWritten")

//                bufferedSink.writeAll(responseBody.source())
//                bufferedSink.close()
            }


        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to download file: ${e.message}")
        }

//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Failed to download file: $response")
//
//            val sink: BufferedSink = imageFile.sink().buffer()
//            response.body?.let { sink.writeAll(it.source()) }
//            sink.close()
//        }
        return imageFile

//                val request = Request.Builder()
//            .url(url)
//            .build()
//
//        return try {
//            val response = client.newCall(request).execute()
//            if (!response.isSuccessful) {
//                Log.e("Download", "Failed to download file: ${response.message}")
//                return null
//            }

//            val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            if (picturesDir == null || !picturesDir.exists()) {
//                Log.e("Download", "Pictures directory does not exist")
//                return null
//            }
//
//
//            val imageFile = File(picturesDir, "downloaded_image.png")

//            imageFile
//        } catch (e: IOException) {
//            Log.e("Download", "Error downloading file", e)
//            null
//        }
    }

    override suspend fun doWork(): Result {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val url = sharedPreferences.getString("url", Constants.DEFAULT_URL) ?: Constants.DEFAULT_URL

        return withContext(Dispatchers.IO) {
            val imageFile = downloadPngToPictures(context, url)
            if (imageFile != null && imageFile.exists()) {
                val successScreensaver = ScreenResourceManager.setScreensaver(context, imageFile.absolutePath, true)
                val successShutdown = ScreenResourceManager.setShutdown(context, imageFile.absolutePath, true)

                if (successScreensaver && successShutdown) {
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