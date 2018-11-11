package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Embedded;

public class City {

    @SerializedName("server_city_id")
    private int serverCityId;
    @SerializedName("city_name")
    private String cityName;
    @Embedded
    private Country country;

    public City(int serverCityId, String cityName, Country country) {
        this.serverCityId = serverCityId;
        this.cityName = cityName;
        this.country = country;
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
