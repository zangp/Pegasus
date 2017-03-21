package com.paddy.pegasus;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paddy.pegasus.data.DownloadInfo;
import com.paddy.pegasus.executors.DownloadTaskRunnable;
import com.paddy.pegasus.executors.PegasusExecutors;
import com.paddy.pegasus.util.UrlUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by pengpeng on 2017/3/21.
 */

public class ListViewAdapter extends BaseAdapter {
    private List<DownloadInfo> infoList;
    private Context context;
    private PegasusExecutors executors;
    private Map<String,Future<?>> runMap = new HashMap<>();

    public ListViewAdapter(Context context,List<DownloadInfo> infoList,
                                    PegasusExecutors executors) {
        this.infoList = infoList;
        this.context = context;
        this.executors = executors;
    }

    @Override
    public int getCount() {
        if (infoList == null)
            return 0;

        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_progress,null);
            viewHolder.pb = (ProgressBar) convertView.findViewById(R.id.item_progress_bar);
            viewHolder.tvDownload = (TextView) convertView.findViewById(R.id.item_tv_download);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.pb.setProgress(infoList.get(position).getStart());
        viewHolder.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = infoList.get(position).getUrl();

                if (viewHolder.tvDownload.getText().equals("暂停")) {
                    Log.d("zp_test","暂停: " + position);
                    viewHolder.tvDownload.setText("下载");
                    Future<?> future =runMap.remove(UrlUtil.keyFromUrl(url));
                    if (future != null)
                        future.cancel(true);
                } else {
                    viewHolder.tvDownload.setText("暂停");
                    runMap.put(UrlUtil.keyFromUrl(url),
                            executors.submit(new DownloadTaskRunnable(url, new MyHandler()
                                    ,viewHolder.pb)));

                }
            }
        });

        return convertView;
    }

    class ViewHolder{
        ProgressBar pb;
        TextView tvDownload;

    }

    static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg == null)
                return;

            ProgressBar pb = (ProgressBar) msg.obj;
            pb.setProgress(msg.arg1);

            if (msg.arg1 == 100)
                pb = null;
        }
    }
}
