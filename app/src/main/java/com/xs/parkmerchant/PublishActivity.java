package com.xs.parkmerchant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Man on 2016/7/5.
 */
public class PublishActivity extends AppCompatActivity{
    private ImageView editBack;
    private Button editPublish;
    private LinearLayout editActivityImage;
    private ImageView editDetailImage;
    private EditText editActivityName;
    private TextView editActivityLocation;
    private EditText editActivityStart;
    private EditText editActivityEnd;
    private EditText editActivityDetails;
    private final String [] methods = {"从图库", "从拍照"};
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;
    private String[] ValueName = {"activity_name", "activity_start", "activity_end", "activity_details", "activity_imageurl"};
    private String key;
    private Bitmap bitmap_photo;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Log.d("publish_result", "fffffffffffffff");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
        editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        /**
         * 上传图片
         * */
        editActivityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //为Dialog加监听，setItems给items加监听，setNavigation给导航键，如“取消”加监听
                new AlertDialog.Builder(PublishActivity.this).setTitle("上传头像").setItems(methods, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i){
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo1.JPG")));
                                startActivityForResult(intent1, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        editActivityLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PublishActivity.this, "活动地址即为商家地址，不可更改！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView(){
        editBack = (ImageView) findViewById(R.id.edit_back);
        editPublish = (Button) findViewById(R.id.edit_publish);
        editActivityImage = (LinearLayout) findViewById(R.id.edit_activity_image);
        editDetailImage = (ImageView)findViewById(R.id.detail_image);
        editActivityName = (EditText) findViewById(R.id.edit_activity_name);
        editActivityLocation = (TextView) findViewById(R.id.edit_activity_location);
        editActivityLocation.setText(Constants.seller_address);
        editActivityStart = (EditText) findViewById(R.id.edit_activity_starttime);
        editActivityEnd = (EditText) findViewById(R.id.edit_activity_endtime);
        editActivityDetails = (EditText) findViewById(R.id.edit_activity_details);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null || requestCode == CAMERA_REQUEST_CODE) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    if (data.getData() != null) {
                        Uri iconUri = data.getData();
                        resizeImage(iconUri);
                    }
                    break;
                case CAMERA_REQUEST_CODE:
                    Uri uri_photo = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo1.JPG"));
                    resizeImage(uri_photo);
                    break;
                case RESIZE_REQUEST_CODE:
                    if (data != null) showResizeImage(data);
                    break;
            }
        }

    }

    public void resizeImage(Uri uri) {//重塑图片大小
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可以裁剪
        intent.putExtra("aspectX", 15);
        intent.putExtra("aspectY", 10);
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 225);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    private void showResizeImage(Intent data) {//显示图片
        Bundle extras = data.getExtras();
        if (extras != null) {
            bitmap_photo = data.getParcelableExtra("data");
            editDetailImage.setImageBitmap(bitmap_photo);
        }
    }

    private void getInfo(){
        ValueName[0] = editActivityName.getText().toString().trim();
        ValueName[1] = editActivityStart.getText().toString().trim();
        ValueName[2] = editActivityEnd.getText().toString().trim();
        ValueName[3] = editActivityDetails.getText().toString().trim();

        if("".equals(ValueName[0])||"".equals(ValueName[1])||"".equals(ValueName[2])||"".equals(ValueName[3])||"".equals(ValueName[4])){
            Toast.makeText(this,  "填写信息不能为空", Toast.LENGTH_LONG).show();
        }
        else {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("seller_id", Constants.seller_id));
            param.add(new BasicNameValuePair("activity_name", ValueName[0]));
            param.add(new BasicNameValuePair("activity_starttime", ValueName[1]));
            param.add(new BasicNameValuePair("activity_endtime", ValueName[2]));
            param.add(new BasicNameValuePair("activity_detail", ValueName[3]));
            param.add(new BasicNameValuePair("activity_img", ValueName[4]));
            upload(param);
        }
    }

    private void upload(final List<NameValuePair> param){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data=NetCore.postResulttoNet(Url.publishActivity, param);
                    JSONObject jb = new JSONObject(data);
                    String result = jb.getString("state");
                    int activity_id = jb.getInt("activity_id");
                    Log.v("上传成功","上传成功，state = "+ result +" activity_id = "+ activity_id);
                    Looper.prepare();
                    if("1".equals(result)){
                        Toast.makeText(getApplicationContext(),  "上传失败", Toast.LENGTH_LONG).show();
                    }else if("0".equals(result)) {
                        Toast.makeText(getApplicationContext(),  "上传成功!", Toast.LENGTH_LONG).show();
                        Constants.isPublished = true;
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void uploadImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                try {
                    String date = NetCore.postResulttoNet(Url.upload_img,params);
                    JSONObject jb = new JSONObject(date);
                    String token = jb.getString("uptoken");
                    SimpleDateFormat nowTime = new SimpleDateFormat("yyyyMMddHHmmss");
                    key = "activity_" + nowTime.format(new Date());
                    UploadManager imageLoader = new UploadManager();
                    imageLoader.put(Bitmap2Bytes(bitmap_photo), key, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                            if (responseInfo.isOK()) {
                                ValueName[4] = Url.touxiang + key;
                                Log.d("publish_img", "A"+ValueName[4]);
                                //start update database
                                getInfo();
                            }else{
                                handler.sendEmptyMessage(1);
                            }
                        }
                    }, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }

            }
        }).start();
    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
