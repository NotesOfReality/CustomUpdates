package com.hardik.updates.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.hardik.updates.Utils.CheckUpdate;

public class JobIntentCheckUpdate extends JobService {

    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("BackgroundUpdateCheck", "Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        CheckUpdate checkUpdate = new CheckUpdate(getApplicationContext());
        if (checkUpdate.updateAvailable()){
            checkUpdate.createNotification("Update Availble!", "ROM Update available.");
        }
        Log.d("BackgroundUpdateCheck", "Started");
        jobFinished(params, false);
        Log.d("BackgroundUpdateCheck", "Done");
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("BackgroundUpdateCheck", "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
