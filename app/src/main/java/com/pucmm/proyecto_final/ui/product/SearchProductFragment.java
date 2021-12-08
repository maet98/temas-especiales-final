package com.pucmm.proyecto_final.ui.product;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.FragmentSearchProductBinding;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.ProductDao;
import com.pucmm.proyecto_final.room_view_model.listener.OnItemTouchListener;
import com.pucmm.proyecto_final.room_view_model.listener.OptionsMenuListener;
import com.pucmm.proyecto_final.room_view_model.viewmodel.ProductViewModel;
import com.pucmm.proyecto_final.ui.ViewModelFactory;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.Constants;
import com.pucmm.proyecto_final.utils.Session;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchProductFragment extends Fragment {

    private FragmentSearchProductBinding binding;
    private ProductDao productDao;
    private User user;

    public SearchProductFragment() {
        // Required empty public constructor
    }

    public static SearchProductFragment newInstance(String param1, String param2) {
        SearchProductFragment fragment = new SearchProductFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productDao = AppDataBase.getInstance(getContext()).productDao();
        Session session = new Session(getContext());
        user = session.getUserSesion();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchProductBinding.inflate(inflater, container, false);

        ProductViewModel productViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext()))
                .get(ProductViewModel.class);


        productViewModel.getProductListLiveData().observe(this, elements -> {
            final Stream<ProductWithCarousel> stream = user.getRol().equals(User.ROL.CUSTOMER)
                    ? elements.stream().filter(f -> (f.product.isActive()))
                    : elements.stream();
            ArrayList<ProductWithCarousel> products = (ArrayList<ProductWithCarousel>) stream.collect(Collectors.toList());
            String[] productNames = new String[products.size()];
            for(int i = 0;i < productNames.length;i++) {
                System.out.println(products.get(i).product.getName());
                productNames[i] = products.get(i).product.getName();
            }
            SearchProductAdapter adapter = new SearchProductAdapter(getActivity(), products, productNames,
                    user, (OptionsMenuListener<ProductWithCarousel>) (view1, element) -> {
                CommonUtil.popupMenu(getContext(), view1, () -> {
                    final Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.PRODUCT_CAROUSEL, element);
                    bundle.putSerializable(Constants.USER, user);

                    NavHostFragment.findNavController(SearchProductFragment.this)
                            .navigate(R.id.action_nav_search_product_to_nav_product_form, bundle);
                }, () -> {
                    CommonUtil.alertDialog(getContext(), "Confirm dialog delete!",
                            "You are about to delete record. Do you really want to proceed?",
                            () -> {
                        //delete(element);
                    });
                });
            },
            (OnItemTouchListener<ProductWithCarousel>) element -> {
                final Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PRODUCT_CAROUSEL, element);
                bundle.putSerializable(Constants.USER, user);

                NavHostFragment.findNavController(SearchProductFragment.this)
                        .navigate(R.id.action_nav_search_product_to_productDetailsFragment, bundle);
            }
            );
            binding.filterProductList.setAdapter(adapter);
            binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.setSearch(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.setSearch(newText);
                    return false;
                }
            });
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}