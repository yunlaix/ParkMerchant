package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.ticket_state = (TextView) view.findViewById(R.id.ticket_state);
            holder.background = (ImageView) view.findViewById(R.id.background);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        if(mValues.get(i).state.equals("2")){//unused
            holder.time.setText("有效期限："+mValues.get(i).deadline);
            holder.background.setImageResource(R.mipmap.ticket_unused);
            holder.ticket_state.setText("未使用");
        }else if(mValues.get(i).state.equals("3")){//out of time
            holder.time.setText("有效期限："+mValues.get(i).deadline);
            holder.background.setImageResource(R.mipmap.ticket_down);
            holder.ticket_state.setText("已过期");
        }else if(mValues.get(i).state.equals("4")){//used
            holder.time.setText("使用时间："+mValues.get(i).usetime);
            holder.background.setImageResource(R.mipmap.ticket_used);
            holder.ticket_state.setText("已使用");
        }
        holder.user_name.setText(mValues.get(i).user_name);
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
        private TextView time;
        private TextView ticket_state;
        private ImageView background;
    }
}