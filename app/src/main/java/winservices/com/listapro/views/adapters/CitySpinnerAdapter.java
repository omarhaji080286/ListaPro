package winservices.com.listapro.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.City;

public class CitySpinnerAdapter extends ArrayAdapter<City> {


    public CitySpinnerAdapter(@NonNull Context context, @NonNull List<City> cities) {
        super(context, 0, cities);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_city_spinner, parent, false);
        }

        TextView spinnerText = convertView.findViewById(R.id.spinnerText);

        City currentCity = getItem(position);
        if (currentCity != null) {
            spinnerText.setText(currentCity.getCityName());
        }

        return convertView;
    }

}
