package com.xs.parkmerchant.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Man on 2016/7/4.
 */
public class ActivityListView extends ListView{

    private int screenWidth;
    private int downX, downY;
    private int btnWidth;

    public static boolean isDeleteShown = false;
    private ViewGroup mPointChild;	// 当前处理的item
    private LinearLayout.LayoutParams mLayoutParams;

    public static boolean isOn = false;
    private boolean isOn2 = false;

    public ActivityListView(Context context, AttributeSet attrs){
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isDeleteShown) return true;
        return super.onInterceptTouchEvent(ev);//true;//
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performActionDown(e);
                break;
            case MotionEvent.ACTION_MOVE:
                return performActionMove(e);
            case MotionEvent.ACTION_UP:
                performActionUp(e);
                break;
        }
        return super.onTouchEvent(e);
    }

    private void performActionDown(MotionEvent e){
        if(isOn2) return;
        downX = (int) e.getX();
        downY = (int) e.getY();
        mPointChild = (ViewGroup) getChildAt(pointToPosition(downX, downY)-getFirstVisiblePosition());
        if(mPointChild!=null) {
            btnWidth =  mPointChild.getChildAt(1).getLayoutParams().width;
            mLayoutParams = (LinearLayout.LayoutParams)mPointChild.getChildAt(0).getLayoutParams();
            mLayoutParams.width = screenWidth;
            mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
        }
    }

    private boolean performActionMove(MotionEvent e){
        int nowX = (int)e.getX();
        int nowY = (int)e.getY();
        //level move
        if(Math.abs(nowX-downX) > Math.abs(nowY-downY)){
            //to left
            if(nowX < downX && mLayoutParams!=null && mPointChild!=null){
                isDeleteShown = true;//
                int scroll = (nowX-downX)/2;
                if(-scroll >= btnWidth){
                    scroll = -btnWidth;
                }
                mLayoutParams.leftMargin = scroll;
                mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
            }
            return true;
        }
        return super.onTouchEvent(e);
    }

    private void performActionUp(MotionEvent e){
        Log.d("isOn", "A"+isOn2+"A"+isDeleteShown);
        if(isOn2) {
            turnToNormal();
            isOn2 = false;
            if(pointToPosition((int)e.getX(), (int)e.getY())==-1) isOn = false;
        }
        if(mLayoutParams==null || mPointChild==null) return;
        if(-mLayoutParams.leftMargin >= btnWidth /2){
            mLayoutParams.leftMargin = -btnWidth;
            isDeleteShown = true;
            isOn = true;
            isOn2 = true;
        }else{
            turnToNormal();
        }

        mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
    }

    public void turnToNormal(){
        if(mPointChild==null || mLayoutParams==null) return;
        mLayoutParams.leftMargin = 0;
        mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
        isDeleteShown = false;
    }

    public boolean canClick() {
        return !isDeleteShown;
    }

}