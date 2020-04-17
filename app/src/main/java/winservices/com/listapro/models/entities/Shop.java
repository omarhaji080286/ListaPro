package winservices.com.listapro.models.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
            tableName = "shops",
            foreignKeys = @ForeignKey(entity = ShopKeeper.class, parentColumns = "serverShopKeeperId", childColumns = "serverShopKeeperIdFk",
                            onDelete = CASCADE),
            indices = @Index(value = "serverShopKeeperIdFk")
        )

public class Shop {

    @Ignore
    public static final String DEFAULT_IMAGE = "defaultImage";
    @Ignore
    public static final String PREFIX_SHOP = "shop_";

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
    @SerializedName("opening_time")
    private String openingTime;
    @SerializedName("closing_time")
    private String closingTime;
    @SerializedName("is_delivering")
    private int isDelivering;

    //secondary key
    @SerializedName("server_shopkeeper_id")
    private int serverShopKeeperIdFk;

    @Embedded
    @SerializedName("shop_type")
    private ShopType shopType;

    @Embedded
    private City city;

    @Ignore
    @SerializedName("d_categories")
    private List<DefaultCategory> dCategories;

    @Ignore
    @SerializedName("shop_image")
    private String shopImage;

    public Shop(String shopName, String shopPhone, double longitude, double latitude, int serverShopKeeperIdFk) {

        this.shopName = shopName;
        this.shopPhone = shopPhone;
        this.longitude = longitude;
        this.latitude = latitude;
        this.serverShopKeeperIdFk = serverShopKeeperIdFk;
    }

    public Shop() {
    }

    public int getIsDelivering() {
        return isDelivering;
    }

    public void setIsDelivering(int isDelivering) {
        this.isDelivering = isDelivering;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
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

