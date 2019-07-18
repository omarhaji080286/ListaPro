package winservices.com.listapro.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    private boolean multipleCategories;

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

                SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
                int selectedShopTypeId = spm.getServerShopTypeId();
                if (selectedShopTypeId != 0) btnNext.setVisibility(View.VISIBLE);

                ShopTypesAdapter shopTypesAdapter = new ShopTypesAdapter(getContext(), shopTypes, SelectShopTypeFragment.this, selectedShopTypeId);
                rvShopTypes.setAdapter(shopTypesAdapter);

                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                rvShopTypes.setLayoutManager(llm);

            }
        });

        prepareButtons();

    }

    private void prepareButtons() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
                shopTypeVM.getCategories(spm.getServerShopTypeId()).observe(SelectShopTypeFragment.this, new Observer<List<DefaultCategory>>() {
                    @Override
                    public void onChanged(List<DefaultCategory> defaultCategories) {

                        if (defaultCategories == null) return;

                        if (defaultCategories.size() > 1) {

                            ((AddShopActivity) Objects.requireNonNull(getActivity())).
                                    displayFragment(new SelectCategoriesFragment(), SelectCityFragment.TAG);
                        } else {

                            ((AddShopActivity) Objects.requireNonNull(getActivity())).
                                    displayFragment(new ShopNameFragment(), ShopNameFragment.TAG);

                        }

                    }
                });

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

        final SharedPrefManager spm = SharedPrefManager.getInstance(getContext());

        shopTypeVM.getCategories(spm.getServerShopTypeId()).observe(SelectShopTypeFragment.this, new Observer<List<DefaultCategory>>() {
            @Override
            public void onChanged(List<DefaultCategory> defaultCategories) {
                if (defaultCategories == null) return;
                spm.storeSelectedCategories(defaultCategories);
                btnNext.setVisibility(View.VISIBLE);
            }
        });

    }
}
