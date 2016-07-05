package com.xs.parkmerchant.Net;

import android.content.Context;
import android.widget.Toast;

import com.xs.parkmerchant.Adapter.ActivityListViewAdapter;
import com.xs.parkmerchant.Adapter.TicketListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TicketContent {

    private List<TicketItem> ITEMS = new ArrayList<TicketItem>();
    private static final int COUNT = 25;
    private TicketListViewAdapter myListViewAdapter;
    private Context context;

    public TicketContent(Context c, TicketListViewAdapter mla){
        myListViewAdapter = mla;
        context = c;
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    public void refresh(){
        Toast.makeText(context, "刷新...", Toast.LENGTH_SHORT).show();

    }

    public List<TicketContent.TicketItem> getITEMS(){
        return ITEMS;
    }

    private void addItem(TicketItem item) {
        ITEMS.add(item);
    }

    private TicketItem createDummyItem(int position) {
        return new TicketItem(String.valueOf(position), "Item " + position, "2016.7.1-2016.7.8");
    }

    private String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
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