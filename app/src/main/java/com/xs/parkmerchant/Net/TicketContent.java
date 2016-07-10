package com.xs.parkmerchant.Net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.xs.parkmerchant.Adapter.TicketListViewAdapter;
import com.xs.parkmerchant.Activity.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TicketContent {

    private boolean isLoadingMore = false, sync = false;
    private List<TicketItem> items = new ArrayList<TicketItem>();
    private JSONObject jsonObject;
    private int count;
    private JSONArray jsonArray;
    private TicketListViewAdapter myListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutTicket;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                myListViewAdapter.notifyDataSetChanged();
                Toast.makeText(context, isLoadingMore?"加载更多成功！":"刷新成功", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 2){
                Toast.makeText(context, isLoadingMore?"加载失败！":"刷新失败", Toast.LENGTH_SHORT).show();
            }else if(msg.what==3){
                Toast.makeText(context, "没有更多数据！", Toast.LENGTH_SHORT).show();
            }
            isLoadingMore = false;
            MainActivity.isLoadingMoreTicket = false;
            sync = false;
            swipeRefreshLayoutTicket.setRefreshing(false);
        }
    };

    public TicketContent(Context c){
        context = c;
    }

    public void setAdapter(TicketListViewAdapter mla){
        myListViewAdapter = mla;
    }

    public void setSwipe(SwipeRefreshLayout srl){
        swipeRefreshLayoutTicket = srl;
    }

    public void refresh(){
        if(sync) {
            Toast.makeText(context, "请等待当前操作完成！", Toast.LENGTH_SHORT).show();
            return;
        }
        sync = true;
        if(!isLoadingMore) swipeRefreshLayoutTicket.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("seller_id", Constants.seller_id));
                params.add(new BasicNameValuePair("num", isLoadingMore?""+myListViewAdapter.getCount():"0"));
                String result = NetCore.postResulttoNet(Url.ticketList_7, params);Log.d("aaaaaa", "A"+result);
                if(result != null && !result.equalsIgnoreCase("")){
                    try {
                        if(!isLoadingMore) items.clear();
                        jsonObject = new JSONObject(result);
                        count = Integer.parseInt(jsonObject.getString("count"));
                        if(count==0){
                            handler.sendEmptyMessage(3);
                        }else {
                        jsonArray = jsonObject.getJSONArray("array");
                        for(int i=0; i<count; i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            TicketItem tmp = new TicketItem(jo.getString("activity_id"), jo.getString("activity_name"), Constants.seller_address,
                                    getFormatDate(jo.getString("activity_starttime"))+"-"+getFormatDate(jo.getString("activity_endtime")), jo.getString("Tcount"));
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

    public List<TicketContent.TicketItem> getITEMS(){
        return items;
    }

    public class TicketItem {
        public final String id;
        public final String name;
        public final String address;
        public final String time;
        public final String tount;

        public TicketItem(String i, String n, String a, String t, String tc) {
            this.id = i;
            this.name = n;
            this.address = a;
            this.time = t;
            this.tount = tc;
        }
    }
}