package winservices.com.listapro.views.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private int serverOrderId;

    public OrderDetailsFragment() {  }

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
        GridLayoutManager glm = new GridLayoutManager(getContext(), GRID_COLUMN_NUMBER );
        rvOGoods.setLayoutManager(glm);
        rvOGoods.setAdapter(oGoodsAdapter);

        if (getArguments() != null) {
            serverOrderId = getArguments().getInt(Order.SERVER_ORDER_ID);
            loadOrderedGoods(serverOrderId);
            initFinishButton(serverOrderId);
        }



    }

    private void initFinishButton(final int serverOrderId) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(R.string.confirm_order_ready);
        builder.setTitle(R.string.order_ready);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getContext(), "confirm order n° " + serverOrderId, Toast.LENGTH_SHORT).show();
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
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void loadOrderedGoods(int serverOrderId) {
        orderVM.getOrderedGoodsByOrderId(serverOrderId).observe(this, new Observer<List<OrderedGood>>() {
            @Override
            public void onChanged(List<OrderedGood> orderedGoods) {
                oGoodsAdapter.setOGoods(orderedGoods);
            }
        });
    }
}
