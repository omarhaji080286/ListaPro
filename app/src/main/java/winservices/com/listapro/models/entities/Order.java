package winservices.com.listapro.models.entities;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import winservices.com.listapro.R;
import winservices.com.listapro.utils.UtilsFunctions;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "orders",
        foreignKeys = @ForeignKey(entity = Shop.class, parentColumns = "serverShopId", childColumns = "serverShopIdFk",
                onDelete = CASCADE),
        indices = @Index(value = "serverShopIdFk")
)
public class Order {

    public final static String TAG = Order.class.getSimpleName();

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
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public String getDisplayedCollectTime(Context context){
        String day = "empty";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        Date collectDay = null;
        Date today = null;
        try {
            collectDay = sdf.parse(this.startTime);
            today = sdf.parse(UtilsFunctions.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd"));

            long diffMilli = collectDay.getTime() - today.getTime();
            String diff = String.valueOf(TimeUnit.DAYS.convert(diffMilli, TimeUnit.MILLISECONDS)).substring(0,1);

            switch (diff){
                case "0":
                    day = context.getResources().getString(R.string.today);
                    break;
                case "1":
                    day = context.getResources().getString(R.string.tomorrow);
                    break;
                default:
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(collectDay);
                    day = UtilsFunctions.getDayOfWeek(context,cal.get(Calendar.DAY_OF_WEEK)) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH));
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = this.startTime.substring(11,16) + " - " + this.endTime.substring(11,16);

        Log.d(TAG, "DisplayedCollectTime: " + day + " " + time);

        return day + " " + time;
    }

}
