package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xs.parkmerchant.Helper.ItemTouchHelperAdapter;
import com.xs.parkmerchant.Helper.ItemTouchHelperViewHolder;
import com.xs.parkmerchant.Helper.OnStartDragListener;
import com.xs.parkmerchant.ItemDetailActivity;
import com.xs.parkmerchant.ItemDetailFragment;
import com.xs.parkmerchant.R;
import com.xs.parkmerchant.View.MyListView;
import com.xs.parkmerchant.dummy.DummyContent;

import java.util.Collections;
import java.util.List;

/**
 * Created by Man on 2016/7/1.
 */
public class SimpleItemRecyclerViewAdapter extends BaseAdapter {

    private List<DummyContent.DummyItem> mValues;
    private Holder holder;
    private LayoutInflater mInflater;
    private MyListView myListView;

    public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items, Context context, MyListView my) {
        mValues = items;
        mInflater = LayoutInflater.from(context);
        myListView = my;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.item_list_content, null);
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
