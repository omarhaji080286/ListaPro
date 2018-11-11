package winservices.com.listapro.views.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.activities.LauncherActivity;
import winservices.com.listapro.views.activities.MyOrdersActivity;
import winservices.com.listapro.views.activities.MyShopActivity;

public class WelcomeFragment extends Fragment {

    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private OrderVM orderVM;
    private ImageView imgLogOut;
    private LinearLayout linlayMyShop;
    private ConstraintLayout consLayMyOrders;
    private TextView txtOrdersSentNum;

    public WelcomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);

        imgLogOut = view.findViewById(R.id.imgLogOut);
        consLayMyOrders = view.findViewById(R.id.consLayMyOrders);
        linlayMyShop = view.findViewById(R.id.linlayMyShop);
        txtOrdersSentNum = view.findViewById(R.id.txtOrdersSentNum);

        initData();
        initMyShopItem();
        initMyOrdersItem();

    }

    private void initData() {
        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(final ShopKeeper shopKeeper) {

                loadSentOrdersNum(shopKeeper);
                initLogOutImg(shopKeeper);
            }
        });
    }

    private void loadSentOrdersNum(ShopKeeper shopKeeper) {
        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                updateSentOrdersNum(shops.get(0));
            }
        });

    }

    private void updateSentOrdersNum(Shop shop) {
        orderVM.getSentOrdersNum(shop.getServerShopId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer sentOrdersNum) {
                txtOrdersSentNum.setText(String.valueOf(sentOrdersNum));
            }
        });
    }


    private void initLogOutImg(final ShopKeeper shopKeeper){
        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopKeeper.setIsLoggedIn(ShopKeeper.LOGGED_OUT);
                shopKeeperVM.update(shopKeeper);
                LauncherActivity launcherActivity = (LauncherActivity) getActivity();
                Objects.requireNonNull(launcherActivity).displayFragment(new WelcomeFragment());
            }
        });
    }

    private void initMyShopItem() {

        linlayMyShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyShopActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initMyOrdersItem() {


        consLayMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsFunctions.checkNetworkConnection(Objects.requireNonNull(getContext()))) {
                    Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}
