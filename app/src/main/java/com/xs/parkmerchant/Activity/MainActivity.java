package com.xs.parkmerchant.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.xs.parkmerchant.Adapter.MyViewPagerAdapter;
import com.xs.parkmerchant.Adapter.ActivityListViewAdapter;
import com.xs.parkmerchant.Adapter.TicketListViewAdapter;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.TicketContent;
import com.xs.parkmerchant.R;
import com.xs.parkmerchant.View.ActivityListView;
import com.xs.parkmerchant.View.MySwipeRefreshLayout;
import com.xs.parkmerchant.View.MyViewPager;
import com.xs.parkmerchant.Net.ActivityContent;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 * 展示停车券按照活动分类的部分列表
 * 展示部分活动列表
 * 提供个人中心入口
 * 提供发布活动入口
 * 提供删除活动入口
 * 提供刷新和加载更多
 */
public class MainActivity extends AppCompatActivity {

    private ImageView iv_me, iv_publish;

    public static boolean isLoadingMore = false;
    private ActivityListViewAdapter adapter;
    private ActivityContent activityContent;
    private MySwipeRefreshLayout mySwipeRefreshLayout;
    int lastItem;

    public static boolean isLoadingMoreTicket = false;
    private TicketListViewAdapter adapterTicket;
    private TicketContent ticketContent;
    private SwipeRefreshLayout swipeRefreshLayoutTicket;
    int lastItemTicket;

    private MyViewPager viewPager;
    private ImageView imageView;
    private TextView textView1, textView2;
    private List<View> views;
    private int offset = 0;
    public static int currIndex = 1;
    private int bmpW;
    private View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_me = (ImageView) findViewById(R.id.me);
        iv_publish = (ImageView) findViewById(R.id.publish);
        iv_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MineActivity.class);
                startActivity(intent);
            }
        });
        iv_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PublishActivity.class);
                intent.putExtra("source", "MainActivity");
                startActivity(intent);
            }
        });

        //viewPager
        initImageView();
        initTextView();
        initViewPager();

        //listView
        View listView = view2.findViewById(R.id.item_list);
        assert listView != null;
        setupActivityList((ActivityListView) listView);

        View listViewTicket = view1.findViewById(R.id.item_list);
        assert listViewTicket != null;
        setupTicketList((ListView) listViewTicket);

        //refresh
        mySwipeRefreshLayout = (MySwipeRefreshLayout) view2.findViewById(R.id.swipeLayout);
        mySwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.white, android.R.color.holo_green_light, android.R.color.white);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //onRefresh
                activityContent.refresh();
            }
        });

        swipeRefreshLayoutTicket = (SwipeRefreshLayout) view1.findViewById(R.id.swipeLayout);
        swipeRefreshLayoutTicket.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayoutTicket.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.white, android.R.color.holo_green_light, android.R.color.white);
        swipeRefreshLayoutTicket.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //onRefresh
                ticketContent.refresh();
            }
        });

        ticketContent.setSwipe(swipeRefreshLayoutTicket);
        activityContent.setSwipe(mySwipeRefreshLayout);
        ticketContent.refresh();
        activityContent.refresh();
    }

    private void initViewPager(){
        viewPager = (MyViewPager) findViewById(R.id.vPager);
        views = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater();
        view1 = layoutInflater.inflate(R.layout.tab_tickets, null);
        view2 = layoutInflater.inflate(R.layout.tab_activities, null);
        views.add(view1);
        views.add(view2);
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(1);
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            int one = offset * 2 + bmpW;
            int two = one * 2;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Animation animation = new TranslateAnimation(one*currIndex, one*position, 0, 0);
                currIndex = position;
                animation.setFillAfter(true);
                animation.setDuration(300);
                imageView.startAnimation(animation);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.setOnPageChangeListener(onPageChangeListener);
        onPageChangeListener.onPageSelected(1);
    }

    private void initTextView(){
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
    }

    private void initImageView(){
        imageView= (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.line).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);
    }

    private void setupActivityList(@NonNull final ActivityListView listView) {
        activityContent = new ActivityContent(this);
        adapter = new ActivityListViewAdapter(activityContent.getITEMS(), this, listView);
        activityContent.setAdapter(adapter);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listView.canClick() && !ActivityListView.isOn){
                    Intent intent = new Intent(getApplicationContext(), ActivityDetailActivity.class);
                    intent.putExtra("activity_id", activityContent.getITEMS().get(i).id);
                    intent.putExtra("activity_name", activityContent.getITEMS().get(i).name);
                    startActivity(intent);
                }
                if(listView.canClick()) ActivityListView.isOn = false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(lastItem >= adapter.getCount()-1 && i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore){
                    isLoadingMore = true;
                    //onLoadMore
                    Toast.makeText(getApplicationContext(), "加载更多...", Toast.LENGTH_LONG).show();
                    activityContent.loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                lastItem = i+i1-1;
            }
        });
    }

    private void setupTicketList(@NonNull final ListView listView) {
        ticketContent = new TicketContent(this);
        adapterTicket = new TicketListViewAdapter(ticketContent.getITEMS(), this);
        ticketContent.setAdapter(adapterTicket);
        listView.setAdapter(adapterTicket);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), TicketDetailActivity.class);
                intent.putExtra("activity_id", ticketContent.getITEMS().get(i).id);
                intent.putExtra("activity_name", activityContent.getITEMS().get(i).name);
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(lastItemTicket >= adapterTicket.getCount()-1 && i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMoreTicket){
                    isLoadingMoreTicket = true;
                    //onLoadMore
                    Toast.makeText(getApplicationContext(), "加载更多...", Toast.LENGTH_LONG).show();
                    ticketContent.loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                lastItemTicket = i+i1-1;
            }
        });
    }

    //返回桌面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return super.onKeyDown(keyCode, event);
    }

    //自动刷新
    @Override
    protected void onStart() {
        super.onStart();
        if(Constants.isPublished){
            activityContent.refresh();
            ticketContent.refresh();
            Constants.isPublished = false;
        }
    }
}
