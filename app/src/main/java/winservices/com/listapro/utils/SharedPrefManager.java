package winservices.com.listapro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Shop;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "listaSharedPreferences";
    private static final String KEY_ACCESS_TOKEN = "token";
    private static final String KEY_ACCESS_SERVER_CITY_ID = "serverCityId";
    private static final String KEY_ACCESS_SHOP_TYPE_ID = "serverShopTypeId";
    private static final String KEY_ACCESS_SELECTED_CATEGORIES = "selectedCategories";

    private Context context;
    private static SharedPrefManager instance;

    private SharedPrefManager(Context context){
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if (instance==null){
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void storeSelectedCategories(List<DefaultCategory> categories){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < categories.size(); i++) {
            set.add(String.valueOf(categories.get(i).getDCategoryId()));
        }
        editor.putStringSet(KEY_ACCESS_SELECTED_CATEGORIES, set );
        editor.apply();
    }

    public List<Integer> getSelectedCategoriesIds(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Set<String> stringSet = sharedPreferences.getStringSet(KEY_ACCESS_SELECTED_CATEGORIES, null);
        if (stringSet==null) return null;
        List<Integer> intSet = new ArrayList<>();
        for(String strCategoryId : stringSet)
            intSet.add(Integer.parseInt(strCategoryId));
        return intSet;
    }

    public void storeServerShopTypeId(int serverShopTypeId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ACCESS_SHOP_TYPE_ID, serverShopTypeId);
        editor.apply();
    }

    public int getServerShopTypeId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ACCESS_SHOP_TYPE_ID, 0);
    }

    public void storeServerCityId(int serverCityId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ACCESS_SERVER_CITY_ID, serverCityId);
        editor.apply();
    }

    public int getServerCityId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ACCESS_SERVER_CITY_ID, 0);
    }

    public void storeToken(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void storeShopImagePath(int serverShopId, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Shop.PREFIX_SHOP + serverShopId, path);
        editor.apply();
    }

    public void storeImageToFile(final String encodedImage, final String imageType, final String prefix, final int sharedPrefKey) {
        final String file_path = context.getFilesDir().getPath() + "/" + imageType;
        Thread thread = new Thread(){
            public void run() {
                File dir = new File(file_path);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, prefix + sharedPrefKey + "."+imageType);
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    Bitmap bitmap = UtilsFunctions.stringToBitmap(encodedImage);
                    Bitmap.CompressFormat compressFormat;
                    switch (imageType){
                        case "png":
                            compressFormat = Bitmap.CompressFormat.PNG;
                            break;
                        case "jpg":
                            compressFormat = Bitmap.CompressFormat.JPEG;
                            break;
                        default:
                            compressFormat = Bitmap.CompressFormat.JPEG;
                    }
                    bitmap.compress(compressFormat, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                storeImagePath(prefix+sharedPrefKey,file.getAbsolutePath());
            }
        };
        thread.run();
    }

    private void storeImagePath(String key, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, path);
        editor.apply();
    }

    public String getImagePath(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

}
