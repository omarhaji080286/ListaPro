package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "default_categories")
public class DefaultCategory {

    @PrimaryKey
    @SerializedName("d_category_id")
    private int dCategoryId;
    @SerializedName("d_category_name")
    private String dCategoryName;


    public DefaultCategory(int dCategoryId, String dCategoryName) {
        this.dCategoryId = dCategoryId;
        this.dCategoryName = dCategoryName;
    }

    public int getDCategoryId() {
        return dCategoryId;
    }

    public void setDCategoryId(int dCategoryId) {
        this.dCategoryId = dCategoryId;
    }

    public String getDCategoryName() {
        return dCategoryName;
    }

    public void setDCategoryName(String dCategoryName) {
        this.dCategoryName = dCategoryName;
    }
}
