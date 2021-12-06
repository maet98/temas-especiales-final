package com.pucmm.proyecto_final.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.activities.login.LoginActivity;
import com.pucmm.proyecto_final.api.ApiClient;
import com.pucmm.proyecto_final.api.ApiUser;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.data.ForgotPassword;
import com.pucmm.proyecto_final.databinding.ActivityRegisterBinding;
import com.pucmm.proyecto_final.utils.FirebaseConnection;
import com.pucmm.proyecto_final.utils.NetResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Uri uri;
    private TextView firstName;
    private TextView email;
    private TextView lastName;
    private TextView password;
    private TextView confirm;
    private TextView contact;
    private ApiUser apiUser;
    private Spinner rols;
    private User.ROL selectedRol;
    private EditText birthdate;
    private String birthdateSelected;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiUser = ApiClient.getClient().create(ApiUser.class);

        firstName = findViewById(R.id.name);
        email = findViewById(R.id.email);
        lastName = findViewById(R.id.lastName);
        password = findViewById(R.id.passwordRegister);
        confirm = findViewById(R.id.confirmPasswordRegister);
        contact = findViewById(R.id.contactRegister);
        birthdate = findViewById(R.id.et_date);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.profile.setClickable(true);
        binding.profile.setOnClickListener( v-> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imageResultLauncher.launch(intent);
        });

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                System.out.println("date set");
                                month = month + 1;
                                birthdateSelected = dayOfMonth+"/"+month+"/"+year;
                                birthdate.setText(birthdateSelected);

                            }
                        }, year, month, day
                );
                datePickerDialog.show();
            }
        });
        birthdate.setRawInputType(InputType.TYPE_NULL);

        rols = findViewById(R.id.roles);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rols, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rols.setAdapter(adapter);
        rols.setOnItemSelectedListener(this);

    }


    private ActivityResultLauncher<Intent> imageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            uri = result.getData().getData();
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            binding.profile.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Boolean validateInput(User user) {
        return !(user.getEmail().isEmpty()  || user.getPassword().isEmpty());
    }

    public void register(View view) {
        if(password.getText().toString().equals(confirm.getText().toString())) {
            User user = new User();
            user.setContact(contact.getText().toString());
            user.setEmail(email.getText().toString());
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            user.setPassword(password.getText().toString());
            user.setRol(selectedRol);
            if(validateInput(user)) {
                Call<User> call = apiUser.createUser(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        int uid = response.body().getUid();
                        FirebaseConnection.obtain().upload(uri, String.format("profile/%s.jpg", uid),
                                new NetResponse<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        user.setUid(uid);
                                        user.setPhoto(response);
                                        final Call<User> userUpdateCall = apiUser.changeUser(user);
                                        userUpdateCall.enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Toast.makeText(getApplicationContext(), "Your user was registered", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        System.out.println(t.toString());
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Password should match", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1) {
            this.selectedRol = User.ROL.SELLER;
        } else {
            this.selectedRol = User.ROL.CUSTOMER;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}