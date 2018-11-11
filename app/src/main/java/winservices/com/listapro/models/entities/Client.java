package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients")
public class Client {

    @PrimaryKey
    @SerializedName("server_user_id")
    private int serverUserId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("email")
    private String userEmail;

    public Client(int serverUserId, String userName, String userEmail) {
        this.serverUserId = serverUserId;
        this.userName = userName;
        this.userEmail = userEmail;
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
}
