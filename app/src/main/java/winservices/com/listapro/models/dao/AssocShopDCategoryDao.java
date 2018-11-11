package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.AssocShopDCategory;

@Dao
public interface AssocShopDCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssocShopDCategory assoc);

    @Update
    void update(AssocShopDCategory assoc);

    @Delete
    void delete(AssocShopDCategory assoc);

    @Query("SELECT * FROM shops_has_default_categories")
    LiveData<List<AssocShopDCategory>> getAllAssocShopDCategory();
}
