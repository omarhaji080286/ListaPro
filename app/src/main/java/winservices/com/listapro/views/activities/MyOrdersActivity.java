package winservices.com.listapro.views.activities;

import android.os.Bundle;

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
        setTitle(getString(R.string.ongoing_orders));
        setContentView(R.layout.activity_my_orders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(ORDERS_TYPE, ONGOING_ORDERS);
        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setArguments(bundle);
        displayFragment(ordersFragment, OrdersFragment.TAG, ONGOING_ORDERS, 0);

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders, menu);
        menu.findItem(R.id.share).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menuOngoingOrders :
                setTitle(getString(R.string.ongoing_orders));
                displayFragment(new OrdersFragment(), OrdersFragment.TAG, ONGOING_ORDERS);
                currentOrdersType = ONGOING_ORDERS;
                break;
            case R.id.menuClosedOrders :
                setTitle(getString(R.string.closed_orders));
                displayFragment(new OrdersFragment(), OrdersFragment.TAG, CLOSED_ORDERS);
                currentOrdersType = CLOSED_ORDERS;
                break;
            case android.R.id.home :
                if (OrderDetailsFragment.TAG.equals(currentFragmentTag)) {
                    displayFragment(new OrdersFragment(), OrdersFragment.TAG, currentOrdersType);
                } else {
                    this.finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/

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

}
