package winservices.com.listapro.models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import winservices.com.listapro.models.entities.ShopKeeper;

@Dao
public interface ShopKeeperDao {

    @Insert
    void insert(ShopKeeper shopKeeper);

    @Update
    void update(ShopKeeper shopKeeper);

    @Delete
    void delete(ShopKeeper shopKeeper);

    @Query("SELECT * FROM shopkeepers")
    LiveData<List<ShopKeeper>> getAllShopKeepers();

    @Query("SELECT * FROM shopkeepers WHERE lastLogged = " + ShopKeeper.LAST_LOGGED)
    LiveData<ShopKeeper> getLastLogged();

    @Query("UPDATE shopkeepers SET lastLogged = :lastLogged" )
    void changeLastLogged(int lastLogged);

    @Query("SELECT * FROM shopkeepers WHERE phone = :phone")
    LiveData<ShopKeeper> getShopKeeperByPhone(String phone);


}
