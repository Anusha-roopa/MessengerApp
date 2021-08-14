package com.example.messengerapp.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.messengerapp.R;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    TextView txt_signup;
    EditText login_email, login_password;
    TextView signIn_btn;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        txt_signup = findViewById(R.id.txt_signup);
        signIn_btn = findViewById(R.id.signin_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        signIn_btn.setOnClickListener(v -> {
            progressDialog.show();
            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
            } else if (!email.matches(emailPattern)) {
                progressDialog.dismiss();
                login_email.setError("Envalid Email");
                Toast.makeText(LoginActivity.this, "Envalid Email", Toast.LENGTH_SHORT).show();
            }
            else if (password.length() < 6) {
                progressDialog.dismiss();
                login_password.setError("Envalid Password");
                Toast.makeText(LoginActivity.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error in login", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        txt_signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }
}