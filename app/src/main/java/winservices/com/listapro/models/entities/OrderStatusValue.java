package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_status_values")
public class OrderStatusValue {

    @PrimaryKey
    @SerializedName("status_id")
    private int statusId;

    @SerializedName("status_name")
    private String statusName;

    public OrderStatusValue() {
    }

    public OrderStatusValue(int statusId, String statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}


