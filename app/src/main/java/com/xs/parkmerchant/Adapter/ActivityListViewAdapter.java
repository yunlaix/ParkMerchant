package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import com.xs.parkmerchant.R;
import com.xs.parkmerchant.View.ActivityListView;
import com.xs.parkmerchant.Net.ActivityContent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Man on 2016/7/1.
 */
public class ActivityListViewAdapter extends BaseAdapter {

    private int p;
    private boolean sync = false;
    private List<ActivityContent.ActivityItem> mValues;
    private Holder holder;
    private LayoutInflater mInflater;
    private ActivityListView myListView;
    private Context context;
    private DisplayImageOptions options;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){//success
                mValues.remove(p);
                notifyDataSetChanged();
                myListView.turnToNormal();
                Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
                Log.d("delete", "ssssssssssssssssss");
            }else if(msg.what==2){//fail
                Toast.makeText(context, "删除失败！", Toast.LENGTH_SHORT).show();
                myListView.turnToNormal();
                Log.d("delete", "ffffffffffffffffff");
            }
            sync = false;
        }
    };

    public ActivityListViewAdapter(List<ActivityContent.ActivityItem> items, Context c, ActivityListView my) {
        mValues = items;
        mInflater = LayoutInflater.from(c);
        myListView = my;
        this.context = c;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.dark)
                .showImageForEmptyUri(R.mipmap.dark)
                .showImageOnFail(R.mipmap.dark)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.list_content_activity, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.address = (TextView) view.findViewById(R.id.address);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.delete = (TextView) view.findViewById(R.id.delete);
            holder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.name.setText(mValues.get(i).name);
        holder.address.setText("地址:"+mValues.get(i).address);
        holder.time.setText("时间:"+mValues.get(i).time);

        final int pos = i; p =i;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sync) Toast.makeText(context, "请等待当前删除完成！", Toast.LENGTH_SHORT).show();
                sync = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("activity_id", mValues.get(pos).id));
                            String result = NetCore.postResulttoNet(Url.deleteActivity_9, params);
                            Log.d("delete", result+"A");
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.getString("state").equals("0")) handler.sendEmptyMessage(1);
                            else handler.sendEmptyMessage(2);
                        }catch (Exception e){
                            e.printStackTrace();
                            handler.sendEmptyMessage(2);
                        }
                    }
                }).start();
            }
        });

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(mValues.get(i).imgUrl, holder.img, options);
        return view;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    private class Holder{
        private ImageView img;
        private TextView name;
        private TextView address;
        private TextView time;
        private TextView delete;
    }
}
