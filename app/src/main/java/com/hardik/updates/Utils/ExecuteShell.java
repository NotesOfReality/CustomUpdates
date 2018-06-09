package com.hardik.updates.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteShell {

    public ExecuteShell() {
    }

    //executes a shell command
    public String executeShell(String command) {
        try {
            StringBuffer output = new StringBuffer();

            Process p;
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            String response = output.toString();
            return response;
        } catch (Exception e) {
            Log.e("ShellExecuter", e.toString());
        }
        return null;
    }
}
