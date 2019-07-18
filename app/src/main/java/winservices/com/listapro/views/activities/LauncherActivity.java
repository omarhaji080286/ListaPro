package winservices.com.listapro.views.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.services.ListaMessagingService;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.fragments.SignUpFragment;
import winservices.com.listapro.views.fragments.WelcomeFragment;

public class LauncherActivity extends AppCompatActivity {

    private final String TAG = LauncherActivity.class.getSimpleName();
    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private OrderVM orderVM;
    private String currentFragTag = "none";
    private ShopTypeVM shopTypeVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);
        setTitle(R.string.welcome_to_listapro);

        Bundle extras = getIntent().getExtras();

        if(extras!=null){
            String fcmType = extras.getString(ListaMessagingService.FCM_TYPE);
            if (fcmType != null && fcmType.equals(ListaMessagingService.FCM_DATA_UPDATE)) {
                this.finish();
                showAppOnGooglePlay();
            } else {
                launchApp();
            }
        } else {
            launchApp();
        }


    }

    private void launchApp(){

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);

        loadParametersFromServer();

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                if (shopKeeper == null) {
                    displayFragment(new SignUpFragment(), SignUpFragment.TAG);
                    return;
                }
                loadShops(shopKeeper);
            }
        });

    }

    private void showAppOnGooglePlay() {

        final String appPackageName = getPackageName();
        Intent intent;

        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            launchApp();
        }

    }

    private void loadShops(final ShopKeeper shopKeeper) {
        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                if (shops == null || shops.size()==0){
                    startActivity(new Intent(LauncherActivity.this, AddShopActivity.class));
                    LauncherActivity.this.finish();
                    return;
                }
                shopKeeper.setShops(shops);
                routeUser(shopKeeper);
                orderVM.loadOrders( getApplicationContext(), shops.get(0).getServerShopId());
            }
        });
    }

    private void routeUser(ShopKeeper shopKeeper) {
        Fragment fragment = new SignUpFragment();
        String tag = SignUpFragment.TAG;
        if (shopKeeper.getIsLoggedIn() == ShopKeeper.LOGGED_IN) {
            if (shopKeeper.getShops().size() > 0) {
                fragment = new WelcomeFragment();
                tag = WelcomeFragment.TAG;
            } else {
                startActivity(new Intent(this, AddShopActivity.class));
                this.finish();
            }
        } else {
            fragment = new SignUpFragment();
            tag = SignUpFragment.TAG;
        }
        displayFragment(fragment, tag);
    }

    public void displayFragment(Fragment fragment, String tag) {
        if (currentFragTag.equals(tag)) return;
        currentFragTag = tag;
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.frameLauncherActivity, fragment, tag)
                .commit();
    }

    private void loadParametersFromServer() {

        shopTypeVM.loadCitiesFromServer();
        shopTypeVM.loadShopTypes(this);

    }


}
