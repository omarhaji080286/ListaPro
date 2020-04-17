package winservices.com.listapro.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import winservices.com.listapro.R;
import winservices.com.listapro.views.fragments.SelectCityFragment;

public class AddShopActivity extends AppCompatActivity {

    private String currentFragTag = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        setTitle(R.string.shop_data);

        displayFragment(new SelectCityFragment(), SelectCityFragment.TAG );

    }

    public void displayFragment(Fragment fragment, String tag) {
        if (currentFragTag.equals(tag)) return;
        currentFragTag = tag;
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.frameAddShopActivity, fragment, tag)
                .commit();
    }

}
