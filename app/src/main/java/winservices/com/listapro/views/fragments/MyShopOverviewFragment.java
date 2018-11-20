package winservices.com.listapro.views.fragments;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.viewmodels.ShopVM;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class MyShopOverviewFragment extends Fragment {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static String TAG = MyShopOverviewFragment.class.getSimpleName();

    private ShopKeeperVM shopKeeperVM;
    private ShopVM shopVM;
    private TextView txtShopName, txtPhone, txtShopType, txtCategories;
    private ImageView imgShopPic;
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

                txtShopName = view.findViewById(R.id.txtShopName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtShopType = view.findViewById(R.id.txtShopType);
        txtCategories = view.findViewById(R.id.txtShopDCategories);
        imgShopPic = view.findViewById(R.id.imgShopPic);


        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

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
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentImagePath);
            SharedPrefManager.getInstance(getContext()).storeShopImagePath(serverShopId, currentImagePath);
            shopVM.uploadShopImage(getContext(), serverShopId);
            imgShopPic.setImageBitmap(imageBitmap);
        }
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE).format(new Date());
        String imageName = "lista_pro_"+timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void loadShopData(ShopKeeper shopKeeper) {

        shopVM.getShopsByShopKeeperId(shopKeeper.getServerShopKeeperId()).observe(this, new Observer<List<Shop>>() {
            @Override
            public void onChanged(List<Shop> shops) {
                Shop shop = shops.get(0);
                serverShopId = shop.getServerShopId();

                String shopImgPath = SharedPrefManager.getInstance(getContext()).getShopImagePath(shop.getServerShopId());
                if (shopImgPath!=null) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(shopImgPath);
                    imgShopPic.setImageBitmap(imageBitmap);
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
                initBtnChangePic();

            }
        });

    }

    private void initBtnChangePic() {

        imgShopPic.setEnabled(true);
        imgShopPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
    }

}
