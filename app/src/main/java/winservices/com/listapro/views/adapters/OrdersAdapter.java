package winservices.com.listapro.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.Client;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderStatusValue;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.OrderVM;
import winservices.com.listapro.views.activities.MyOrdersActivity;
import winservices.com.listapro.views.fragments.OrderDetailsFragment;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderVH> {

    private List<Order> orders = new ArrayList<>();
    private OrderVM orderVM;
    private Context context;
    private Fragment fragment;

    public OrdersAdapter(Fragment fragment,Context context, OrderVM orderVM) {
        this.context = context;
        this.orderVM = orderVM;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderVH holder, int position) {
        final Order order = orders.get(position);

        SharedPrefManager sp = SharedPrefManager.getInstance(context);
        String imagePath = sp.getImagePath(Client.PREFIX + order.getClient().getServerUserId());
        if (imagePath != null) {
            float width = UtilsFunctions.convertDpToPx(context, 90);
            float height = UtilsFunctions.convertDpToPx(context, 90);

            Bitmap imageBitmap = sp.rotate(-90.0f, BitmapFactory.decodeFile(imagePath), width, height);
            holder.imgClientPic.setScaleType(ImageView.ScaleType.CENTER);
            holder.imgClientPic.setImageBitmap(imageBitmap);
        } else {
            holder.imgClientPic.setImageResource(R.drawable.user_default_image);
        }

        if (order.getIsToDeliver()==Order.IS_TO_COLLECT){
            holder.txtToDeliver.setVisibility(View.GONE);
            holder.imgDelivery.setVisibility(View.GONE);
        }

        holder.txtClientName.setText(order.getClient().getUserName());
        holder.txtDate.setText(order.getDisplayedCollectTime(context, order.getCreationDate()));
        holder.txtReference.setText(String.valueOf(order.getServerOrderId()));
        holder.txtCollectTime.setText(order.getDisplayedCollectTime(context, order.getEndTime()));
        holder.txtItemsNb.setText(String.valueOf(order.getOrderedGoodsNum()));

        if (!order.getOrderPriceTemp(context).equals("") ){
            holder.llOrderPrice.setVisibility(View.VISIBLE);
            holder.txtOrderPrice.setText(order.getOrderPrice());
        }

        switch (order.getStatus().getStatusId()){
            case Order.REGISTERED :
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked_gray);
                holder.imgAvailable.setImageResource(R.drawable.checked_gray);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtStatus.setText(context.getString(R.string.registered));
                break;
            case Order.READ :
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked);
                holder.imgAvailable.setImageResource(R.drawable.checked_gray);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtStatus.setText(context.getString(R.string.read));

                break;
            case Order.AVAILABLE :
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked);
                holder.imgAvailable.setImageResource(R.drawable.checked);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtStatus.setText(context.getString(R.string.can_collect));

                break;
            case Order.COMPLETED :
                holder.imgRegistered.setVisibility(View.GONE);
                holder.imgRead.setVisibility(View.GONE);
                holder.imgAvailable.setVisibility(View.GONE);
                holder.imgClosedOrNotSuported.setImageResource(R.drawable.completed);
                holder.imgClosedOrNotSuported.setVisibility(View.VISIBLE);
                holder.txtStatus.setText(context.getString(R.string.completed));

                break;
            case Order.NOT_SUPPORTED:
                holder.imgRegistered.setVisibility(View.GONE);
                holder.imgRead.setVisibility(View.GONE);
                holder.imgAvailable.setVisibility(View.GONE);
                holder.imgClosedOrNotSuported.setImageResource(R.drawable.not_supported);
                holder.imgClosedOrNotSuported.setVisibility(View.VISIBLE);
                holder.txtStatus.setText(context.getString(R.string.not_supported));
                break;
        }


        holder.llOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailsFragment fragment = new OrderDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Order.SERVER_ORDER_ID, order.getServerOrderId());
                fragment.setArguments(bundle);
                MyOrdersActivity myOrdersActivity = (MyOrdersActivity) view.getContext();

                int orders_type = MyOrdersActivity.ONGOING_ORDERS;
                int orderStatusId = order.getStatus().getStatusId();
                if (orderStatusId == Order.COMPLETED || orderStatusId == Order.AVAILABLE){
                    orders_type = MyOrdersActivity.CLOSED_ORDERS;
                }

                myOrdersActivity.displayFragment(fragment, OrderDetailsFragment.TAG, orders_type, order.getServerOrderId());
                if (orderStatusId == Order.REGISTERED) {
                    OrderStatusValue status = new OrderStatusValue(Order.READ, "READ");
                    order.setStatus(status);
                    orderVM.updateOrderOnServer(order);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    static class OrderVH extends RecyclerView.ViewHolder {

        private TextView txtClientName, txtReference, txtDate, txtStatus, txtItemsNb, txtCollectTime, txtToDeliver, txtOrderPrice;
        private ImageView imgClientPic, imgRegistered, imgRead, imgAvailable, imgClosedOrNotSuported, imgDelivery ;
        private LinearLayoutCompat llOrderContainer, llOrderPrice;

        OrderVH(@NonNull View itemView) {
            super(itemView);

            llOrderContainer = itemView.findViewById(R.id.llOrderContainer);
            txtClientName = itemView.findViewById(R.id.txtClientName);
            txtReference = itemView.findViewById(R.id.txtReference);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtItemsNb = itemView.findViewById(R.id.txtItemsNb);
            imgClientPic = itemView.findViewById(R.id.imgClientPic);
            txtCollectTime = itemView.findViewById(R.id.txtCollectTime);
            imgRegistered = itemView.findViewById(R.id.imgRegistered);
            imgRead = itemView.findViewById(R.id.imgRead);
            imgAvailable = itemView.findViewById(R.id.imgAvailable);
            imgClosedOrNotSuported = itemView.findViewById(R.id.imgClosedOrNotSuported);
            txtToDeliver = itemView.findViewById(R.id.txtToDeliver);
            imgDelivery = itemView.findViewById(R.id.imgDelivery);
            llOrderPrice = itemView.findViewById(R.id.llOrderPrice);
            txtOrderPrice = itemView.findViewById(R.id.txtOrderPrice);


        }
    }

}
