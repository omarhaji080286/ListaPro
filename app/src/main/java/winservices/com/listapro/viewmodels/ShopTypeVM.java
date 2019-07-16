package winservices.com.listapro.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.ExecutionException;

import winservices.com.listapro.models.entities.AssocShopTypeDCategory;
import winservices.com.listapro.models.entities.City;
import winservices.com.listapro.models.entities.DefaultCategory;
import winservices.com.listapro.models.entities.ShopType;
import winservices.com.listapro.repositories.ShopTypeRepository;

public class ShopTypeVM extends AndroidViewModel {
    private final String TAG = ShopTypeVM.class.getSimpleName();

    private ShopTypeRepository repository;

    public ShopTypeVM(@NonNull Application application) {
        super(application);
        repository = new ShopTypeRepository(application);
    }

    public void insert(ShopType shopType) {
        repository.insert(shopType);
        for (DefaultCategory dCategory : shopType.getdCategories()) {
            repository.insert(dCategory);
            AssocShopTypeDCategory assoc = new AssocShopTypeDCategory(shopType.getServerShopTypeId(), dCategory.getDCategoryId());
            repository.insert(assoc);
        }
    }

    public LiveData<ShopType> getShopType(int serverShopTypeId){
        return repository.getShopType(serverShopTypeId);
    }

    public LiveData<List<ShopType>> getAllShopTypes() {
        LiveData<List<ShopType>> shopTypes = repository.getAllShopTypes();
        shopTypes = Transformations.map(shopTypes, new Function<List<ShopType>, List<ShopType>>() {

            @Override
            public List<ShopType> apply(List<ShopType> inputShopTypes) {

                for (ShopType shopType : inputShopTypes) {
                    try {
                        shopType.setdCategories(repository.getShopTypeDCategories(shopType.getServerShopTypeId()));
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return inputShopTypes;
            }
        });

        return shopTypes;
    }

    public void loadShopTypes(Context context){
        repository.loadShopTypesFromServer(context);
    }

    public void loadCitiesFromServer(){
        repository.loadCitiesFromServer();
    }

    public LiveData<List<City>> getAllCities(){
        return repository.getAllCities();
    }

    public LiveData<List<DefaultCategory>> getCategories(int serverShopTypeId) {
        return repository.getCategories(serverShopTypeId);

    }

}