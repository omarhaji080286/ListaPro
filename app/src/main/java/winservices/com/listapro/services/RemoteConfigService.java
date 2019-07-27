package winservices.com.listapro.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Objects;

import winservices.com.listapro.utils.SharedPrefManager;

public class RemoteConfigService {

    private final static String TAG = RemoteConfigService.class.getSimpleName();

    private Context context;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    public RemoteConfigService(Context context) {
        this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        this.context = context;
    }

    public void loadGooglePlayVersion() {

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                //TODO For test only
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    SharedPrefManager spm = SharedPrefManager.getInstance(context);
                    String version = firebaseRemoteConfig.getString(SharedPrefManager.GOOGLE_PLAY_VERSION_CODE);
                    Log.d(TAG, "google_play_version_code = " + version);
                    int googlePlayVersionCode = Integer.parseInt(version);
                    spm.storeGooglePlayVersion(googlePlayVersionCode);
                } else {
                    Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });

    }


}
