package com.pucmm.proyecto_final.ui.product;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.FragmentProductFormBinding;
import com.pucmm.proyecto_final.models.Carousel;
import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.room_view_model.database.ProductDao;
import com.pucmm.proyecto_final.utils.CarouselUpload;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.Constants;
import com.pucmm.proyecto_final.utils.FirebaseConnection;
import com.pucmm.proyecto_final.utils.KProgressHUDUtils;
import com.pucmm.proyecto_final.utils.NetResponse;
import com.pucmm.proyecto_final.utils.ValidUtil;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductFormFragment extends Fragment {

    private static final String TAG = "ProductFragmentManager";

    private FragmentProductFormBinding binding;
    private ArrayList<Drawable> drawables;
    private ArrayList<Uri> uris;
    private int position = 0;
    private ProductWithCarousel element;
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private Category category;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productDao = AppDataBase.getInstance(getContext()).productDao();
        categoryDao = AppDataBase.getInstance(getContext()).categoryDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductFormBinding.inflate(inflater, container, false);
        binding.active.setChecked(true);
        View root = binding.getRoot();

        element = (ProductWithCarousel) getArguments().getSerializable(Constants.PRODUCT_CAROUSEL);
        User user = (User) getArguments().getSerializable(Constants.USER);
        drawables = new ArrayList<>();
        uris = new ArrayList<>();

        categoryDao.findAll().observe(this, categories -> {
            final Stream<Category> stream = user.getRol().equals(User.ROL.CUSTOMER)
                    ? categories.stream().filter(f -> (f.getActive()))
                    : categories.stream();
            final ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), R.layout.auto_complete_item, stream.collect(Collectors.toList()));
            binding.category.setAdapter(adapter);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        if (element != null) {

            binding.itemName.setText(element.product.getName());
            binding.price.setText(String.valueOf(element.product.getPrice()));
            binding.active.setChecked(element.product.isActive());
            AppExecutors.getInstance().diskIO().execute(() -> {
                category = categoryDao.find(element.product.getCategory());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.category.setText(category.toString());
                    }
                });
            });

            if (element.carousels != null && !element.carousels.isEmpty()) {
                final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showDownload();
                FirebaseConnection.obtain().downloads(element.carousels, new NetResponse<List<Bitmap>>() {
                    @Override
                    public void onResponse(List<Bitmap> response) {
                        for (Bitmap bitmap : response) {
                            drawables.add(new BitmapDrawable(getContext().getResources(), bitmap));
                        }
                        binding.image.setImageDrawable(drawables.get(0));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }

        binding.image.setFactory(() -> new ImageView(getContext()));

        // click here to select next image
        binding.next.setOnClickListener(v -> {
            if (position < drawables.size() - 1) {
                binding.image.setImageDrawable(drawables.get(++position));
            } else {
                Toast.makeText(getContext(), "Last Image Already Shown", Toast.LENGTH_SHORT).show();
            }
        });

        // click here to view previous image
        binding.previous.setOnClickListener(v -> {
            if (position > 0) {
                binding.image.setImageDrawable(drawables.get(--position));
            } else {
                Toast.makeText(getContext(), "First Image Already Shown", Toast.LENGTH_SHORT).show();
            }
        });

        binding.select.setOnClickListener(v -> photoOptions());

        binding.category.setOnItemClickListener((parent, view1, position, id) -> category = (Category) parent.getItemAtPosition(position));

        binding.save.setOnClickListener(v -> {
            if (ValidUtil.isEmpty(getContext(), binding.itemName)) {
                return;
            }

            binding.category.setError(null);
            if (category == null) {
                binding.category.setError("Required!");
                binding.category.requestFocus();
                return;
            }

            if (ValidUtil.isEmpty(getContext(), binding.price)) {
                return;
            }

            CommonUtil.alertDialog(getContext(), "Confirm dialog save!",
                    "You are about to save record. Do you really want to proceed?",
                    () -> createOrUpdate());
        });

        binding.back.setOnClickListener(v -> NavHostFragment.findNavController(ProductFormFragment.this)
                .navigate(R.id.action_nav_product_form_to_nav_product));

    }


    private void createOrUpdate() {
        if (element == null) {
            element = new ProductWithCarousel();
        }

        element.product.setName(binding.itemName.getText().toString());
        element.product.setCategory(category.getId());
        element.product.setPrice(Double.valueOf(binding.price.getText().toString()));
        element.product.setActive(binding.active.isChecked());

        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (Integer.valueOf(element.product.getId()).equals(0)) {
                long uid = productDao.insert(element.product);
                System.out.println("All good");
                System.out.println(uid);
                element.product.setId((int) uid);
                System.out.println(element.product.getId());
            } else {
                productDao.update(element.product);
            }
            List<CarouselUpload> uploads = new ArrayList<>();

            productDao.deleteCarousels(element.product.getId());
            final List<Carousel> carousels = new ArrayList<>();
            for (int index = 0; index < drawables.size(); index++) {
                Carousel carousel = new Carousel(element.product.getId(), index, String.format("products/%s/%s.jpg", element.product.getId(), index));
                carousels.add(carousel);
                uploads.add(new CarouselUpload(uris.get(index), carousel));
            }
            productDao.insertCarousels(carousels);

            if (drawables != null && !drawables.isEmpty() && element.product.getId() != 0) {
                function.apply(uploads).accept(progressDialog);
            } else {
                progressDialog.dismiss();
            }
        });
    }

    private final Function<List<CarouselUpload>, Consumer<KProgressHUD>> function = uploads -> progress -> {
        FirebaseConnection.obtain().uploads(uploads, new NetResponse<Void>() {
            @Override
            public void onResponse(Void response) {
                Toast.makeText(getContext(), "Successfully upload images", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    };


    private void photoOptions() {
        // initialising intent
        Intent intent = new Intent();
        // setting type to select to be image
        intent.setType("image/*");
        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        pickAndChoosePictureResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> pickAndChoosePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        drawables = new ArrayList<>();
                        try {
                            final ClipData clipData = result.getData().getClipData();
                            if (clipData != null) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    // adding imageuri in array
                                    final Uri uri = clipData.getItemAt(i).getUri();
                                    final InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    drawables.add(new BitmapDrawable(getContext().getResources(), bitmap));
                                    uris.add(uri);
                                }
                            } else {
                                final Uri uri = result.getData().getData();
                                final InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                drawables.add(new BitmapDrawable(getContext().getResources(), bitmap));
                                uris.add(uri);
                            }
                            binding.image.setImageDrawable(drawables.get(0));
                            position = 0;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}