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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.data.network.dto.FCMTokenDto
import com.mnnit.moticlubs.data.repository.RepositoryImpl
import com.mnnit.moticlubs.di.AppModule
import com.mnnit.moticlubs.domain.model.PostNotificationModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppFCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "AppFCMService"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken")
        CoroutineScope(Dispatchers.IO).launch {
            val response = RepositoryImpl(
                AppModule.provideLocalDatabase(this@AppFCMService.application).dao,
                AppModule.provideApiService(),
                this@AppFCMService.application
            ).getAPIService().setFCMToken(getAuthToken(), FCMTokenDto(token))

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "onNewToken: pushed")
            } else {
                Log.d(TAG, "onNewToken: err: ${response.code()}: ${response.message()}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: ${message.from}")

        val user = FirebaseAuth.getInstance().currentUser
        user ?: return
        Log.d(TAG, "onMessageReceived")

        Handler(mainLooper).post { handleData(message.data) }
    }

    private fun handleData(data: Map<String, String>) {
        val deleteMode = data["deleted"]?.toInt() ?: -1
        if (deleteMode == -1) {
            postNotification(data)
        } else {
            val channelID = data["chid"]?.toLong() ?: -1
            val postID = data["pid"]?.toLong() ?: -1

            if (channelID == -1L || postID == -1L) {
                Log.d(TAG, "handleData: deleteMode: ERR -1: chid $channelID, pid: $postID")
                return
            }

            Log.d(TAG, "handleData: deleteMode: chid: $channelID, pid: $postID")
            postRead(channelID, postID, true)

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(postID.toNotificationID())
        }
    }

    private fun postNotification(data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val clubName = data["clubName"] ?: ""
        val channelName = data["channelName"] ?: ""
        val channelID = data["chid"]?.toLong() ?: -1
        val postID = data["pid"]?.toLong() ?: -1
        val clubID = data["cid"]?.toInt() ?: -1
        val userID = data["uid"]?.toInt() ?: -1
        val message = data["message"] ?: ""
        val adminName = data["adminName"] ?: ""
        val url = data["adminAvatar"] ?: ""
        val updated = (data["updated"]?.toInt() ?: 0) == 1
        val time = data["time"]!!

        if (userID == getUserID()) {
            Log.d(TAG, "postNotification: post sender and receiver same")
            return
        }

        val post = PostNotificationModel(
            clubName, channelName,
            channelID, postID, userID, adminName, url,
            message, time.toLong().toTimeString()
        )
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(
                Intent(
                    Intent.ACTION_VIEW,
                    "${Constants.APP_SCHEME_URL}/post=${Uri.encode(Gson().toJson(post))}".toUri()
                )
            )
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        postRead(channelID, postID)

        postNotificationCompat(
            notificationManager,
            postID,
            clubID.toString(),
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
        postID: Long,
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
                notificationManager.notify(postID.toNotificationID(), notificationHandler.build())
                Log.d(TAG, "postNotification: loaded profile icon")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        })
        notificationManager.notify(postID.toNotificationID(), notificationHandler.build())
    }

    private fun Long.toNotificationID(): Int = (this % 1000000L).toInt()
}
