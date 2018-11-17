package winservices.com.listapro.views.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.City;
import winservices.com.listapro.models.entities.Country;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.utils.PermissionUtil;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.activities.LauncherActivity;
import winservices.com.listapro.views.adapters.CitySpinnerAdapter;
import winservices.com.listapro.views.adapters.DCategoriesToSelectAdapter;
import winservices.com.listapro.views.adapters.ShopTypeSpinnerAdapter;

import static winservices.com.listapro.utils.PermissionUtil.TXT_FINE_LOCATION;

public class AddShopFragment extends Fragment {

    private final String TAG = AddShopFragment.class.getSimpleName();
    boolean isExpanded = false;
    private ShopKeeperVM shopKeeperVM;
    private ShopTypeVM shopTypeVM;
    private ShopVM shopVM;
    private ShopKeeper currentSK = null;
    private EditText editShopName, editLongitude, editLatitude;
    private Button btnAddShop;
    private Spinner spinnerShopType;
    private Spinner spinnerCity;
    private RecyclerView rvDCategoryToSelect;
    private LinearLayout linlayDCategorieToSelect;
    private DCategoriesToSelectAdapter dCategoriesToSelectAdapter;
    private Location lastLocation;


    public AddShopFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);

        editShopName = view.findViewById(R.id.editShopName);
        spinnerShopType = view.findViewById(R.id.spinnerShopType);
        spinnerCity = view.findViewById(R.id.spinnerCity);
        btnAddShop = view.findViewById(R.id.btnAddShop);
        rvDCategoryToSelect = view.findViewById(R.id.rvDCategoriesToSelect);
        linlayDCategorieToSelect = view.findViewById(R.id.linlayDCategoriesToSelect);

        btnAddShop.setEnabled(false);
        initSpinners();
        requestPermissionInFragment();

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                if (shopKeeper == null) return;
                currentSK = shopKeeper;
                btnAddShop.setEnabled(true);
                initBtnAddShop();
            }
        });

    }

    private void initBtnAddShop() {
        btnAddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsFunctions.checkNetworkConnection(Objects.requireNonNull(getContext()))) {

                    getLocation();

                } else {
                    Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        mFusedLocationClient.getLastLocation().addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    lastLocation = task.getResult();
                    Log.d(TAG, "onComplete:success  " + lastLocation.toString());
                } else {
                    Log.d(TAG, "onComplete:exception  " + task.getException());
                }

                addNewShop();
            }
        });


    }

    private void requestPermissionInFragment() {
        //requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);

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
        }


    }

    private void addNewShop() {

        double longitude = -6.902916;
        double latitude = 33.967371;
        String shopName = editShopName.getText().toString();
        int serverShopId = (new Random()).nextInt();
        ShopType shopType = (ShopType) spinnerShopType.getSelectedItem();
        City city = (City) spinnerCity.getSelectedItem();
        List<DefaultCategory> selectedCategories = dCategoriesToSelectAdapter.getSelectedDCategories();

        if (inputsOk(shopName, shopType.getServerShopTypeId(), city.getServerCityId(), selectedCategories.size())) {
            Shop shop = new Shop(serverShopId, shopName, currentSK.getPhone(),
                    longitude, latitude, currentSK.getServerShopKeeperId());
            shop.setShopType(shopType);
            shop.setCity(city);
            shop.setDCategories(selectedCategories);

            if (lastLocation != null) {
                shop.setLatitude(lastLocation.getLatitude());
                shop.setLongitude(lastLocation.getLongitude());
            }

            shopVM.insert(shop);
            Toast.makeText(getContext(), getString(R.string.shop_added), Toast.LENGTH_SHORT).show();

            LauncherActivity launcherActivity = (LauncherActivity) getActivity();
            Objects.requireNonNull(launcherActivity).displayFragment(new WelcomeFragment());
        }

    }

    private boolean inputsOk(String shopName, int serverShopTypeId, int serverCityId, int selectedCategoriesSize) {
        if (shopName.isEmpty()) {
            editShopName.setError(getString(R.string.shop_name_required));
            editShopName.requestFocus();
            return false;
        }

        if (serverShopTypeId == 0) {
            Toast.makeText(getContext(), getString(R.string.required_shop_type), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (serverCityId == 0) {
            Toast.makeText(getContext(), getString(R.string.city_required), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedCategoriesSize == 0) {
            Toast.makeText(getContext(), getString(R.string.default_category_required), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void initSpinners() {
        loadShopTypesSpinner();
        loadCitiesSpinner();
    }

    private void loadCitiesSpinner() {

        List<City> cities = new ArrayList<>();
        Country country = new Country(1, "morocco");
        cities.add(new City(0, "Choose your City", country));
        cities.add(new City(1, "Rabat", country));
        cities.add(new City(2, "Casablanca", country));
        cities.add(new City(3, "Tanger", country));
        cities.add(new City(4, "Marrakech", country));

        CitySpinnerAdapter citySpinnerAdapter = new CitySpinnerAdapter(Objects.requireNonNull(getContext()), cities);
        spinnerCity.setAdapter(citySpinnerAdapter);

    }

    private void loadShopTypesSpinner() {

        shopTypeVM.loadShopTypes();

        shopTypeVM.getAllShopTypes().observe(this, new Observer<List<ShopType>>() {
            @Override
            public void onChanged(List<ShopType> shopTypes) {
                shopTypes.add(0, new ShopType(0, getString(R.string.choose_shop_type)));

                ShopTypeSpinnerAdapter shopTypeSpinnerAdapter =
                        new ShopTypeSpinnerAdapter(Objects.requireNonNull(getContext()), shopTypes);
                spinnerShopType.setAdapter(shopTypeSpinnerAdapter);
                spinnerShopType.setSelection(0);

                dCategoriesToSelectAdapter = new DCategoriesToSelectAdapter();
                rvDCategoryToSelect.setAdapter(dCategoriesToSelectAdapter);
                rvDCategoryToSelect.setLayoutManager(new LinearLayoutManager(getContext()));

                spinnerShopType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        ShopType shopTypeSelected = (ShopType) adapterView.getItemAtPosition(position);
                        if (shopTypeSelected.getServerShopTypeId() != 0) {
                            if (!isExpanded) {
                                UtilsFunctions.expand(linlayDCategorieToSelect);
                                isExpanded = true;
                            }
                            dCategoriesToSelectAdapter.setDCategories(shopTypeSelected.getdCategories());
                        } else {
                            UtilsFunctions.collapse(linlayDCategorieToSelect);
                            isExpanded = false;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
        });

    }


}
