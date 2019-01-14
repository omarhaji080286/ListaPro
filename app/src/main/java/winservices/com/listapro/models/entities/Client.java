package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients")
public class Client {

    public static final String PREFIX = "client_";
    @PrimaryKey
    @SerializedName("server_user_id")
    private int serverUserId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("email")
    private String userEmail;

    @SerializedName("user_image")
    private String userImage;

    public Client(int serverUserId, String userName, String userEmail, String userImage) {
        this.serverUserId = serverUserId;
        this.userName = userName;
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
