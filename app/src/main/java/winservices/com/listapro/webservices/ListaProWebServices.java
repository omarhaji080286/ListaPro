package winservices.com.listapro.webservices;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ListaProWebServices {

    //@GET("getShopKeeper.php")
    //Call<List<ShopKeeper>> getShopKeeper();

    @GET("getShopTypesWithCategories.php")
    Call<WebServiceResponse> getShopTypes();

    @FormUrlEncoded
    @POST("addShopKeeper.php")
    Call<WebServiceResponse>addShopKeeper(@FieldMap Map<String, String> body);

    @FormUrlEncoded
    @POST("addShop.php")
    Call<WebServiceResponse>addShop(@FieldMap Map<String, String> body);

    @FormUrlEncoded
    @POST("getShopOrders.php")
    Call<WebServiceResponse> getShopOrders(@FieldMap Map<String, String> body);
}
