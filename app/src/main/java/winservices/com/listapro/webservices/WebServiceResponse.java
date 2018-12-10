package winservices.com.listapro.webservices;

import java.util.List;

import winservices.com.listapro.models.entities.City;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.models.entities.ShopType;

public class WebServiceResponse {

    private boolean error;
    private String message;
    private ShopKeeper shopKeeper;
    private List<ShopType> shopTypesWithCategories;
    private int serverShopId;
    private List<Order> orders;
    private List<City> cities;
    private Shop shop;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ShopKeeper getShopKeeper() {
        return shopKeeper;
    }

    public void setShopKeeper(ShopKeeper shopKeeper) {
        this.shopKeeper = shopKeeper;
    }

    public List<ShopType> getShopTypesWithCategories() {
        return shopTypesWithCategories;
    }

    public void setShopTypesWithCategories(List<ShopType> shopTypesWithCategories) {
        this.shopTypesWithCategories = shopTypesWithCategories;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
