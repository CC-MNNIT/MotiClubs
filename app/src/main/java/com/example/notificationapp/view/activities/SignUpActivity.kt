package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.notificationapp.R
import com.example.notificationapp.data.network.UserModel
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SignUpActivity"
    }

    private lateinit var mInputEmail: TextInputEditText
    private lateinit var mInputPassword: TextInputEditText
    private lateinit var mInputName: TextInputEditText
    private lateinit var mInputMobile: TextInputEditText
    private lateinit var mInputRegNo: TextInputEditText

    private lateinit var mLoginTV: TextView
    private lateinit var mATVYear: AutoCompleteTextView
    private lateinit var mATVCourse: AutoCompleteTextView
    private lateinit var mSignUpBtn: Button
    private lateinit var mParent: ConstraintLayout

    private lateinit var mAuth: FirebaseAuth

    private var mCourse = ""
    private var mYear = ""
    private val itemsCourse = listOf("B.Tech", "M.Tech", "MBA", "MCA", "PhD")
    private val itemsYear = listOf(2023, 2024, 2025, 2026)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        setReferences()
        setListeners()

        val adapterCourse = ArrayAdapter(this, R.layout.list_item, itemsCourse)
        mATVCourse.setAdapter(adapterCourse)
        mATVCourse.onItemClickListener = OnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, id: Long ->
            mCourse = adapterView.getItemAtPosition(position).toString()
        }
        val adapterYear = ArrayAdapter(this, R.layout.list_item, itemsYear)
        mATVYear.setAdapter(adapterYear)
        mATVCourse.onItemClickListener = OnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, id: Long ->
            mYear = adapterView.getItemAtPosition(position).toString()
        }
        mLoginTV.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setListeners() {
        mSignUpBtn.setOnClickListener { signUpUser() }
    }

    private fun signUpUser() {
        val emailText = mInputEmail.text?.toString() ?: ""
        val passwordText = mInputPassword.text?.toString() ?: ""
        val mobileText = mInputMobile.text?.toString() ?: ""
        val nameText = mInputName.text?.toString() ?: ""
        val regNoText = mInputRegNo.text?.toString() ?: ""
        val courseText = mATVCourse.text?.toString() ?: ""
        val yearText = mATVYear.text.toString()
        if (!validate()) return

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
                            RetrofitAccessObject.getRetrofitAccessObject().saveUser(idToken, userModel)
                                .enqueue(object : Callback<UserResponse?> {
                                    override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                                        if (response.body() != null) {
                                            Toast.makeText(
                                                this@SignUpActivity,
                                                "Registered Successfully, Please Verify Your Account and login!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            goToLogin()
                                        }
                                    }

                                    override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                                        retry(call.toString())
                                        mSignUpBtn.isEnabled = true
                                    }
                                })
                        }
                    }
                }
            }
    }

    private fun setReferences() {
        mInputEmail = findViewById(R.id.et_email)
        mInputPassword = findViewById(R.id.et_password)
        mATVYear = findViewById(R.id.et_grad_year)
        mATVCourse = findViewById(R.id.et_course)
        mInputMobile = findViewById(R.id.et_mobile)
        mInputName = findViewById(R.id.et_username)
        mInputRegNo = findViewById(R.id.et_reg_no)
        mSignUpBtn = findViewById(R.id.signup_btn)
        mParent = findViewById(R.id.parent)
        mLoginTV = findViewById(R.id.login)
    }

    private fun validate(): Boolean {
        val emailText = mInputEmail.text?.toString() ?: ""
        val passwordText = mInputPassword.text?.toString() ?: ""
        val mobileText = mInputMobile.text?.toString() ?: ""
        val nameText = mInputName.text?.toString() ?: ""
        val regNoText = mInputRegNo.text?.toString() ?: ""
        if (emailText == "") {
            mInputEmail.requestFocus()
            return false
        }
        if (passwordText == "") {
            mInputPassword.requestFocus()
            return false
        }
        if (nameText == "") {
            mInputName.requestFocus()
            return false
        }
        if (mobileText == "") {
            mInputMobile.requestFocus()
            return false
        }
        if (regNoText == "") {
            mInputRegNo.requestFocus()
            return false
        }
        val compare = "@mnnit.ac.in"
        var j = compare.length - 1
        var check = false
        var n = emailText.length - 1
        check = false

        //helperTextForEmail.text = "Please enter the valid e-mail address"
        while (n >= 0 && j >= 0) {
            if (emailText[n] != compare[j]) {
                break
            }
            n--
            j--
        }
        if (j == -1 && compare.length < emailText.length) {
            check = true
            //helperTextForEmail.text = "Valid Email entered!"
        } else {
            Log.d(TAG, "Invalid")
            Toast.makeText(applicationContext, "Please Enter G-Suite ID.", Toast.LENGTH_LONG).show()
        }
        return check
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun retry(message: String) {
        Snackbar.make(mParent, message, Snackbar.LENGTH_LONG).show()
        mSignUpBtn.isEnabled = true
    }
}