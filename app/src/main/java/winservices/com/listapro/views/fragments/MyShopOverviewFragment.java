package winservices.com.listapro.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;


public class MyShopOverviewFragment extends Fragment {

    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private TextView txtShopName, txtPhone, txtShopType, txtCategories;


    public MyShopOverviewFragment() { }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_shop_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);

        txtShopName = view.findViewById(R.id.txtShopName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtShopType = view.findViewById(R.id.txtShopType);
        txtCategories = view.findViewById(R.id.txtShopDCategories);

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                loadShopData(shopKeeper);
            }
        });


    }

    private void loadShopData(ShopKeeper shopKeeper) {

        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                Shop shop = shops.get(0);
                txtShopName.setText(shop.getShopName());
                txtPhone.setText(shop.getShopPhone());
                txtShopType.setText(shop.getShopType().getShopTypeName());

                StringBuilder sb = new StringBuilder();
                for(DefaultCategory dCategory : shop.getdCategories()){
                    sb.append(" - ");
                    sb.append(dCategory.getDCategoryName());
                    sb.append("\n");
                }
                txtCategories.setText(sb);
            }
        });

    }

}
