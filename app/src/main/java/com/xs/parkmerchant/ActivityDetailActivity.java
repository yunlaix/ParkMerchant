package com.xs.parkmerchant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        activity_name = intent.getStringExtra("activity_name");

        downLoadDetails(activity_id);

        initView();

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
                intent.putExtra("activity_time",activity_time);
                startActivity(intent);
            }
        });

        deleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityDetailActivity.this, "删除活动", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void downLoadDetails(String activity_id){
        new Thread(new Runnable() {
            @Override
            public void run() {


            }
        });


    }

    private  void deleteActivity(){

    }

    private void downloadImage(){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(ActivityDetailActivity.this));
        imageLoader.displayImage(activity_img, activityDetailImage, options);
    }

    private void initView() {

        activityName = (TextView) findViewById(R.id.activity_detail_name);
        detailBack = (ImageView) findViewById(R.id.activity_detail_back);
        activityDetailImage = (ImageView)findViewById(R.id.activity_detail_image);
        detailActivityTime = (TextView) findViewById(R.id.activity_detail_time);
        detailActivityLocation = (TextView) findViewById(R.id.activity_detail_location);
        detailActivityDescri = (TextView) findViewById(R.id.activity_detail_descri);

        createQR = (Button) findViewById(R.id.createQR);
        deleteActivity = (Button) findViewById(R.id.delete_activity);

        downloadImage();
        activityName.setText(activity_name);
        detailActivityTime.setText(activity_time);
        detailActivityLocation.setText(seller_address);
        detailActivityDescri.setText(activity_detail);

    }
}
