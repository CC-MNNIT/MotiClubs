package com.example.notificationapp.view.activities;

import static android.widget.Toast.makeText;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    TextInputEditText et_email, et_password, et_name, et_mobile, et_regno;
    TextView login;
    AutoCompleteTextView et_year, et_course;
    String course, year;
    Button signup_btn;
    ConstraintLayout parent;
    ArrayAdapter adapterCourse, adapterYear;
    List<String> itemsCourse = listOf("B.Tech", "M.Tech", "MBA", "MCA", "PhD");
    List<Integer> itemsYear = listOf(2023, 2024, 2025, 2026);
    private FirebaseAuth mAuth;
    private final String TAG = "HELLO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        setReferences();
        setListeners();


        adapterCourse = new ArrayAdapter(this, R.layout.list_item, itemsCourse);
        et_course.setAdapter(adapterCourse);

        et_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                course = item;
            }
        });

        adapterYear = new ArrayAdapter(this, R.layout.list_item, itemsYear);
        et_year.setAdapter(adapterYear);

        et_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                year = item;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    private void setListeners() {
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        String emailText = et_email.getText().toString();
        String passwordText = et_password.getText().toString();
        String mobileText = et_mobile.getText().toString();
        String nameText = et_name.getText().toString();
        String regNoText = et_regno.getText().toString();
        String courseText = et_course.getText().toString();
        String yearText = et_year.getText().toString();

        if (!validate()) return;

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                                                        makeText(SignupActivity.this,
                                                                "Registered Successfully, Please Verify Your Account and login!",
                                                                Toast.LENGTH_SHORT).show();
                                                        goToLogin();
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call<UserResponse> call, Throwable t) {
                                                    retry(call.toString());
                                                    signup_btn.setEnabled(true);
                                                }
                                            });
                                        });
                                    }
                                }
                            });

                        } else {
                            makeText(SignupActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setReferences() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_year = findViewById(R.id.et_grad_year);
        et_course = findViewById(R.id.et_course);
        et_mobile = findViewById(R.id.et_mobile);
        et_name = findViewById(R.id.et_username);
        et_regno = findViewById(R.id.et_reg_no);
        signup_btn = findViewById(R.id.signup_btn);
        parent = findViewById(R.id.parent);
        login = findViewById(R.id.login);
    }

    private boolean validate() {
        String emailText = et_email.getText().toString();
        String passwordText = et_password.getText().toString();
        String mobileText = et_mobile.getText().toString();
        String nameText = et_name.getText().toString();
        String regNoText = et_regno.getText().toString();

        if (emailText.equals("")) {
            et_email.requestFocus();
            return false;
        }
        if (passwordText.equals("")) {
            et_password.requestFocus();
            return false;
        }
        if (nameText.equals("")) {
            et_name.requestFocus();
            return false;
        }
        if (mobileText.equals("")) {
            et_mobile.requestFocus();
            return false;
        }
        if (regNoText.equals("")) {
            et_regno.requestFocus();
            return false;
        }
        String s = emailText;
        String compare = "@mnnit.ac.in";
        int j = compare.length()-1;
        boolean check = false;
        int n = emailText.length() - 1;

        check = false;

        //helperTextForEmail.text = "Please enter the valid e-mail address"

        while (n >=0 && j >= 0){

            if(s.charAt(n) != compare.charAt(j)){
                break;
            }

            n--; j--;
        }
        if(j == -1 && compare.length() < s.length()){
            check = true;
            //helperTextForEmail.text = "Valid Email entered!"
        }
        else  {
            Log.d(TAG,"Invalid");
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
        Snackbar.make(parent, message, Snackbar.LENGTH_LONG).show();
        signup_btn.setEnabled(true);
    }

}