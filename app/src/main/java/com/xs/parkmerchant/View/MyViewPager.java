package com.xs.parkmerchant.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.xs.parkmerchant.MainActivity;

/**
 * Created by Man on 2016/7/4.
 */
public class MyViewPager extends ViewPager {

    private int downX;

    public MyViewPager(Context ct, AttributeSet attrs){
        super(ct, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) e.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int)e.getX();
                if(nowX < downX && MainActivity.currIndex == 1){//to left
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

}
