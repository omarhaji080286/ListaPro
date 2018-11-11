package winservices.com.listapro.webservices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private final static String HOST = "http://192.168.43.211/lista_local/webservices/";

    public ListaProWebServices initWebServices() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ListaProWebServices.class);
    }

}
