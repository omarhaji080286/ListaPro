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
        tableName = "orders",
        foreignKeys = @ForeignKey(entity = Shop.class, parentColumns = "serverShopId", childColumns = "serverShopIdFk",
                onDelete = CASCADE),
        indices = @Index(value = "serverShopIdFk")
)
public class Order {

    public final static String SERVER_ORDER_ID = "serverOrderId";
    public final static int REGISTERED = 1;
    public final static int READ = 2;
    public final static int IN_PREPARATION = 3;
    public final static int AVAILABLE = 4;
    public final static int COMPLETED = 5;
    public final static int NOT_SUPPORTED = 6;

    @PrimaryKey
    @SerializedName("server_order_id")
    private int serverOrderId;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("server_shop_id")
    private int serverShopIdFk;

    @Embedded
    @SerializedName("client")
    private Client client;

    @Embedded
    @SerializedName("status")
    private OrderStatusValue status;

    @Ignore
    private List<OrderedGood> orderedGoods;

    public Order(int serverOrderId, String creationDate, int serverShopIdFk, Client client, OrderStatusValue status) {
        this.serverOrderId = serverOrderId;
        this.creationDate = creationDate;
        this.serverShopIdFk = serverShopIdFk;
        this.client = client;
        this.status = status;
    }

    public List<OrderedGood> getOrderedGoods() {
        return orderedGoods;
    }

    public void setOrderedGoods(List<OrderedGood> orderedGoods) {
        this.orderedGoods = orderedGoods;
    }


    public int getServerShopIdFk() {
        return serverShopIdFk;
    }

    public void setServerShopIdFk(int serverShopIdFk) {
        this.serverShopIdFk = serverShopIdFk;
    }

    public int getServerOrderId() {
        return serverOrderId;
    }

    public void setServerOrderId(int serverOrderId) {
        this.serverOrderId = serverOrderId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public OrderStatusValue getStatus() {
        return status;
    }

    public void setStatus(OrderStatusValue status) {
        this.status = status;
    }

}
