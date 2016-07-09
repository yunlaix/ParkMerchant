package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xs.parkmerchant.Net.TicketContent;
import com.xs.parkmerchant.R;
import com.xs.parkmerchant.View.ActivityListView;
import com.xs.parkmerchant.Net.ActivityContent;
import java.util.List;

/**
 * Created by Man on 2016/7/1.
 */
public class TicketListViewAdapter extends BaseAdapter {

    private List<TicketContent.TicketItem> mValues;
    private Holder holder;
    private LayoutInflater mInflater;

    public TicketListViewAdapter(List<TicketContent.TicketItem> items, Context context) {
        mValues = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.list_content_ticket, null);
            holder.mActivityName = (TextView) view.findViewById(R.id.activity_name);
            holder.mActivityAddress = (TextView) view.findViewById(R.id.activity_address);
            holder.mActivityTime = (TextView) view.findViewById(R.id.activity_time);
            holder.mActivityTcount = (TextView) view.findViewById(R.id.tcount);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.mActivityName.setText(mValues.get(i).name);
        holder.mActivityAddress.setText(mValues.get(i).address);
        holder.mActivityTime.setText("使用期限:"+mValues.get(i).time);
        holder.mActivityTcount.setText(mValues.get(i).tount);
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
        private TextView mActivityName;
        private TextView mActivityAddress;
        private TextView mActivityTime;
        private TextView mActivityTcount;
    }
}