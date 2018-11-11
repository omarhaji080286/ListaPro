package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopkeepers")
public class ShopKeeper {

    public static final int LOGGED_IN = 1;
    public static final int LOGGED_OUT = 0;

    public static final int LAST_LOGGED = 1;
    public static final int NOT_LAST_LOGGED = 0;

    @PrimaryKey
    @SerializedName("server_sk_id")
    private int serverShopKeeperId;
    @SerializedName("sk_email")
    private String email;
    @SerializedName("sk_password")
    private String password;
    @SerializedName("sk_first_name")
    private String firstName;
    @SerializedName("sk_last_name")
    private String lastName;
    @SerializedName("sk_phone")
    private String phone;
    @SerializedName("sk_uuid")
    private String uuid;
    @SerializedName("fcm_token")
    private String fcmToken;

    private int isLoggedIn;
    private int lastLogged;

    @Ignore
    private List<Shop> shops;

    public ShopKeeper(String phone, String uuid, String fcmToken) {
        this.phone = phone;
        this.uuid = uuid;
        this.fcmToken = fcmToken;
    }

    @Ignore
    public ShopKeeper(int serverShopKeeperId, String email, String password, String firstName, String lastName, String phone, String uuid, String fcmToken, int isLoggedIn, int lastLogged) {
        this.serverShopKeeperId = serverShopKeeperId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.uuid = uuid;
        this.fcmToken = fcmToken;
        this.isLoggedIn = isLoggedIn;
        this.lastLogged = lastLogged;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public List<Shop> getShops() {
        return shops;
    }
    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public int getServerShopKeeperId() {
        return serverShopKeeperId;
    }

    public void setServerShopKeeperId(int serverShopKeeperId) {
        this.serverShopKeeperId = serverShopKeeperId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(int isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getLastLogged() {
        return lastLogged;
    }

    public void setLastLogged(int lastLogged) {
        this.lastLogged = lastLogged;
    }
}
