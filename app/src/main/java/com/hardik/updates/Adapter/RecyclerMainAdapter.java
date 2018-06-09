package com.hardik.updates.Adapter;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hardik.updates.Item.MainItem;
import com.hardik.updates.R;
import com.hardik.updates.Service.JobIntentCheckUpdate;
import com.hardik.updates.Utils.MenuHandler;
import com.hardik.updates.Xml.DateIntervalXml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.JOB_SCHEDULER_SERVICE;


/**
 * Created by Hardik on 08-04-2018.
 */

public class RecyclerMainAdapter extends RecyclerView.Adapter<RecyclerMainAdapter.ViewHolder> {

    private ArrayList<MainItem> list;
    private Context context;
    private final String TAG = "RecyclerAdapter";

    public RecyclerMainAdapter(ArrayList<MainItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainrecycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MainItem listItem = list.get(position);

        //setting data
        if (listItem.isIsheading()){
            holder.container.setVisibility(View.GONE);
            holder.heading.setText(listItem.getHeader());
        }
        else if (listItem.getId() == 3){
            holder.heading.setVisibility(View.GONE);
            holder.titletext.setText(listItem.getTitle());
            holder.subtext.setVisibility(View.GONE);
            holder.spinner.setVisibility(View.VISIBLE);
            holder.icon.setImageDrawable(context.getResources().getDrawable(listItem.getIconId()));
            holder.touch.setVisibility(View.GONE);
            DateIntervalXml dateIntervalXml = readinterval();
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.oonee, android.R.layout.simple_spinner_item);
            switch (dateIntervalXml.getInterval()){
                case 3 :
                    adapter = ArrayAdapter.createFromResource(context, R.array.three, android.R.layout.simple_spinner_item);
                    break;
                case 6 :
                    adapter = ArrayAdapter.createFromResource(context, R.array.six, android.R.layout.simple_spinner_item);
                    break;
                case 12 :
                    adapter = ArrayAdapter.createFromResource(context, R.array.twelve, android.R.layout.simple_spinner_item);
                    break;
                case 24 :
                    adapter = ArrayAdapter.createFromResource(context, R.array.twentyfour, android.R.layout.simple_spinner_item);
                    break;
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(context, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    String s = parent.getItemAtPosition(position).toString();
                    saveinterval(Integer.parseInt(s.substring(s.indexOf(" ")+1, s.lastIndexOf(" "))), "");
                    startservice();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else {
            holder.heading.setVisibility(View.GONE);
            holder.titletext.setText(listItem.getTitle());
            holder.subtext.setText(listItem.getText());
            holder.icon.setImageDrawable(context.getResources().getDrawable(listItem.getIconId()));
            final MenuHandler menuHandler = new MenuHandler(context);
            holder.touch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuHandler.handle(listItem.getId());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //item
        public ImageView icon;
        public TextView heading, titletext, subtext;
        public LinearLayout container;
        public Button touch;
        public Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);

            //initialising them
            icon = itemView.findViewById(R.id.imgIcon);
            heading = itemView.findViewById(R.id.txHeading);
            titletext = itemView.findViewById(R.id.txTitle);
            subtext = itemView.findViewById(R.id.txSubtitle);
            container = itemView.findViewById(R.id.llContainer);
            touch = itemView.findViewById(R.id.btnTouch);
            spinner = itemView.findViewById(R.id.spinnerMain);
        }
    }

    //save interval
    private void saveinterval(int interval, String time){
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
