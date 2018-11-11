package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

@Entity(tableName = "shops_has_default_categories",
        primaryKeys = {"serverShopId","dCategoryId"})
public class AssocShopDCategory {

    @SerializedName("server_shop_id")
    private int serverShopId;
    @SerializedName("d_category_id")
    private int dCategoryId;

    public AssocShopDCategory(int serverShopId, int dCategoryId) {
        this.serverShopId = serverShopId;
        this.dCategoryId = dCategoryId;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public int getDCategoryId() {
        return dCategoryId;
    }

    public void setDCategoryId(int dCategoryId) {
        this.dCategoryId = dCategoryId;
    }
}
