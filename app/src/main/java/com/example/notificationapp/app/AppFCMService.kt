package com.example.notificationapp.app

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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
        Log.d(TAG, "onMessageReceived: ${message.data}")
    }
}
