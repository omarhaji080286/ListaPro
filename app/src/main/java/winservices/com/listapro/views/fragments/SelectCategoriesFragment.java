package winservices.com.listapro.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.views.activities.AddShopActivity;
import winservices.com.listapro.views.adapters.CategoriesAdapter;

public class SelectCategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private Button btnNext, btnPrevious;
    private List<DefaultCategory> categories;
    private ShopTypeVM shopTypeVM;

    public SelectCategoriesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rvCategories);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);

        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);

        loadCategories();

        prepareButtons();

    }

    private void prepareButtons() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddShopActivity) Objects.requireNonNull(getActivity())).
                        displayFragment(new SelectShopTypeFragment(), SelectShopTypeFragment.TAG);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "go to next", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadCategories() {

        SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
        int serverShopTypeId = spm.getServerShopTypeId();
        shopTypeVM.getCategories(serverShopTypeId).observe(this, new Observer<List<DefaultCategory>>() {
            @Override
            public void onChanged(List<DefaultCategory> defaultCategories) {
                categories = defaultCategories;
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories);
                rvCategories.setAdapter(categoriesAdapter);
                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                rvCategories.setLayoutManager(llm);
            }
        });

    }
}
