package winservices.com.listapro.views.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderStatusValue;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.views.adapters.OrderedGoodsAdapter;

public class OrderDetailsFragment extends Fragment {

    public final static String TAG = "OrderDetailsFragment";

    private final static int GRID_COLUMN_NUMBER = 3;
    private OrderedGoodsAdapter oGoodsAdapter;
    private OrderVM orderVM;
    private Button btnFinishOrder;
    private GridLayoutManager glm;
    private RecyclerView rvOGoods;
    private TextView txtOrderId, txtDeliveryType, txtDeliveryDay, txtClientName, txtClientPhone, txtClientAddress;
    private ImageButton imgBtnPhone, imgBtnLocation;
    private LinearLayoutCompat llAddress, llLocation;
    private Order orderToShare;
    private List<OrderedGood> orderedGoodsToShare;

    public OrderDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_orders, menu);
        menu.findItem(R.id.menuClosedOrders).setVisible(false);
        menu.findItem(R.id.menuOngoingOrders).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share){
            if (orderToShare!=null && orderedGoodsToShare!=null){
                shareOrderData(orderToShare, orderedGoodsToShare);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        rvOGoods = view.findViewById(R.id.rvOGoods);
        btnFinishOrder = view.findViewById(R.id.btnFinishOrder);

        //Card components
        txtOrderId = view.findViewById(R.id.txtOrderId);
        txtDeliveryType = view.findViewById(R.id.txtDeliveryType);
        txtDeliveryDay = view.findViewById(R.id.txtDeliveryDay);
        txtClientName = view.findViewById(R.id.txtClientName);
        txtClientPhone = view.findViewById(R.id.txtClientPhone);
        imgBtnPhone = view.findViewById(R.id.imgBtnPhone);
        txtClientAddress = view.findViewById(R.id.txtClientAddress);
        imgBtnLocation = view.findViewById(R.id.imgBtnLocation);
        llAddress = view.findViewById(R.id.llAddress);
        llLocation = view.findViewById(R.id.llLocation);

        oGoodsAdapter = new OrderedGoodsAdapter(orderVM);
        glm = new GridLayoutManager(getContext(), GRID_COLUMN_NUMBER);
        rvOGoods.setLayoutManager(glm);
        rvOGoods.setAdapter(oGoodsAdapter);

        if (getArguments() != null) {
            int serverOrderId = getArguments().getInt(Order.SERVER_ORDER_ID);
            orderVM.getOrderByServerOrderId(serverOrderId).observe(this, new Observer<Order>() {
                @Override
                public void onChanged(Order order) {

                    orderToShare = order;
                    setupOrderCard(order);

                    oGoodsAdapter.setOrderStatusId(order.getStatus().getStatusId());
                    loadOrderedGoods(order);
                    int statusId = order.getStatus().getStatusId();
                    if (statusId == Order.AVAILABLE || statusId == Order.NOT_SUPPORTED || statusId == Order.COMPLETED) {
                        btnFinishOrder.setVisibility(View.GONE);
                    } else {
                        initFinishButton(order);
                    }
                }
            });
        }


    }

    private void setupOrderCard(final Order order) {
        txtOrderId.setText(String.valueOf(order.getServerOrderId()));

        if (order.getIsToDeliver()==Order.IS_TO_DELIVER){
            txtDeliveryType.setText(R.string.delivery_type);
            llAddress.setVisibility(View.VISIBLE);
            llLocation.setVisibility(View.VISIBLE);
        } else {
            txtDeliveryType.setText(R.string.to_collect);
            llAddress.setVisibility(View.GONE);
            llLocation.setVisibility(View.GONE);
        }

        txtDeliveryDay.setText(order.getDisplayedCollectTime(getContext(), order.getEndTime()));

        txtClientName.setText(order.getClient().getUserName());
        txtClientPhone.setText(order.getClient().getUserPhone());

        imgBtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialer(order.getClient().getUserPhone());
            }
        });

        txtClientAddress.setText(order.getUserAddress());

        imgBtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoogleMaps(order.getUserLocation());
            }
        });

    }

    private boolean isOrderSupported(List<OrderedGood> oGoods){
        boolean isOrderSupported = false;
        for (int i = 0; i < oGoods.size(); i++) {
            if (oGoods.get(i).getStatus()==OrderedGood.PROCESSED) isOrderSupported = true;
        }
        return  isOrderSupported;
    }

    private void initFinishButton(final Order order) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(R.string.confirm_order_ready);
        builder.setTitle(R.string.order_ready);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                OrderStatusValue status = new OrderStatusValue();
                List<OrderedGood> oGoods = oGoodsAdapter.getUpdatedOGoods();
                if (isOrderSupported(oGoods)) {
                    status.setStatusId(Order.AVAILABLE);
                    status.setStatusName("AVAILABLE");
                } else {
                    status.setStatusId(Order.NOT_SUPPORTED);
                    status.setStatusName("NOT SUPPORTED");
                }

                updateOrderStatus(order, status);
                orderVM.updateOrderedGoodsOnServer(oGoods);


            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        btnFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyOGoodsStatus()) {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.update_items_status), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void animateItem(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                View v = glm.findViewByPosition(position);
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                if (v != null) {
                    v.startAnimation(anim);
                }

            }
        }, 50);
    }

    private boolean verifyOGoodsStatus() {
        boolean allGoodsUpdated = true;
        List<OrderedGood> oGoods = oGoodsAdapter.getUpdatedOGoods();
        for (int i = 0; i < oGoods.size(); i++) {
            OrderedGood oGood = oGoods.get(i);
            if (oGood.getStatus() == OrderedGood.UNPROCESSED) {
                allGoodsUpdated = false;
                animateItem(i);
            }
        }
        return allGoodsUpdated;
    }

    private void updateOrderStatus(Order order, OrderStatusValue status) {
            order.setStatus(status);
            orderVM.updateOrderOnServer(order);
            Toast.makeText(getContext(), getString(R.string.notification_sent_to_client), Toast.LENGTH_SHORT).show();
    }

    private void loadOrderedGoods(Order order) {
        orderVM.getOrderedGoodsByOrderId(order.getServerOrderId()).observe(this, new Observer<List<OrderedGood>>() {
            @Override
            public void onChanged(List<OrderedGood> orderedGoods) {
                oGoodsAdapter.setOGoods(orderedGoods);
                orderedGoodsToShare = orderedGoods;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        List<OrderedGood> oGoods = oGoodsAdapter.getUpdatedOGoods();
        orderVM.updateOrderedGoodsOnServer(oGoods);
    }

    private void startGoogleMaps(String location){
        String uri = "geo:"+ location+"?q="+location;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void startDialer(String clientPhone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+clientPhone));
        startActivity(intent);
    }

    private void shareOrderData(Order order, List<OrderedGood> orderedGoodsToShare){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String plainText = order.toPlainText() +
                "\n" +
                " - Articles : " + orderedGoodsToShare.size()+
                "\n" +
                listToPlainText(orderedGoodsToShare);
        sendIntent.putExtra(Intent.EXTRA_TEXT, plainText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private String listToPlainText(List<OrderedGood> oGoods){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<oGoods.size(); i++) {
            OrderedGood oGood = oGoods.get(i);
            sb.append("   ");sb.append(i+1);sb.append("- ");sb.append(oGood.getGoodName());sb.append(" (");sb.append(oGood.getGoodDesc());sb.append(")");
            if (oGood.getStatus()==OrderedGood.NOT_AVAILABLE){
                sb.append(" (NON DISPONIBLE)");
            }
            if (i<oGoods.size()-1){
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
