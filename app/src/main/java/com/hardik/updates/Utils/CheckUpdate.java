package com.hardik.updates.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hardik.updates.Activity.MainActivity;
import com.hardik.updates.R;
import com.hardik.updates.Xml.RomXml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

public class CheckUpdate {

    private Context context;

    public CheckUpdate(Context context) {
        this.context = context;
    }

    private final String TAG = "CheckUpdate";

    //checks itnernet connection
    private boolean checkinternet() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateAvailable(){
        try {
            ReadRomCode readRomCode = new ReadRomCode();
            int current = readRomCode.readRomCode();//current code
            Log.d(TAG, "Current : "+current);
            File rom = new File(Environment.getExternalStorageDirectory() + "/VaBeUpdater/rom.xml");
            Serializer serializer = new Persister();
            RomXml romXml = serializer.read(RomXml.class, rom);//rom item loaded
            int romcode = romXml.getBuildCode();
            Log.d(TAG, "Rom : "+romcode);
            if (romcode > current)
                return true;
            return false;
        }
        catch (Exception e){
            Log.e(TAG, "Error : "+e.toString());
        }
        return false;
    }

    public void createNotification(String title, String message) {
        //Creates an explicit intent for an Activity in app
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_update);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        String channelid = "1001";
        NotificationChannel notificationChannel = new NotificationChannel(channelid, "Updates", importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        assert mNotificationManager != null;
        mBuilder.setChannelId(channelid);
        mNotificationManager.createNotificationChannel(notificationChannel);
        if (mNotificationManager != null)
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());

    }
}
