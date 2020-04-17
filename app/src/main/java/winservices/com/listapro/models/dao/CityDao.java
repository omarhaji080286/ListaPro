package winservices.com.listapro.models.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import winservices.com.listapro.models.entities.City;

@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(City city);

    @Update
    void update(City city);

    @Delete
    void delete(City city);

    @Query("SELECT * FROM cities")
    LiveData<List<City>> getAllCities();

    @Query("SELECT * FROM cities WHERE serverCityId = :serverCityId")
    LiveData<City> getCity(int serverCityId);

}
