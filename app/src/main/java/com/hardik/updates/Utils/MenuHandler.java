package com.hardik.updates.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.hardik.updates.Activity.MainActivity;
import com.hardik.updates.Xml.RomXml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

public class MenuHandler {

    private Context context;
    private final String TAG = "MenuHandler";

    public MenuHandler(Context context) {
        this.context = context;
    }

    public void handle(int id) {
        try {
            File rom = new File(Environment.getExternalStorageDirectory() + "/VaBeUpdater/rom.xml");
            Serializer serializer = new Persister();
            RomXml romXml = serializer.read(RomXml.class, rom);//rom item loaded
            MainActivity mainActivity = new MainActivity();
            switch (id) {
                case 0:
                    break;
                case 1://maintainer
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getMaintainer())));
                    break;
                case 3 : //update interval
                    //context.startActivity(new Intent(context, AskInterval.class));
                    break;
                case 4 : //latest build
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getLatestLink())));
                    break;
                case 5 : //changelog
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getChangelog())));
                    break;
                case 6 : //gapps
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getGappsLink())));
                    break;
                case 7 : //forum
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getForumLink())));
                    break;
                case 8 : //recovery
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getRecoveryLink())));
                    break;
                case 9 : //donate
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getDonationLink())));
                    break;
                case 10 : //tg
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(romXml.getTelegramLink())));
                    break;
                case 99 :
                    String l = "https://t.me/MyNameIsRage";
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(l)));
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error : "+e.toString());
        }
    }
}
