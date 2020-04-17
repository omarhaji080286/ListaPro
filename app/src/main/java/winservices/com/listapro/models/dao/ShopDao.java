package winservices.com.listapro.models.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.Shop;

@Dao
public interface ShopDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Shop shop);

    @Update
    void update(Shop shop);

    @Delete
    void delete(Shop shop);

    @Query("SELECT * FROM shops WHERE serverShopKeeperIdFk = :serverShopKeeperId")
    LiveData<List<Shop>> getShopsByShopKeeperId(int serverShopKeeperId);

    @Query("UPDATE shops SET isDelivering = :isDelivering WHERE serverShopId = :serverShopId")
    void updateShopDelivering(int isDelivering, int serverShopId);

    @Query("SELECT * FROM default_categories as dc, shops_has_default_categories as shdc, shops as s" +
            " WHERE dc.dCategoryId = shdc.dCategoryId" +
            " AND shdc.serverShopId = s.serverShopId" +
            " AND s.serverShopId = :serverShopId")
    List<DefaultCategory> getShopDCategoriesByShopId(int serverShopId);
}
