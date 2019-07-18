package winservices.com.listapro.views.fragments;


import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.City;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.viewmodels.ShopVM;
import winservices.com.listapro.views.activities.AddShopActivity;

import static winservices.com.listapro.utils.UtilsFunctions.to2digits;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopNameFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    public static final String TAG = "ShopNameFragment";
    private Button btnFinish, btnPrevious;
    private EditText editShopName, editMinuteOpening, editMinuteClosing, editHourOpening, editHourClosing;
    private ShopKeeperVM shopKeeperVM;
    private ShopTypeVM shopTypeVM;
    private ShopVM shopVM;
    private Shop shop;
    private boolean[] isDataReady = new boolean[3];
    private ShopKeeper currentSK;
    private int clickedViewId;
    private Location lastLocation;
    private SharedPrefManager spm = SharedPrefManager.getInstance(getContext());


    public ShopNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Thread(new Runnable() {
            public void run() {
                getLocation();
            }
        }).start();

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);

        editShopName = view.findViewById(R.id.editShopName);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnFinish = view.findViewById(R.id.btnFinish);

        editShopName.requestFocus();
        UtilsFunctions.showKeyboardOn(getActivity(), editShopName);

        editMinuteOpening = view.findViewById(R.id.editMinuteOpening);
        editMinuteClosing = view.findViewById(R.id.editMinuteClosing);
        editHourOpening = view.findViewById(R.id.editHourOpening);
        editHourClosing = view.findViewById(R.id.editHourClosing);

        editMinuteOpening.setOnClickListener(this);
        editMinuteClosing.setOnClickListener(this);
        editHourOpening.setOnClickListener(this);
        editHourClosing.setOnClickListener(this);

        btnFinish.setEnabled(false);

        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                if (shopKeeper == null) return;
                currentSK = shopKeeper;
                btnFinish.setEnabled(true);
                prepareButtons();
            }
        });
    }

    private void prepareButtons() {

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddShopActivity) Objects.requireNonNull(getActivity())).
                        displayFragment(new SelectShopTypeFragment(), SelectShopTypeFragment.TAG);

                UtilsFunctions.hideKeyboardFrom(Objects.requireNonNull(getContext()), editShopName);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shop = new Shop();
                loadData();
            }
        });

        editShopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0) {
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    btnFinish.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadData() {
        shopTypeVM.getShopType(spm.getServerShopTypeId()).observe(ShopNameFragment.this, new Observer<ShopType>() {
            @Override
            public void onChanged(ShopType shopType) {
                if (shopType == null) return;
                shop.setShopType(shopType);
                loadCity();
            }
        });
    }

    private void loadCity() {
        shopTypeVM.getCity(spm.getServerCityId()).observe(ShopNameFragment.this, new Observer<City>() {
            @Override
            public void onChanged(City city) {
                if (city == null) return;
                shop.setCity(city);
                loadDCategories();
            }
        });
    }

    private void loadDCategories() {
        shopTypeVM.getCategoriesByIds(spm.getSelectedCategoriesIds()).observe(ShopNameFragment.this, new Observer<List<DefaultCategory>>() {
            @Override
            public void onChanged(List<DefaultCategory> defaultCategories) {
                if (defaultCategories == null) return;
                shop.setDCategories(defaultCategories);
                addShop();
            }
        });
    }

    private void addShop() {

        String shopName = editShopName.getText().toString();
        String openingTime = editHourOpening.getText().toString() + ":" + editMinuteOpening.getText().toString();
        String closingTime = editHourClosing.getText().toString() + ":" + editMinuteClosing.getText().toString();

        if (lastLocation != null) {
            shop.setLatitude(lastLocation.getLatitude());
            shop.setLongitude(lastLocation.getLongitude());
        } else {
            shop.setLatitude(shop.getCity().getCityLatitude());
            shop.setLongitude(shop.getCity().getCityLongitude());
        }

        if (inputsOk(shopName)) {
            shop.setShopName(shopName);
            shop.setServerShopKeeperIdFk(currentSK.getServerShopKeeperId());
            shop.setShopPhone(currentSK.getPhone());
            shop.setOpeningTime(openingTime);
            shop.setClosingTime(closingTime);

            shopVM.insert(shop);
            Toast.makeText(getContext(), getString(R.string.shop_added), Toast.LENGTH_SHORT).show();

            ((AddShopActivity) Objects.requireNonNull(getActivity())).
                    displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);
        }

    }

    private boolean inputsOk(String shopName) {
        if (shopName.isEmpty()) {
            editShopName.setError(getString(R.string.shop_name_required));
            editShopName.requestFocus();
            return false;
        }
        return true;
    }

    private void loadTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                ShopNameFragment.this, Calendar.HOUR, Calendar.MINUTE, true);
        timePickerDialog.show();
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        switch (clickedViewId) {
            case R.id.editHourOpening:
                editHourOpening.setText(to2digits(hour));
                editMinuteOpening.setText(to2digits(minute));
                break;
            case R.id.editMinuteOpening:
                editHourOpening.setText(to2digits(hour));
                editMinuteOpening.setText(to2digits(minute));
                break;
            case R.id.editHourClosing:
                editHourClosing.setText(to2digits(hour));
                editMinuteClosing.setText(to2digits(minute));
                break;
            case R.id.editMinuteClosing:
                editHourClosing.setText(to2digits(hour));
                editMinuteClosing.setText(to2digits(minute));
                break;
        }

    }

    @Override
    public void onClick(View view) {
        clickedViewId = view.getId();
        loadTimePicker();
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

            }
        });

    }

}
