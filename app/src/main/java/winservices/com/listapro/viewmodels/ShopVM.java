package winservices.com.listapro.viewmodels;

import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import winservices.com.listapro.models.entities.Shop;
import winservices.com.listapro.repositories.ShopRepository;

public class ShopVM extends AndroidViewModel {

    private ShopRepository repository;

    public ShopVM(@NonNull Application application) {
        super(application);
        repository = new ShopRepository(application);
    }

    public LiveData<List<Shop>> getShopsByShopKeeperId(int skId){
        LiveData<List<Shop>> shops = repository.getShopsByShopKeeperId(skId);
        shops = Transformations.map(shops, new Function<List<Shop>, List<Shop>>() {

            @Override
            public List<Shop> apply(List<Shop> list) {
                for (Shop shop : list) {
                    try {
                        shop.setDCategories(repository.getShopDCategoriesByShopId(shop.getServerShopId()));
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            }
        });
        return shops;
    }

    public void insert(Shop shop){
        repository.insertShopOnServer(shop);
    }

    public void uploadShopImage(Context context, int serverShopId){
        repository.uploadShopImage(context, serverShopId);
    }

}
