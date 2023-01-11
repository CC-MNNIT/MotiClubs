package com.example.notificationapp.view.activities

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.app.Constants
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.app.isNotValidDomain
import com.example.notificationapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE)

        setListeners()
        binding.signUpText.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()
        }
    }

    private fun setListeners() {
        binding.loginBtn.setOnClickListener { logInUser() }
    }

    private fun logInUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.password.text.toString()
        if (!validate(email, password)) return

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = mAuth.currentUser
                    if (user == null) {
                        Toast.makeText(this@LoginActivity, "Error: Could not log in", Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                        return@addOnCompleteListener
                    }
                    if (user.isEmailVerified) {
                        UserInstance.refreshUserSession(user, this, {
                            UserInstance.updateFCMToken(this@LoginActivity, {
                                Toast.makeText(this@LoginActivity, "Login Successful.", Toast.LENGTH_SHORT).show()
                                goToHome()
                            }) {
                                Toast.makeText(this@LoginActivity, "Error: Couldn't set msg token", Toast.LENGTH_SHORT).show()
                                mAuth.signOut()
                            }
                        }) {
                            Toast.makeText(this@LoginActivity, "Error: Could not init session", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
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

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validate(emailText: String, passwordText: String): Boolean {
        if (emailText.isEmpty()) {
            binding.etEmail.requestFocus()
            return false
        }
        if (emailText.isNotValidDomain()) {
            Toast.makeText(this, "Please use G-Suite ID", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return false
        }
        if (passwordText.isEmpty()) {
            binding.password.requestFocus()
            return false
        }
        return true
    }
}