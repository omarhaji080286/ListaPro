package winservices.com.listapro.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import winservices.com.listapro.models.dao.AssocShopTypeDCategoryDao;
import winservices.com.listapro.models.dao.DefaultCategoryDao;
import winservices.com.listapro.models.dao.ShopTypeDao;
import winservices.com.listapro.models.database.ListaProDataBase;
import winservices.com.listapro.models.entities.AssocShopTypeDCategory;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.webservices.ListaProWebServices;
import winservices.com.listapro.webservices.RetrofitHelper;
import winservices.com.listapro.webservices.WebServiceResponse;

public class ShopTypeRepository {

    private final String TAG = ShopTypeRepository.class.getSimpleName();

    private ShopTypeDao shopTypeDao;
    private DefaultCategoryDao defaultCategoryDao;
    private AssocShopTypeDCategoryDao assocShopTypeDCategoryDao;

    public ShopTypeRepository(Application application) {
        ListaProDataBase db = ListaProDataBase.getInstance(application);
        this.shopTypeDao = db.shopTypeDao();
        this.defaultCategoryDao = db.defaultCategoryDao();
        this.assocShopTypeDCategoryDao = db.assocShopTypeDCategoryDao();
    }

    public LiveData<List<ShopType>> getAllShopTypes(){
        return shopTypeDao.getAllShopTypes();
    }

    public List<DefaultCategory> getShopTypeDCategories(final int serverShopTypeId)
            throws ExecutionException, InterruptedException{

        Callable<List<DefaultCategory>>callable = new Callable<List<DefaultCategory>>() {
            @Override
            public List<DefaultCategory> call() throws Exception {
                return defaultCategoryDao.getShopTypeDCategories(serverShopTypeId);
            }
        };
        Future<List<DefaultCategory>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public void insert(ShopType shopType) {
        new InsertShopTypeAsyncTask(shopTypeDao, defaultCategoryDao, assocShopTypeDCategoryDao).execute(shopType);
    }

    public void insert(DefaultCategory dCategory) {
        new InsertDefaultCategoryAsyncTask(defaultCategoryDao).execute(dCategory);
    }

    public void insert(AssocShopTypeDCategory assoc) {
        new InsertAssocAsyncTask(assocShopTypeDCategoryDao).execute(assoc);
    }


    private static class GetShopTypeDCategoriesAsyncTask extends AsyncTask<Integer, Void, List<DefaultCategory>> {
        private DefaultCategoryDao defaultCategoryDao;
        private GetShopTypeDCategoriesAsyncTask(DefaultCategoryDao defaultCategoryDao) {
            this.defaultCategoryDao = defaultCategoryDao;
        }
        @Override
        protected List<DefaultCategory> doInBackground(Integer... integers) {
            return defaultCategoryDao.getShopTypeDCategories(integers[0]);
        }
    }

    private static class InsertShopTypeAsyncTask extends AsyncTask<ShopType, Void, Void> {

        private ShopTypeDao shopTypeDao;
        private DefaultCategoryDao defaultCategoryDao;
        private AssocShopTypeDCategoryDao assocDao;

        private InsertShopTypeAsyncTask(ShopTypeDao shopTypeDao, DefaultCategoryDao defaultCategoryDao, AssocShopTypeDCategoryDao assocDao) {
            this.shopTypeDao = shopTypeDao;
            this.defaultCategoryDao = defaultCategoryDao;
            this.assocDao = assocDao;
        }

        @Override
        protected Void doInBackground(ShopType... shopTypes) {
            shopTypeDao.insert(shopTypes[0]);
            for (DefaultCategory dCategory : shopTypes[0].getdCategories()) {
                defaultCategoryDao.insert(dCategory);
                AssocShopTypeDCategory assoc = new AssocShopTypeDCategory(shopTypes[0].getServerShopTypeId(), dCategory.getDCategoryId());
                assocDao.insert(assoc);
            }
            return null;
        }
    }

    private static class InsertAssocAsyncTask extends AsyncTask<AssocShopTypeDCategory, Void, Void> {

        private AssocShopTypeDCategoryDao assocShopTypeDCategoryDao;

        private InsertAssocAsyncTask(AssocShopTypeDCategoryDao assocShopTypeDCategoryDao) {
            this.assocShopTypeDCategoryDao = assocShopTypeDCategoryDao;
        }

        @Override
        protected Void doInBackground(AssocShopTypeDCategory... assocs) {
            assocShopTypeDCategoryDao.insert(assocs[0]);
            return null;
        }
    }

    private static class InsertDefaultCategoryAsyncTask extends AsyncTask<DefaultCategory, Void, Void> {

        private DefaultCategoryDao defaultCategoryDao;

        private InsertDefaultCategoryAsyncTask(DefaultCategoryDao defaultCategoryDao) {
            this.defaultCategoryDao = defaultCategoryDao;
        }

        @Override
        protected Void doInBackground(DefaultCategory... dCategories) {
            defaultCategoryDao.insert(dCategories[0]);
            return null;
        }
    }

    public void loadShopTypesFromServer() {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Call<WebServiceResponse> call = ws.getShopTypes();

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            List<ShopType> shopTypes = wsResponse.getShopTypesWithCategories();
                            for(ShopType shopType : shopTypes){
                                shopType.setDefaultIcon();
                                insert(shopType);
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


}
