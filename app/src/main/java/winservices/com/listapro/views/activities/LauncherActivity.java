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
    private String currentFragTag = "none";

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
                if (shopKeeper == null) {
                    displayFragment(new SignUpFragment(), SignUpFragment.TAG);
                    return;
                }
                loadShops(shopKeeper);
            }
        });
    }

    private void loadShops(final ShopKeeper shopKeeper) {
        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                if (shops == null || shops.size()==0){
                    displayFragment(new AddShopFragment(), AddShopFragment.TAG);
                    return;
                }
                shopKeeper.setShops(shops);
                routeUser(shopKeeper);
                orderVM.loadOrders( getApplicationContext(), shops.get(0).getServerShopId());
            }
        });
    }

    private void routeUser(ShopKeeper shopKeeper) {
        Fragment fragment;
        String tag;
        if (shopKeeper.getIsLoggedIn() == ShopKeeper.LOGGED_IN) {
            if (shopKeeper.getShops().size() > 0) {
                fragment = new WelcomeFragment();
                tag = WelcomeFragment.TAG;
            } else {
                fragment = new AddShopFragment();
                tag = AddShopFragment.TAG;
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


}
