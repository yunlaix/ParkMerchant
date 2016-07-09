package com.xs.parkmerchant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private String activity_img, activity_id, activity_name,activity_starttime, activity_endtime, seller_address,activity_detail,activity_time;
    private DisplayImageOptions options;

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
        downloadImage();

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
//        showToast("downLoadDetails","downLoadDetails");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("activity_id", activity_id));
                Log.d("downLoadDetails",activity_id);

                try {
                    String data = NetCore.postResulttoNet(Url.activityDetail,param);
                    Log.d("NetCoredata", data);
                    if(data != null){

                        JSONObject jb = new JSONObject(data);
                        int state = jb.getInt("state");
                        activity_img = jb.getString("activity_img");
                        activityName.setText(activity_name);
                        detailActivityTime.setText(jb.getString("activity_starttime")+" 至 " +jb.getString("activity_endtime"));
                        detailActivityLocation.setText(jb.getString("seller_address"));
                        detailActivityDescri.setText(jb.getString("activity_detail"));

                        Log.d("activity_state", Integer.toString(state));
                        if(state == state){
                            Toast.makeText(ActivityDetailActivity.this,"成功",Toast.LENGTH_LONG).show();

                        }else if(state == 1){
                            Toast.makeText(ActivityDetailActivity.this,"失败",Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private  void deleteActivity(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("activity_id", activity_id));
                try {
                    String data = NetCore.postResulttoNet(Url.activityDetail,param);
                    if(data!=null && !data.equals("")) {
                        JSONObject jb = new JSONObject(data);
                        String state = jb.getString("state");
                        if ("0".equals(state)){
                            Toast.makeText(ActivityDetailActivity.this, "删除活动成功", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(ActivityDetailActivity.this, "删除活动失败", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void downloadImage() {
        Log.d("activity_down", "downloadImage");
        ImageLoader imageLoader = ImageLoader.getInstance();
        Log.d("imageLoader", "getInstance");
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        Log.d("imageLoader", "init");
        imageLoader.displayImage(activity_img, activityDetailImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.d("onLoadingComplete","String = "+s+",Bitmap="+bitmap);
                ((ImageView)view).setImageBitmap(bitmap);
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
}
