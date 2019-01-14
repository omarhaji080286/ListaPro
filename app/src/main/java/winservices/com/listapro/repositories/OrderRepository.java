package winservices.com.listapro.repositories;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import winservices.com.listapro.models.dao.OrderDao;
import winservices.com.listapro.models.dao.OrderedGoodDao;
import winservices.com.listapro.models.database.ListaProDataBase;
import winservices.com.listapro.models.entities.Client;
import winservices.com.listapro.models.entities.Order;
import winservices.com.listapro.models.entities.OrderedGood;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.webservices.ListaProWebServices;
import winservices.com.listapro.webservices.RetrofitHelper;
import winservices.com.listapro.webservices.WebServiceResponse;

public class OrderRepository {

    private final static String TAG = OrderRepository.class.getSimpleName();

    private OrderDao orderDao;
    private OrderedGoodDao orderedGoodDao;

    public OrderRepository(Application application) {
        ListaProDataBase db = ListaProDataBase.getInstance(application);
        this.orderDao = db.orderDao();
        this.orderedGoodDao = db.orderedGoodDao();
    }

    public LiveData<List<Order>> getOrdersByServerShopId(int serverShopId) {
        return orderDao.getOrdersByServerShopId(serverShopId);
    }

    public LiveData<List<OrderedGood>> getOrderedGoodsByOrderId(int serverOrderId) {
        return orderedGoodDao.getOrderedGoodsByOrderId(serverOrderId);
    }

    public void insert(Order order) {
        new InsertOrderAsyncTask(orderDao).execute(order);
    }

    public void insert(OrderedGood orderedGood) {
        new InsertOrderedGoodAsyncTask(orderedGoodDao).execute(orderedGood);
    }

    public void update(Order order) {
        new UpdateOrderAsyncTask(orderDao).execute(order);
    }

    public void update(OrderedGood orderedGood) {
        new UpdateOrderedGoodAsyncTask(orderedGoodDao).execute(orderedGood);
    }

    public void loadOrdersFromServer(final Context context, int serverShopId) {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("serverShopId", String.valueOf(serverShopId));
        Call<WebServiceResponse> call = ws.getShopOrders(hashMap);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            List<Order> orders = wsResponse.getOrders();
                            for (Order order : orders) {
                                insert(order);
                                String userImage = order.getClient().getUserImage();
                                if (!userImage.equals("defaultImage")) {
                                    SharedPrefManager.getInstance(context).storeImageToFile(userImage, "jpg", Client.PREFIX, order.getClient().getServerUserId());
                                }
                                for (OrderedGood orderedGood : order.getOrderedGoods()) {
                                    insert(orderedGood);
                                }
                            }
                            Log.d(TAG, "onResponse: " + orders.size() + " orders inserted or updated");
                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure : " + t.getMessage());
            }
        });
    }

    public void updateOrderOnServer(final Order order) {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Gson gson = new Gson();
        Map<String, String> hashMap = new HashMap<>();
        String jsonRequest = gson.toJson(order);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        hashMap.put("jsonRequest", jsonRequest);
        Call<WebServiceResponse> call = ws.updateOrder(hashMap);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    Log.d(TAG, "response body: " + response.body());
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            update(order);
                        } else {
                            Log.d(TAG, "Error on server : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure : " + t.getMessage());
            }
        });
    }

    public LiveData<Integer> getSentOrdersNum(int serverShopId) {
        return orderDao.getSentOrdersNum(serverShopId);
    }

    public LiveData<Order> getOrderByServerOrderId(int serverOrderId) {
        return orderDao.getOrderByServerOrderId(serverOrderId);
    }

    public void updateOrderedGoodsOnServer(List<OrderedGood> oGoods) {
        RetrofitHelper rh = new RetrofitHelper();
        ListaProWebServices ws = rh.initWebServices();

        Gson gson = new Gson();
        Map<String, String> hashMap = new HashMap<>();
        String jsonRequest = gson.toJson(oGoods);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        hashMap.put("jsonRequest", jsonRequest);
        Call<WebServiceResponse> call = ws.updateOrderedGoods(hashMap);

        call.enqueue(new Callback<WebServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<WebServiceResponse> call, @NonNull Response<WebServiceResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error : " + response.code());
                } else {
                    WebServiceResponse wsResponse = response.body();
                    Log.d(TAG, "response body: " + response.body());
                    if (wsResponse != null) {
                        if (!wsResponse.isError()) {
                            Log.d(TAG, "WS Success : " + wsResponse.getMessage());
                        } else {
                            Log.d(TAG, "WS Error : " + wsResponse.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebServiceResponse> call, @NonNull Throwable t) {
                Log.d(TAG, " WS Failure : " + t.getMessage());
            }
        });
    }

    private static class InsertOrderedGoodAsyncTask extends AsyncTask<OrderedGood, Void, Void> {
        private OrderedGoodDao orderedGoodDao;

        private InsertOrderedGoodAsyncTask(OrderedGoodDao orderedGoodDao) {
            this.orderedGoodDao = orderedGoodDao;
        }

        @Override
        protected Void doInBackground(OrderedGood... orderedGoods) {
            orderedGoodDao.insert(orderedGoods[0]);
            return null;
        }
    }

    private static class InsertOrderAsyncTask extends AsyncTask<Order, Void, Void> {
        private OrderDao orderDao;

        private InsertOrderAsyncTask(OrderDao orderDao) {
            this.orderDao = orderDao;
        }

        @Override
        protected Void doInBackground(Order... orders) {
            orderDao.insert(orders[0]);
            return null;
        }
    }

    private static class UpdateOrderAsyncTask extends AsyncTask<Order, Void, Void> {
        private OrderDao orderDao;

        private UpdateOrderAsyncTask(OrderDao orderDao) {
            this.orderDao = orderDao;
        }

        @Override
        protected Void doInBackground(Order... orders) {
            orderDao.update(orders[0]);
            return null;
        }
    }

    private static class UpdateOrderedGoodAsyncTask extends AsyncTask<OrderedGood, Void, Void> {
        private OrderedGoodDao orderedGoodDao;

        private UpdateOrderedGoodAsyncTask(OrderedGoodDao orderedGoodDao) {
            this.orderedGoodDao = orderedGoodDao;
        }

        @Override
        protected Void doInBackground(OrderedGood... oGoods) {
            orderedGoodDao.update(oGoods[0]);
            return null;
        }
    }

}
