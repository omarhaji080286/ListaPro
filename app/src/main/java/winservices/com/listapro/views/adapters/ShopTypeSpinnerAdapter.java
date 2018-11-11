package winservices.com.listapro.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.ShopType;

public class ShopTypeSpinnerAdapter extends ArrayAdapter<ShopType> {


    public ShopTypeSpinnerAdapter(@NonNull Context context, List<ShopType> shopTypeList) {
        super(context, 0, shopTypeList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_shoptype_spinner, parent, false);
        }
        ImageView spinnerIcon = convertView.findViewById(R.id.spinnerImg);
        TextView spinnerText = convertView.findViewById(R.id.spinnerText);

        ShopType currentShopType = getItem(position);
        if (currentShopType != null) {
            spinnerIcon.setImageResource(currentShopType.getIcon());
            spinnerText.setText(currentShopType.getShopTypeName());
        }

        return convertView;
    }
}
