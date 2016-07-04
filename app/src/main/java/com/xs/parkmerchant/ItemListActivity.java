package com.xs.parkmerchant;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.xs.parkmerchant.Adapter.SimpleItemRecyclerViewAdapter;
import com.xs.parkmerchant.View.MyListView;
import com.xs.parkmerchant.View.MyViewPager;
import com.xs.parkmerchant.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    private boolean isLoadingMore = false;
    private SimpleItemRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int lastItem;

    private MyViewPager viewPager;
    private ImageView imageView;
    private TextView textView1, textView2;
    private List<View> views;
    private int offset = 0;
    public static int currIndex = 0;
    private int bmpW;
    private View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //viewPager
        initImageView();
        initTextView();
        initViewPager();

        //listView
        View listView = view2.findViewById(R.id.item_list);
        assert listView != null;
        setupRecyclerView((MyListView) listView);

        //refresh
        swipeRefreshLayout = (SwipeRefreshLayout) view2.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.white, android.R.color.holo_green_light, android.R.color.white);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                //onRefresh
            }
        });
    }

    private void initViewPager(){
        viewPager = (MyViewPager) findViewById(R.id.vPager);
        views = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater();
        view1 = layoutInflater.inflate(R.layout.tab1, null);
        view2 = layoutInflater.inflate(R.layout.tab2, null);
        views.add(view1);
        views.add(view2);
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
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

    private void setupRecyclerView(@NonNull final MyListView listView) {
        adapter = new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS, this, listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listView.canClick() && !MyListView.isOn){
                    Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, i);
                    startActivity(intent);
                }
                if(listView.canClick()) MyListView.isOn = false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(lastItem >= adapter.getCount()-1 && i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore){
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
