package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.DefaultCategory;

@Dao
public interface DefaultCategoryDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(DefaultCategory dCategory);

    @Update
    void update(DefaultCategory dCategory);

    @Delete
    void delete(DefaultCategory dCategory);

    @Query("SELECT * FROM default_categories")
    LiveData<List<DefaultCategory>> getAllDCategories();

    @Query("SELECT dc.* FROM default_categories as dc, shop_types_has_default_categories as assoc, shop_types as st" +
            " WHERE dc.dCategoryId = assoc.dCategoryId" +
            " AND assoc.serverShopTypeId = st.serverShopTypeId " +
            " AND st.serverShopTypeId = :serverShopTypeId ")
    List<DefaultCategory> getShopTypeDCategories(int serverShopTypeId);

}
