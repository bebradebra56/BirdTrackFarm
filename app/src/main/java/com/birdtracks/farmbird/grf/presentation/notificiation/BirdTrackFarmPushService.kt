package com.birdtracks.farmbird.grf.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.birdtracks.farmbird.BirdTrackFarmActivity
import com.birdtracks.farmbird.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication

private const val BIRD_TRACK_FARM_CHANNEL_ID = "bird_track_farm_notifications"
private const val BIRD_TRACK_FARM_CHANNEL_NAME = "BirdTrackFarm Notifications"
private const val BIRD_TRACK_FARM_NOT_TAG = "BirdTrackFarm"

class BirdTrackFarmPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                birdTrackFarmShowNotification(it.title ?: BIRD_TRACK_FARM_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                birdTrackFarmShowNotification(it.title ?: BIRD_TRACK_FARM_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            birdTrackFarmHandleDataPayload(remoteMessage.data)
        }
    }

    private fun birdTrackFarmShowNotification(title: String, message: String, data: String?) {
        val birdTrackFarmNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BIRD_TRACK_FARM_CHANNEL_ID,
                BIRD_TRACK_FARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            birdTrackFarmNotificationManager.createNotificationChannel(channel)
        }

        val birdTrackFarmIntent = Intent(this, BirdTrackFarmActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val birdTrackFarmPendingIntent = PendingIntent.getActivity(
            this,
            0,
            birdTrackFarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val birdTrackFarmNotification = NotificationCompat.Builder(this, BIRD_TRACK_FARM_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.bird_track_farm_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(birdTrackFarmPendingIntent)
            .build()

        birdTrackFarmNotificationManager.notify(System.currentTimeMillis().toInt(), birdTrackFarmNotification)
    }

    private fun birdTrackFarmHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}