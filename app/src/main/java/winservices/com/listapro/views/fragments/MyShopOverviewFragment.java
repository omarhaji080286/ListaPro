package winservices.com.listapro.views.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.utils.PermissionUtil;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopTypeVM;
import winservices.com.listapro.viewmodels.ShopVM;

import static android.app.Activity.RESULT_OK;
import static winservices.com.listapro.utils.PermissionUtil.TXT_CAMERA;


public class MyShopOverviewFragment extends Fragment {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static String TAG = MyShopOverviewFragment.class.getSimpleName();

    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private ShopTypeVM shopTypeVM;
    private TextView txtShopName, txtPhone, txtShopType, txtCategories;
    private ImageView imgShopPic, imgShopTypeImg;
    private String currentImagePath;
    private int serverShopId;


    public MyShopOverviewFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_shop_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopTypeVM = ViewModelProviders.of(this).get(ShopTypeVM.class);

        txtShopName = view.findViewById(R.id.txtShopName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtShopType = view.findViewById(R.id.txtShopType);
        txtCategories = view.findViewById(R.id.txtShopDCategories);
        imgShopPic = view.findViewById(R.id.imgShopPic);
        imgShopTypeImg = view.findViewById(R.id.imgShopTypeImg);

        imgShopPic.setEnabled(false);
        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                loadShopData(shopKeeper);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bitmap imageBitmap = BitmapFactory.decodeFile(currentImagePath);
            Bitmap imageBitmap = UtilsFunctions.getOrientedBitmap(currentImagePath);
            imgShopPic.setImageBitmap(imageBitmap);
            storeAndUploadImage();
        }
    }

    private void storeAndUploadImage() {
        Thread thread = new Thread() {
            public void run() {
                SharedPrefManager.getInstance(getContext()).storeShopImagePath(serverShopId, currentImagePath);
                shopVM.uploadShopImage(getContext(), serverShopId);
            }
        };
        thread.run();
    }

    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "winservices.com.listapro", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File getImageFile() throws IOException {
        String imageName = Shop.PREFIX_SHOP + String.valueOf(serverShopId);
        String file_path = Objects.requireNonNull(getContext()).getFilesDir().getPath() + "/jpg";
        File storageDir = new File(file_path);
        if (!storageDir.exists()) storageDir.mkdirs();
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void loadShopData(ShopKeeper shopKeeper) {

        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                if (shops == null || shops.size() == 0) return;
                Shop shop = shops.get(0);
                serverShopId = shop.getServerShopId();

                String shopImgPath = SharedPrefManager.getInstance(getContext()).getShopImagePath(Shop.PREFIX_SHOP, shop.getServerShopId());
                if (shopImgPath != null) {
                    Bitmap imageBitmap = UtilsFunctions.getOrientedBitmap(shopImgPath);
                    if (imageBitmap != null) {
                        imgShopPic.setImageBitmap(imageBitmap);
                    } else {
                        imgShopPic.setImageResource(R.drawable.ic_store_black);
                    }
                } else {
                    imgShopPic.setImageResource(R.drawable.ic_store_black);
                }

                txtShopName.setText(shop.getShopName());
                txtPhone.setText(shop.getShopPhone());
                txtShopType.setText(shop.getShopType().getShopTypeName());

                StringBuilder sb = new StringBuilder();
                for (DefaultCategory dCategory : shop.getdCategories()) {
                    sb.append(" - ");
                    sb.append(dCategory.getDCategoryName());
                    sb.append("\n");
                }
                txtCategories.setText(sb);

                setShoTypeImage(shop.getShopType().getServerShopTypeId());

                initBtnChangePic();

            }
        });

    }

    private void setShoTypeImage(int serverShopTypeId) {
        shopTypeVM.getShopType(serverShopTypeId).observe(this, new Observer<ShopType>() {
            @Override
            public void onChanged(ShopType shopType) {
                Bitmap bitmap = UtilsFunctions.getOrientedBitmap(shopType.getShopTypeImagePath());
                imgShopTypeImg.setImageBitmap(bitmap);
            }
        });
    }

    private void initBtnChangePic() {

        imgShopPic.setEnabled(true);
        imgShopPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtil permissionUtil = new PermissionUtil(Objects.requireNonNull(getContext()));
                if (permissionUtil.checkPermission(TXT_CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                    return;
                }
                requestPermissionInFragment();
            }
        });
    }


    private void requestPermissionInFragment() {

        PermissionUtil permissionUtil = new PermissionUtil(Objects.requireNonNull(getContext()));

        if (permissionUtil.checkPermission(TXT_CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                permissionUtil.showPermissionExplanation(TXT_CAMERA, getActivity());
            } else if (!permissionUtil.checkPermissionPreference(TXT_CAMERA)) {
                permissionUtil.requestPermission(TXT_CAMERA, getActivity());
                permissionUtil.updatePermissionPreference(TXT_CAMERA);
            } else {
                permissionUtil.goToAppSettings();
            }
        }

    }


}
