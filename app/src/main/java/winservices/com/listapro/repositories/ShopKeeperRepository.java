package winservices.com.listapro.repositories;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import winservices.com.listapro.models.dao.ShopKeeperDao;
import winservices.com.listapro.models.database.ListaProDataBase;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.webservices.ListaProWebServices;
import winservices.com.listapro.webservices.RetrofitHelper;
import winservices.com.listapro.webservices.WebServiceResponse;

public class ShopKeeperRepository {

    private final static String TAG = ShopKeeperRepository.class.getSimpleName();

    private ShopKeeperDao shopKeeperDao;
    private LiveData<List<ShopKeeper>> allShopKeepers;
    private ShopRepository shopRepository;
    private ShopTypeRepository shopTypeRepository;

    public ShopKeeperRepository(Application application) {
        ListaProDataBase db = ListaProDataBase.getInstance(application);
        shopKeeperDao = db.shopKeeperDao();
        allShopKeepers = shopKeeperDao.getAllShopKeepers();
        shopRepository = new ShopRepository(application);
        shopTypeRepository = new ShopTypeRepository(application);
    }

    public LiveData<ShopKeeper> getLastLogged() {
        return shopKeeperDao.getLastLogged();
    }

    public void update(ShopKeeper shopKeeper) {
        new UpdateShopKeeperAsyncTask(shopKeeperDao).execute(shopKeeper);
    }

    public void logIn(ShopKeeper shopKeeper) {
        new LogInAsyncTask(shopKeeperDao).execute(shopKeeper);
    }

    public void insert(ShopKeeper shopKeeper) {
        new InsertShopKeeperAsyncTask(shopKeeperDao).execute(shopKeeper);
    }

    public void delete(ShopKeeper shopKeeper) {
        new DeleteShopKeeperAsyncTask(shopKeeperDao).execute(shopKeeper);
    }

    public LiveData<List<ShopKeeper>> getAllShopKeepers() {
        return allShopKeepers;
    }

    public LiveData<ShopKeeper> getShopKeeperByPhone(String phone) {
        return shopKeeperDao.getShopKeeperByPhone(phone);
    }

    public void signUpShopKeeper(ShopKeeper shopKeeper, final Context context) {
        RetrofitHelper rh = new RetrofitHelper();
        final ListaProWebServices ws = rh.initWebServices();

        Gson gson = new Gson();
        Map<String, String> hashMap = new HashMap<>();
        String jsonRequest = gson.toJson(shopKeeper);
        hashMap.put("jsonRequest", jsonRequest);
        Call<WebServiceResponse> call = ws.addShopKeeper(hashMap);
        Log.d(TAG, "jsonRequest: " + jsonRequest);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            ShopKeeper shopKeeper = wsResponse.getShopKeeper();
                            shopKeeper.setLastLogged(ShopKeeper.LAST_LOGGED);
                            shopKeeper.setIsLoggedIn(ShopKeeper.LOGGED_IN);
                            insert(shopKeeper);

                            Shop shop = wsResponse.getShop();
                            if (shop!=null){
                                shopRepository.insert(shop);
                                SharedPrefManager.getInstance(context).storeImageToFile(shop.getShopImage(), "jpg", Shop.PREFIX_SHOP, shop.getServerShopId());
                            }

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

    private static class InsertShopKeeperAsyncTask extends AsyncTask<ShopKeeper, Void, Void> {

        private ShopKeeperDao shopKeeperDao;

        private InsertShopKeeperAsyncTask(ShopKeeperDao shopKeeperDao) {
            this.shopKeeperDao = shopKeeperDao;
        }

        @Override
        protected Void doInBackground(ShopKeeper... shopKeepers) {
            Log.d(TAG, "insert doInBackground called ");
            shopKeeperDao.changeLastLogged(ShopKeeper.NOT_LAST_LOGGED);
            shopKeeperDao.insert(shopKeepers[0]);
            return null;
        }
    }

    private static class UpdateShopKeeperAsyncTask extends AsyncTask<ShopKeeper, Void, Void> {

        private ShopKeeperDao shopKeeperDao;

        private UpdateShopKeeperAsyncTask(ShopKeeperDao shopKeeperDao) {
            this.shopKeeperDao = shopKeeperDao;
        }

        @Override
        protected Void doInBackground(ShopKeeper... shopKeepers) {
            shopKeeperDao.update(shopKeepers[0]);
            return null;
        }
    }

    private static class DeleteShopKeeperAsyncTask extends AsyncTask<ShopKeeper, Void, Void> {

        private ShopKeeperDao shopKeeperDao;

        private DeleteShopKeeperAsyncTask(ShopKeeperDao shopKeeperDao) {
            this.shopKeeperDao = shopKeeperDao;
        }

        @Override
        protected Void doInBackground(ShopKeeper... shopKeepers) {
            shopKeeperDao.delete(shopKeepers[0]);
            return null;
        }
    }

    private static class LogInAsyncTask extends AsyncTask<ShopKeeper, Void, Void> {

        private ShopKeeperDao shopKeeperDao;

        private LogInAsyncTask(ShopKeeperDao shopKeeperDao) {
            this.shopKeeperDao = shopKeeperDao;
        }

        @Override
        protected Void doInBackground(ShopKeeper... shopKeepers) {
            shopKeeperDao.changeLastLogged(ShopKeeper.NOT_LAST_LOGGED);
            shopKeeperDao.update(shopKeepers[0]);
            return null;
        }
    }

}
