package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "ordered_goods",
        foreignKeys = @ForeignKey(entity = Order.class, parentColumns = "serverOrderId", childColumns = "serverOrderIdFk",
                onDelete = CASCADE),
        indices = @Index(value = "serverOrderIdFk")
)
public class OrderedGood {

    public final static int UNPROCESSED = 0;
    public final static int PROCESSED = 1;
    public final static int NOT_AVAILABLE = 2;

    @PrimaryKey
    @SerializedName("server_ordered_good_id")
    private int serverOrderedGoodId;
    @SerializedName("server_good_id")
    private int serverGoodId;
    @SerializedName("server_category_id")
    private int serverCategoryId;
    @SerializedName("server_user_id")
    private int serverUserId;
    @SerializedName("server_shop_id")
    private int serverShopId;
    @SerializedName("good_desc")
    private String goodDesc;
    @SerializedName("good_name")
    private String goodName;
    @SerializedName("server_order_id")
    private String serverOrderIdFk;
    @SerializedName("status")
    private int status;


    public OrderedGood(int serverOrderedGoodId, int serverGoodId, int serverCategoryId, int serverUserId, int serverShopId, String goodDesc, String goodName, String serverOrderIdFk, int status) {
        this.serverOrderedGoodId = serverOrderedGoodId;
        this.serverGoodId = serverGoodId;
        this.serverCategoryId = serverCategoryId;
        this.serverUserId = serverUserId;
        this.serverShopId = serverShopId;
        this.goodDesc = goodDesc;
        this.goodName = goodName;
        this.serverOrderIdFk = serverOrderIdFk;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getServerOrderIdFk() {
        return serverOrderIdFk;
    }

    public void setServerOrderIdFk(String serverOrderIdFk) {
        this.serverOrderIdFk = serverOrderIdFk;
    }

    public int getServerOrderedGoodId() {
        return serverOrderedGoodId;
    }

    public void setServerOrderedGoodId(int serverOrderedGoodId) {
        this.serverOrderedGoodId = serverOrderedGoodId;
    }

    public int getServerGoodId() {
        return serverGoodId;
    }

    public void setServerGoodId(int serverGoodId) {
        this.serverGoodId = serverGoodId;
    }

    public int getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(int serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }

    public int getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(int serverUserId) {
        this.serverUserId = serverUserId;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public String getGoodDesc() {
        return goodDesc;
    }

    public void setGoodDesc(String goodDesc) {
        this.goodDesc = goodDesc;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }
}
