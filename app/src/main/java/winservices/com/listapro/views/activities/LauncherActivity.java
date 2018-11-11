package winservices.com.listapro.views.activities;

import android.os.Bundle;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.fragments.AddShopFragment;
import winservices.com.listapro.views.fragments.SignUpFragment;
import winservices.com.listapro.views.fragments.WelcomeFragment;

public class LauncherActivity extends AppCompatActivity {

    private final String TAG = LauncherActivity.class.getSimpleName();
    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private OrderVM orderVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setTitle(R.string.welcome_to_listapro);

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                loadShops(shopKeeper);
            }
        });


    }


    private void loadShops(final ShopKeeper shopKeeper) {
        if (shopKeeper != null) {
            shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
                @Override
                public void onChanged(List<Shop> shops) {
                    shopKeeper.setShops(shops);
                    routeUser(shopKeeper);
                    orderVM.loadOrders(shops.get(0).getServerShopId());
                }
            });
        } else {
            displayFragment(new SignUpFragment());
        }
    }

    private void routeUser(ShopKeeper shopKeeper) {
        Fragment fragment;
        if (shopKeeper.getIsLoggedIn() == ShopKeeper.LOGGED_IN) {
            if (shopKeeper.getShops().size() > 0) {
                fragment = new WelcomeFragment();
            } else {
                fragment = new AddShopFragment();
            }
        } else {
            fragment = new SignUpFragment();
        }
        displayFragment(fragment);
    }


    public void displayFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.frameLauncherActivity, fragment, fragment.getTag())
                .commit();
    }


}
