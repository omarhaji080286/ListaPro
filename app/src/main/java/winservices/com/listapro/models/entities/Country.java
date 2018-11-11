package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

public class Country {
    @SerializedName("server_country_id")
    private int serverCountryId;
    @SerializedName("country_name")
    private String countryName;

    public Country(int serverCountryId, String countryName) {
        this.serverCountryId = serverCountryId;
        this.countryName = countryName;
    }

    public int getServerCountryId() {
        return serverCountryId;
    }

    public void setServerCountryId(int serverCountryId) {
        this.serverCountryId = serverCountryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
