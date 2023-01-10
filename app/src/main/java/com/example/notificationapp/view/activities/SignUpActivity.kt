package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.data.network.API
import com.example.notificationapp.data.network.UserModel
import com.example.notificationapp.databinding.ActivitySignupBinding
import com.example.notificationapp.isNotValidDomain
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult

class SignUpActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SignUpActivity"
    }

    private lateinit var binding: ActivitySignupBinding

    private lateinit var mAuth: FirebaseAuth

    private var mCourse = ""
    private var mYear = ""
    private val itemsCourse = listOf("B.Tech", "M.Tech", "MBA", "MCA", "PhD")
    private val itemsYear = listOf(2023, 2024, 2025, 2026)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        setListeners()

        val adapterCourse = ArrayAdapter(this, R.layout.list_item, itemsCourse)
        binding.etCourse.setAdapter(adapterCourse)
        binding.etCourse.onItemClickListener = OnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, _: Long ->
            mCourse = adapterView.getItemAtPosition(position).toString()
        }
        val adapterYear = ArrayAdapter(this, R.layout.list_item, itemsYear)
        binding.etGradYear.setAdapter(adapterYear)
        binding.etGradYear.onItemClickListener = OnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, _: Long ->
            mYear = adapterView.getItemAtPosition(position).toString()
        }
        binding.login.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setListeners() {
        binding.signupBtn.setOnClickListener { signUpUser() }
    }

    private fun signUpUser() {
        val emailText = binding.etEmail.text?.toString() ?: ""
        val passwordText = binding.etPassword.text?.toString() ?: ""
        val regNoText = binding.etRegNo.text?.toString() ?: ""
        val mobileText = binding.etMobile.text?.toString() ?: ""
        val nameText = binding.etUsername.text?.toString() ?: ""
        val courseText = binding.etCourse.text?.toString() ?: ""
        val yearText = binding.etGradYear.text?.toString() ?: ""
        if (!validate(emailText, passwordText, regNoText, mobileText, nameText, courseText, yearText)) return

        binding.signupBtn.isEnabled = false
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener(this) { createUserTask: Task<AuthResult?> ->
                if (!createUserTask.isSuccessful) {
                    Toast.makeText(this@SignUpActivity, createUserTask.exception?.message ?: "Null", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                val user = mAuth.currentUser
                if (user == null) {
                    Toast.makeText(this@SignUpActivity, "Error: User null despite sign up", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                user.sendEmailVerification().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                        user.getIdToken(true).addOnSuccessListener { result: GetTokenResult ->
                            val idToken = result.token
                            val userModel = UserModel(nameText, regNoText, yearText, courseText, emailText, emailText, mobileText)
                            API.saveUser(idToken, userModel, {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Registered Successfully, Please Verify Your Account and login!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                goToLogin()
                            }) { retry("$it: Error Signing Up") }
                        }
                    }
                }
            }
    }

    private fun validate(
        emailText: String,
        passwordText: String,
        regNoText: String,
        mobileText: String,
        nameText: String,
        courseText: String,
        yearText: String
    ): Boolean {
        if (emailText.isEmpty()) {
            binding.etEmail.requestFocus()
            return false
        }
        if (emailText.isNotValidDomain()) {
            Toast.makeText(this, "Please use G-Suite ID", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return false
        }

        if (regNoText.isEmpty()) {
            binding.etRegNo.requestFocus()
            return false
        }
        if (!"^\\d{8}$".toRegex().containsMatchIn(regNoText)) {
            Toast.makeText(this, "Invalid registration number", Toast.LENGTH_SHORT).show()
            binding.etRegNo.requestFocus()
            return false
        }

        if (passwordText.isEmpty()) {
            binding.etPassword.requestFocus()
            return false
        }
        if (courseText.isEmpty()) {
            binding.etCourse.requestFocus()
            return false
        }
        if (yearText.isEmpty()) {
            binding.etGradYear.requestFocus()
            return false
        }
        if (nameText.isEmpty()) {
            binding.etUsername.requestFocus()
            return false
        }
        if (mobileText.isEmpty()) {
            binding.etMobile.requestFocus()
            return false
        }
        return true
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun retry(message: String) {
        Snackbar.make(binding.parent, message, Snackbar.LENGTH_LONG).show()
        binding.signupBtn.isEnabled = true
    }
}