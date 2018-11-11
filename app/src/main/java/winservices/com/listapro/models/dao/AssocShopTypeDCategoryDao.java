package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.AssocShopTypeDCategory;

@Dao
public interface AssocShopTypeDCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssocShopTypeDCategory assoc);

    @Update
    void update(AssocShopTypeDCategory assoc);

    @Delete
    void delete(AssocShopTypeDCategory assoc);

    @Query("SELECT * FROM shop_types_has_default_categories")
    LiveData<List<AssocShopTypeDCategory>> getAllAssocShopTypeDCategory();

}
