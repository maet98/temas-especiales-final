package com.pucmm.proyecto_final.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.UserDAO;
import com.pucmm.proyecto_final.room_view_model.model.User;
import com.pucmm.proyecto_final.ui.login.LoginActivity;

public class ForgotPassword extends AppCompatActivity {

    private TextView emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.email_forgot);
    }

    public void checkPassword(View view) {
        String email = emailInput.getText().toString();
        if(email.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Should give an email",Toast.LENGTH_SHORT ).show();
        } else {
            AppDataBase appDataBase = AppDataBase.getInstance(getApplicationContext());
            UserDAO userDAO = appDataBase.userDAO();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = userDAO.findByEmail(email);
                    if(user == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "The user doesn't exist", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "The password is: "+ user.getPassword(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }


    public void changeToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void changeToRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}