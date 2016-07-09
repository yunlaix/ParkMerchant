package com.xs.parkmerchant.Net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.xs.parkmerchant.Adapter.ActivityListViewAdapter;
import com.xs.parkmerchant.MainActivity;
import com.xs.parkmerchant.View.MySwipeRefreshLayout;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityContent {

    private boolean isLoadingMore = false, sync = false;
    private List<ActivityItem> items = new ArrayList<ActivityItem>();
    private JSONObject jsonObject;
    private int count;
    private JSONArray jsonArray;
    private ActivityListViewAdapter myListViewAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){//success
                myListViewAdapter.notifyDataSetChanged();
                Toast.makeText(context, isLoadingMore?"加载更多成功！":"刷新成功", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 2){//fail
                Toast.makeText(context, isLoadingMore?"加载失败！":"刷新失败", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 3){
                Toast.makeText(context, "没有更多数据！", Toast.LENGTH_SHORT).show();
            }
            isLoadingMore = false;
            MainActivity.isLoadingMore = false;
            sync = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    public ActivityContent(Context c){
        context = c;
    }

    public void setAdapter(ActivityListViewAdapter mla){
        myListViewAdapter = mla;
    }

    public void setSwipe(MySwipeRefreshLayout srl){
        swipeRefreshLayout = srl;
    }

    public List<ActivityContent.ActivityItem> getITEMS(){
        return items;
    }

    public void refresh(){
        if(sync) {
            Toast.makeText(context, "请等待当前操作完成！", Toast.LENGTH_SHORT).show();
            return;
        }
        sync = true;
        if(!isLoadingMore) swipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("seller_id", Constants.seller_id));//
                    params.add(new BasicNameValuePair("num", isLoadingMore?""+myListViewAdapter.getCount():"0"));
                    String result = NetCore.postResulttoNet(Url.activityList_5, params);
                    if(result != null && !result.equalsIgnoreCase("")){
                        try {
                            if(!isLoadingMore) items.clear();
                            jsonObject = new JSONObject(result);
                            count = Integer.parseInt(jsonObject.getString("count"));
                            if(count==0){
                                handler.sendEmptyMessage(3);
                            }else{
                            jsonArray = jsonObject.getJSONArray("array");
                            for(int i=0; i<count; i++){
                                JSONObject jo = jsonArray.getJSONObject(i);
                                ActivityItem tmp = new ActivityItem(jo.getString("activity_id"), jo.getString("activity_name"), Constants.seller_address, getFormatDate(jo.getString("activity_starttime"))+"-"+getFormatDate(jo.getString("activity_endtime")), jo.getString("activity_img"));
                                Log.d("image", jo.getString("activity_img"));
                                items.add(tmp);
                            }
                            handler.sendEmptyMessage(1);}
                        }catch (JSONException e){
                            e.printStackTrace();
                            handler.sendEmptyMessage(2);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();
    }

    public void loadMore(){
        isLoadingMore = true;
        refresh();
    }

    private String getFormatDate(String date){
        return date.substring(0, 4)+"."+date.substring(5, 7)+"."+date.substring(8, 10);
    }

    public class ActivityItem {
        public final String id;
        public final String name;
        public final String address;
        public final String time;
        public final String imgUrl;

        public ActivityItem(String id, String n, String a, String t, String i) {
            this.id = id;
            this.name = n;
            this.address = a;
            this.time = t;
            this.imgUrl = i;
        }
    }
}
