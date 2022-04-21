package com.example.howsweather.manager

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class OneTimeRequest(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext,
    params
) {
    override suspend fun doWork(): Result {

        startMyService(inputData.getString("key")!!)
        return Result.success()
    }

    private fun startMyService(description: String) {
        val intent = Intent(applicationContext, MyService::class.java)
        intent.putExtra("description", description)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(applicationContext, intent)
        } else {
            applicationContext.startService(intent)
        }
    }


}