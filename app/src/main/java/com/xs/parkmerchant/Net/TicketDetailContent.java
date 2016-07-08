package com.xs.parkmerchant.Net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.xs.parkmerchant.Adapter.TicketDetailAdapter;
import com.xs.parkmerchant.Adapter.TicketListViewAdapter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TicketDetailContent {

    private List<TicketDetailItem> items = new ArrayList<TicketDetailItem>();
    private JSONObject jsonObject;
    private int count;
    private String address;
    private JSONArray jsonArray;
    private TicketDetailAdapter myListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutTicket;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                swipeRefreshLayoutTicket.setRefreshing(true);
            }else if(msg.what == 2){
//                myListViewAdapter.notifyDataSetChanged();
                swipeRefreshLayoutTicket.setRefreshing(false);
            }
        }
    };

    public TicketDetailContent(Context c){
        context = c;
    }

    public void setAdapter(TicketDetailAdapter mla){
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
                    params.add(new BasicNameValuePair("seller_id", Constants.seller_id));
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
//                                TicketDetailItem tmp = new TicketDetailItem(jo.getString("activity_name"), address, jo.getString("activity_starttime")+"-"+jo.getString("activity_endtime"));
//                                items.add(tmp);
                            }
                            handler.sendEmptyMessage(2);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    public List<TicketDetailContent.TicketDetailItem> getITEMS(){
        return items;
    }

    public class TicketDetailItem {
        public final String ticket_id;
        public final String user_name;
        public final String deadline;
        public final String state;

        public TicketDetailItem(String t, String u, String d, String s) {
            this.ticket_id = t;
            this.user_name = u;
            this.deadline = d;
            this.state = s;
        }
    }
}