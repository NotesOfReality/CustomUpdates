package com.hardik.updates.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.hardik.updates.Adapter.RecyclerMainAdapter;
import com.hardik.updates.Item.MainItem;
import com.hardik.updates.R;
import com.hardik.updates.Service.JobIntentCheckUpdate;
import com.hardik.updates.Utils.CheckDeviceCompatible;
import com.hardik.updates.Utils.CheckUpdate;
import com.hardik.updates.Utils.ExecuteShell;
import com.hardik.updates.Xml.DateIntervalXml;
import com.hardik.updates.Xml.RomXml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //recycler view stuff
    private RecyclerView recyclerView;
    private ArrayList<MainItem> list;
    private final String TAG = "MainActivity";
    private ProgressBar progressBar;
    private RecyclerMainAdapter adapter;
    private DateIntervalXml currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //defining view
        defineItems();

        if (checkpermission())
            startProcess();
        else
            requestpermission();
    }

    //defining items
    private void defineItems() {
        //defining them
        recyclerView = findViewById(R.id.rvMain);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        progressBar = findViewById(R.id.pbMain);
    }

    //starts the process
    private void startProcess() {
        //making directories
        makeDirectories();

        //checking if date file exists
        File date = new File(Environment.getExternalStorageDirectory()+"/VaBeUpdater/date.xml");
        if (!date.exists()){
            saveinterval(24);
            currentDate = readinterval();
            startservice();
        }
        currentDate = readinterval();

        //loading recycler first
        loadData();
        Log.d(TAG, "Recycler View Loaded");

        //main file
        File rom = new File(Environment.getExternalStorageDirectory()+"/VaBeUpdater/rom.xml");

        //checking if device is compatible
        final CheckDeviceCompatible checkDeviceCompatible = new CheckDeviceCompatible(this);
        if (checkinternet()) {
            checkDeviceCompatible.downloadData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Handler Begin");
                    if (checkDeviceCompatible.isLoaded()) {
                        if (checkDeviceCompatible.isCompatible()) {
                            /*Device compatible*/
                            Log.d(TAG, "Device Compatible.");
                            Toast.makeText(getApplicationContext(), "Device compatible.", Toast.LENGTH_SHORT).show();
                            downloadRomData(checkDeviceCompatible.getInfoObj().getXmlLink());

                        } else {
                            Toast.makeText(getApplicationContext(), "Device is not compatible.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Device not compatible with this app.");
                            finish();
                        }
                    }
                }
            }, 3000);
        }
        else if (rom.exists() && !checkinternet()){
            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            updateRecyclerData();
        }
        else {
            finish();
            Toast.makeText(getApplicationContext(), "Connect to internet first!", Toast.LENGTH_SHORT).show();
        }
    }

    //makes the directories
    private void makeDirectories() {
        File datafolder = new File(Environment.getExternalStorageDirectory() + "/VaBeUpdater/");
        datafolder.mkdirs();
    }

    public void loadData() {
        list.add(new MainItem(true, 0, "OTA", "", "", -1));
        list.add(new MainItem(false, R.drawable.ic_question, "", "Loading...", "Fetching Data", -1));
        list.add(new MainItem(false, R.drawable.ic_maintainer, "", "Maintainer", "No info available", -1));
        //list.add(new MainItem(false, R.drawable.ic_update, "", "Check update", "Last check: ???", -1));
        list.add(new MainItem(false, R.drawable.ic_autorenew, "", "Automatic update interval", "", 3));
        list.add(new MainItem(true, R.drawable.ic_maintainer, "Links", "", "", -1));
        list.add(new MainItem(false, R.drawable.ic_latestdownload, "", "Download latest build", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.ic_changelog, "", "Changelog", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.ic_google, "", "Download gapps", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.ic_forum, "", "Forum", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.ic_latestdownload, "", "Download compatible recovery", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.ic_paypal, "", "Donate", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.ic_telegram, "", "Telegram", "Link not available", -1));
        list.add(new MainItem(false, R.drawable.rage, "", "Developer", "Hardik Srivastava (MyNameIsRage)", 99));

        adapter = new RecyclerMainAdapter(list, this);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void downloadRomData(String link) {
        Log.d(TAG, "Rom data download started.");
        PRDownloader.initialize(this);
        DownloadRequest rom = PRDownloader.download(link, Environment.getExternalStorageDirectory() + "/VaBeUpdater", "rom.xml").build();
        rom.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Log.d(TAG, "Rom Data Downloaded");
                updateRecyclerData();
            }

            @Override
            public void onError(Error error) {
                Log.e(TAG, "Rom data not downloaded");
                list.remove(1);
                list.add(1, new MainItem(false, R.drawable.ic_question, "", "Unable to fetch info", "Connection Error", -1));
                /*list.remove(2);
                list.add(2,new MainItem(false, R.drawable.ic_maintainer, "", "Maintainer", "No info available", -1));
                list.remove(3);
                list.add(3,new MainItem(false, R.drawable.ic_update, "", "Check update", "Last check: ???",2));
                list.remove(4);
                list.add(4,new MainItem(false, R.drawable.ic_autorenew, "", "Automatic update interval", "Every 12 hours",3));
                list.remove(6);
                list.add(6,new MainItem(false, R.drawable.ic_latestdownload, "", "Download latest build", "Link not available", 4));
                list.remove(7);
                list.add(7,new MainItem(false, R.drawable.ic_changelog, "", "Changelog", "Link not available",5));
                list.remove(8);
                list.add(8,new MainItem(false, R.drawable.ic_google, "", "Download gapps", "Link not available", 6));
                list.remove(9);
                list.add(9,new MainItem(false, R.drawable.ic_forum, "", "Forum", "Link not available", 7));
                list.remove(10);
                list.add(10,new MainItem(false, R.drawable.ic_latestdownload, "", "Download compatible recovery", "Link not available", 8));
                list.remove(11);
                list.add(11,new MainItem(false, R.drawable.ic_paypal, "", "Donate", "Link not available", 9));
                list.remove(12);
                list.add(12,new MainItem(false, R.drawable.ic_telegram, "", "Telegram", "Link not available", 10));
                */
                //adapter = new RecyclerMainAdapter(list, getApplicationContext());
                //recyclerView.setAdapter(adapter);
            }
        });
    }

    public void updateRecyclerData() {
        try {
            File rom = new File(Environment.getExternalStorageDirectory() + "/VaBeUpdater/rom.xml");
            Serializer serializer = new Persister();
            RomXml romXml = serializer.read(RomXml.class, rom);//rom item loaded
            String imgid = romXml.getIconId();
            ExecuteShell executeShell = new ExecuteShell();
            int imgCode = R.drawable.ic_question;
            if (imgid.equalsIgnoreCase("aicp")){
                imgCode = R.drawable.ic_aicp;
            }
            else if(imgid.equals("aokp")){
                imgCode = R.drawable.ic_aokp;
            }
            else if (imgid.equals("validus")){
                imgCode = R.drawable.ic_validus;
            }

            CheckUpdate checkUpdate = new CheckUpdate(this);
            boolean update = checkUpdate.updateAvailable();
            String updateS = "No update available";
            if (update)
                updateS = "Update available!";

            list.remove(1);
            list.add(1, new MainItem(false, imgCode, "", executeShell.executeShell(romXml.getBuildCommand()), updateS, 0));
            list.remove(2);
            list.add(2, new MainItem(false, R.drawable.ic_maintainer, "", "Maintainer", romXml.getMaintainer(), 1));
            //list.remove(3);
            //list.add(3, new MainItem(false, R.drawable.ic_update, "", "Check update", "Last check: " + currentDate.getLastCheck(), 2));
            list.remove(3);
            list.add(3, new MainItem(false, R.drawable.ic_autorenew, "", "Automatic update interval", "Every "+currentDate.getInterval()+" hours", 3));
            list.remove(5);
            list.add(5, new MainItem(false, R.drawable.ic_latestdownload, "", "Download latest build", "Visit download page", 4));
            list.remove(6);
            list.add(6, new MainItem(false, R.drawable.ic_changelog, "", "Changelog", "View changelog", 5));
            list.remove(7);
            list.add(7, new MainItem(false, R.drawable.ic_google, "", "Download gapps", "Visit google apps download page", 6));
            list.remove(8);
            list.add(8, new MainItem(false, R.drawable.ic_forum, "", "Forum", "View latest discussions", 7));
            list.remove(9);
            list.add(9, new MainItem(false, R.drawable.ic_latestdownload, "", "Download compatible recovery", "Visit recovery download page", 8));
            list.remove(10);
            list.add(10, new MainItem(false, R.drawable.ic_paypal, "", "Donate", "Support device development", 9));
            list.remove(11);
            list.add(11, new MainItem(false, R.drawable.ic_telegram, "", "Telegram", "Join latest discussion on telegram", 10));

            adapter = new RecyclerMainAdapter(list, getApplicationContext());
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error in loading rom xml : " + e.toString());
        }
    }

    //save interval
    private void saveinterval(int interval){
        try {
            Serializer serializer = new Persister();
            DateIntervalXml date = new DateIntervalXml(interval);
            serializer.write(date, new File(Environment.getExternalStorageDirectory() + "/VaBeUpdater/date.xml"));
        }
        catch (Exception e){
            Log.e(TAG, "Error in writing date file : "+e.toString());
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

    //starts service
    public void startservice() {
        ComponentName componentName = new ComponentName(getApplicationContext(), JobIntentCheckUpdate.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(true)
                .setPeriodic(currentDate.getInterval() * 60 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    //checks if permission is granted
    private boolean checkpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    //callback id
    private int callback = 111;

    //requests the permission
    private void requestpermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //granted
            startProcess();
        } else {
            // permission denied
            Toast.makeText(getApplicationContext(), "Sorry, this app can't proceed without storage permissions.", Toast.LENGTH_LONG).show();
        }
    }


    //checks itnernet connection
    private boolean checkinternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        } else {
            return true;
        }
    }

}
