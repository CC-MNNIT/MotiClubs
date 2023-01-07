package com.example.notificationapp.view.activities

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.Constants
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

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validate(): Boolean {
        val emailText = binding.etEmail.text?.toString() ?: ""
        val passwordText = binding.password.text?.toString() ?: ""
        if (emailText == "") {
            binding.etEmail.requestFocus()
            return false
        }
        if (passwordText == "") {
            binding.password.requestFocus()
            return false
        }
        return true
    }
}