package nl.hsleiden.notifier.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.math.BigDecimal;

import nl.hsleiden.notifier.Activity.OverviewActivity;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Ruben van Til on 22-1-2017.
 */

public class NotificationService extends Service {
    NotificationCompat.Builder Builder;
    NotificationManager NotifyMgr;
    Notification notification;
    int notificationID;

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("notify", "badabing");
//        Get Model.notification object
        notification = Notification.load(Notification.class,intent.getLongExtra("NotificationID", 0));
        notificationID = new BigDecimal(notification.getId()).intValueExact();

//        Create notification layout
        Builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_feedback_black_24dp)
                .setContentTitle(notification.title)
                .setContentText(notification.details)
                .setPriority(0)
                .setAutoCancel(true);

//        Intent to open activity after user selects notification
        PendingIntent resultIntent = PendingIntent.getActivity(
                this,
                notificationID,
                new Intent(this, OverviewActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);



//      Make notification happen
        Builder.setContentIntent(resultIntent);
        NotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotifyMgr.notify(notificationID, Builder.build());

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
