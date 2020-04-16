package winservices.com.listapro.views.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Client;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.activities.LauncherActivity;
import winservices.com.listapro.views.activities.MyOrdersActivity;
import winservices.com.listapro.views.adapters.OrdersAdapter;
import winservices.com.listapro.webservices.ListaProWebServices;
import winservices.com.listapro.webservices.RetrofitHelper;
import winservices.com.listapro.webservices.WebServiceResponse;

import static winservices.com.listapro.views.activities.MyOrdersActivity.CLOSED_ORDERS;
import static winservices.com.listapro.views.activities.MyOrdersActivity.ONGOING_ORDERS;


public class OrdersFragment extends Fragment {

    public final static String TAG = "OrdersFragment";
    private final static int ROWS_COUNT = 4;

    private OrderVM orderVM;
    private ShopVM shopVM;
    private OrdersAdapter ordersAdapter;
    private TextView txtNoOrderRegistered;
    private ProgressBar progressBar;
    private int ordersType;
    private int serverShopKeeperId;
    private int serverShopId;
    private int row = 0;

    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 0;

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
        switch (ordersType){
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
            case android.R.id.home :
                startActivity(new Intent(ordersActivity, LauncherActivity.class));
                ordersActivity.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShopKeeperVM shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        txtNoOrderRegistered = view.findViewById(R.id.txtNoOrderRegistered);
        progressBar = view.findViewById(R.id.progressBar);

        Bundle bundle = getArguments();
        if (bundle!=null){
            ordersType = bundle.getInt(MyOrdersActivity.ORDERS_TYPE);
        }

        ordersAdapter = new OrdersAdapter(getContext(), orderVM);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvOrders.setLayoutManager(layoutManager);
        rvOrders.setAdapter(ordersAdapter);

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                serverShopKeeperId = shopKeeper.getServerShopKeeperId();
                loadShop(serverShopKeeperId);
            }
        });

        rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (dy>0){
                    if (isLoading){
                        if (totalItemCount > previousTotal){
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!isLoading && (totalItemCount-visibleItemCount)<=(pastVisibleItems+viewThreshold) ){
                        performPagination();
                        isLoading = true;
                    }
                }

            }
        });

    }

    private int i = 0;
    private void loadShop(int serverShopKeeperId) {

        shopVM.getShopsByShopKeeperId(serverShopKeeperId).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                if (shops==null || shops.size()==0) return;
                serverShopId = shops.get(0).getServerShopId();
                if (i>0) return;
                i++;
                setOrdersToAdapter(serverShopId);
            }
        });
    }

    private void setOrdersToAdapter(int serverShopId){
        progressBar.setVisibility(View.VISIBLE);
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("serverShopId", String.valueOf(serverShopId));
        hashMap.put("row", String.valueOf(row));
        hashMap.put("orders_type", String.valueOf(ordersType));
        Call<WebServiceResponse> call = ws.getShopOrdersPage(hashMap);
        Log.d(TAG, "Server called from setOrdersToAdaper");

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            if (wsResponse.getShopOrdersCount()>0){
                                List<Order> orders = wsResponse.getOrders();
                                updateOrdersInLocalDB(orders);

                                txtNoOrderRegistered.setVisibility(View.GONE);

                                ordersAdapter.setOrders(orders);
                                ordersAdapter.notifyDataSetChanged();

                                row = wsResponse.getNewRow();
                                updateTitle(wsResponse.getShopOrdersCount());

                                Log.d(TAG, "onResponse: " + orders.size() + " orders loaded");
                            } else {
                                txtNoOrderRegistered.setVisibility(View.VISIBLE);
                            }

                            SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
                            spm.storeOngoingOrdersCount(wsResponse.getShopOrdersCount());

                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure loading orders from server : " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void performPagination(){
        progressBar.setVisibility(View.VISIBLE);
        RetrofitHelper rh = new RetrofitHelper();
        final ListaProWebServices ws = rh.initWebServices();

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("serverShopId", String.valueOf(serverShopId));
        hashMap.put("row", String.valueOf(row));
        hashMap.put("rows_count", String.valueOf(ROWS_COUNT));
        hashMap.put("orders_type", String.valueOf(ordersType));
        Call<WebServiceResponse> call = ws.getShopOrdersPage(hashMap);
        Log.d(TAG, "Server called from performPagination");

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            if (wsResponse.getMessage().equals("on")){
                                List<Order> orders = wsResponse.getOrders();
                                updateOrdersInLocalDB(orders);
                                ordersAdapter.addOrders(orders);
                                row = wsResponse.getNewRow();
                                Log.d(TAG, "onResponse: " + orders.size() + " orders loaded");

                            } else {
                                Toast.makeText(getContext(), R.string.no_more_orders, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onResponse: no more orders loaded");
                            }

                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }

                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure loading orders from server : " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateOrdersInLocalDB(final List<Order> orders){
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (Order order : orders) {
                    orderVM.insert(order);
                    String userImage = order.getClient().getUserImage();
                    if (!userImage.equals("defaultImage")) {
                        SharedPrefManager.getInstance(getContext()).storeImageToFile(userImage, "jpg", Client.PREFIX, order.getClient().getServerUserId());
                    }
                    for (OrderedGood orderedGood : order.getOrderedGoods()) {
                        orderVM.insertOGood(orderedGood);
                    }
                }
            }
        };
        thread.start();
    }

    private void updateTitle(final int ordersNum){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                MyOrdersActivity ordersActivity = Objects.requireNonNull((MyOrdersActivity) getActivity());
                String ordersTypeTitle = getString(R.string.ongoing_orders);
                if (ordersType == CLOSED_ORDERS) ordersTypeTitle = getString(R.string.closed_orders);
                String title = ordersTypeTitle + " ( " + ordersNum + " ) ";
                ordersActivity.setCustomTitle(title);
            }
        });

    }

}
