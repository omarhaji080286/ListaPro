package winservices.com.listapro.webservices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    //Lista ALPHA (compte karimamrani0909@gmail.com)
    //private static final String HOST = "http://lista-alpha.onlinewebshop.net/webservices/";

    //Lista LOCAL
    private final static String HOST = "http://192.168.43.211/lista_local/webservices/";

    //Lista LWS_PRE_PROD
    //private final static String HOST = "http://lista-courses.com/lista_pre_prod/webservices/";

    //Lista LWS_PROD
    //private final static String HOST = "http://lista-courses.com/lista_prod/webservices/";

    public ListaProWebServices initWebServices() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ListaProWebServices.class);
    }

}
