package com.pucmm.proyecto_final.ui.category;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.databinding.FragmentCategoryBinding;
import com.pucmm.proyecto_final.databinding.FragmentCategoryFormBinding;
import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.Constants;
import com.pucmm.proyecto_final.utils.FirebaseConnection;
import com.pucmm.proyecto_final.utils.NetResponse;
import com.pucmm.proyecto_final.utils.PhotoOptions;
import com.pucmm.proyecto_final.utils.ValidUtil;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

public class CategoryFormFragment extends Fragment {

    private static final String TAG = "CategoryFragmentManager";

    private FragmentCategoryFormBinding binding;
    private Uri uri;
    private Category element;
    private CategoryDao categoryDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryDao = AppDataBase.getInstance(getContext()).categoryDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryFormBinding.inflate(inflater, container, false);
        binding.active.setChecked(true);
        View root = binding.getRoot();

        element = (Category) getArguments().getSerializable(Constants.CATEGORY);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        if (element != null) {
            binding.categoryName.setText(element.getName());
            binding.active.setChecked(element.getActive());

            CommonUtil.downloadImage(element.getImage(), binding.avatar);
        }

        binding.saveCategory.setOnClickListener(v -> {
            if (ValidUtil.isEmpty(getContext(), binding.categoryName)) {
                return;
            }

            CommonUtil.alertDialog(getContext(), "Confirm dialog save!",
                    "You are about to save record. Do you really want to proceed?",
                    () -> createOrUpdate());
        });


        binding.avatar.setOnClickListener(v -> {
            photoOptions();
        });
    }


    private void createOrUpdate() {
        if (element == null) {
            element = new Category();
        }

        element.setName(binding.categoryName.getText().toString());
        element.setActive(binding.active.isChecked());


        AppExecutors.getInstance().diskIO().execute(() -> {
            if (Integer.valueOf(element.getId()).equals(0)) {
                long uid = categoryDao.insert(element);
                element.setId((int) uid);
            } else {
                categoryDao.update(element);
            }

            if (uri != null && element.getId() != 0) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        FirebaseConnection.obtain().upload(uri, String.format("categories/%s.jpg", element.getId()),
                new NetResponse<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "Successfully upload image", Toast.LENGTH_LONG).show();
                        element.setImage(response);
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            categoryDao.update(element);
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private final Consumer<KProgressHUD> consumer = new Consumer<KProgressHUD>() {
        @Override
        public void accept(KProgressHUD progressDialog) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void photoOptions() {
        final Function<PhotoOptions, Consumer<Intent>> function = photoOptions -> intent -> {
            switch (photoOptions) {
                case CHOOSE_FOLDER:
                case CHOOSE_GALLERY:
                    pickAndChoosePictureResultLauncher.launch(intent);
                    break;
                case TAKE_PHOTO:
                    takePictureResultLauncher.launch(intent);
                    break;

            }
        };
        CommonUtil.photoOptions(getContext(), function);
    }

    private ActivityResultLauncher<Intent> pickAndChoosePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            uri = result.getData().getData();
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            binding.avatar.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private ActivityResultLauncher<Intent> takePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        binding.avatar.setImageBitmap(bitmap);
                        uri = CommonUtil.getImageUri(getContext(), bitmap);
                    }
                }
            });
}