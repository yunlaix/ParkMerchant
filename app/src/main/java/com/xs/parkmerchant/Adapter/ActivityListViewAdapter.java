package com.xs.parkmerchant.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
    private Context context;
    private DisplayImageOptions options;

    public ActivityListViewAdapter(List<ActivityContent.ActivityItem> items, Context c, ActivityListView my) {
        mValues = items;
        mInflater = LayoutInflater.from(c);
        myListView = my;
        this.context = c;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.dark)
                .showImageForEmptyUri(R.mipmap.dark)
                .showImageOnFail(R.mipmap.dark)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.list_content_activity, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.address = (TextView) view.findViewById(R.id.address);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.delete = (TextView) view.findViewById(R.id.delete);
            holder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.name.setText(mValues.get(i).name);
        holder.address.setText(mValues.get(i).address);
        holder.time.setText(mValues.get(i).time);

        final int pos = i;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValues.remove(pos);
                notifyDataSetChanged();
                myListView.turnToNormal();
            }
        });

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(mValues.get(i).imgUrl, holder.img, options);
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
        private ImageView img;
        private TextView name;
        private TextView address;
        private TextView time;
        private TextView delete;
    }
}
