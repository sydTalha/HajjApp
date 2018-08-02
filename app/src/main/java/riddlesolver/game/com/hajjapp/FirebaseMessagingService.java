package riddlesolver.game.com.hajjapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Log.e("notif", "Received");
        showNotifications(remoteMessage.getData().get("data"));
    }

    private void showNotifications(String message) {


        Log.e("Error", "Notification recieved");
        String text = "";
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        //notificationIntent.putExtra(Constants.FROM_NOTIFICATION, true);

        text = "You have New Rides Available. Click to Pick a ride.";


        PendingIntent contentIntent = PendingIntent.getActivity(this,
                151, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = getResources();
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "w01", name = "LoaderPk";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String desc = "Ride Available";

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(desc);
            nm.createNotificationChannel(channel);
            builder = new Notification.Builder(this, id);
        } else {
            builder = new Notification.Builder(this);
        }
        //builder.build().flags |= Notification.FLAG_AUTO_CANCEL;


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground))
                .setTicker(getBaseContext().getResources().getString(R.string.app_name))
                .setSound(uri)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Ride Available")
                .setContentText(text);


        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        Notification n = builder.build();

        nm.notify(151, n);
    }
}
