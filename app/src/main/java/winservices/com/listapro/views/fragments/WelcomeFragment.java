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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import winservices.com.listapro.BuildConfig;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.RemoteConfigParams;
import winservices.com.listapro.utils.AnimationManager;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.views.activities.MyOrdersActivity;
import winservices.com.listapro.views.activities.MyShopActivity;
import winservices.com.listapro.webservices.ListaProWebServices;
import winservices.com.listapro.webservices.RetrofitHelper;
import winservices.com.listapro.webservices.WebServiceResponse;

public class WelcomeFragment extends Fragment {

    private LinearLayout linlayMyShop;
    private ConstraintLayout consLayMyOrders;
    private TextView txtOrdersSentNum;
    private ImageView imgGooglePlay;
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

        consLayMyOrders = view.findViewById(R.id.consLayMyOrders);
        linlayMyShop = view.findViewById(R.id.linlayMyShop);
        txtOrdersSentNum = view.findViewById(R.id.txtOrdersSentNum);
        imgGooglePlay = view.findViewById(R.id.imgGooglePlay);
        ImageView imgShare = view.findViewById(R.id.imgShare);

        UtilsFunctions.hideKeyboardFrom(Objects.requireNonNull(getContext()), consLayMyOrders);

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppStoreLink();
            }
        });

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateOrdersNum();
                Log.d(TAG, "Order numbers handled");
            }
        });

        initMyShopItem();
        initMyOrdersItem();
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

    private void updateOrdersNum() {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        SharedPrefManager spm = SharedPrefManager.getInstance(getContext());

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("serverShopId", String.valueOf(spm.getServerShopId()));
        Call<WebServiceResponse> call = ws.getShopIndicators(hashMap);
        Log.d(TAG, "Server called from WelcomeFragment");

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            int onGoingOrdersCount = wsResponse.getShopOrdersCount();
                            SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
                            spm.storeOngoingOrdersCount(onGoingOrdersCount);
                            updateIndicator(onGoingOrdersCount);
                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure loading orders count from server : " + t.getMessage());
            }
        });

    }

    private void updateIndicator(int shopOrdersCount){
        if (shopOrdersCount>0){
            txtOrdersSentNum.setVisibility(View.VISIBLE);
            txtOrdersSentNum.setText(String.valueOf(shopOrdersCount));
        } else {
            txtOrdersSentNum.setVisibility(View.GONE);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                manageGooglePlayIcon();
                SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
                updateIndicator(spm.getOngoingOrdersCount());
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

        RemoteConfigParams rcp = new RemoteConfigParams(getContext());
        String mainMessage = Objects.requireNonNull(getContext()).getResources().getString(R.string.share_message);
        try {
            JSONObject jsonObject = new JSONObject(rcp.getAppMessages());
            mainMessage = jsonObject.getString("shareMessage");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String listaLink = "https://play.google.com/store/apps/details?id=com.winservices.wingoods";
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
