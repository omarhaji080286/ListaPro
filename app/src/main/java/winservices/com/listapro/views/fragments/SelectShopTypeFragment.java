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
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.views.activities.AddShopActivity;
import winservices.com.listapro.views.adapters.ShopTypesAdapter;

public class SelectShopTypeFragment extends Fragment implements ShopTypesAdapter.IntShowButtons {

    public static final String TAG = "SelectShopTypeFragment";
    private RecyclerView rvShopTypes;
    private Button btnNext, btnPrevious;
    private ShopTypeVM shopTypeVM;
    private boolean isCategoriesToSelect;

    public SelectShopTypeFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_shop_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvShopTypes = view.findViewById(R.id.rvShopTypes);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);

        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);

        shopTypeVM.getAllShopTypes().observe(this, new Observer<List<ShopType>>() {
            @Override
            public void onChanged(List<ShopType> shopTypes) {

                ShopTypesAdapter shopTypesAdapter = new ShopTypesAdapter(getContext(), shopTypes, SelectShopTypeFragment.this);
                rvShopTypes.setAdapter(shopTypesAdapter);

                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                rvShopTypes.setLayoutManager(llm);

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCategoriesToSelect) {
                    ((AddShopActivity) Objects.requireNonNull(getActivity())).
                            displayFragment(new SelectCategoriesFragment(), SelectCityFragment.TAG);
                } else {
                    Toast.makeText(getContext(), "go to shop name and horaire", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddShopActivity) Objects.requireNonNull(getActivity())).
                        displayFragment(new SelectCityFragment(), SelectCityFragment.TAG);
            }
        });
    }

    @Override
    public void onShopTypeSelected() {

        SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
        int selectedShopId = spm.getServerShopTypeId();
        shopTypeVM.getCategories(selectedShopId).observe(this, new Observer<List<DefaultCategory>>() {
            @Override
            public void onChanged(List<DefaultCategory> defaultCategories) {
                if (defaultCategories.size() > 1) isCategoriesToSelect = true;
                btnNext.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);
            }
        });
    }
}
