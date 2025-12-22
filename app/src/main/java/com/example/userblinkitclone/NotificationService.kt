package com.example.userblinkitclone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
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

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.app_icon)
            .build()
        manager.notify(Random.nextInt(), notification)
    }
}