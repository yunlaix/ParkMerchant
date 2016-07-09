package com.xs.parkmerchant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


    private File file;
    private String filepath;
    private final String [] methods = {"从图库", "从拍照"};

    private final int REQUEST_CODE_CHOOSE_IMAGE = 1;
    private final int REQUEST_CODE_CROP_IMAGE = 3;
    private final int REQUEST_CODE_TAKE_PHOTO = 2;

    String[] ValueName = {"activity_name", "activity_start", "activity_end", "activity_details", "activity_imageurl"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        filepath = Environment.getExternalStorageState() + "/MyActivity.jpg";
        file = new File(filepath);

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
                new AlertDialog.Builder(PublishActivity.this).setTitle("选择活动图片").setItems(methods, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                                gallery.addCategory(Intent.CATEGORY_OPENABLE);
                                gallery.setType("image/*");
                                startActivityForResult(gallery, REQUEST_CODE_CHOOSE_IMAGE);
                                break;
                            case 1:
                                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                    Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "activity.jpg")));
                                    camera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                    startActivityForResult(camera, REQUEST_CODE_TAKE_PHOTO);
                                }else{
                                        Toast.makeText(PublishActivity.this, "SD卡获取不到", Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
//                uploadImage();
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
        setToast("requestCode", Integer.toString(requestCode));
        setToast("resultCode", Integer.toString(resultCode));
        if(resultCode != RESULT_OK){
            return;
        }else{
            switch (requestCode) {
                // 将拍摄的照片进行裁剪(注意，这里需要传递的是照片的路径，而不是intent.getData(), 因为intent.getData()返回的是缩略图的数据)
                case REQUEST_CODE_TAKE_PHOTO:
                    Uri uri_photo = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "activity.jpg"));
                    setToast("take photo",uri_photo.toString());
                    startCropImage(uri_photo);
                    setToast("startCropImage","success");
                    break;
                // 将选择的图片进行裁剪
                case REQUEST_CODE_CHOOSE_IMAGE:

                    if (data.getData() != null) {
                        Uri iconUri = data.getData();
                        startCropImage(iconUri);
                    }
                    break;
                // 将裁剪后的图片进行上传
                case REQUEST_CODE_CROP_IMAGE:
                    // 上传图片操作
                    setToast("mine","sssssssssssssssss"+data.getData());
                    if(resultCode == RESULT_OK){
//                        Toast.makeText(PublishActivity.this, "裁剪失败", Toast.LENGTH_LONG).show();
//                    }else{
                        setImageToHeadView(data);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImageToHeadView(Intent data){
        setToast("setImageToHeadView",data.getStringExtra("uri"));
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            editDetailImage.setImageDrawable(drawable);
            saveBitMap(photo);
        }
    }

    private void saveBitMap(Bitmap bitmap){
        if(file.exists()) file.delete();
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startCropImage(Uri uri) {//重塑图片大小
        setToast("startCropImage", uri.toString()+"is running");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可以裁剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        setToast("startCropImage","over");

    }

//    private void startCropImage(Uri uri) {
//        setToast("startCropImage","is running");
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // 使图片处于可裁剪状态
//        intent.putExtra("crop", "true");
//        // 裁剪框的比例（根据需要显示的图片比例进行设置）
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // 让裁剪框支持缩放
//        intent.putExtra("scale", true);
//        // 裁剪后图片的大小（注意和上面的裁剪比例保持一致）
//        intent.putExtra("outputX", dip2px(this, 80));
//        intent.putExtra("outputY", dip2px(this, 80));
//        // 传递原图路径
//
////        File cropFile = new File(Environment.getExternalStorageDirectory() + "photo.JPG");
////        Uri cropImageUri = Uri.fromFile(cropFile);
////        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
//        // 设置裁剪区域的形状，默认为矩形，也可设置为原形
//        intent.putExtra("circleCrop", "true");
//        // 设置图片的输出格式
////        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
//
//        intent.putExtra("return-data", true);//true
////        intent.putExtra("noFaceDetection", true);
//        setToast();
//        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
//
//    }

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



    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
                    Log.v("token + state", token + " " + state);

                    if(!"1".equals(state)) {
                        UploadManager imageLoader = new UploadManager();
                        SimpleDateFormat nowTime = new SimpleDateFormat();
                        final String key = "activity_" + nowTime.format(new Date());

                        imageLoader.put(filepath, key, token, new UpCompletionHandler() {
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
;