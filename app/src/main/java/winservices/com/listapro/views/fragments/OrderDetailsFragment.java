package winservices.com.listapro.views.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderStatusValue;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.views.adapters.OrderedGoodsAdapter;

public class OrderDetailsFragment extends Fragment {

    public final static String TAG = "OrderDetailsFragment";

    private final static int GRID_COLUMN_NUMBER = 3;
    private RecyclerView rvOGoods;
    private OrderedGoodsAdapter oGoodsAdapter;
    private OrderVM orderVM;
    private Button btnFinishOrder;
    private GridLayoutManager glm;

    public OrderDetailsFragment() {
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

        oGoodsAdapter = new OrderedGoodsAdapter(orderVM);
        glm = new GridLayoutManager(getContext(), GRID_COLUMN_NUMBER);
        rvOGoods.setLayoutManager(glm);
        rvOGoods.setAdapter(oGoodsAdapter);

        if (getArguments() != null) {
            int serverOrderId = getArguments().getInt(Order.SERVER_ORDER_ID);
            orderVM.getOrderByServerOrderId(serverOrderId).observe(this, new Observer<Order>() {
                @Override
                public void onChanged(Order order) {
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

    private void initFinishButton(final Order order) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(R.string.confirm_order_ready);
        builder.setTitle(R.string.order_ready);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setOrderStatusToAvailable(order);
                List<OrderedGood> oGoods = oGoodsAdapter.getUpdatedOGoods();
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

    private void setOrderStatusToAvailable(Order order) {
        if (order.getStatus().getStatusId() != Order.AVAILABLE) {
            OrderStatusValue status = new OrderStatusValue(Order.AVAILABLE, "AVAILABLE");
            order.setStatus(status);
            orderVM.updateOrderOnServer(order);
            Toast.makeText(getContext(), getString(R.string.notification_sent_to_client), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrderedGoods(Order order) {
        orderVM.getOrderedGoodsByOrderId(order.getServerOrderId()).observe(this, new Observer<List<OrderedGood>>() {
            @Override
            public void onChanged(List<OrderedGood> orderedGoods) {
                oGoodsAdapter.setOGoods(orderedGoods);
            }
        });
    }
}
