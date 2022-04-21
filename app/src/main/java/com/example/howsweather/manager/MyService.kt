package com.example.howsweather.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.howsweather.R

class MyService : Service() {

    val NOTIFICATION_ID = 10
    val CHANNEL_ID = "CHANNEL_ID"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var description = intent?.getStringExtra("description")
        showNotification(this, description)

        if (Settings.canDrawOverlays(this)) {
            var WindowManger = WindowManager(this, description!!)
            WindowManger!!.setMyWindowManger()
        }
        return START_NOT_STICKY
    }

    fun showNotification(context: Context, body: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                1.toString(),
                "notif",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = " description"
            val nm = context.getSystemService(
                NotificationManager::class.java
            )
            nm.createNotificationChannel(channel)
            // Create an Intent for the activity you want to start
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)

            builder.setContentTitle("Missed Notification").setSmallIcon(R.drawable.clouds)
                .setContentText(body).setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true).setContentTitle("How's Weather")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val nmc = NotificationManagerCompat.from(context)
            nmc.notify(NOTIFICATION_ID, builder.build())
        }else{
            startForeground(1, Notification())
        }
    }



}