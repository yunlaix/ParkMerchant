package com.xs.parkmerchant.View;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Man on 2016/7/6.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    private int downX, downY;

    public MySwipeRefreshLayout(Context c, AttributeSet a){
        super(c, a);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ActivityListView.isDeleteShown) return false;
        return super.onInterceptTouchEvent(ev);//false;//
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                performActionDown(e);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                return performActionMove(e);
//            case MotionEvent.ACTION_UP:
//                performActionUp();
//                break;
//        }
//        return super.onTouchEvent(e);
//    }
//
//    private void performActionDown(MotionEvent e){
//        downX = (int) e.getX();
//        downY = (int) e.getY();
//    }
//
//    private boolean performActionMove(MotionEvent e){
//        int nowX = (int)e.getX();
//        int nowY = (int)e.getY();
//        Log.d("move", "aaaaaaaaaaa"+(nowY-downY));
//        //level move
//        if(ActivityListView.isDeleteShown || Math.abs(nowX-downX) > Math.abs(nowY-downY)){
//            return false;
//        }
//        return super.onTouchEvent(e);
//    }
//
//    private void performActionUp(){
//
//    }

}