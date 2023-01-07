package com.example.notificationapp.view.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.notificationapp.R;
import com.example.notificationapp.data.network.api.RetrofitAccessObject;
import com.example.notificationapp.data.network.model.UserModel;
import com.example.notificationapp.data.network.model.UserResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText mInputEmail, mInputPassword, mInputName, mInputMobile, mInputRegNo;
    private TextView mLoginTV;
    private AutoCompleteTextView mATVYear, mATVCourse;
    private String mCourse, mYear;
    private Button mSignUpBtn;
    private ConstraintLayout mParent;
    private final List<String> itemsCourse = Arrays.asList("B.Tech", "M.Tech", "MBA", "MCA", "PhD");
    private final List<Integer> itemsYear = Arrays.asList(2023, 2024, 2025, 2026);

    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        setReferences();
        setListeners();


        ArrayAdapter<String> adapterCourse = new ArrayAdapter<>(this, R.layout.list_item, itemsCourse);
        mATVCourse.setAdapter(adapterCourse);

        mATVCourse.setOnItemClickListener((adapterView, view, position, id) -> {
            mCourse = adapterView.getItemAtPosition(position).toString();
        });

        ArrayAdapter<Integer> adapterYear = new ArrayAdapter<>(this, R.layout.list_item, itemsYear);
        mATVYear.setAdapter(adapterYear);

        mATVCourse.setOnItemClickListener((adapterView, view, position, id) -> {
            mYear = adapterView.getItemAtPosition(position).toString();
        });

        mLoginTV.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private void setListeners() {
        mSignUpBtn.setOnClickListener(view -> signUpUser());
    }

    private void signUpUser() {
        String emailText = mInputEmail.getText().toString();
        String passwordText = mInputPassword.getText().toString();
        String mobileText = mInputMobile.getText().toString();
        String nameText = mInputName.getText().toString();
        String regNoText = mInputRegNo.getText().toString();
        String courseText = mATVCourse.getText().toString();
        String yearText = mATVYear.getText().toString();

        if (!validate()) return;

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    user.getIdToken(true).addOnSuccessListener(result -> {
                                        String idToken = result.getToken();
                                        UserModel userModel = new UserModel();
                                        userModel.setEmail(emailText);
                                        userModel.setName(nameText);
                                        userModel.setCourse(courseText);
                                        userModel.setGraduationYear(yearText);
                                        userModel.setPersonalEmail(emailText);
                                        userModel.setPhoneNumber(mobileText);
                                        userModel.setRegistrationNumber(regNoText);

                                        RetrofitAccessObject.getRetrofitAccessObject().saveUser(idToken, userModel).enqueue(new Callback<UserResponse>() {
                                            @Override
                                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                                if (response.body() != null) {
                                                    makeText(SignUpActivity.this,
                                                            "Registered Successfully, Please Verify Your Account and login!",
                                                            Toast.LENGTH_SHORT).show();
                                                    goToLogin();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<UserResponse> call, Throwable t) {
                                                retry(call.toString());
                                                mSignUpBtn.setEnabled(true);
                                            }
                                        });
                                    });
                                }
                            }
                        });

                    } else {
                        makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setReferences() {
        mInputEmail = findViewById(R.id.et_email);
        mInputPassword = findViewById(R.id.et_password);
        mATVYear = findViewById(R.id.et_grad_year);
        mATVCourse = findViewById(R.id.et_course);
        mInputMobile = findViewById(R.id.et_mobile);
        mInputName = findViewById(R.id.et_username);
        mInputRegNo = findViewById(R.id.et_reg_no);
        mSignUpBtn = findViewById(R.id.signup_btn);
        mParent = findViewById(R.id.parent);
        mLoginTV = findViewById(R.id.login);
    }

    private boolean validate() {
        String emailText = mInputEmail.getText().toString();
        String passwordText = mInputPassword.getText().toString();
        String mobileText = mInputMobile.getText().toString();
        String nameText = mInputName.getText().toString();
        String regNoText = mInputRegNo.getText().toString();

        if (emailText.equals("")) {
            mInputEmail.requestFocus();
            return false;
        }
        if (passwordText.equals("")) {
            mInputPassword.requestFocus();
            return false;
        }
        if (nameText.equals("")) {
            mInputName.requestFocus();
            return false;
        }
        if (mobileText.equals("")) {
            mInputMobile.requestFocus();
            return false;
        }
        if (regNoText.equals("")) {
            mInputRegNo.requestFocus();
            return false;
        }
        String s = emailText;
        String compare = "@mnnit.ac.in";
        int j = compare.length() - 1;
        boolean check = false;
        int n = emailText.length() - 1;

        check = false;

        //helperTextForEmail.text = "Please enter the valid e-mail address"

        while (n >= 0 && j >= 0) {

            if (s.charAt(n) != compare.charAt(j)) {
                break;
            }

            n--;
            j--;
        }
        if (j == -1 && compare.length() < s.length()) {
            check = true;
            //helperTextForEmail.text = "Valid Email entered!"
        } else {
            Log.d(TAG, "Invalid");
            makeText(getApplicationContext(), "Please Enter G-Suite ID.", Toast.LENGTH_LONG).show();
        }
        return check;
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void retry(String message) {
        Snackbar.make(mParent, message, Snackbar.LENGTH_LONG).show();
        mSignUpBtn.setEnabled(true);
    }
}