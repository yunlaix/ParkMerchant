package com.xs.parkmerchant.Activity;

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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import com.xs.parkmerchant.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
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
    private TextView modify;
    private Button createQR;
    private Button deleteActivity;
    private String activity_img;
    private final int MSG_SUCCESS = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_SUCCESS:
                    detailActivityTime.setText(Constants.activity_starttime+" 至 " +Constants.activity_endttime);
                    detailActivityLocation.setText(Constants.seller_address);
                    detailActivityDescri.setText(Constants.activity_detail);
                    downloadImage(Constants.activity_img);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);
        Intent intent =  getIntent();
        Constants.activity_id = intent.getStringExtra("activity_id");
        Constants.activity_name = intent.getStringExtra("activity_name");
        initView();
        downLoadDetails();
        activityName.setText(Constants.activity_name);
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
                intent.putExtra("activity_id", Constants.activity_id);
                intent.putExtra("activity_name",Constants.activity_name);
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

    private void downLoadDetails(){
//        Log.d("downLoadDetails","downLoadDetails");
       new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("activity_id", Constants.activity_id));
//                Log.d("downLoadDetails",activity_id);

                try {
                    String data = NetCore.postResulttoNet(Url.activityDetail,param);
//                    Log.d("NetCoredata", data);
                    if(data != null) {
                        JSONObject jb = new JSONObject(data);
                        activity_img = Constants.activity_img = jb.getString("activity_img");
                        Constants.activity_starttime = jb.getString("activity_starttime").substring(0,10);
                        Constants.activity_endttime = jb.getString("activity_endtime").substring(0,10);
                        Constants.activity_detail = jb.getString("activity_detail");
                        mHandler.sendEmptyMessage(MSG_SUCCESS);
                    }
                } catch (Exception e) {
                    Log.d("downLoadDetails","wrong downLoadDetails");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //删除活动，已完成
    private  void deleteActivity(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("deleteActivity",":in Thread");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.clear();
                param.add(new BasicNameValuePair("activity_id", Constants.activity_id));
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
        }).start();
    }

    private void downloadImage(String str) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        imageLoader.displayImage(str, activityDetailImage, new ImageLoadingListener() {
            public void onLoadingStarted(String s, View view) {}
            public void onLoadingFailed(String s, View view, FailReason failReason) {}
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Constants.activity_bitmap = bitmap;
            }
            public void onLoadingCancelled(String s, View view) {}
        });
    }

    private void initView() {
        activityName = (TextView) findViewById(R.id.activity_detail_name);
        detailBack = (ImageView) findViewById(R.id.activity_detail_back);
        activityDetailImage = (ImageView)findViewById(R.id.activity_detail_image);
        detailActivityTime = (TextView) findViewById(R.id.activity_detail_time);
        detailActivityLocation = (TextView) findViewById(R.id.activity_detail_location);
        detailActivityDescri = (TextView) findViewById(R.id.activity_detail_descri);
        modify = (TextView) findViewById(R.id.modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PublishActivity.class);
                intent.putExtra("source", "ActivityDetailActivity");
                startActivity(intent);
            }
        });

        createQR = (Button) findViewById(R.id.createQR);
        deleteActivity = (Button) findViewById(R.id.delete_activity);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Constants.isPublished){
            activityName.setText(Constants.activity_name);
            detailActivityTime.setText(Constants.activity_starttime+" 至 " +Constants.activity_endttime);
            detailActivityLocation.setText(Constants.seller_address);
            detailActivityDescri.setText(Constants.activity_detail);
            if(!activity_img.equals(Constants.activity_img)){
                activityDetailImage.setImageBitmap(Constants.activity_bitmap);
                activity_img = Constants.activity_img;
            }
        }
    }
}
