package com.xs.parkmerchant.Net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

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

    private boolean isLoadingMore = false, sync = false;
    private List<TicketDetailItem> items = new ArrayList<TicketDetailItem>();
    private JSONObject jsonObject;
    private int count;
    private String address;
    private JSONArray jsonArray;
    private TicketDetailAdapter myListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutTicket;
    private Context context;
    private String activity_id;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                myListViewAdapter.notifyDataSetChanged();
                Toast.makeText(context, isLoadingMore?"加载更多成功！":"刷新成功", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 2){
                Toast.makeText(context, isLoadingMore?"加载失败！":"刷新失败", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 3){
                Toast.makeText(context, "没有更多数据！", Toast.LENGTH_SHORT).show();
            }
            isLoadingMore = false;
            sync = false;
            swipeRefreshLayoutTicket.setRefreshing(false);
        }
    };

    public TicketDetailContent(Context c, String i){
        context = c;
        activity_id = i;
    }

    public void setAdapter(TicketDetailAdapter mla){
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
                    params.add(new BasicNameValuePair("activity_id", activity_id));
                    params.add(new BasicNameValuePair("num", isLoadingMore?""+myListViewAdapter.getCount():"0"));
                    String result = NetCore.postResulttoNet(Url.ticketDetail_8, params);
                    Log.d("ticketdetail", "A"+result+"A"+activity_id);
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
                                TicketDetailItem tmp = new TicketDetailItem(jo.getString("ticket_id"), jo.getString("user_name"), jo.getString("ticket_deadLine"), jo.getString("ticket_state"));
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