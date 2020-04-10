package winservices.com.listapro.models.entities;

import android.content.Context;
import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import winservices.com.listapro.R;
import winservices.com.listapro.utils.UtilsFunctions;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "orders",
        foreignKeys = @ForeignKey(entity = Shop.class, parentColumns = "serverShopId", childColumns = "serverShopIdFk",
                onDelete = CASCADE),
        indices = @Index(value = "serverShopIdFk")
)
public class Order implements Comparable<Order>{

    public final static String TAG = Order.class.getSimpleName();

    public final static String SERVER_ORDER_ID = "serverOrderId";
    public final static int REGISTERED = 1;
    public final static int READ = 2;
    public final static int IN_PREPARATION = 3;
    public final static int AVAILABLE = 4;
    public final static int COMPLETED = 5;
    public final static int NOT_SUPPORTED = 6;

    public final static int IS_TO_DELIVER = 1;
    public final static int IS_TO_COLLECT = 0;

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
    @SerializedName("is_to_deliver")
    private int isToDeliver;
    @SerializedName("user_address")
    private String userAddress;
    @SerializedName("user_location")
    private String userLocation;

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

    public int getIsToDeliver() {
        return isToDeliver;
    }

    public void setIsToDeliver(int isToDeliver) {
        this.isToDeliver = isToDeliver;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
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

    public String getDisplayedCollectTime(Context context, String dateTime){
        String day = "empty";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        Date collectDay;
        Date today;
        try {
            collectDay = sdf.parse(dateTime);
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

        return day;// + " " + time*
    }

    private Date getDateStartTime(){
        return UtilsFunctions.stringToDate(this.startTime);
    }

    @Override
    public int compareTo(Order o) {
        return  getDateStartTime().compareTo(o.getDateStartTime());
    }


    public String toPlainText(){
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

            String sb = "- Réf commande : " + this.serverOrderId +
                    "\n" +
                    "- préparer pour le : " +
                    UtilsFunctions.dateToString(sdf.parse(this.getEndTime()), "dd/MM/yyyy") +
                    "\n" +
                    "- Client : " + this.client.getUserName() +
                    "\n" +
                    "- Téléphone : " + this.client.getUserPhone() +
                    "\n" +
                    "- Adresse : " + this.getUserAddress() +
                    "\n" +
                    "- GPS : " + this.getUserLocation();
            return sb;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

}
