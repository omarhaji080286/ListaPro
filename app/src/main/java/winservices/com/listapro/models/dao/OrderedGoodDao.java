package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.OrderedGood;

@Dao
public interface OrderedGoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderedGood orderedGood);

    @Update
    void update(OrderedGood orderedGood);

    @Delete
    void delete(OrderedGood orderedGood);

    @Query("SELECT * FROM ordered_goods WHERE serverOrderIdFk = :serverOrderId")
    LiveData<List<OrderedGood>> getOrderedGoodsByOrderId(int serverOrderId);
}
