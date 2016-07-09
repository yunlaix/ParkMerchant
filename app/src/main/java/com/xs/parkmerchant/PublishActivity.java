package com.xs.parkmerchant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.xs.parkmerchant.View.GetImage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    private static final String IMAGE_FILE_NAME = "faceImage.jpg";

    /* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;



    String[] ValueName = {"activity_name", "activity_start", "activity_end", "activity_details", "activity_imageurl"};

    String key,img_url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        SimpleDateFormat nowTime = new SimpleDateFormat();
        key = "activity_" + nowTime.format(new Date());


        initView();
//        getInfo();

        editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
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
                                //新建一个intent，用来获取相册中的内容，种类是image，然后startActivityForResult传入intent，和打开图像请求
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                galleryIntent.setType("image/*");//图片
                                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
                                break;

                            case 1:

                                //先检查SD卡是否能用，能用则new一个intent用来传入
                                if (isSdcardExisting()) {
                                    Intent cameraIntent = new Intent(
                                            "android.media.action.IMAGE_CAPTURE");//拍照
                                    //获取存储中image的存储路径getImageUri
                                    cameraIntent.putExtra
                                            (MediaStore.EXTRA_OUTPUT, getImageUri());
                                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                    //请求打开相机，这一句是为了给intent请求编号，cameraIntent才是处理打开相机请求的
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                } else {
                                    Toast.makeText(PublishActivity.this, "请插入sd卡", Toast.LENGTH_LONG)
                                            .show();
                                }
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                uploadImage();
            }
        });

        editActivityLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PublishActivity.this, "活动地址即为商家地址，不可更改！", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initView(){

        editBack = (ImageView) findViewById(R.id.edit_back);
        editPublish = (Button) findViewById(R.id.edit_publish);

        editActivityImage = (LinearLayout) findViewById(R.id.edit_activity_image);
        editDetailImage = (ImageView)findViewById(R.id.activity_detail_image);

        editActivityName = (EditText) findViewById(R.id.edit_activity_name);

        editActivityLocation = (TextView) findViewById(R.id.edit_activity_location);
        editActivityLocation.setText(Constants.seller_address);

        editActivityStart = (EditText) findViewById(R.id.edit_activity_starttime);
        editActivityEnd = (EditText) findViewById(R.id.edit_activity_endtime);
        editActivityDetails = (EditText) findViewById(R.id.edit_activity_details);

    }


    public void setToast(String str,String str1){
        Toast.makeText(PublishActivity.this, str + ":" + str1, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    Uri originalUri=data.getData();//获取图片uri
                    //编辑图片大小，裁切
                    resizeImage(originalUri);
                    //下面方法将获取的uri转为String类型哦！
                    String []imgs1={MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                    //Cursor光标，用来获取图片名称
                    Cursor cursor=this.managedQuery(originalUri, imgs1, null, null, null);
                    int index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    img_url =cursor.getString(index);
//                    upload(img_url);
                    break;
                case CAMERA_REQUEST_CODE:
                    if (isSdcardExisting()) {
                        resizeImage(getImageUri());
                        String []imgs={MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                        Cursor cursor1=this.managedQuery(getImageUri(), imgs, null, null, null);
                        int index1=cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor1.moveToFirst();
                        img_url=cursor1.getString(index1);
////                        upload(img_url1);
//                        //showToast(img_url1);
                    } else {
                        Toast.makeText(PublishActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                case RESIZE_REQUEST_CODE:
                    if (data != null) {
                        showResizeImage(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isSdcardExisting() {//判断SD卡是否存在
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void resizeImage(Uri uri) {//重塑图片大小
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可以裁剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    private void showResizeImage(Intent data) {//显示图片
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            editDetailImage.setImageDrawable(drawable);
        }
    }

    private Uri getImageUri() {//获取路径
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
    }

    /**
     * 获取editText传入的内容
     * */
    public void getInfo(){

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

    public void upload(final List<NameValuePair> param){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data=NetCore.postResulttoNet(Url.publishActivity, param);
                    JSONObject jb = new JSONObject(data);
                    String result = jb.getString("state");
                    int activity_id = jb.getInt("activity_id");
                    Log.v("上传成功","上传成功，state = "+ result +" activity_id = "+ activity_id);

                    if("1".equals(result)){
                        Toast.makeText(getApplication(),  "上传失败", Toast.LENGTH_LONG).show();
                    }else if("0".equals(result)) {
                        Toast.makeText(getApplication(),  "上传成功", Toast.LENGTH_LONG).show();
                        Intent toSetting = new Intent(PublishActivity.this,MainActivity.class);
                        toSetting.putExtra("activity_id", activity_id);
                        startActivity(toSetting);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
                    String state = jb.getString("state");
                    setToast("token + state", token + " " + state);

                    if(!"1".equals(state)) {
                        UploadManager imageLoader = new UploadManager();
                        imageLoader.put(img_url, key, token, new UpCompletionHandler() {
                            @Override
                            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                                if (responseInfo.isOK()) {
                                    ValueName[4] = Url.touxiang + key;

                                }
                            }
                        }, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
