package com.hardik.updates.Service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.hardik.updates.Xml.DateIntervalXml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class ServiceStarter extends BroadcastReceiver {

    private static final String TAG = "Activity";


    @Override
    public void onReceive(Context context, Intent intent) {
        //start service here
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            File date = new File(Environment.getExternalStorageDirectory()+"/VaBeUpdater/date.xml");
            if (date.exists()) {
                ComponentName componentName = new ComponentName(context, JobIntentCheckUpdate.class);
                JobInfo info = new JobInfo.Builder(123, componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                        .setPersisted(true)
                        .setPeriodic(readinterval().getInterval() * 60 * 60 * 1000)
                        .build();

                JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
                int resultCode = scheduler.schedule(info);
                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Log.d(TAG, "Job scheduled");
                } else {
                    Log.d(TAG, "Job scheduling failed");
                }
            }
        }
    }

    //reads interval
    private DateIntervalXml readinterval(){
        try {
            Serializer serializer = new Persister();
            File date = new File(Environment.getExternalStorageDirectory()+"/VaBeUpdater/date.xml");
            return serializer.read(DateIntervalXml.class, date);
        }
        catch (Exception e){
            Log.e(TAG, "Error in reading date file : "+e.toString());
        }
        return null;
    }
}
