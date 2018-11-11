package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.Order;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM orders WHERE serverShopIdFk = :serverShopId")
    LiveData<List<Order>> getOrdersByServerShopId(int serverShopId);

    @Query("SELECT COUNT(*) FROM orders" +
            " WHERE serverShopIdFk = :serverShopId" +
            " AND statusId = 1")
    LiveData<Integer> getSentOrdersNum(int serverShopId);
}
