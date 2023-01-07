package com.example.notificationapp.view.activities

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mInputEmail: TextInputEditText
    private lateinit var mInputPassword: TextInputEditText
    private lateinit var mSignUpTV: TextView
    private lateinit var mLoginBtn: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setReferences()
        setListeners()
        mSignUpTV.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()
        }
    }

    private fun setListeners() {
        mLoginBtn.setOnClickListener { logInUser() }
    }

    private fun logInUser() {
        val email = mInputEmail.text.toString()
        val password = mInputPassword.text.toString()
        if (!validate()) return

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = mAuth.currentUser
                    if (user == null) {
                        goToHome()
                        return@addOnCompleteListener
                    }
                    if (user.isEmailVerified) {
                        user.getIdToken(true).addOnSuccessListener { result ->
                            val idToken = result.token
                            mPreferences.edit().putString(Constants.TOKEN, idToken).apply()
                            Toast.makeText(this@LoginActivity, "Login Successful.", Toast.LENGTH_SHORT).show()
                            goToHome()
                        }
                    } else {
                        mAuth.signOut()
                        Toast.makeText(applicationContext, "Please Verify Your Email.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, task.exception?.message ?: "Null", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setReferences() {
        mInputEmail = findViewById(R.id.et_email)
        mInputPassword = findViewById(R.id.password)
        mAuth = FirebaseAuth.getInstance()
        mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE)
        mLoginBtn = findViewById(R.id.login_btn)
        mSignUpTV = findViewById(R.id.signUp)
    }

    private fun retry(message: String) {
        mLoginBtn.isEnabled = true
        mPreferences.edit().clear().apply()
    }

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validate(): Boolean {
        val emailText = mInputEmail.text?.toString() ?: ""
        val passwordText = mInputPassword.text?.toString() ?: ""
        if (emailText == "") {
            mInputEmail.requestFocus()
            return false
        }
        if (passwordText == "") {
            mInputPassword.requestFocus()
            return false
        }
        return true
    }
}