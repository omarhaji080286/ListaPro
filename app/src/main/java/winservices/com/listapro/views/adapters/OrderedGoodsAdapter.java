package winservices.com.listapro.views.adapters;

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
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.viewmodels.OrderVM;

public class OrderedGoodsAdapter extends RecyclerView.Adapter<OrderedGoodsAdapter.OrderedGoodVH> {

    private List<OrderedGood> oGoods = new ArrayList<>();
    private List<OrderedGood> updatedOGoods = new ArrayList<>();
    private OrderVM orderVM;
    private int orderStatusId;

    public OrderedGoodsAdapter(OrderVM orderVM) {
        this.orderVM = orderVM;
    }

    public void setOGoods(List<OrderedGood> oGoods) {
        this.oGoods = oGoods;
        this.updatedOGoods = oGoods;
        notifyDataSetChanged();
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    @NonNull
    @Override
    public OrderedGoodVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ordered_good, parent, false);
        return new OrderedGoodVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderedGoodVH holder, final int position) {

        final OrderedGood oGood = oGoods.get(position);

        holder.txtOGoodName.setText(oGood.getGoodName());
        holder.txtOGoodDesc.setText(oGood.getGoodDesc());
        holder.imgOGoodPic.setImageResource(R.drawable.ic_store_black);

        if (oGood.getStatus() != OrderedGood.UNPROCESSED) {
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.imgCheck.setImageResource(getImageResource(oGood));
        } else {
            holder.imgCheck.setVisibility(View.GONE);
        }

        if (orderStatusId != Order.COMPLETED && orderStatusId != Order.NOT_SUPPORTED && orderStatusId != Order.AVAILABLE) {
            holder.consLayOGoodContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updatedOGoods.remove(oGood);
                    switch (oGood.getStatus()) {
                        case OrderedGood.UNPROCESSED:
                            oGood.setStatus(OrderedGood.PROCESSED);
                            holder.imgCheck.setImageResource(R.drawable.check);
                            holder.imgCheck.setVisibility(View.VISIBLE);
                            break;
                        case OrderedGood.PROCESSED:
                            oGood.setStatus(OrderedGood.NOT_AVAILABLE);
                            holder.imgCheck.setImageResource(R.drawable.cross);
                            holder.imgCheck.setVisibility(View.VISIBLE);
                            break;
                        default:
                            oGood.setStatus(OrderedGood.UNPROCESSED);
                            holder.imgCheck.setVisibility(View.GONE);
                    }
                    orderVM.update(oGood);
                    updatedOGoods.add(oGood);
                }
            });
        }

    }

    private int getImageResource(OrderedGood oGood) {
        switch (oGood.getStatus()) {
            case OrderedGood.NOT_AVAILABLE:
                return R.drawable.cross;
            case OrderedGood.PROCESSED:
                return R.drawable.check;
            default:
                return 0;
        }
    }


    @Override
    public int getItemCount() {
        return oGoods.size();
    }

    public List<OrderedGood> getUpdatedOGoods() {
        return updatedOGoods;
    }

    class OrderedGoodVH extends RecyclerView.ViewHolder {

        private TextView txtOGoodName, txtOGoodDesc;
        private ImageView imgOGoodPic, imgCheck;
        private ConstraintLayout consLayOGoodContainer;

        OrderedGoodVH(@NonNull View itemView) {
            super(itemView);

            consLayOGoodContainer = itemView.findViewById(R.id.consLayOGoodContainer);
            imgCheck = itemView.findViewById(R.id.imgCheck);
            imgOGoodPic = itemView.findViewById(R.id.imgOGoodPic);
            txtOGoodName = itemView.findViewById(R.id.txtOGoodName);
            txtOGoodDesc = itemView.findViewById(R.id.txtOGoodDesc);

        }
    }
}
