package org.y20k.transistor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.ui.activities.RadioPlayerActivity;
import com.newandromo.dev18147.app821162.utils.AppUtils;

/**
 * RadioNotificationUtils class
 */
public final class RadioNotificationUtils {
    private static final String ONLINE_RADIO_CHANNEL = "new_posts_channel";
    private NotificationManager manager;
    private NotificationManagerCompat managerCompat;
    private final Context mContext;
    private int mStationID = -1;
    private String mStationName;
    private String mStationIcon;

    /* Constructor */
    RadioNotificationUtils(Context context) {
        mContext = context;
        if (AppUtils.hasOreo()) {
            NotificationChannel channel = new NotificationChannel(ONLINE_RADIO_CHANNEL,
                    mContext.getString(R.string.notification_channel_radio), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }

    /* Setter for name of station */
    public void setStationID(int stationID) {
        mStationID = stationID;
    }

    /* Setter for name of station */
    public void setStationName(String stationName) {
        mStationName = stationName;
    }

    public void setStationIcon(String stationIcon) {
        mStationIcon = stationIcon;
    }

    /* Construct and put up notification */
    public void createNotification() {
        NotificationCompat.Builder builder;
        Notification notification;
        String notificationText;
        String notificationTitle;
        int notificationColor;

        // create content of notification
        notificationText = mContext.getString(R.string.notification_swipe_to_stop);
        notificationTitle = mStationName;
        notificationColor = ContextCompat.getColor(mContext, R.color.app_primary_material_red);

        // explicit intent for notification tap
        Intent tapIntent = new Intent(mContext, RadioPlayerActivity.class);
        tapIntent.setAction(TransistorKeys.ACTION_SHOW_PLAYER);
        tapIntent.putExtra(TransistorKeys.EXTRA_STATION_ID, mStationID);

        // explicit intent for notification swipe
        Intent swipeIntent = new Intent(mContext, RadioPlayerService.class);
        swipeIntent.setAction(TransistorKeys.ACTION_STOP);

        // artificial back stack for started Activity.
        // -> navigating backward from the Activity leads to Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // backstack: adds back stack for Intent (but not the Intent itself)
        stackBuilder.addParentStack(RadioPlayerActivity.class);
        // backstack: add explicit intent for notification tap
        stackBuilder.addNextIntent(tapIntent);

        // pending intent wrapper for notification tap
        PendingIntent tapPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // pending intent wrapper for notification swipe
        PendingIntent swipePendingIntent = PendingIntent.getService(mContext, 0, swipeIntent, 0);

        // construct notification in builder
        builder = new NotificationCompat.Builder(mContext, ONLINE_RADIO_CHANNEL);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setSmallIcon(R.drawable.ic_notification_radio);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationText);
        builder.setColor(notificationColor);
        builder.setUsesChronometer(true);
        builder.setContentIntent(tapPendingIntent);
        builder.setDeleteIntent(swipePendingIntent);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_radio);

        remoteViews.setImageViewResource(R.id.station_icon, R.drawable.radio_tower);
        remoteViews.setTextViewText(R.id.station_name, mStationName);
        remoteViews.setTextViewText(R.id.station_slogan, notificationText);

        builder.setCustomContentView(remoteViews);

        // build notification
        notification = builder.build();

        try {
            Picasso.get()
                    .load(mStationIcon)
                    .into(remoteViews, R.id.station_icon, TransistorKeys.PLAYER_SERVICE_NOTIFICATION_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // display notification
        if (notification != null)
            getManagerCompat().notify(TransistorKeys.PLAYER_SERVICE_NOTIFICATION_ID, notification);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private NotificationManagerCompat getManagerCompat() {
        if (managerCompat == null) {
            managerCompat = NotificationManagerCompat.from(mContext);
        }
        return managerCompat;
    }
}
