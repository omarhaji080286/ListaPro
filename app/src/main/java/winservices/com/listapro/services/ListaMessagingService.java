package winservices.com.listapro.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import winservices.com.listapro.R;
import winservices.com.listapro.repositories.OrderRepository;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.views.activities.LauncherActivity;

public class ListaMessagingService extends FirebaseMessagingService {

    private final static String TAG = ListaMessagingService.class.getSimpleName();
    public final static String FCM_TYPE = "fcm_type";
    private final static String FCM_DATA_NEW_ORDER = "New order";
    public final static String FCM_DATA_UPDATE = "update";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: " + token);
        storeToken(token);
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = "none";
        String body = "none";
        Intent intent = new Intent(this, LauncherActivity.class);

        String token = SharedPrefManager.getInstance(getApplicationContext()).getToken();
        Log.d(TAG, "token: " + token);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            intent = getIntentNotification(remoteMessage.getData());
        }

        // Check if message contains a data payload.
        JSONObject jsonData = new JSONObject();
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            jsonData = new JSONObject(remoteMessage.getData());
            String data_value_1 = remoteMessage.getData().get(FCM_TYPE);
            if (data_value_1 != null && data_value_1.equals(FCM_DATA_NEW_ORDER)) {
                updateOrders(jsonData);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(title, body, intent);
    }

    private void sendNotification(String title, String body, Intent intent) {

        String CHANNEL_ID = "my_channel_01";// The id of the channel.

        //Define the intent that will fire when the user taps the notification
        int requestID = (int) System.currentTimeMillis();
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a notification and set the notification channel.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.logo)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        CharSequence name = "Lista Pro FCM channel";// The user-visible name of the channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(requestID, notification);

    }


    private Intent getIntentNotification(Map<String, String> data) {

        Intent intent = new Intent(this, LauncherActivity.class);

        String data_value_1 = data.get(FCM_TYPE);
        final String appPackageName = getPackageName();

        if (data_value_1 != null) {
            if (data_value_1.equals("update")) {
                try {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                } catch (ActivityNotFoundException anfe) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                }
            }
        }

        PackageManager manager = this.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            //Then there is an Application(s) can handle your intent
            Log.d(TAG, "Notification intent ok");
        } else {
            //No Application can handle your intent
            Log.d(TAG, "Notification intent ok NOT ok");
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return intent;
    }


    private void updateOrders(JSONObject jsonData) {
        try {
            int serverShopId = jsonData.getInt("serverShopId");
            OrderRepository orderRepository = new OrderRepository(getApplication());
            orderRepository.loadOrdersFromServer(getApplicationContext(), serverShopId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
