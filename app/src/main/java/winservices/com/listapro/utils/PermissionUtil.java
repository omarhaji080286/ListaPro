package winservices.com.listapro.utils;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import winservices.com.listapro.R;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class PermissionUtil {

    public final static String TXT_FINE_LOCATION = "access_fine_location";
    public final static String TXT_COARSE_LOCATION = "access_coarse_location";
    public final static String TXT_CAMERA = "access_camera";
    private final static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private final static int REQUEST_ACCESS_COARSE_LOCATION = 102;
    private final static int REQUEST_ACCESS_CAMERA = 103;
    private final static String TAG = PermissionUtil.class.getSimpleName();
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PermissionUtil(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void requestPermissionInFragment(Activity activity) {

        PermissionUtil permissionUtil = new PermissionUtil(Objects.requireNonNull(activity));

        if (permissionUtil.checkPermission(TXT_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionUtil.requestPermission(TXT_FINE_LOCATION, activity);
            Log.d(TAG, "permission requested");
        }

    }

    public static void checkLocationPermission(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {//Can add more as per requirement
            Log.d(TAG, "permission not granted yet");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);
            } else {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);

            }
        } else {
            Log.d(TAG, "permission granted");
        }
    }

    public void updatePermissionPreference(String permission) {

        switch (permission) {
            case TXT_FINE_LOCATION:
                editor.putBoolean(context.getString(R.string.permission_access_fine_location), true);
                editor.commit();
                break;
            case TXT_COARSE_LOCATION:
                editor.putBoolean(context.getString(R.string.permission_access_coarse_location), true);
                editor.commit();
                break;
            case TXT_CAMERA:
                editor.putBoolean(context.getString(R.string.permission_access_camera), true);
                editor.commit();
                break;

        }

    }

    public boolean checkPermissionPreference(String permission) {

        boolean isShown = false;
        switch (permission) {
            case TXT_FINE_LOCATION:
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_fine_location), false);
                break;
            case TXT_COARSE_LOCATION:
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_coarse_location), false);
                break;
            case TXT_CAMERA:
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_camera), false);
                break;
        }
        return isShown;
    }

    public int checkPermission(String permission) {
        int status = PackageManager.PERMISSION_DENIED;
        switch (permission) {
            case TXT_FINE_LOCATION:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                break;
            case TXT_CAMERA:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                break;
        }
        return status;
    }

    public void showPermissionExplanation(final String permission, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (permission.equals(TXT_FINE_LOCATION)) {
            builder.setMessage("This app needs to access your location. Please allow.");
            builder.setTitle("Location permission needed..");
        } else if (permission.equals(TXT_COARSE_LOCATION)) {
            builder.setMessage("This app needs to access your location. Please allow.");
            builder.setTitle("Location permission needed..");
        } else if (permission.equals(TXT_CAMERA)) {
            builder.setMessage("This app needs to access your camera. Please allow.");
            builder.setTitle("Camera permission needed..");
        }

        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission(permission, activity);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void requestPermission(String permission, Activity activity) {
        switch (permission) {
            case TXT_FINE_LOCATION:
                requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                break;
            case TXT_CAMERA:
                requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);
                break;
        }
    }

    public void goToAppSettings() {
        Toast.makeText(context, "Please allow Camera/Location permissions in your app settings", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
