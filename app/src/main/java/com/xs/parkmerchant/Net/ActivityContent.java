package com.xs.parkmerchant.Net;

import com.xs.parkmerchant.Adapter.ActivityListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityContent {

    private List<ActivityItem> ITEMS = new ArrayList<ActivityItem>();
    private static final int COUNT = 25;
    private ActivityListViewAdapter myListViewAdapter;

    public ActivityContent(ActivityListViewAdapter mla){
        myListViewAdapter = mla;
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    public List<ActivityContent.ActivityItem> getITEMS(){
        return ITEMS;
    }

    private void addItem(ActivityItem item) {
        ITEMS.add(item);
    }

    private ActivityItem createDummyItem(int position) {
        return new ActivityItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public class ActivityItem {
        public final String id;
        public final String content;
        public final String details;

        public ActivityItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
