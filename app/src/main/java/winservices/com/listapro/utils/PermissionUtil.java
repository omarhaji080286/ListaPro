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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import winservices.com.listapro.R;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class PermissionUtil {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private final static int REQUEST_ACCESS_COARSE_LOCATION = 102;

    public final static String TXT_FINE_LOCATION = "access_fine_location";
    public final static String TXT_COARSE_LOCATION = "access_coarse_location";

    private final static String TAG = PermissionUtil.class.getSimpleName();

    public PermissionUtil(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void updatePermissionPreference(String permission){

        switch (permission){
            case TXT_FINE_LOCATION :
                editor.putBoolean(context.getString(R.string.permission_access_fine_location), true);
                editor.commit();
                break;
            case TXT_COARSE_LOCATION :
                editor.putBoolean(context.getString(R.string.permission_access_coarse_location), true);
                editor.commit();
                break;
        }

    }

    public boolean checkPermissionPreference(String permission){

        boolean isShown = false;
        switch (permission){
            case TXT_FINE_LOCATION :
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_fine_location), false);
                break;
            case TXT_COARSE_LOCATION :
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_coarse_location), false);
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
                requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                break;
        }
    }

    public void goToAppSettings() {
        Toast.makeText(context, R.string.allow_location_permission_in_settings, Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }


}
