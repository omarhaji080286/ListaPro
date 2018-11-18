package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cities")
public class City {

    @PrimaryKey
    @SerializedName("server_city_id")
    private int serverCityId;
    @SerializedName("city_name")
    private String cityName;
    @SerializedName("city_longitude")
    private double cityLongitude;
    @SerializedName("city_latitude")
    private double cityLatitude;
    @Embedded
    private Country country;

    public City(int serverCityId, String cityName, double cityLongitude, double cityLatitude, Country country) {
        this.serverCityId = serverCityId;
        this.cityName = cityName;
        this.cityLongitude = cityLongitude;
        this.cityLatitude = cityLatitude;
        this.country = country;
    }

    public double getCityLongitude() {
        return cityLongitude;
    }

    public void setCityLongitude(double cityLongitude) {
        this.cityLongitude = cityLongitude;
    }

    public double getCityLatitude() {
        return cityLatitude;
    }

    public void setCityLatitude(double cityLatitude) {
        this.cityLatitude = cityLatitude;
    }

    public int getServerCityId() {
        return serverCityId;
    }

    public void setServerCityId(int serverCityId) {
        this.serverCityId = serverCityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
