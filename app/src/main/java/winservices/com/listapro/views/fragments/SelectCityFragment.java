package winservices.com.listapro.views.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;
import java.util.Objects;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.City;
import winservices.com.listapro.utils.PermissionUtil;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.views.activities.AddShopActivity;


public class SelectCityFragment extends Fragment {

    public static final String TAG = "SelectCityFragment";
    private ShopTypeVM shopTypeVM;
    private RadioGroup rgCities;
    private Button btnNext;

    public SelectCityFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);
        rgCities = view.findViewById(R.id.rgCities);
        btnNext = view.findViewById(R.id.btnNext);

        loadCities(view);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rgCities.getCheckedRadioButtonId();
                SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
                spm.storeServerCityId(rgCities.getCheckedRadioButtonId());
                Log.d(TAG, "serverCityId = " + spm.getServerCityId());
                ((AddShopActivity) Objects.requireNonNull(getActivity())).
                            displayFragment(new SelectShopTypeFragment(), SelectShopTypeFragment.TAG);
            }
        });
    }

    private void loadCities(final View view) {
        shopTypeVM.getAllCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {
                if (cities == null) return;
                prepareCitiesRadioGroup(cities);
            }
        });
    }

    private void prepareCitiesRadioGroup(List<City> cities) {

        SharedPrefManager spm = SharedPrefManager.getInstance(getContext());
        int storedServerCityId = spm.getServerCityId();

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        rgCities.removeAllViews();
        for (int i = 0; i < cities.size(); i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(cities.get(i).getCityName());
            rb.setId(cities.get(i).getServerCityId());
            if (cities.get(i).getServerCityId() == storedServerCityId){
                rb.setChecked(true);
                btnNext.setVisibility(View.VISIBLE);
            }
            rgCities.addView(rb, lp);
        }

        PermissionUtil.requestPermissionInFragment(getActivity());

        rgCities.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnNext.setVisibility(View.VISIBLE);
            }
        });

    }

}
