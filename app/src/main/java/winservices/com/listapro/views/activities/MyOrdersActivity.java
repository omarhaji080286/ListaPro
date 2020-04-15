package winservices.com.listapro.views.activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.views.fragments.OrdersFragment;

public class MyOrdersActivity extends AppCompatActivity {

    private final static String TAG = MyOrdersActivity.class.getSimpleName();

    public String currentFragmentTag = OrdersFragment.TAG;
    public final static int CLOSED_ORDERS = 2;
    public final static int ONGOING_ORDERS = 1;
    public final static String ORDERS_TYPE = "orders_type";
    //public int currentOrdersType = ONGOING_ORDERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        setCustomTitle(getString(R.string.ongoing_orders));

        Bundle bundle = new Bundle();
        bundle.putInt(ORDERS_TYPE, ONGOING_ORDERS);
        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setArguments(bundle);
        displayFragment(ordersFragment, OrdersFragment.TAG, ONGOING_ORDERS, 0);

    }

    public void displayFragment(Fragment fragment, String fragmentTag, int orders_type, int serverOrderId) {
        FragmentManager manager = getSupportFragmentManager();

        if (orders_type != 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(ORDERS_TYPE, orders_type);
            bundle.putInt(Order.SERVER_ORDER_ID, serverOrderId);
            fragment.setArguments(bundle);
        }

        manager.beginTransaction()
                .replace(R.id.frameMyOrdersActivity, fragment, fragmentTag)
                .commit();
        currentFragmentTag = fragmentTag;
    }

    public void setCustomTitle(String title){
        TextView textview = new TextView(MyOrdersActivity.this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(layoutParams);
        textview.setText(title);
        textview.setTextColor(Color.WHITE);
        textview.setGravity(Gravity.START);
        textview.setTextSize(18);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
                    | androidx.appcompat.app.ActionBar.DISPLAY_HOME_AS_UP |
                    androidx.appcompat.app.ActionBar.DISPLAY_SHOW_HOME);
            actionBar.setCustomView(textview);
        }
    }

}
