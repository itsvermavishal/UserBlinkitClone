package com.example.userblinkitclone.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.userblinkitclone.R
import com.example.userblinkitclone.activity.UsersMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage){
        super.onMessageReceived(message)

        val channelId = "UserBlinkit"
        val channel = NotificationChannel(channelId, "Blinkit", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "New Order"
            enableLights(true)
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, UsersMainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(Random.nextInt(), notification)
    }
}