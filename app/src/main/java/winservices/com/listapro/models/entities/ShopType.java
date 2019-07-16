package winservices.com.listapro.models.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import winservices.com.listapro.R;

@Entity(tableName = "shop_types")
public class ShopType {

    private final static int GENERAL_SHOP = 1;
    private final static int MEAT_SHOP = 2;
    private final static int FISH_SHOP = 3;
    private final static int BAKERY = 4;
    private final static int PHARMACY = 5;
    private final static int HYGIENE_SHOP = 6;
    private final static int COSMETICS_SHOP = 7;
    private final static int SPICE_SHOP = 8;

    @PrimaryKey
    @SerializedName("server_shop_type_id")
    private int serverShopTypeId;
    @SerializedName("shop_type_name")
    private String shopTypeName;
    @SerializedName("shop_type_image_path")
    private String shopTypeImagePath;
    private int icon;

    @Ignore
    @SerializedName("shop_type_image")
    private String shopTypeImage;

    @Ignore
    private List<DefaultCategory> dCategories;

    //for shop list selection when creationg a shop
    @Ignore
    private boolean isChecked;

    public ShopType(int serverShopTypeId, String shopTypeName) {
        this.serverShopTypeId = serverShopTypeId;
        this.shopTypeName = shopTypeName;
        this.icon = getResourceId(serverShopTypeId);
    }

    public ShopType(int serverShopTypeId, String shopTypeName, boolean isChecked) {
        this.serverShopTypeId = serverShopTypeId;
        this.shopTypeName = shopTypeName;
        this.isChecked = isChecked;
    }

    private int getResourceId(int serverShopTypeId) {
        switch (serverShopTypeId) {
            case GENERAL_SHOP:
                return R.drawable.grocery;
            case MEAT_SHOP:
                return R.drawable.steak;
            case FISH_SHOP:
                return R.drawable.fish;
            case BAKERY:
                return R.drawable.bread;
            case PHARMACY:
                return R.drawable.others;
            case HYGIENE_SHOP:
                return R.drawable.gel;
            case COSMETICS_SHOP:
                return R.drawable.others;
            case SPICE_SHOP:
                return R.drawable.spices;
            default:
                return R.drawable.others;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getShopTypeImagePath() {
        return shopTypeImagePath;
    }

    public void setShopTypeImagePath(String shopTypeImagePath) {
        this.shopTypeImagePath = shopTypeImagePath;
    }

    public String getShopTypeImage() {
        return shopTypeImage;
    }

    public void setShopTypeImage(String shopTypeImage) {
        this.shopTypeImage = shopTypeImage;
    }

    public List<DefaultCategory> getdCategories() {
        return dCategories;
    }

    public void setdCategories(List<DefaultCategory> dCategories) {
        this.dCategories = dCategories;
    }

    public int getServerShopTypeId() {
        return serverShopTypeId;
    }

    public void setServerShopTypeId(int serverShopTypeId) {
        this.serverShopTypeId = serverShopTypeId;
    }

    public String getShopTypeName() {
        return shopTypeName;
    }

    public void setShopTypeName(String shopTypeName) {
        this.shopTypeName = shopTypeName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setDefaultIcon() {
        this.icon = getResourceId(this.serverShopTypeId);
    }
}