package com.mnnit.moticlubs.ui.screens

import android.content.Context
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.UserModel
import com.mnnit.moticlubs.getDomainMail
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor() : ViewModel() {

    val emailID = mutableStateOf("")
    val name = mutableStateOf("")
    val regNo = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val password = mutableStateOf("")

    val courseList = listOf("B.Tech", "M.Tech", "MBA", "MCA", "PhD")
    val selectedCourse = mutableStateOf("")

    val isPasswordVisible = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val isPasswordInvalid
        get() = password.value.isNotEmpty() && password.value.length <= 6

    val isSignUpButtonEnabled
        get() = !isLoading.value
                && !isPasswordInvalid
                && password.value.isNotEmpty()
                && emailID.value.isNotEmpty()
                && name.value.isNotEmpty()
                && regNo.value.isNotEmpty()
                && phoneNumber.value.isNotEmpty()
                && selectedCourse.value.isNotEmpty()

    val dropDownExpanded = mutableStateOf(false)

    fun resetState() {
        emailID.value = ""
        name.value = ""
        regNo.value = ""
        phoneNumber.value = ""
        password.value = ""
        selectedCourse.value = ""
        isPasswordVisible.value = false
        isLoading.value = false
        dropDownExpanded.value = false
    }
}

@Composable
fun SignupScreen(
    appViewModel: AppViewModel,
    onNavigateToLogin: () -> Unit,
    viewModel: SignUpScreenViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()
    val context = LocalContext.current

    MotiClubsTheme(colorScheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
            color = colorScheme.background
        ) {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(color = colorScheme.background)

            Column(
                modifier = Modifier
                    .padding(top = 120.dp, start = 16.dp, end = 16.dp)
                    .animateContentSize()
            ) {
                Text(text = "Sign up", fontSize = 32.sp)

                Spacer(modifier = Modifier.padding(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Name") },
                    enabled = !viewModel.isLoading.value,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = viewModel.phoneNumber.value,
                    onValueChange = {
                        viewModel.phoneNumber.value =
                            it.substring(0, kotlin.math.min(10, it.length))
                    },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Phone No") },
                    enabled = !viewModel.isLoading.value,
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            text = "+ 91",
                            fontSize = 15.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .fillMaxHeight(),
                            textAlign = TextAlign.Center
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )

                Row(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp),
                        value = viewModel.regNo.value,
                        onValueChange = {
                            viewModel.regNo.value = it.substring(0, kotlin.math.min(8, it.length))
                        },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text(text = "Reg No") },
                        enabled = !viewModel.isLoading.value,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    )

                    ExposedDropdownMenuBox(
                        expanded = viewModel.dropDownExpanded.value,
                        onExpandedChange = {
                            viewModel.dropDownExpanded.value = !viewModel.dropDownExpanded.value
                        }) {
                        OutlinedTextField(
                            value = viewModel.selectedCourse.value,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(text = "Course") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.dropDownExpanded.value)
                            },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.menuAnchor(),
                            enabled = !viewModel.isLoading.value,
                        )

                        ExposedDropdownMenu(
                            expanded = viewModel.dropDownExpanded.value,
                            onDismissRequest = { viewModel.dropDownExpanded.value = false }
                        ) {
                            viewModel.courseList.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.selectedCourse.value = option
                                        viewModel.dropDownExpanded.value = false
                                    })
                            }
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                        .padding(top = 8.dp)
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

                AnimatedVisibility(
                    visible = !viewModel.isLoading.value,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.isLoading.value = true
                            signUpUser(context, viewModel, onNavigateToLogin)
                        },
                        enabled = viewModel.isSignUpButtonEnabled
                    ) {
                        Text(text = "Sign up", fontSize = 14.sp)
                    }
                }

                AnimatedVisibility(
                    visible = viewModel.isLoading.value,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    CircularProgressIndicator()
                }

                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.resetState()
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = !viewModel.isLoading.value
                ) {
                    Text(
                        text = "Already a user ? Login",
                        color = colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun signUpUser(
    context: Context,
    viewModel: SignUpScreenViewModel,
    onNavigateToLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(
        "${viewModel.emailID.value}@mnnit.ac.in",
        viewModel.password.value
    )
        .addOnCompleteListener { createUserTask ->
            if (!createUserTask.isSuccessful) {
                viewModel.isLoading.value = false
                Toast.makeText(
                    context,
                    createUserTask.exception?.message ?: "NULL",
                    Toast.LENGTH_SHORT
                ).show()
                return@addOnCompleteListener
            }
            val user = auth.currentUser
            if (user == null) {
                viewModel.isLoading.value = false
                Toast.makeText(context, "Error: User null despite sign up", Toast.LENGTH_SHORT)
                    .show()
                return@addOnCompleteListener
            }
            user.getIdToken(false).addOnSuccessListener { result ->
                val token = result.token ?: ""
                val userModel = UserModel(
                    viewModel.name.value,
                    viewModel.regNo.value,
                    viewModel.selectedCourse.value,
                    viewModel.emailID.value.getDomainMail(),
                    viewModel.phoneNumber.value
                )
                API.saveUser(token, userModel, {
                    user.sendEmailVerification().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.signOut()
                            viewModel.resetState()
                            Toast.makeText(
                                context,
                                "Please verify email to continue",
                                Toast.LENGTH_SHORT
                            ).show()
                            onNavigateToLogin()
                        }
                    }
                }) {
                    auth.signOut()
                    viewModel.isLoading.value = false
                    Toast.makeText(context, "$it: Error signing up", Toast.LENGTH_SHORT).show()
                }
            }
        }
}
