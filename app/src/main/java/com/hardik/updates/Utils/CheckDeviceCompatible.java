package com.hardik.updates.Utils;

import android.app.Activity;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.hardik.updates.R;
import com.hardik.updates.Xml.DeviceRomCheckInfo;
import com.hardik.updates.Xml.DeviceRomInfo;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CheckDeviceCompatible {

    private Context context;
    private final String TAG = "DeviceCompatibleCheck";
    private ArrayList<DeviceRomInfo> datalist;
    private DeviceRomInfo infoObj;

    public boolean isLoaded() {
        return isLoaded;
    }

    private boolean isLoaded = false;

    public CheckDeviceCompatible(Context context) {
        this.context = context;
    }

    //downloads the main locater xml
    public void downloadData(){
        PRDownloader.initialize(context);
        DownloadRequest data = PRDownloader.download(context.getResources().getString(R.string.main_link), Environment.getExternalStorageDirectory()+"/VaBeUpdater/", "main.xml").build();
        Log.d(TAG, "Download Started.");
        data.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                /**here*/
                Log.d(TAG, "Download Completed.");
                readList();
            }

            @Override
            public void onError(Error error) {
                /**Exit*/
                Log.d(TAG, "Download Incomplete.");
                Toast.makeText(context, "Kindly check your internet connection to proceed.", Toast.LENGTH_LONG).show();
                ((Activity) context).finish();
            }
        });
    }

    //reads the data to the list
    private void readList(){
        try{
            File datafile = new File(Environment.getExternalStorageDirectory()+"/VaBeUpdater/main.xml");
            Serializer serializer = new Persister();
            DeviceRomCheckInfo deviceRomCheckInfo = serializer.read(DeviceRomCheckInfo.class, datafile);
            datalist = deviceRomCheckInfo.getRomList();
            Log.d(TAG, "Data successfully loaded.");
            isLoaded = true;
        }
        catch (Exception e){
            Log.e(TAG, "Error in Loading Data. : " + e.toString());
            ((Activity) context).finish();
        }
    }

    //reads the file and checks
    public boolean isCompatible(){
        ExecuteShell executeShell = new ExecuteShell();
        for (int i = 0; i < datalist.size(); i++) {
            StringTokenizer commands = new StringTokenizer(datalist.get(i).getCommand(), ",");
            StringTokenizer outputs = new StringTokenizer(datalist.get(i).getOutput(), ",");
            while (commands.hasMoreTokens()) {//cycling through commands and stuff
                String com = commands.nextToken();
                String inp = executeShell.executeShell(com);
                String op = outputs.nextToken();
                Log.d(TAG, com+","+inp + "," + op);
                if (inp.contains(op)) {//required output found
                    infoObj = datalist.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    public DeviceRomInfo getInfoObj() {
        return infoObj;
    }
}
