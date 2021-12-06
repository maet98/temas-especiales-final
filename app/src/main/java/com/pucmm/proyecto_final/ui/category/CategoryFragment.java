package com.pucmm.proyecto_final.ui.category;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.FragmentCategoryBinding;
import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.room_view_model.listener.OnItemTouchListener;
import com.pucmm.proyecto_final.room_view_model.listener.OptionsMenuListener;
import com.pucmm.proyecto_final.room_view_model.viewmodel.CategoryViewModel;
import com.pucmm.proyecto_final.room_view_model.viewmodel.CategoryViewModelFactory;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.Constants;
import com.pucmm.proyecto_final.utils.Session;

import java.util.List;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryDao categoryDao;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        categoryDao = AppDataBase.getInstance(getContext()).categoryDao();
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Session session = new Session(getContext());
        user = session.getUserSesion();

        binding.fab.setOnClickListener(v -> {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CATEGORY, null);
            NavHostFragment.findNavController(CategoryFragment.this)
                    .navigate(R.id.action_nav_category_to_nav_category_form, bundle);
        });

        int spanCount = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 4 : 2;
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        final CategoryAdapter adapter = new CategoryAdapter(getContext(), user);
        binding.recyclerView.setAdapter(adapter);

        if (!this.user.getRol().equals(User.ROL.SELLER)) {
            binding.fab.setVisibility(View.INVISIBLE);
        }

        adapter.setOptionsMenuListener((OptionsMenuListener<Category>) (view1, element) -> {
            CommonUtil.popupMenu(getContext(), view1, () -> {
                final Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CATEGORY, element);

                NavHostFragment.findNavController(CategoryFragment.this)
                        .navigate(R.id.action_nav_category_to_nav_category_form,bundle);
            }, () -> {
                CommonUtil.alertDialog(getContext(), "Confirm dialog delete!",
                        "You are about to delete record. Do you really want to proceed?",
                        () -> delete(element));
            });
        });

        adapter.setOnItemTouchListener((OnItemTouchListener<Category>) element -> {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CATEGORY, element);

            NavHostFragment.findNavController(CategoryFragment.this)
                    .navigate(R.id.action_nav_category_to_nav_product, bundle);
        });

        CategoryViewModel categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(categoryDao))
                .get(CategoryViewModel.class);

        categoryViewModel.getCategoryListLiveData().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                adapter.setCategories(categories);
            }
        });


        return root;
    }

    public void onClick(View view) {
        System.out.println("nononono");
        NavHostFragment.findNavController(CategoryFragment.this)
                .navigate(R.id.action_nav_product_to_nav_product_form);
    }


    private void delete(Category element) {
//        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();
//
//        if (element.getImage() != null && !element.getImage().isEmpty()) {
//            FirebaseConnection.obtain().delete(element.getImage(), new NetResponse<String>() {
//                @Override
//                public void onResponse(String response) {
//                    AppExecutors.getInstance().diskIO().execute(() -> {
//                        categoryDao.delete(element);
//                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Succesfully deleted", Toast.LENGTH_LONG).show(););
//                    });
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//        } else {
//            Toast.makeText(getContext(), "Succesfully deleted", Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}