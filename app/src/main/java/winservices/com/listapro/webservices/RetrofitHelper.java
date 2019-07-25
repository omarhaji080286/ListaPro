package winservices.com.listapro.webservices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {


    //TODO Version web
    private final static String webVersion = "L15_LP7";

    //TODO Lista LOCAL
    //private static final String HOST = "http://192.168.43.211/lista_local/lista_"+webVersion+"/webservices/";

    //TODO Lista LWS_PRE_PROD
    //private final static String HOST = "http://lista-courses.com/lista_pre_prod/lista_"+webVersion+"/webservices/";

    //TODO Lista LWS_PROD
    private final static String HOST = "http://lista-courses.com/lista_prod/lista_"+webVersion+"/webservices/";

    public ListaProWebServices initWebServices() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ListaProWebServices.class);
    }

}
