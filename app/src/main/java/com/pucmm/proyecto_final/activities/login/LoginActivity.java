package com.pucmm.proyecto_final.activities.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pucmm.proyecto_final.MainActivity;
import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.ApiClient;
import com.pucmm.proyecto_final.api.ApiUser;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.api.dto.UserLogin;
import com.pucmm.proyecto_final.data.ForgotPassword;
import com.pucmm.proyecto_final.activities.RegisterActivity;
import com.pucmm.proyecto_final.utils.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ApiUser apiUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiUser = ApiClient.getClient().create(ApiUser.class);
        setTitle("Login");
        setContentView(R.layout.activity_login);

        String[] permissionList = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        };
        ActivityCompat.requestPermissions(this, permissionList, 0);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        usernameEditText.setText("maletaveras@gmail.com");
        passwordEditText.setText("12345");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("login");
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if( email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill all Fields!", Toast.LENGTH_SHORT);
                } else {
                    UserLogin userLogin = new UserLogin(email, password);
                    Call<User> call = apiUser.login(userLogin);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            System.out.println(response.code());
                            if(response.code() == 200) {
                                Session session = new Session(getApplicationContext());
                                session.createLoginSession(response.body());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            System.out.println("error");
                            System.out.println(t.toString());
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


    public void changeToForgot(View view) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }


    public void changeToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}