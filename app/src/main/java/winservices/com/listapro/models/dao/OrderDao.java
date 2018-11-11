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
import winservices.com.listapro.models.entities.ShopKeeper;

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

    @Query("SELECT COUNT(*)" +
            " FROM orders as o, shops as s, shopkeepers as sk" +
            " WHERE o.serverShopIdFk = s.serverShopId " +
            " AND s.serverShopKeeperIdFk = sk.serverShopKeeperId" +
            " AND sk.lastLogged = " + ShopKeeper.LAST_LOGGED +
            " AND sk.isLoggedIn = " + ShopKeeper.LOGGED_IN +
            " AND s.serverShopId = :serverShopId" +
            " AND o.statusId IN (1, 2, 6)")
    LiveData<Integer> getSentOrdersNum(int serverShopId);
}
