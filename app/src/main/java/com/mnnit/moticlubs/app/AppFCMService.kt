package com.mnnit.moticlubs.app

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.FCMTokenDto
import com.mnnit.moticlubs.di.AppModule
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.getAuthToken
import com.mnnit.moticlubs.domain.util.getMkdFormatter
import com.mnnit.moticlubs.domain.util.getUserId
import com.mnnit.moticlubs.domain.util.postRead
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppFCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "AppFCMService"
    }

    private val notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val apiService: ApiService
        get() = AppModule.provideApiService()

    private val repository: Repository
        get() = AppModule.provideRepository(
            application,
            apiService,
            AppModule.provideLocalDatabase(application)
        )

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.setFCMToken(getAuthToken(), FCMTokenDto(token))

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
        Log.d(TAG, "onMessageReceived: user present")

        Handler(mainLooper).post { handleData(message.data) }
    }

    private fun handleData(data: Map<String, String>) {
        val type = data["type"]?.toInt() ?: -1
        Log.d(TAG, "handleData: $type")
        when (type) {
            0 -> postNotification(data)
            1 -> deletePostNotification(data)
            2 -> replyNotification(data)
            3 -> deleteReplyNotification(data)
            else -> Log.d(TAG, "handleData: unknown type: $type - #${data["type"]}")
        }
    }

    private fun deletePostNotification(data: Map<String, String>) {
        Log.d(TAG, "handleData: delete post")

        val channelID = data["chid"]?.toLong() ?: -1
        val postID = data["pid"]?.toLong() ?: -1

        if (channelID == -1L || postID == -1L) {
            Log.d(TAG, "deletePostNotification: deleteMode: ERR -1: chid $channelID, pid: $postID")
            return
        }

        Log.d(TAG, "deletePostNotification: deleteMode")
        postRead(channelID, postID, true)

        notificationManager.cancel(postID.toNotificationID())

        CoroutineScope(Dispatchers.IO).launch {
            AppModule.provideRepository(
                this@AppFCMService.application,
                AppModule.provideApiService(),
                AppModule.provideLocalDatabase(this@AppFCMService.application)
            ).deletePostID(postID)
        }
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(Constants.POST_BROADCAST_ACTION))
    }

    private fun deleteReplyNotification(data: Map<String, String>) {
        Log.d(TAG, "handleData: delete reply")

        val channelID = data["chid"]?.toLong() ?: -1
        val postID = data["pid"]?.toLong() ?: -1
        val replyID = data["time"]?.toLong() ?: -1

        if (channelID == -1L || postID == -1L || replyID == -1L) {
            Log.d(TAG, "deleteReplyNotification: deleteMode: ERR -1: chid $channelID, pid: $postID, rid: $replyID")
            return
        }

        Log.d(TAG, "deleteReplyNotification: deleteMode")
        postRead(channelID, postID, true)

        notificationManager.cancel(replyID.toNotificationID())

        CoroutineScope(Dispatchers.IO).launch {
            AppModule.provideRepository(
                this@AppFCMService.application,
                AppModule.provideApiService(),
                AppModule.provideLocalDatabase(this@AppFCMService.application)
            ).deleteReplyID(replyID)
        }
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(Constants.REPLY_BROADCAST_ACTION))
    }

    private fun postNotification(data: Map<String, String>) {
        Log.d(TAG, "handleData: post notification")

        val postId = data["pid"]?.toLong() ?: -1
        val userId = data["postUid"]?.toLong() ?: -1
        val message = data["postMessage"] ?: ""
        val adminName = data["postUserName"] ?: ""
        val url = data["postUserAvatar"] ?: ""

        val clubName = data["clubName"] ?: ""
        val channelName = data["channelName"] ?: ""
        val channelId = data["chid"]?.toLong() ?: -1
        val clubId = data["cid"]?.toInt() ?: -1

        val updated = data["updated"]?.toBoolean() ?: false

        prePost(
            Post(
                postId = postId,
                channelId = channelId,
                pageNo = 1,
                message = message,
                userId = userId
            )
        )

        if (userId == getUserId()) {
            Log.d(TAG, "postNotification: post sender and receiver same")
            return
        }

        postRead(channelId, postId)

        notificationCompat(
            notificationStamp = postId,
            channelId = channelId.toString(),
            channelName = channelName,
            clubId = clubId.toString(),
            clubName = clubName,
            title = "$adminName ${if (updated) "updated" else "posted"} in $channelName - $clubName",
            message = message,
            url = url,
            pendingIntent = getPendingIntent(postId)
        )
    }

    private fun replyNotification(data: Map<String, String>) {
        Log.d(TAG, "handleData: reply notification")

        val postId = data["pid"]?.toLong() ?: -1

        val clubName = data["clubName"] ?: ""
        val channelName = data["channelName"] ?: ""
        val channelId = data["chid"]?.toLong() ?: -1
        val clubId = data["cid"]?.toInt() ?: -1

        val replyUserId = data["replyUid"]?.toLong() ?: -1
        val replyId = data["replyTime"]?.toLong() ?: -1
        val replyMessage = data["replyMessage"] ?: ""
        val replyUserName = data["replyUserName"] ?: ""
        val url = data["replyUserAvatar"] ?: ""

        preReply(
            Reply(
                postId = postId,
                userId = replyUserId,
                message = replyMessage,
                pageNo = 1,
                time = replyId
            )
        )

        if (replyUserId == getUserId()) {
            Log.d(TAG, "replyNotification: reply sender and receiver same")
            return
        }

        postRead(channelId, postId)

        notificationCompat(
            notificationStamp = replyId,
            channelId = channelId.toString(),
            channelName = channelName,
            clubId = clubId.toString(),
            clubName = clubName,
            title = "$replyUserName replied to a post in $channelName",
            message = replyMessage,
            url = url,
            pendingIntent = getPendingIntent(postId)
        )
    }

    private fun notificationCompat(
        notificationStamp: Long,
        channelId: String,
        channelName: String,
        clubId: String,
        clubName: String,
        title: String,
        message: String,
        url: String,
        pendingIntent: PendingIntent?
    ) {
        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(clubId, clubName))

        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.BLUE
                enableVibration(true)
                enableLights(true)
                group = clubId
            })

        val notificationHandler = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(getMkdFormatter().toMarkdown(message))
            .setColorized(true)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(applicationContext, R.color.backGroundColor))
            .setSmallIcon(R.drawable.round_notifications_active_24)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        Picasso.get().load(url).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                notificationHandler.setLargeIcon(bitmap)
                notificationManager.notify(notificationStamp.toNotificationID(), notificationHandler.build())
                Log.d(TAG, "postNotification: loaded profile icon")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        })
        notificationManager.notify(notificationStamp.toNotificationID(), notificationHandler.build())
    }

    private fun getPendingIntent(postId: Long) = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(
            Intent(
                Intent.ACTION_VIEW,
                "${Constants.APP_SCHEME_URL}/post=${Uri.encode(postId.toString())}".toUri()
            )
        )
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun Long.toNotificationID(): Int = (this % 1000000L).toInt()

    private fun prePost(post: Post) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(Constants.POST_BROADCAST_ACTION))

        CoroutineScope(Dispatchers.IO).launch { repository.insertOrUpdatePost(post) }
    }

    private fun preReply(reply: Reply) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(Constants.REPLY_BROADCAST_ACTION))

        CoroutineScope(Dispatchers.IO).launch { repository.insertOrUpdateReply(reply) }
    }
}
