package winservices.com.listapro.viewmodels;

import android.app.Application;
import android.content.Context;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.repositories.ShopKeeperRepository;

public class ShopKeeperVM extends AndroidViewModel {

    private ShopKeeperRepository repository;
    private LiveData<List<ShopKeeper>> allShopKeepers;

    public ShopKeeperVM(@NonNull Application application) {
        super(application);
        repository = new ShopKeeperRepository(application);
        allShopKeepers = repository.getAllShopKeepers();
    }

    public LiveData<ShopKeeper> getLastLoggedShopKeeper() {
        return repository.getLastLogged();
    }

    public void insert(ShopKeeper shopKeeper){
        repository.insert(shopKeeper);
    }

    public void logIn(ShopKeeper shopKeeper){
        repository.logIn(shopKeeper);
    }

    public void update(ShopKeeper shopKeeper){
        repository.update(shopKeeper);
    }

    public void delete(ShopKeeper shopKeeper){
        repository.delete(shopKeeper);
    }

    public LiveData<List<ShopKeeper>> getAllShopKeepers() {
        return allShopKeepers;
    }


    public LiveData<ShopKeeper> getShopKeeperByPhone(String phone) {
        return repository.getShopKeeperByPhone(phone);
    }

    public void signUp(ShopKeeper shopKeeper, Context context){
        repository.signUpShopKeeper(shopKeeper, context);
    }
}
