package com.xs.parkmerchant.Net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import com.xs.parkmerchant.Adapter.TicketListViewAdapter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TicketContent {

    private List<TicketItem> items = new ArrayList<TicketItem>();
    private JSONObject jsonObject;
    private int count;
    private String address;
    private JSONArray jsonArray;
    private TicketListViewAdapter myListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutTicket;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                swipeRefreshLayoutTicket.setRefreshing(true);
            }else if(msg.what == 2){
                myListViewAdapter.notifyDataSetChanged();
                swipeRefreshLayoutTicket.setRefreshing(false);
            }
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
        handler.sendEmptyMessage(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("seller_id", "a"));
                params.add(new BasicNameValuePair("num", "0"));
                String result = NetCore.postResulttoNet(Url.ticketList_7, params);
                if(result != null && !result.equalsIgnoreCase("")){
                    try {
                        items.clear();
                        jsonObject = new JSONObject(result);
                        count = Integer.parseInt(jsonObject.getString("count"));
                        address = jsonObject.getString("seller_address");
                        jsonArray = jsonObject.getJSONArray("array");
                        for(int i=0; i<count; i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            TicketItem tmp = new TicketItem(jo.getString("activity_name"), address, jo.getString("activity_starttime")+"-"+jo.getString("activity_endtime"));
                            items.add(tmp);
                        }
                        handler.sendEmptyMessage(2);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
//                Log.d("result", result+"aaaaaa");
            }catch (Exception e){
                e.printStackTrace();
                handler.sendEmptyMessage(1);
//                Log.d("aaaa", "ccccccccccccccccccc");
            }
            }
        }).start();
    }

    public List<TicketContent.TicketItem> getITEMS(){
        return items;
    }

    public class TicketItem {
        public final String name;
        public final String address;
        public final String time;

        public TicketItem(String n, String a, String t) {
            this.name = n;
            this.address = a;
            this.time = t;
        }
    }
}