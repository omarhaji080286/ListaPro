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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;
import java.util.Objects;

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
    private OrderVM orderVM;
    private ShopVM shopVM;
    private ImageView imgLogOut;
    private LinearLayout linlayMyShop;
    private ConstraintLayout consLayMyOrders;
    private TextView txtOrdersSentNum;
    private int serverShopId;
    public final static String TAG = WelcomeFragment.class.getSimpleName();

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
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);

        imgLogOut = view.findViewById(R.id.imgLogOut);
        consLayMyOrders = view.findViewById(R.id.consLayMyOrders);
        linlayMyShop = view.findViewById(R.id.linlayMyShop);
        txtOrdersSentNum = view.findViewById(R.id.txtOrdersSentNum);

        initLogOutAndOrdersNum();
        initMyShopItem();
        initMyOrdersItem();
    }

    private void initLogOutAndOrdersNum() {
        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(final ShopKeeper shopKeeper) {
                if (shopKeeper == null) return;
                imgLogOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shopKeeper.setIsLoggedIn(ShopKeeper.LOGGED_OUT);
                        shopKeeperVM.update(shopKeeper);
                        LauncherActivity launcherActivity = (LauncherActivity) getActivity();
                        Objects.requireNonNull(launcherActivity).displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);
                    }
                });
                loadOrdersNum(shopKeeper);
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

    private void loadOrdersNum(ShopKeeper shopKeeper) {
        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                if (shops == null || shops.size() == 0) return;
                updateOrdersNum(shops.get(0));
            }
        });

    }

    private void updateOrdersNum(final Shop shop) {
        orderVM.getSentOrdersNum(shop.getServerShopId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer ordersNum) {
                if (ordersNum == null) return;
                if (ordersNum > 0) {
                    serverShopId = shop.getServerShopId();
                    txtOrdersSentNum.setVisibility(View.VISIBLE);
                    txtOrdersSentNum.setText(String.valueOf(ordersNum));
                } else {
                    txtOrdersSentNum.setVisibility(View.GONE);
                }
            }
        });

        orderVM.loadOrders(getContext(), shop.getServerShopId());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (serverShopId!=0){
            orderVM.loadOrders(getContext(), serverShopId);
        }

    }


}
