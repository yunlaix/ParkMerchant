package com.xs.parkmerchant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
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

import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import com.xs.parkmerchant.View.GetImage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

    private boolean isUpload = false;
    private final int REQUEST_CODE_CHOOSE_IMAGE = 1;
    private final int REQUEST_CODE_CROP_IMAGE = 2;
    private final int REQUEST_CODE_TAKE_PHOTO = 3;
    private boolean isFinished;

//    String activity_name, activity_location, activity_start, activity_end, activity_details, activity_imageurl;
    String[] ValueName = {"activity_name", "activity_start", "activity_end", "activity_details", "activity_imageurl"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        filepath = Environment.getExternalStorageState() + "/activity.jpg";
        file = new File(filepath);

        initView();
        getInfo();

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
                new AlertDialog.Builder(PublishActivity.this).setTitle("选择头像").setItems(methods, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent gallery = GetImage.getFromLocal();
                                startActivityForResult(gallery,REQUEST_CODE_CHOOSE_IMAGE);
                                break;
                            case 1:
                                Intent camera = GetImage.getFromCamera();
                                startActivityForResult(camera,REQUEST_CODE_CHOOSE_IMAGE);
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
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

    public void initView(){

        editBack = (ImageView) findViewById(R.id.edit_back);
        editPublish = (Button) findViewById(R.id.edit_publish);

        editActivityImage = (LinearLayout) findViewById(R.id.edit_activity_image);
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
        if(data!=null || requestCode == REQUEST_CODE_TAKE_PHOTO){
            switch (requestCode) {
                // 将拍摄的照片进行裁剪(注意，这里需要传递的是照片的路径，而不是intent.getData(), 因为intent.getData()返回的是缩略图的数据)
                case REQUEST_CODE_TAKE_PHOTO:
                    Uri uri_photo = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.JPG"));
                    startCropImage(uri_photo);
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
                    Log.d("mine","sssssssssssssssss"+data.getData());
                    if(data!=null) setImageToHeadView(data);
                    break;
                default:
                    break;

            }
        }
    }

    private void setImageToHeadView(Intent data){
        Bundle bundle = data.getExtras();
        if(bundle!=null){
            Bitmap bitmap = data.getParcelableExtra("data");
//            addImage.setImageBitmap(bitmap);
            saveBitMap(bitmap);
        }
    }

    private void saveBitMap(Bitmap bitmap){
        if(file.exists()) file.delete();
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)){
                fileOutputStream.flush();
                fileOutputStream.close();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 使图片处于可裁剪状态
        intent.putExtra("crop", "true");
        // 裁剪框的比例（根据需要显示的图片比例进行设置）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 让裁剪框支持缩放
        intent.putExtra("scale", true);
        // 裁剪后图片的大小（注意和上面的裁剪比例保持一致）
        intent.putExtra("outputX", dip2px(this, 80));
        intent.putExtra("outputY", dip2px(this, 80));
        // 传递原图路径

//        File cropFile = new File(Environment.getExternalStorageDirectory() + "photo.JPG");
//        Uri cropImageUri = Uri.fromFile(cropFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        // 设置裁剪区域的形状，默认为矩形，也可设置为原形
        intent.putExtra("circleCrop", "true");
        // 设置图片的输出格式
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败

        intent.putExtra("return-data", true);//true


        // 是否需要人脸识别
//        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    /**
     * 获取editText传入的内容，并存储到hashmap里面去
     * */
    public void getInfo(){

        ValueName[0] = editActivityName.getText().toString().trim();
        ValueName[1] = editActivityStart.getText().toString().trim();
        ValueName[2] = editActivityEnd.getText().toString().trim();
        ValueName[3] = editActivityDetails.getText().toString().trim();
//        ValueName[4] = getImageUrl();

        if("".equals(ValueName[0])||"".equals(ValueName[1])||"".equals(ValueName[2])||"".equals(ValueName[3])||"".equals(ValueName[4])){
            Toast.makeText(this,  "填写信息不能为空", Toast.LENGTH_LONG).show();
        }
        else {

            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("seller_id", Constants.seller_id));
            param.add(new BasicNameValuePair("activity_name", ValueName[0]));
            param.add(new BasicNameValuePair("activity_starttime", ValueName[1]));
            param.add(new BasicNameValuePair("activity_endtime", ValueName[2]));
            param.add(new BasicNameValuePair("activity_", ValueName[3]));
            param.add(new BasicNameValuePair("activity_name", ValueName[4]));

            upload(param);
        }
    }

    public void upload(List<NameValuePair> param){
        NetCore string=new NetCore();
        try {
            String data=string.postResulttoNet("http://139.129.24.127/parking_app/Seller/seller_add_activity.php", param);
            data = data.substring(0, 1);
            int result = Integer.parseInt(data);
            switch (result){
                case 0:
                    Toast.makeText(getApplication(),  "上传失败", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplication(),  "上传成功", Toast.LENGTH_LONG).show();
                    Intent toSetting = new Intent(PublishActivity.this,MainActivity.class);
                    startActivity(toSetting);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
