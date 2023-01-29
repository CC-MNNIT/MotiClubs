package com.mnnit.moticlubs.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.PostNotificationModel
import com.mnnit.moticlubs.getMkdFormatter
import com.mnnit.moticlubs.toTimeString
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

        Handler(mainLooper).post { postNotification(user, message.data) }
    }

    private fun postNotification(user: FirebaseUser, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val clubName = data["clubName"] ?: ""
        val clubID = data["club"] ?: ""
        val message = data["message"] ?: ""
        val adminName = data["adminName"] ?: ""
        val url = data["adminAvatar"] ?: ""
        val updated = (data["updated"]?.toInt() ?: 0) == 1
        val time = data["time"]!!

        val adminEmail = data["adminEmail"] ?: ""
        val appUserEmail = user.email ?: ""
        if (adminEmail == appUserEmail) {
            Log.d(TAG, "postNotification: post sender and receiver same")
            return
        }

        val post = PostNotificationModel(clubName, adminName, url, message, time.toLong().toTimeString())
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(
                Intent(
                    Intent.ACTION_VIEW,
                    "${Constants.POST_URL}/post=${Uri.encode(Gson().toJson(post))}".toUri()
                )
            )
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        postNotificationCompat(
            notificationManager,
            time.substring(time.length - 5).toInt(),
            clubID,
            clubName,
            adminName,
            message,
            url,
            updated,
            pendingIntent
        )
    }

    private fun postNotificationCompat(
        notificationManager: NotificationManager,
        id: Int,
        clubID: String,
        clubName: String,
        adminName: String,
        message: String,
        url: String,
        updated: Boolean,
        pendingIntent: PendingIntent?
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                clubID,
                clubName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.BLUE
                enableVibration(true)
                enableLights(true)
            })

        val notificationHandler = NotificationCompat.Builder(applicationContext, clubID)
            .setContentTitle("$adminName ${if (updated) "updated" else "posted"} in $clubName")
            .setContentText(getMkdFormatter().toMarkdown(message))
            .setColorized(true)
            .setColor(ContextCompat.getColor(applicationContext, R.color.backGroundColor))
            .setSmallIcon(R.drawable.round_notifications_active_24)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        Picasso.get().load(url).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                notificationHandler.setLargeIcon(bitmap)
                Log.d(TAG, "postNotification: loaded profile icon")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        })
        Log.d(TAG, "postNotificationCompat: ID $id")
        notificationManager.notify(id, notificationHandler.build())
    }
}