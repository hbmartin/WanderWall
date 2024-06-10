package com.example.wanderwall

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import java.io.IOException
import java.util.concurrent.ExecutionException

class WallpaperWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private const val WALLPAPER_URL = "https://fernandomeyer.com/photography/workstations/003.jpeg" // Replace with your image URL
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun doWork(): Result {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        return try {
            val bitmap: Bitmap = Glide.with(applicationContext)
                .asBitmap()
                .load(WALLPAPER_URL)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get()

            wallpaperManager.setBitmap(bitmap)
            Result.success()
        } catch (e: ExecutionException) {
            Log.e("WallpaperWorker", "Error setting wallpaper", e)
            Result.failure()
        } catch (e: InterruptedException) {
            Log.e("WallpaperWorker", "Error setting wallpaper", e)
            Result.failure()
        } catch (e: IOException) {
            Log.e("WallpaperWorker", "Error setting wallpaper", e)
            Result.failure()
        }
    }
}