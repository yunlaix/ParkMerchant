package com.xs.parkmerchant.Net;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Man on 2016/7/7.
 * 静态数据和静态方法
 */
public class Constants {

    public static String seller_id="";
    public static String seller_name="";
    public static String seller_password="";
    public static String seller_address="";
    public static String seller_address_detail="";
    public static float addr_lan, addr_lon;
    public static String seller_contact="";
    public static String seller_img="";

    public static String activity_id = "";
    public static String activity_name="";
    public static String activity_starttime="";
    public static String activity_endttime="";
    public static String activity_img="";
    public static String activity_detail="";
    public static Bitmap activity_bitmap;

    public static boolean isPublished = false;

    public static boolean isPicked = false;

    public static boolean isNetWorkConnected(Context context){
        if(context!=null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null) return networkInfo.isAvailable();
        }
        return false;
    }

}
