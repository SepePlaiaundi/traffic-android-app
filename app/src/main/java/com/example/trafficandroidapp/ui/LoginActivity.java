package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        TextView txtRegister = findViewById(R.id.txtRegister);

        AuthRepository authRepository = new AuthRepository(this);

        findViewById(R.id.btnLogin).setOnClickListener(v -> {

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            authRepository.login(email, password, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
