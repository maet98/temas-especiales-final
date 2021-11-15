package com.pucmm.proyecto_final.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.model.User;
import com.pucmm.proyecto_final.room_view_model.viewmodel.UserViewModel;
import com.pucmm.proyecto_final.ui.login.LoginActivity;

public class Register extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView username;
    private TextView password;
    private TextView confirm;
    private TextView contact;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        username = findViewById(R.id.user);
        password = findViewById(R.id.passwordRegister);
        confirm = findViewById(R.id.confirmPasswordRegister);
        contact = findViewById(R.id.contactRegister);

        userViewModel = new UserViewModel(AppDataBase.getInstance(getApplicationContext()));
    }

    private Boolean validateInput(User user) {
        System.out.println(user.getContact());
        System.out.println(user.getEmail());
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        return !(user.getContact().isEmpty() || user.getEmail().isEmpty() || user.getUsername().isEmpty() || user.getPassword().isEmpty());
    }

    public void register(View view) {
        if(password.getText().toString().equals(confirm.getText().toString())) {
            User user = new User(
                    email.getText().toString(),
                    username.getText().toString(),
                    contact.getText().toString(),
                    name.getText().toString(),
                    password.getText().toString()
            );
            if(validateInput(user)) {
                userViewModel.insert(user);
                Toast.makeText(getApplicationContext(), "Your user was registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void changeToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void changeToForgot(View view) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }
}