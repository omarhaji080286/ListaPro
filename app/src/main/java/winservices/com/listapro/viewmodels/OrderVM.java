package winservices.com.listapro.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.repositories.OrderRepository;

public class OrderVM extends AndroidViewModel {

    private OrderRepository repository;

    public OrderVM(@NonNull Application application) {
        super(application);
        this.repository = new OrderRepository(application);
    }

    public void insert(Order order) {
        repository.insert(order);
    }

    public LiveData<List<Order>> getOrdersByServerShopId(int serverShopId, int orders_type) {
        return repository.getOrdersByServerShopId(serverShopId, orders_type);
    }

    public void loadOrders(Context context, int serverShopId) {
        repository.loadOrdersFromServer(context, serverShopId);
    }

    public LiveData<List<OrderedGood>> getOrderedGoodsByOrderId(int serverOrderId) {
        return repository.getOrderedGoodsByOrderId(serverOrderId);
    }

    public void update(OrderedGood oGood) {
        repository.update(oGood);
    }

    public LiveData<Integer> getSentOrdersNum(int serverShopId){
        return repository.getSentOrdersNum(serverShopId);
    }

    public void updateOrderOnServer(Order order){
        repository.updateOrderOnServer(order);
    }

    public void updateOrderedGoodsOnServer(List<OrderedGood> oGoods){
        repository.updateOrderedGoodsOnServer(oGoods);
    }

    public LiveData<Order> getOrderByServerOrderId(int serverOrderId) {
        return repository.getOrderByServerOrderId(serverOrderId);
    }

/*    public void updateOrderPriceTemp(Order order){
        repository.updateOrderTempPrice(order);
    }*/


}
