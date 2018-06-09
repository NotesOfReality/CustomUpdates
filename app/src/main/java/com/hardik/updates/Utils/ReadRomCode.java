package com.hardik.updates.Utils;

import android.os.Environment;
import android.util.Log;

import com.hardik.updates.Xml.RomXml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.net.Inet4Address;
import java.util.StringTokenizer;

public class ReadRomCode {

    public ReadRomCode() {
    }

    private RomXml romXml;
    private String TAG = "RomCodeReader";

    public int readRomCode(){
        try {
            File rom = new File(Environment.getExternalStorageDirectory()+"/VaBeUpdater/rom.xml");
            Serializer serializer = new Persister();
            romXml = serializer.read(RomXml.class, rom);//rom item loaded
            Log.d(TAG, "Rom Data Loaded Successfully.");
            ExecuteShell executeShell = new ExecuteShell();
            String buildRawDetail = executeShell.executeShell(romXml.getBuildCommand());//full build name string
            StringTokenizer buildTokens = new StringTokenizer(buildRawDetail, romXml.getDelimiter());
            int i = 0;
            int code = 0;
            while (buildTokens.hasMoreTokens()){
                String t = buildTokens.nextToken();
                i++;
                if (i == romXml.getPosition()){//found code
                    String tempcode = "";
                    StringTokenizer inner = new StringTokenizer(t, "-");
                    if (inner.countTokens()>0){//removing - as in aokp
                        while (inner.hasMoreTokens()){
                            tempcode = tempcode+inner.nextToken();
                        }
                    }
                    else {
                        tempcode = t;
                    }
                    //now reversing it
                    tempcode = tempcode.substring(0,4)+tempcode.substring(6)+tempcode.substring(4,6);
                    code = Integer.parseInt(tempcode);//shifting it to other var
                    break;
                }
            }
            Log.d(TAG, "Build code : "+code);
            return code;
        }
        catch (Exception e){
            Log.e(TAG, "Error : " +e.toString());
            return -1;
        }
    }
}
