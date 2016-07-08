package com.xs.parkmerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.xs.parkmerchant.Adapter.TicketDetailAdapter;
import com.xs.parkmerchant.Adapter.TicketListViewAdapter;
import com.xs.parkmerchant.Net.TicketContent;
import com.xs.parkmerchant.Net.TicketDetailContent;

/**
 * Created by Man on 2016/7/6.
 */
public class TicketDetailActivity extends AppCompatActivity{

    private boolean isLoadingMore = false;
    private TicketDetailAdapter ticketDetailAdapter;
    private TicketDetailContent ticketDetailContent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int lastItem;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        id = getIntent().getStringExtra("activity_id");
        View listView = findViewById(R.id.item_list);
        assert listView != null;
        setListView((ListView)listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.white, android.R.color.holo_green_light, android.R.color.white);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //onRefresh
                ticketDetailContent.refresh();
            }
        });
        ticketDetailContent.setSwipe(swipeRefreshLayout);
        ticketDetailContent.refresh();
    }

    private void setListView(ListView listView){
        ticketDetailContent = new TicketDetailContent(this, id);
        ticketDetailAdapter = new TicketDetailAdapter(ticketDetailContent.getITEMS(), this);
        ticketDetailContent.setAdapter(ticketDetailAdapter);
        listView.setAdapter(ticketDetailAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getApplicationContext(), TicketDetailActivity.class);
//                startActivity(intent);
//            }
//        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(lastItem >= ticketDetailAdapter.getCount()-1 && i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore){
                    isLoadingMore = true;
                    //onLoadMore
                    Toast.makeText(getApplicationContext(), "加载更多...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                lastItem = i+i1-1;
            }
        });
    }

}
