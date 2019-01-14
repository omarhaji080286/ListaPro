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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
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

    public OrdersAdapter(Context context, OrderVM orderVM) {
        this.context = context;
        this.orderVM = orderVM;
    }

    @NonNull
    @Override
    public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderVH holder, int position) {
        final Order order = orders.get(position);

        String imagePath = SharedPrefManager.getInstance(context).getShopImagePath(Client.PREFIX, order.getClient().getServerUserId());
        if (imagePath!=null){
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
            holder.imgClientPic.setImageBitmap(imageBitmap);
        } else {
            holder.imgClientPic.setImageResource(R.drawable.user_default_image);
        }

        holder.txtClientName.setText(order.getClient().getUserName());
        String stringDate = UtilsFunctions.dateToString(UtilsFunctions.stringToDate(order.getCreationDate()), "dd/MM/YYYY HH:mm:ss");
        holder.txtDate.setText(stringDate);
        holder.txtReference.setText(String.valueOf(order.getServerOrderId()));
        holder.txtStatus.setText(order.getStatus().getStatusName());
        holder.consLayOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailsFragment fragment = new OrderDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Order.SERVER_ORDER_ID, order.getServerOrderId());
                fragment.setArguments(bundle);
                MyOrdersActivity myOrdersActivity = (MyOrdersActivity) view.getContext();
                myOrdersActivity.displayFragment(fragment, OrderDetailsFragment.TAG);
                if (order.getStatus().getStatusId() == Order.REGISTERED) {
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

    class OrderVH extends RecyclerView.ViewHolder {

        private TextView txtClientName, txtReference, txtDate, txtStatus;
        private ImageView imgClientPic;
        private ConstraintLayout consLayOrderContainer;

        OrderVH(@NonNull View itemView) {
            super(itemView);

            consLayOrderContainer = itemView.findViewById(R.id.consLayOrderContainer);
            txtClientName = itemView.findViewById(R.id.txtClientName);
            txtReference = itemView.findViewById(R.id.txtReference);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgClientPic = itemView.findViewById(R.id.imgClientPic);

        }
    }

}
