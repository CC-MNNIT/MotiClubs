package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.getDomainMail
import com.mnnit.moticlubs.top
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "LoginScreen"

@HiltViewModel
class LoginScreenViewModel @Inject constructor() : ViewModel() {

    val emailID = mutableStateOf("")
    val password = mutableStateOf("")

    val isPasswordVisible = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val isPasswordInvalid
        get() = password.value.isNotEmpty() && password.value.length <= 6

    val isLoginButtonEnabled
        get() = !isLoading.value
                && !isPasswordInvalid
                && password.value.isNotEmpty()
                && emailID.value.isNotEmpty()

    fun resetState() {
        emailID.value = ""
        password.value = ""
        isPasswordVisible.value = false
        isLoading.value = false
    }
}

@Composable
fun LoginScreen(
    appViewModel: AppViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()
    val context = LocalContext.current
    MotiClubsTheme(colorScheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
                .verticalScroll(scrollState)
                .padding(top = appViewModel.paddingValues.value.top()),
            color = colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 120.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(text = "Log in", fontSize = 32.sp)

                Spacer(modifier = Modifier.padding(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.emailID.value,
                    onValueChange = { viewModel.emailID.value = it.replace("@", "") },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "G-Suite ID") },
                    enabled = !viewModel.isLoading.value,
                    trailingIcon = {
                        Text(
                            text = "@mnnit.ac.in",
                            modifier = Modifier.padding(end = 16.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .animateContentSize(),
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.value = it },
                    shape = RoundedCornerShape(24.dp),
                    enabled = !viewModel.isLoading.value,
                    visualTransformation = if (viewModel.isPasswordVisible.value) VisualTransformation.None else {
                        PasswordVisualTransformation()
                    },
                    label = { Text(text = "Password") },
                    isError = viewModel.isPasswordInvalid,
                    supportingText = {
                        if (viewModel.isPasswordInvalid) {
                            Text(text = "Length should be more than 6", fontSize = 12.sp)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.isPasswordVisible.value = !viewModel.isPasswordVisible.value
                        }) {
                            Icon(
                                imageVector = if (viewModel.isPasswordVisible.value) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                }, contentDescription = "Password visibility toggle"
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                )

                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.isLoading.value = true
                        FirebaseAuth.getInstance().sendPasswordResetEmail(viewModel.emailID.value.getDomainMail())
                            .addOnCompleteListener { task ->
                                viewModel.isLoading.value = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(context, "Could not send reset password mail", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End),
                    enabled = !viewModel.isLoading.value && viewModel.emailID.value.isNotEmpty()
                ) {
                    Text(text = "Forgot password", color = colorScheme.error, fontSize = 14.sp)
                }

                AnimatedVisibility(
                    visible = !viewModel.isLoading.value,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.isLoading.value = true
                            login(context, viewModel, appViewModel, onNavigateToMain)
                        },
                        enabled = viewModel.isLoginButtonEnabled
                    ) {
                        Text(text = "Log in", fontSize = 14.sp)
                    }
                }

                AnimatedVisibility(
                    visible = viewModel.isLoading.value,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    CircularProgressIndicator()
                }

                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        onNavigateToSignUp()
                        viewModel.resetState()
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = !viewModel.isLoading.value
                ) {
                    Text(text = "New user ? Sign up", color = colorScheme.primary, fontSize = 14.sp)
                }
            }
        }
    }
}

private fun login(
    context: Context,
    viewModel: LoginScreenViewModel,
    appViewModel: AppViewModel,
    onNavigateToMain: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(viewModel.emailID.value.getDomainMail(), viewModel.password.value)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                viewModel.isLoading.value = false
                Toast.makeText(context, task.exception?.message ?: "Login failure", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            val user = auth.currentUser
            if (user == null) {
                viewModel.isLoading.value = false
                Toast.makeText(context, "Error: Could not login", Toast.LENGTH_SHORT).show()
                auth.signOut()
                return@addOnCompleteListener
            }

            if (user.isEmailVerified) {
                val authToken = appViewModel.getAuthToken(context)
                if (authToken.isEmpty()) {
                    Log.d(TAG, "login: FirebaseIDToken not invoked. Fetching token")
                    user.getIdToken(false).addOnSuccessListener {
                        val token = it.token
                        if (token == null) {
                            auth.signOut()
                            viewModel.isLoading.value = false
                            Toast.makeText(context, "Error: Couldn't init session", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }
                        appViewModel.setAuthToken(context, token)
                        handleUser(context, auth, token, viewModel, appViewModel, onNavigateToMain)
                    }
                } else {
                    Log.d(TAG, "login: FirebaseIDToken invoked")
                    handleUser(context, auth, authToken, viewModel, appViewModel, onNavigateToMain)
                }
            } else {
                auth.signOut()
                viewModel.isLoading.value = false
                Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun handleUser(
    context: Context,
    auth: FirebaseAuth,
    token: String,
    viewModel: LoginScreenViewModel,
    appViewModel: AppViewModel,
    onNavigateToMain: () -> Unit
) {
    FirebaseMessaging.getInstance().token.addOnSuccessListener { fcm ->
        API.setFCMToken(token, fcm, {
            API.getUserData(token, { userRes ->
                viewModel.resetState()
                appViewModel.setUser(userRes)
                onNavigateToMain()
            }) {
                auth.signOut()
                viewModel.isLoading.value = false
                Toast.makeText(context, "Error: Couldn't load user", Toast.LENGTH_SHORT).show()
            }
        }) {
            auth.signOut()
            viewModel.isLoading.value = false
            Toast.makeText(context, "Error: Couldn't set msg token", Toast.LENGTH_SHORT).show()
        }
    }
}
