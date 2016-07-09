package com.xs.parkmerchant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Iterators;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.Handler;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class ActivityDetailActivity extends AppCompatActivity {

    private TextView activityName;
    private ImageView detailBack;
    private ImageView activityDetailImage;
    private TextView detailActivityTime;
    private TextView detailActivityLocation;
    private TextView detailActivityDescri;

    private Button createQR;
    private Button deleteActivity;

    private String activity_img, activity_id, activity_name,activity_addr,activity_starttime, activity_endtime, seller_address,activity_detail,activity_time;
    private DisplayImageOptions options;

    private final int MSG_SUCCESS = 1;
    private final int MSG_GET = 2;
    private HashMap<String,String> map;

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_SUCCESS:
                    activityDetailImage.setImageBitmap((Bitmap) msg.obj);//imageview显示从网络获取到的logo
//                    Toast.makeText(getApplication(), "下载图片成功", Toast.LENGTH_LONG).show();
                    break;
                case MSG_GET:
                    activity_time = map.get("activity_time");
                    activity_addr = map.get("activity_addr");
                    activity_detail = map.get("activity_detail");
                    activity_img = map.get("activity_img");
                    Log.d("MSG_GET:","--activity_time:"+activity_time+"-activity_addr:"+activity_addr+"-activity_detail:"+activity_detail+"-activity_img:"+activity_img);

                    activityName.setText(activity_name);
                    detailActivityTime.setText(activity_time);
                    detailActivityLocation.setText(Constants.seller_address);
                    detailActivityDescri.setText(activity_addr);
                    downloadImage(activity_img);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        Intent intent =  getIntent();
        activity_id = intent.getStringExtra("activity_id");
        Log.d("activity_id", activity_id);
        activity_name = intent.getStringExtra("activity_name");
        Log.d("activity_name", activity_name);

        initView();
        downLoadDetails();

        activityName.setText(activity_name);
        Log.d("details:","--activity_time:"+activity_time+"-activity_addr:"+activity_addr+"-activity_detail:"+activity_detail+"-activity_img:"+activity_img);


//        downloadImage(Constants.activity_img);


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.dark)
                .showImageForEmptyUri(R.mipmap.dark)
                .showImageOnFail(R.mipmap.dark)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        detailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityDetailActivity.this, QRActivity.class);
                intent.putExtra("activity_id", activity_id);
                intent.putExtra("activity_name",activity_name);
                startActivity(intent);
            }
        });

        deleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteActivity();
            }
        });

    }

    public void showToast(String a,String b){
        Toast.makeText(ActivityDetailActivity.this, a + " : " + b, Toast.LENGTH_SHORT).show();
    }

    private void downLoadDetails(){
        Log.d("downLoadDetails","downLoadDetails");
//        map = new HashMap<String, String>();
       Thread dowmLoadDetails =  new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
//                param.clear();
                param.add(new BasicNameValuePair("activity_id", activity_id));
                Log.d("downLoadDetails",activity_id);

                try {
                    String data = NetCore.postResulttoNet(Url.activityDetail,param);
                    Log.d("NetCoredata", data);
                    if(data != null){

                        final JSONObject jb = new JSONObject(data);
                        int state = jb.getInt("state");
                        Constants.activity_img = jb.getString("activity_img");
//                        Log.d("activity_img", activity_img);
                        //错误：不显示
//                        Constants.activity_time = jb.getString("activity_starttime")+" 至 " +jb.getString("activity_endtime");
//                        Constants.activity_addr = jb.getString("seller_address");
//                        Constants.activity_detail = jb.getString("activity_detail");


                        //报错：不能再子线程操作UI
//                        detailActivityTime.setText(jb.getString("activity_starttime")+" 至 " +jb.getString("activity_endtime"));
//                        detailActivityLocation.setText(Constants.seller_address);
//                        detailActivityDescri.setText(jb.getString("activity_detail"));


//                        activity_time = jb.getString("activity_starttime")+" 至 " +jb.getString("activity_endtime");
//                        activity_addr = jb.getString("activity_addr");
//                        activity_detail = jb.getString("activity_detail");
//                        activity_img = jb.getString("activity_img");
//
//                        Log.d("DOWNLOAD:","--activity_time:"+activity_time+"-activity_addr:"+activity_addr+"-activity_detail:"+activity_detail+"-activity_img:"+activity_img);
//                        //错误：不显示
//                        map.put("activity_time", jb.getString("activity_starttime")+" 至 " +jb.getString("activity_endtime"));
//                        map.put("activity_addr", jb.getString("activity_addr"));
//                        map.put("activity_detail",jb.getString("activity_detail"));
//                        map.put("activity_img",jb.getString("activity_img"));
//                         //错误：不显示
//                        mHandler.obtainMessage(MSG_GET,map).sendToTarget();

                        Log.d("MAP:","--activity_time:"+activity_time+"-activity_addr:"+activity_addr+"-activity_detail:"+activity_detail+"-activity_img:"+activity_img);

//                        activityName.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                activityName.setText(activity_name);
//                            }
//                        });
//
                        detailActivityTime.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    detailActivityTime.setText(jb.getString("activity_starttime")+" 至 " +jb.getString("activity_endtime"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        detailActivityLocation.post(new Runnable() {
                            @Override
                            public void run() {
                                detailActivityLocation.setText(Constants.seller_address);
                            }
                        });

                        detailActivityDescri.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    detailActivityDescri.setText(jb.getString("activity_detail"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        downloadImage(jb.getString("activity_img"));

                        Log.d("activity_state", Integer.toString(state));
                        if(state == 0){
//                            Looper.prepare();
//                            Toast.makeText(ActivityDetailActivity.this,"成功",Toast.LENGTH_LONG).show();
//                            Looper.loop();
                        }else if(state == 1){
//                            Looper.prepare();
//                            Toast.makeText(ActivityDetailActivity.this,"失败",Toast.LENGTH_LONG).show();
//                            Looper.loop();
                        }
                    }
                } catch (Exception e) {
                    Log.d("downLoadDetails","wrong downLoadDetails");
                    e.printStackTrace();
                }
            }
        });
        dowmLoadDetails.start();
    }

    //删除活动，已完成
    private  void deleteActivity(){

        Thread deleteActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("deleteActivity",":in Thread");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.clear();
                param.add(new BasicNameValuePair("activity_id", activity_id));
                try {
                    String data = NetCore.postResulttoNet(Url.deleteActivity_9,param);
                    Log.d("delete_activity", data);
                    if(data!=null) {
                        JSONObject jb = new JSONObject(data);
                        String state = jb.getString("state");
                        if ("0".equals(state)){
                            Looper.prepare();
                            Toast.makeText(ActivityDetailActivity.this, "删除活动成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityDetailActivity.this,MainActivity.class);
                            startActivity(intent);
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(ActivityDetailActivity.this, "删除活动失败", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        deleteActivity.start();
    }

    private void downloadImage(String str) {
        Log.d("activity_down", "downloadImage");
        ImageLoader imageLoader = ImageLoader.getInstance();
        Log.d("imageLoader", "getInstance");
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
//        Log.d("imageLoader", str);
        imageLoader.displayImage(str, activityDetailImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.d("onLoadingComplete","String = "+s+",Bitmap="+bitmap);
                mHandler.obtainMessage(MSG_SUCCESS,bitmap).sendToTarget();//获取图片成功，向ui线程发送MSG_SUCCESS标识和bitmap对象
//                ((ImageView)view).setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    private void initView() {
//        showToast("initView", "initView");
        activityName = (TextView) findViewById(R.id.activity_detail_name);
        detailBack = (ImageView) findViewById(R.id.activity_detail_back);
        activityDetailImage = (ImageView)findViewById(R.id.activity_detail_image);
        detailActivityTime = (TextView) findViewById(R.id.activity_detail_time);
        detailActivityLocation = (TextView) findViewById(R.id.activity_detail_location);
        detailActivityDescri = (TextView) findViewById(R.id.activity_detail_descri);

        createQR = (Button) findViewById(R.id.createQR);
        deleteActivity = (Button) findViewById(R.id.delete_activity);

    }

    public void showView(){
        activityName.setText(activity_name);
        detailActivityTime.setText(activity_time);
        detailActivityLocation.setText(activity_addr);
        detailActivityDescri.setText(activity_detail);
    }

}
