package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
            tableName = "shops",
            foreignKeys = @ForeignKey(entity = ShopKeeper.class, parentColumns = "serverShopKeeperId", childColumns = "serverShopKeeperIdFk",
                            onDelete = CASCADE),
            indices = @Index(value = "serverShopKeeperIdFk")
        )

public class Shop {

    @PrimaryKey
    @SerializedName("server_shop_id")
    private int serverShopId;
    @SerializedName("shop_name")
    private String shopName;
    @SerializedName("shop_adress")
    private String shopAdress;
    @SerializedName("shop_email")
    private String shopEmail;
    @SerializedName("shop_phone")
    private String shopPhone;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;

    //secondary key
    @SerializedName("server_shopkeeper_id")
    private int serverShopKeeperIdFk;

    @Embedded
    private ShopType shopType;

    @Embedded
    private City city;

    @Ignore
    private List<DefaultCategory> dCategories;

    public Shop(int serverShopId, String shopName, String shopPhone, double longitude, double latitude, int serverShopKeeperIdFk) {

        this.serverShopId = serverShopId;
        this.shopName = shopName;
        this.shopPhone = shopPhone;
        this.longitude = longitude;
        this.latitude = latitude;
        this.serverShopKeeperIdFk = serverShopKeeperIdFk;
    }

    public List<DefaultCategory> getdCategories() {
        return dCategories;
    }

    public void setDCategories(List<DefaultCategory> dCategories) {
        this.dCategories = dCategories;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAdress() {
        return shopAdress;
    }

    public void setShopAdress(String shopAdress) {
        this.shopAdress = shopAdress;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getServerShopKeeperIdFk() {
        return serverShopKeeperIdFk;
    }

    public void setServerShopKeeperIdFk(int serverShopKeeperIdFk) {
        this.serverShopKeeperIdFk = serverShopKeeperIdFk;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}

