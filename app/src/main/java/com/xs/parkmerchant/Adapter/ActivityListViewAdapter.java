package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xs.parkmerchant.R;
import com.xs.parkmerchant.View.ActivityListView;
import com.xs.parkmerchant.Net.ActivityContent;
import java.util.List;

/**
 * Created by Man on 2016/7/1.
 */
public class ActivityListViewAdapter extends BaseAdapter {

    private List<ActivityContent.ActivityItem> mValues;
    private Holder holder;
    private LayoutInflater mInflater;
    private ActivityListView myListView;

    public ActivityListViewAdapter(List<ActivityContent.ActivityItem> items, Context context, ActivityListView my) {
        mValues = items;
        mInflater = LayoutInflater.from(context);
        myListView = my;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.list_content_activity, null);
            holder.mIdView = (TextView) view.findViewById(R.id.id);
            holder.mContentView = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.mIdView.setText(mValues.get(i).id);
        holder.mContentView.setText(mValues.get(i).content);

        final int pos = i;
        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValues.remove(pos);
                notifyDataSetChanged();
                myListView.turnToNormal();
            }
        });
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
        private TextView mIdView;
        private TextView mContentView;
    }
}
