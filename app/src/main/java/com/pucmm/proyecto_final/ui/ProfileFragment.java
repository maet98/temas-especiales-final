package com.pucmm.proyecto_final.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.ApiClient;
import com.pucmm.proyecto_final.api.ApiUser;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.FragmentProfileBinding;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.FirebaseConnection;
import com.pucmm.proyecto_final.utils.NetResponse;
import com.pucmm.proyecto_final.utils.Session;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private User user;
    private Uri uri;
    private Session session;
    private ApiUser apiUser;
    private LocalDate date;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getContext());
        user = session.getUserSesion();
        apiUser = ApiClient.getClient().create(ApiUser.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.email.setText(user.getEmail());
        binding.name.setText(user.getFirstName());
        binding.lastName.setText(user.getLastName());
        binding.profile.setOnClickListener( v-> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imageResultLauncher.launch(intent);
        });
        if(user.getBirthday().charAt(user.getBirthday().length()-1) != 'Z') {
            Timestamp time = new Timestamp(Long.valueOf(user.getBirthday()));
            date = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            System.out.println(date);
            binding.etDate.setText(date.toString());
        } else {
            LocalDateTime d1 = LocalDateTime.parse(user.getBirthday().substring(0, user.getBirthday().length() -1));
            date = d1.toLocalDate();
            binding.etDate.setText(date.toString());
        }

        binding.contactRegister.setText(user.getContact());

        if(user.getPhoto() != null) {
            CommonUtil.downloadImage(user.getPhoto(), binding.profile);
        }

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month + 1;
                                date = LocalDate.of(year, month, dayOfMonth);
                                binding.etDate.setText(date.toString());
                            }
                        }, year, month, day
                );
                datePickerDialog.show();
            }
        });
        binding.etDate.setRawInputType(InputType.TYPE_NULL);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.rols, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.roles.setAdapter(adapter);
        binding.roles.setSelection(user.getRol().equals(User.ROL.SELLER) ? 1 : 2);
        binding.roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(binding.roles.getSelectedItem());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return binding.getRoot();
    }


    private ActivityResultLauncher<Intent> imageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            uri = result.getData().getData();
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            binding.profile.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void updateProfile() {
        user.setLastName(binding.lastName.getText().toString());
        user.setFirstName(binding.name.getText().toString());
        user.setContact(binding.contactRegister.getText().toString());
        user.setEmail(binding.email.getText().toString());

        user.setBirthday(date.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME) + "Z");
        Call<User> call = apiUser.changeUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int uid = user.getUid();
                if(uri != null) {
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
                                            Toast.makeText(getContext(), "Your profile was updated", Toast.LENGTH_SHORT).show();
                                            session.updateUser(user);
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

                } else {
                    Toast.makeText(getContext(), "Your profile was updated", Toast.LENGTH_SHORT).show();
                    session.updateUser(user);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}