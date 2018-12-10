package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.ShopType;

@Dao
public interface ShopTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShopType shopType);

    @Update
    void update(ShopType shopType);

    @Delete
    void delete(ShopType shopType);

    @Query("SELECT * FROM shop_types")
    LiveData<List<ShopType>> getAllShopTypes();

    @Query("SELECT * FROM shop_types WHERE serverShopTypeId = :serverShopTypeId")
    LiveData<ShopType> getShopType(int serverShopTypeId);
}
