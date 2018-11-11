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

        oGoodsAdapter = new OrderedGoodsAdapter(orderVM);
        GridLayoutManager glm = new GridLayoutManager(getContext(), GRID_COLUMN_NUMBER );
        rvOGoods.setLayoutManager(glm);
        rvOGoods.setAdapter(oGoodsAdapter);

        int serverOrderId;
        if (getArguments() != null) {
            serverOrderId = getArguments().getInt(Order.SERVER_ORDER_ID);
            orderVM.getOrderedGoodsByOrderId(serverOrderId).observe(this, new Observer<List<OrderedGood>>() {
                @Override
                public void onChanged(List<OrderedGood> orderedGoods) {
                    oGoodsAdapter.setOGoods(orderedGoods);
                }
            });
        }

    }
}
