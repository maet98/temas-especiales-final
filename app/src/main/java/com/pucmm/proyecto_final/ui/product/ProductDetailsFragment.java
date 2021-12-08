package com.pucmm.proyecto_final.ui.product;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.databinding.FragmentProductDetailsBinding;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;
import com.pucmm.proyecto_final.utils.Constants;
import com.pucmm.proyecto_final.utils.FirebaseConnection;
import com.pucmm.proyecto_final.utils.FormattedUtil;
import com.pucmm.proyecto_final.utils.KProgressHUDUtils;
import com.pucmm.proyecto_final.utils.NetResponse;
import com.pucmm.proyecto_final.utils.Session;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class ProductDetailsFragment extends Fragment {

    private static final String TAG = "ProductFragmentDetails";

    private Session session;

    private FragmentProductDetailsBinding binding;
    private ProductWithCarousel productWithCarousel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        productWithCarousel = (ProductWithCarousel) getArguments().getSerializable(Constants.PRODUCT_CAROUSEL);

        binding.qty.setText("1");

        if (productWithCarousel.carousels != null && !productWithCarousel.carousels.isEmpty()) {
            final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showDownload();
            FirebaseConnection.obtain().downloads(productWithCarousel.carousels, new NetResponse<List<Bitmap>>() {
                @Override
                public void onResponse(List<Bitmap> response) {
                    ArrayList<Drawable> drawables = new ArrayList<>();
                    for (Bitmap bitmap : response) {
                        drawables.add(new BitmapDrawable(getContext().getResources(), bitmap));
                    }
                    carouselView.accept(drawables);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            binding.avatar.setVisibility(View.GONE);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        session = new Session(getContext());

        binding.itemName.setText(productWithCarousel.product.getName());
        binding.price.setText(FormattedUtil.getDecimalValue(productWithCarousel.product.getPrice()));

        binding.remove.setOnClickListener(v -> {
            if ((Integer.valueOf(binding.qty.getText().toString())) > 1) {
                binding.qty.setText(String.valueOf(Integer.valueOf(binding.qty.getText().toString()) - 1));
            }
        });

        binding.add.setOnClickListener(v -> {
            binding.qty.setText(String.valueOf(Integer.valueOf(binding.qty.getText().toString()) + 1));
        });

        binding.action.setOnClickListener(v -> {
            String photo = null;
            if(!productWithCarousel.carousels.isEmpty()) {
                photo = productWithCarousel.carousels.get(0).getPhoto();
            }
            session.addCart(productWithCarousel.product, Integer.valueOf(binding.qty.getText().toString()), photo);
            Toast.makeText(getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
        });


    }

    private final Consumer<ArrayList<Drawable>> carouselView = new Consumer<ArrayList<Drawable>>() {
        @Override
        public void accept(ArrayList<Drawable> drawables) {
            binding.avatar.setSize(drawables.size());
            binding.avatar.setCarouselViewListener((view1, position) -> {
                ImageView imageView = view1.findViewById(R.id.imageView);
                imageView.setImageDrawable(drawables.get(position));
            });
            binding.avatar.show();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
