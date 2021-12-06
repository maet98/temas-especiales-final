package com.pucmm.proyecto_final.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.ApiClient;
import com.pucmm.proyecto_final.api.ApiUser;
import com.pucmm.proyecto_final.api.dto.UserLogin;
import com.pucmm.proyecto_final.activities.RegisterActivity;
import com.pucmm.proyecto_final.activities.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private TextView emailInput;
    private TextView passwordInput;
    private ApiUser apiUser;
    private Button mPickDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Recover password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiUser = ApiClient.getClient().create(ApiUser.class);

        emailInput = findViewById(R.id.email_forgot);
        passwordInput = findViewById(R.id.password_forgot);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void checkPassword(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Should give an email and the new password",Toast.LENGTH_SHORT ).show();
        } else {
            UserLogin userLogin = new UserLogin(email, password);
            Call<Void> call = apiUser.changePassword(userLogin);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 404) {
                        Toast.makeText(getApplicationContext(), "The user doesn't exist", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "The password have been changed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "The user doesn't exist", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    public void changeToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void changeToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}