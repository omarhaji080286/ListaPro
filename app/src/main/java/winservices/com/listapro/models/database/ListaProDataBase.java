package winservices.com.listapro.models.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import winservices.com.listapro.models.dao.AssocShopDCategoryDao;
import winservices.com.listapro.models.dao.AssocShopTypeDCategoryDao;
import winservices.com.listapro.models.dao.CityDao;
import winservices.com.listapro.models.dao.DefaultCategoryDao;
import winservices.com.listapro.models.dao.OrderDao;
import winservices.com.listapro.models.dao.OrderedGoodDao;
import winservices.com.listapro.models.dao.ShopDao;
import winservices.com.listapro.models.dao.ShopKeeperDao;
import winservices.com.listapro.models.dao.ShopTypeDao;
import winservices.com.listapro.models.entities.AssocShopDCategory;
import winservices.com.listapro.models.entities.AssocShopTypeDCategory;
import winservices.com.listapro.models.entities.City;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.repositories.ShopTypeRepository;

@Database(  entities = {ShopKeeper.class, Shop.class, ShopType.class, AssocShopTypeDCategory.class,
                        DefaultCategory.class, AssocShopDCategory.class, Order.class, OrderedGood.class,
                        City.class},
            version = 5,
            exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ListaProDataBase extends RoomDatabase {

    private static ListaProDataBase instance;

    public static synchronized ListaProDataBase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(), ListaProDataBase.class,
                    "listaPro.db").fallbackToDestructiveMigration().addCallback(roomCallBack).build();
        }
        return instance;
    }

    public abstract ShopKeeperDao shopKeeperDao();
    public abstract ShopDao shopDao();
    public abstract ShopTypeDao shopTypeDao();
    public abstract DefaultCategoryDao defaultCategoryDao();
    public abstract AssocShopTypeDCategoryDao assocShopTypeDCategoryDao();
    public abstract AssocShopDCategoryDao assocShopDCategoryDao();
    public abstract OrderDao orderDao();
    public abstract OrderedGoodDao orderedGoodDao();
    public abstract CityDao cityDao();

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private ShopKeeperDao shopKeeperDao;
        private ShopDao shopDao;
        private ShopTypeRepository shopTypeRepository;


        private PopulateDbAsyncTask(ListaProDataBase db){
            shopKeeperDao = db.shopKeeperDao();
            shopDao = db.shopDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //shopKeeperDao.insert(new ShopKeeper("+212123456789", ShopKeeper.LOGGED_OUT, "uuid", ShopKeeper.LAST_LOGGED));
            //shopDao.insert(new Shop("shope name", "+212123456789",-6.902916,33.967371, 1 ));
            return null;
        }
    }

}
