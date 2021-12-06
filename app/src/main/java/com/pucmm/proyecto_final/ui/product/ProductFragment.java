package com.pucmm.proyecto_final.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.FragmentProductBinding;
import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.ProductDao;
import com.pucmm.proyecto_final.room_view_model.listener.OnItemTouchListener;
import com.pucmm.proyecto_final.room_view_model.listener.OptionsMenuListener;
import com.pucmm.proyecto_final.room_view_model.viewmodel.ProductViewModel;
import com.pucmm.proyecto_final.ui.ViewModelFactory;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.Constants;
import com.pucmm.proyecto_final.utils.FirebaseConnection;
import com.pucmm.proyecto_final.utils.KProgressHUDUtils;
import com.pucmm.proyecto_final.utils.NetResponse;
import com.pucmm.proyecto_final.utils.Session;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductFragment extends Fragment {

    private ProductDao productDao;
    private FragmentProductBinding binding;
    private User user;
    private Category category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session session = new Session(getContext());
        user = session.getUserSesion();

        productDao = AppDataBase.getInstance(getContext()).productDao();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


//        category = (Category) getArgumenVts().getSerializable(Constants.CATEGORY);


        binding.addFabProduct.setOnClickListener( v -> {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.PRODUCT_CAROUSEL, null);
            bundle.putSerializable(Constants.USER, user);
            NavHostFragment.findNavController(ProductFragment.this)
                    .navigate(R.id.action_nav_product_to_nav_product_form, bundle);

        });

        if (!this.user.getRol().equals(User.ROL.SELLER)) {
            binding.addFabProduct.setVisibility(View.INVISIBLE);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        final ProductAdapter adapter = new ProductAdapter(user);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOptionsMenuListener((OptionsMenuListener<ProductWithCarousel>) (view1, element) -> {
            CommonUtil.popupMenu(getContext(), view1, () -> {
                final Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PRODUCT_CAROUSEL, element);
                bundle.putSerializable(Constants.USER, user);

                NavHostFragment.findNavController(ProductFragment.this)
                        .navigate(R.id.action_nav_product_to_nav_product_form, bundle);
            }, () -> {
                CommonUtil.alertDialog(getContext(), "Confirm dialog delete!",
                        "You are about to delete record. Do you really want to proceed?",
                        () -> delete(element));
            });
        });

        adapter.setOnItemTouchListener((OnItemTouchListener<ProductWithCarousel>) element -> {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.PRODUCT_CAROUSEL, element);
            bundle.putSerializable(Constants.USER, user);

            NavHostFragment.findNavController(ProductFragment.this)
                    .navigate(R.id.action_nav_product_to_productDetailsFragment, bundle);
        });

        ProductViewModel productViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext()))
                .get(ProductViewModel.class);


        productViewModel.getProductListLiveData().observe(this, elements -> {
            final Stream<ProductWithCarousel> stream = user.getRol().equals(User.ROL.CUSTOMER)
                    ? elements.stream().filter(f -> (f.product.isActive()))
                    : elements.stream();

            adapter.setProducts(stream.filter(f -> category == null ?
                    true : f.product.getCategory() == category.getId()).collect(Collectors.toList()));
        });

        System.out.println(adapter.getItemCount());
    }

    private void delete(ProductWithCarousel element) {
        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();
        function.apply(progressDialog).apply(true).accept(element);

        if (element.carousels != null && !element.carousels.isEmpty()) {
            FirebaseConnection.obtain().deletes(element.carousels, new NetResponse<String>() {
                @Override
                public void onResponse(String response) {
                    function.apply(progressDialog).apply(true).accept(element);
                }

                @Override
                public void onFailure(Throwable t) {
                    function.apply(progressDialog).apply(false).accept(element);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            function.apply(progressDialog).apply(true).accept(element);
        }
    }

    private final Function<KProgressHUD, Function<Boolean, Consumer<ProductWithCarousel>>> function = progress -> success -> element -> {
        if (success) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                productDao.delete(element.product);
                productDao.deleteCarousels(element.carousels);
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Successfully deleted!", Toast.LENGTH_LONG).show());
            });
        }
        progress.dismiss();
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}