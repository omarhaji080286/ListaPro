package winservices.com.listapro.views.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import winservices.com.listapro.R;
import winservices.com.listapro.views.fragments.OrderDetailsFragment;
import winservices.com.listapro.views.fragments.OrdersFragment;

public class MyOrdersActivity extends AppCompatActivity {

    private final static String TAG = MyOrdersActivity.class.getSimpleName();

    private String currentFragmentTag = OrdersFragment.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        setTitle(R.string.my_orders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        displayFragment(new OrdersFragment(), OrdersFragment.TAG);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            switch (currentFragmentTag) {
                case OrderDetailsFragment.TAG:
                    displayFragment(new OrdersFragment(), OrdersFragment.TAG);
                    break;
                default:
                    this.finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayFragment(Fragment fragment, String fragmentTag) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.frameMyOrdersActivity, fragment, fragmentTag)
                .commit();
        currentFragmentTag = fragmentTag;
    }

}
