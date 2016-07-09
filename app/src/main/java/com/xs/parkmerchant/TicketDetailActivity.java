package com.xs.parkmerchant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xs.parkmerchant.Adapter.TicketDetailAdapter;
import com.xs.parkmerchant.Net.TicketDetailContent;

/**
 * Created by Man on 2016/7/6.
 */
public class TicketDetailActivity extends AppCompatActivity{

    private boolean isLoadingMore = false;
    private TicketDetailAdapter ticketDetailAdapter;
    private TicketDetailContent ticketDetailContent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView back;
    private TextView activity_name;
    private int lastItem;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        id = getIntent().getStringExtra("activity_id");
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        activity_name = (TextView)findViewById(R.id.activity_name);
        activity_name.setText(getIntent().getStringExtra("activity_name"));
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(lastItem >= ticketDetailAdapter.getCount()-1 && i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore){
                    isLoadingMore = true;
                    //onLoadMore
                    Toast.makeText(getApplicationContext(), "加载更多...", Toast.LENGTH_LONG).show();
                    ticketDetailContent.loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                lastItem = i+i1-1;
            }
        });
    }

}
