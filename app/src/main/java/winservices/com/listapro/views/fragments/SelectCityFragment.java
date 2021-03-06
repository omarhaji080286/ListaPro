package winservices.com.listapro.views.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
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
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.views.activities.AddShopActivity;

import static winservices.com.listapro.utils.PermissionUtil.TXT_FINE_LOCATION;


public class SelectCityFragment extends Fragment {

    public static final String TAG = "SelectCityFragment";
    private ShopTypeVM shopTypeVM;
    private RadioGroup rgCities;
    private Button btnNext;
    private Dialog dialog;

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

        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), getContext(), R.string.loading).create();

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    requestLocationPermission();
                } else {
                    ((AddShopActivity) Objects.requireNonNull(getActivity())).
                            displayFragment(new SelectShopTypeFragment(), SelectShopTypeFragment.TAG);
                }


            }
        });
    }

    private void requestLocationPermission() {

        PermissionUtil permissionUtil = new PermissionUtil(Objects.requireNonNull(getContext()));

        if (permissionUtil.checkPermission(TXT_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionUtil.showPermissionExplanation(TXT_FINE_LOCATION, getActivity());
            } else if (!permissionUtil.checkPermissionPreference(TXT_FINE_LOCATION)) {
                permissionUtil.requestPermission(TXT_FINE_LOCATION, getActivity());
                permissionUtil.updatePermissionPreference(TXT_FINE_LOCATION);
            } else {
                permissionUtil.goToAppSettings();
            }
        } else {

            if(UtilsFunctions.isGPSEnabled(getContext())){
                ((AddShopActivity) Objects.requireNonNull(getActivity())).
                        displayFragment(new SelectShopTypeFragment(), SelectShopTypeFragment.TAG);
            } else {
                UtilsFunctions.enableGPS(getActivity());
            }

        }
    }

    private void loadCities(final View view) {
        shopTypeVM.getAllCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {
                dialog.show();
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

        rgCities.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnNext.setVisibility(View.VISIBLE);
            }
        });

        dialog.dismiss();

    }

}
