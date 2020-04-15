package winservices.com.listapro.repositories;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import winservices.com.listapro.models.dao.AssocShopDCategoryDao;
import winservices.com.listapro.models.dao.ShopDao;
import winservices.com.listapro.models.database.ListaProDataBase;
import winservices.com.listapro.models.entities.AssocShopDCategory;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Image;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.webservices.ListaProWebServices;
import winservices.com.listapro.webservices.RetrofitHelper;
import winservices.com.listapro.webservices.WebServiceResponse;

public class ShopRepository {

    private final static String TAG = ShopRepository.class.getSimpleName();

    private ShopDao shopDao;
    private AssocShopDCategoryDao assocDao;

    public ShopRepository(Application application) {
        ListaProDataBase db = ListaProDataBase.getInstance(application);
        this.shopDao = db.shopDao();
        this.assocDao = db.assocShopDCategoryDao();
    }

    public LiveData<List<Shop>> getShopsByShopKeeperId(int skId) {
        return shopDao.getShopsByShopKeeperId(skId);
    }

    public void insert(Shop shop) {
        new InsertShopAsyncTask(shopDao, assocDao).execute(shop);
    }

    private void updateShopDelivering(Shop shop) {
        new UpdateShopDeliveringAsyncTask(shopDao).execute(shop);
    }

    public void insertShopOnServer(final Shop shop) {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Gson gson = new Gson();
        Map<String, String> hashMap = new HashMap<>();
        String jsonRequest = gson.toJson(shop);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        hashMap.put("jsonRequest", jsonRequest);
        Call<WebServiceResponse> call = ws.addShop(hashMap);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    Log.d(TAG, "response body: " + response.body());
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            shop.setServerShopId(wsResponse.getServerShopId());
                            insert(shop);
                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure : " + t.getMessage());
            }
        });
    }

    public void updateShopDelivering(int serverShopId) {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Gson gson = new Gson();
        Map<String, String> hashMap = new HashMap<>();
        String jsonRequest = gson.toJson(serverShopId);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        hashMap.put("jsonRequest", jsonRequest);
        Call<WebServiceResponse> call = ws.getShop(hashMap);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    Log.d(TAG, "response body: " + response.body());
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            Shop shop =  wsResponse.getShop();
                            updateShopDelivering(shop);
                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure : " + t.getMessage());
            }
        });
    }

    public List<DefaultCategory> getShopDCategoriesByShopId(final int serverShopId)
                                        throws ExecutionException, InterruptedException{
            Callable<List<DefaultCategory>> callable = new Callable<List<DefaultCategory>>() {
                @Override
                public List<DefaultCategory> call() {
                    return shopDao.getShopDCategoriesByShopId(serverShopId);
                }
            };
            Future<List<DefaultCategory>> future = Executors.newSingleThreadExecutor().submit(callable);
            return future.get();
    }

    private static class UpdateShopDeliveringAsyncTask extends AsyncTask<Shop, Void, Void> {

        private ShopDao shopDao;

        private UpdateShopDeliveringAsyncTask(ShopDao shopDao) {
            this.shopDao = shopDao;
        }

        @Override
        protected Void doInBackground(Shop... shops) {
            shopDao.updateShopDelivering(shops[0].getIsDelivering(), shops[0].getServerShopId());
            return null;
        }
    }

    private static class InsertShopAsyncTask extends AsyncTask<Shop, Void, Void> {

        private ShopDao shopDao;
        private AssocShopDCategoryDao assocDao;

        private InsertShopAsyncTask(ShopDao shopDao, AssocShopDCategoryDao assocDao) {
            this.shopDao = shopDao;
            this.assocDao = assocDao;
        }

        @Override
        protected Void doInBackground(Shop... shops) {
            shopDao.insert(shops[0]);
            for (DefaultCategory dCategory : shops[0].getdCategories()) {
                AssocShopDCategory assoc = new AssocShopDCategory(shops[0].getServerShopId(), dCategory.getDCategoryId());
                assocDao.insert(assoc);
            }
            return null;
        }
    }

    public void uploadShopImage(Context context, int serverShopId) {
        String imagePath = SharedPrefManager.getInstance(context).getImagePath(Shop.PREFIX_SHOP + serverShopId);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        String imageString = UtilsFunctions.imageToString(imageBitmap, 30);
        String imageTitle = String.valueOf(serverShopId);
        Image shopImage = new Image(imageTitle, imageString);

        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();
        Gson gson = new Gson();
        Map<String, String> hashMap = new HashMap<>();
        String jsonRequest = gson.toJson(shopImage);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        hashMap.put("jsonRequest", jsonRequest);
        Call<WebServiceResponse> call = ws.uploadShopImage(hashMap);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    Log.d(TAG, "response body: " + response.body());
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            Log.d(TAG, "Success : Image uploaded" + wsResponse.getMessage());
                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure : " + t.getMessage());
            }
        });

    }


}
