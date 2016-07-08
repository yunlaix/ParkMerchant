package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xs.parkmerchant.Net.TicketDetailContent;
import com.xs.parkmerchant.R;
import java.util.List;

/**
 * Created by Man on 2016/7/1.
 */
public class TicketDetailAdapter extends BaseAdapter {

    private List<TicketDetailContent.TicketDetailItem> mValues;
    private Holder holder;
    private LayoutInflater mInflater;

    public TicketDetailAdapter(List<TicketDetailContent.TicketDetailItem> items, Context context) {
        mValues = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.list_content_ticket_detail, null);
            holder.user_name = (TextView) view.findViewById(R.id.user_name);
            holder.ticket_deadline = (TextView) view.findViewById(R.id.ticket_deadline);
            holder.ticket_state = (TextView) view.findViewById(R.id.ticket_state);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.user_name.setText(mValues.get(i).user_name);
        holder.ticket_deadline.setText("使用期限："+mValues.get(i).deadline);
        holder.ticket_state.setText("使用状态："+(mValues.get(i).state.equals("1") ? "未使用" : "已使用"));
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
        private TextView user_name;
        private TextView ticket_deadline;
        private TextView ticket_state;
        //private
    }
}