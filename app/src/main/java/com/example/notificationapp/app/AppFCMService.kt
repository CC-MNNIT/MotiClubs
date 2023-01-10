package com.example.notificationapp.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.notificationapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class AppFCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "AppFCMService"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: ${message.from}")

        val user = FirebaseAuth.getInstance().currentUser
        user ?: return
        Log.d(TAG, "onMessageReceived")
        Handler(mainLooper).post { postNotificationLegacy(message.data) }
    }

    private fun postNotificationLegacy(data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val clubName = data["clubName"] ?: ""
        val clubID = data["club"] ?: ""
        val message = data["message"] ?: ""
        val adminName = data["adminName"] ?: ""
        val url = data["adminAvatar"] ?: ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            postNotificationCompat(notificationManager, clubID, clubName, adminName, message, url)
        } else {
            postNotificationLegacy(adminName, clubName, message, url, notificationManager)
        }
    }

    private fun postNotificationLegacy(
        adminName: String,
        clubName: String,
        message: String,
        url: String,
        notificationManager: NotificationManager
    ) {
        val notificationHandler = Notification.Builder(applicationContext)
            .setContentTitle("$adminName posted in $clubName")
            .setContentText(message)
            .setColor(ContextCompat.getColor(applicationContext, R.color.main_color))
            .setSmallIcon(R.drawable.notification)
            .setStyle(Notification.BigTextStyle())
            .setPriority(Notification.PRIORITY_HIGH)

        Picasso.get().load(url).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                notificationHandler.setLargeIcon(bitmap)
                Log.d(TAG, "postNotification: loaded profile icon")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        })
        notificationManager.notify(0, notificationHandler.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postNotificationCompat(
        notificationManager: NotificationManager,
        clubID: String,
        clubName: String,
        adminName: String,
        message: String,
        url: String
    ) {
        notificationManager.createNotificationChannel(NotificationChannel(clubID, clubName, NotificationManager.IMPORTANCE_HIGH).apply {
            lightColor = Color.BLUE
            enableVibration(true)
            enableLights(true)
        })

        val notificationHandler = NotificationCompat.Builder(applicationContext, clubID)
            .setContentTitle("$adminName posted in $clubName")
            .setContentText(message)
            .setColorized(true)
            .setColor(ContextCompat.getColor(applicationContext, R.color.main_color))
            .setSmallIcon(R.drawable.notification)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        Picasso.get().load(url).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                notificationHandler.setLargeIcon(bitmap)
                Log.d(TAG, "postNotification: loaded profile icon")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        })
        notificationManager.notify(0, notificationHandler.build())
    }
}
