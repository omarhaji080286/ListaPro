package winservices.com.listapro.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;

public class ShopTypesAdapter extends RecyclerView.Adapter<ShopTypesAdapter.ShopTypeVH> {

    private List<ShopType> shopTypes;
    private Context context;
    private int rowIndex = -1;
    private static final String TAG = ShopTypesAdapter.class.getSimpleName();

    public ShopTypesAdapter(Context context, List<ShopType> shopTypes, IntShowButtons mCallback) {
        this.shopTypes = shopTypes;
        this.context = context;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public ShopTypeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_type, parent, false);
        return new ShopTypeVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopTypeVH holder, final int position) {
        ShopType shopType = shopTypes.get(position);

        Bitmap bitmap = UtilsFunctions.getOrientedBitmap(shopType.getShopTypeImagePath());
        holder.imgShopType.setImageBitmap(bitmap);
        holder.txtShopType.setText(shopType.getShopTypeName());

        holder.clShopType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
            }
        });

        if (rowIndex == position){
            holder.rbShopType.setChecked(true);
            storeSelectedShopTypeId(shopType.getServerShopTypeId());
            mCallback.onShopTypeSelected();
        } else {
            holder.rbShopType.setChecked(false);
        }

    }

    private void storeSelectedShopTypeId(int serverShopTypeId) {
        SharedPrefManager spm = SharedPrefManager.getInstance(context);
        spm.storeServerShopTypeId(serverShopTypeId);
        Log.d(TAG, "serverShopTypeId = " + spm.getServerShopTypeId());
    }

    @Override
    public int getItemCount() {
        return shopTypes.size();
    }

    public interface IntShowButtons {
        void onShopTypeSelected();
    }

    private IntShowButtons mCallback;


    class ShopTypeVH extends RecyclerView.ViewHolder {

        private ImageView imgShopType;
        private TextView txtShopType;
        private ConstraintLayout clShopType;
        private RadioButton rbShopType;

        ShopTypeVH(@NonNull View itemView) {
            super(itemView);

            clShopType = itemView.findViewById(R.id.clShopType);
            imgShopType = itemView.findViewById(R.id.imgShopType);
            txtShopType= itemView.findViewById(R.id.txtShopType);
            rbShopType = itemView.findViewById(R.id.rbShopType);

        }
    }
}
