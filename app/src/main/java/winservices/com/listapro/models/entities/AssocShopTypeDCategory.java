package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

@Entity(tableName = "shop_types_has_default_categories",
        primaryKeys = {"serverShopTypeId","dCategoryId"})
public class AssocShopTypeDCategory {

    @SerializedName("server_shop_type_id")
    private int serverShopTypeId;
    @SerializedName("d_category_id")
    private int dCategoryId;

    public AssocShopTypeDCategory(int serverShopTypeId, int dCategoryId) {
        this.serverShopTypeId = serverShopTypeId;
        this.dCategoryId = dCategoryId;
    }

    public int getServerShopTypeId() {
        return serverShopTypeId;
    }

    public void setServerShopTypeId(int serverShopTypeId) {
        this.serverShopTypeId = serverShopTypeId;
    }

    public int getDCategoryId() {
        return dCategoryId;
    }

    public void setDCategoryId(int dCategoryId) {
        this.dCategoryId = dCategoryId;
    }
}
