package com.mnnit.moticlubs.domain.util

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.mnnit.moticlubs.data.network.dto.SaveUserDto

object AuthHandler {

    private var callback: (result: ActivityResult) -> Unit = {}

    fun onResult(result: ActivityResult) {
        callback(result)
        callback = {}
    }

    fun oneTapGoogleSignIn(
        oneTapClient: SignInClient,
        signInRequest: BeginSignInRequest,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onSuccess: (credential: AuthCredential, user: SaveUserDto) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ) {
        oneTapClient.signOut()
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                prepareCallback(oneTapClient, onSuccess, onFailure)
                launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
            }
            .addOnFailureListener { onFailure(it) }
    }

    private fun prepareCallback(
        oneTapClient: SignInClient,
        onSuccess: (credential: AuthCredential, user: SaveUserDto) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ) {
        callback = { activityResult ->
            try {
                if (activityResult.resultCode != AppCompatActivity.RESULT_OK) {
                    throw Exception("Aborted")
                }

                val signInCredential = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                if (!signInCredential.id.matches(Constants.EMAIL_REGEX)) {
                    throw Exception("Please use college G-Suite ID")
                }

                val email = signInCredential.id
                val regNo = Constants.REG_NO_REGEX.find(email)?.value
                val displayName = signInCredential.displayName

                regNo ?: throw Exception("Unable to parse regNo for $email")
                displayName ?: throw Exception("Unable to find display name for $email")

                val course = CourseExtractor.extract(regNo)

                onSuccess(
                    GoogleAuthProvider.getCredential(signInCredential.googleIdToken, null),
                    SaveUserDto(
                        regNo = regNo,
                        name = displayName,
                        email = email,
                        course = course.stream,
                        branch = course.branch,
                        avatar = signInCredential.profilePictureUri?.toString() ?: ""
                    )
                )
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}