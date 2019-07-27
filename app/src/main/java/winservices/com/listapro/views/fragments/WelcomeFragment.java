package winservices.com.listapro.views.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import winservices.com.listapro.BuildConfig;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.utils.AnimationManager;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.activities.MyOrdersActivity;
import winservices.com.listapro.views.activities.MyShopActivity;

public class WelcomeFragment extends Fragment {

    private ShopKeeperVM shopKeeperVM;
    private OrderVM orderVM;
    private ShopVM shopVM;
    private LinearLayout linlayMyShop;
    private ConstraintLayout consLayMyOrders;
    private TextView txtOrdersSentNum;
    private int serverShopId;
    private ImageView imgGooglePlay, imgShare;
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

        consLayMyOrders = view.findViewById(R.id.consLayMyOrders);
        linlayMyShop = view.findViewById(R.id.linlayMyShop);
        txtOrdersSentNum = view.findViewById(R.id.txtOrdersSentNum);
        imgGooglePlay = view.findViewById(R.id.imgGooglePlay);
        imgShare = view.findViewById(R.id.imgShare);

        UtilsFunctions.hideKeyboardFrom(Objects.requireNonNull(getContext()), consLayMyOrders);

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppStoreLink();
            }
        });

        initLogOutAndOrdersNum();
        initMyShopItem();
        initMyOrdersItem();
    }

    private void initLogOutAndOrdersNum() {
        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(final ShopKeeper shopKeeper) {
                if (shopKeeper == null) return;
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

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                manageGooglePlayIcon();
                Log.d(TAG, "Icons handled");
            }
        });

    }

    private void manageGooglePlayIcon() {

        SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
        int googlePlayVersion = spm.getGooglePlayVersion();

        int userVersion = BuildConfig.VERSION_CODE;
        if (googlePlayVersion > userVersion) {
            imgGooglePlay.setVisibility(View.VISIBLE);
            AnimationManager am = new AnimationManager(getContext());
            am.animateItem(imgGooglePlay, R.anim.blink, 1000);
            imgGooglePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilsFunctions.goToMarket(Objects.requireNonNull(getContext()));
                }
            });
        } else {
            imgGooglePlay.setVisibility(View.GONE);
        }

    }

    private void shareAppStoreLink() {

        String listaLink = "https://play.google.com/store/apps/details?id=com.winservices.wingoods";
        String mainMessage = Objects.requireNonNull(getContext()).getResources().getString(R.string.share_message);
        String subject = "avec Lista, les courses deviennent fun";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        String body = mainMessage +
                "\n" +
                listaLink;
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.listapro) ));

    }




}
