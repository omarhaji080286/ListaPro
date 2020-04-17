package winservices.com.listapro.models.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "clients")
public class Client {

    public static final String PREFIX = "client_";
    @PrimaryKey
    @SerializedName("server_user_id")
    private int serverUserId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_phone")
    private String userPhone;

    @SerializedName("user_image")
    private String userImage;

    public Client(int serverUserId, String userName, String userPhone, String userImage) {
        this.serverUserId = serverUserId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userImage = userImage;
    }

    public int getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(int serverUserId) {
        this.serverUserId = serverUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
