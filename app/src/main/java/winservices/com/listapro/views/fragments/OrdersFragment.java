package winservices.com.listapro.views.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.activities.MyOrdersActivity;
import winservices.com.listapro.views.adapters.OrdersAdapter;

import static winservices.com.listapro.views.activities.MyOrdersActivity.CLOSED_ORDERS;
import static winservices.com.listapro.views.activities.MyOrdersActivity.ONGOING_ORDERS;


public class OrdersFragment extends Fragment {

    public final static String TAG = "OrdersFragment";

    private OrderVM orderVM;
    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private RecyclerView rvOrders;
    private OrdersAdapter ordersAdapter;
    private TextView txtNoOrderRegistered;
    private int orders_type;
    private Dialog dialog;
    private int serverShopKeeperId;

    public OrdersFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyOrdersActivity ordersActivity = Objects.requireNonNull((MyOrdersActivity) getActivity());
        switch (orders_type){
            case ONGOING_ORDERS :
                ordersActivity.setTitle(getString(R.string.ongoing_orders));
                break;
            case CLOSED_ORDERS :
                ordersActivity.setTitle(getString(R.string.closed_orders));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_orders, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyOrdersActivity ordersActivity = Objects.requireNonNull((MyOrdersActivity) getActivity());
        switch (item.getItemId()) {
            case R.id.menuOngoingOrders:
                ordersActivity.setCustomTitle(getString(R.string.ongoing_orders));
                ordersActivity.displayFragment(new OrdersFragment(), OrdersFragment.TAG, ONGOING_ORDERS, 0);
                break;
            case R.id.menuClosedOrders:
                ordersActivity.setCustomTitle(getString(R.string.closed_orders));
                ordersActivity.displayFragment(new OrdersFragment(), OrdersFragment.TAG, CLOSED_ORDERS, 0);
                break;
            case R.id.sync:
                orderVM.loadOrders( getContext(), serverShopKeeperId);
                break;
            case android.R.id.home :
                ordersActivity.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), getContext(), R.string.loading).create();
        dialog.show();

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        rvOrders = view.findViewById(R.id.rvOrders);
        txtNoOrderRegistered = view.findViewById(R.id.txtNoOrderRegistered);

        Bundle bundle = getArguments();
        if (bundle!=null){
            orders_type = bundle.getInt(MyOrdersActivity.ORDERS_TYPE);
        }

        ordersAdapter = new OrdersAdapter(this, getContext(), orderVM);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(ordersAdapter);

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                serverShopKeeperId = shopKeeper.getServerShopKeeperId();
                loadShop(serverShopKeeperId);
            }
        });

    }

    private void loadShop(int serverShopKeeperId) {
        shopVM.getShopsByShopKeeperId(serverShopKeeperId).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                //orderVM.loadOrders(getContext(), shops.get(0).getServerShopId());
                setOrdersToAdapter(shops.get(0).getServerShopId());
            }
        });
    }

    private void setOrdersToAdapter(int serverShopId) {
        orderVM.getOrdersByServerShopId(serverShopId, orders_type).observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> ordersInDb) {

                int ordersNum = ordersInDb.size();
                if (ordersInDb.size() == 0){
                    txtNoOrderRegistered.setVisibility(View.VISIBLE);
                } else {
                    txtNoOrderRegistered.setVisibility(View.GONE);
                    updateTitle(ordersNum);
                }
                Collections.sort(ordersInDb);
                ordersAdapter.setOrders(ordersInDb);
                ordersAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    private void updateTitle(int ordersNum){
        MyOrdersActivity ordersActivity = Objects.requireNonNull((MyOrdersActivity) getActivity());
        String ordersTypeTitle = getString(R.string.ongoing_orders);
        if (orders_type == CLOSED_ORDERS) ordersTypeTitle = getString(R.string.closed_orders);
        String title = ordersTypeTitle + " ( " + ordersNum + " ) ";
        ordersActivity.setCustomTitle(title);
    }

}
