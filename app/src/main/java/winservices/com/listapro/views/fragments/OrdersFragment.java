package winservices.com.listapro.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.adapters.OrdersAdapter;


public class OrdersFragment extends Fragment {

    public final static String TAG = "OrdersFragment";

    private OrderVM orderVM;
    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private RecyclerView rvOrders;
    private OrdersAdapter ordersAdapter;

    public OrdersFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        rvOrders = view.findViewById(R.id.rvOrders);

        ordersAdapter = new OrdersAdapter(getContext(), orderVM);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(ordersAdapter);

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                loadShop(shopKeeper.getServerShopKeeperId());
            }
        });

    }

    private void loadShop(int serverShopKeeperId) {
        shopVM.getShopsByShopKeeperId(serverShopKeeperId).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                orderVM.loadOrders(getContext(), shops.get(0).getServerShopId());
                setOrdersToAdapter(shops.get(0).getServerShopId());
            }
        });

    }

    private void setOrdersToAdapter(int serverShopId) {
        orderVM.getOrdersByServerShopId(serverShopId).observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> ordersInDb) {
                ordersAdapter.setOrders(ordersInDb);
            }
        });
    }


}
