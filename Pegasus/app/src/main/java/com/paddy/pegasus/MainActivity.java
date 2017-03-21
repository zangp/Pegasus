package com.paddy.pegasus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.paddy.pegasus.data.DownloadInfo;
import com.paddy.pegasus.executors.PegasusExecutors;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MainActivity activity;
    private ListView mList;
    private PegasusExecutors executors;
    private ListViewAdapter mAdapter;
    private ArrayList<DownloadInfo> downloadList;
    private String[] urlList = {"http://app.mi.com/download/18076",
            "http://app.mi.com/download/109",
            "http://app.mi.com/download/110",
            "http://app.mi.com/download/112",
            "http://app.mi.com/download/113",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        initView();

    }

    private void initView() {
        mList = (ListView) findViewById(R.id.main_list);
        downloadList = new ArrayList<>();
        executors = new PegasusExecutors();

        createInfo();
        mAdapter = new ListViewAdapter(activity, downloadList, executors);

        mList.setAdapter(mAdapter);
    }



    private void createInfo() {
        for (int i = 0; i < urlList.length; i++) {
            DownloadInfo info = new DownloadInfo();
            info.setUrl(urlList[i]);
            downloadList.add(info);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executors.shutdown();
    }
}
