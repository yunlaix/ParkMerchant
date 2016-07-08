package com.xs.parkmerchant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Man on 2016/7/5.
 */
public class MineActivity extends AppCompatActivity {

    private ImageView back;
    private ImageView logout;
    private ImageView addImage;
    private TextView bussName;
    private TextView bussAddr;
    private TextView bussTel;
    private DisplayImageOptions options;
    private File file;
    private String filepath;

    private boolean isUpload = false;
    private final int REQUEST_CODE_CHOOSE_IMAGE = 1;
    private final int REQUEST_CODE_CROP_IMAGE = 2;
    private final int REQUEST_CODE_TAKE_PHOTO = 3;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Log.d("handler", "sssssssssssssssss");
            }else if(msg.what==2){
                if(file.exists()) file.delete();
                Log.d("handler", "fffffffffffffffff");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        filepath = Environment.getExternalStorageDirectory()+"/seller_img.PNG";
        file = new File(filepath);//Environment.getExternalStorageDirectory(), "seller_img.PNG"
        initView();

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MineActivity.this, "注销登陆", Toast.LENGTH_LONG).show();
            }
        });

        addImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MineActivity.this, "添加头像", Toast.LENGTH_LONG).show();
                getFromLocal();
//                getFromCamera();
            }
        });

    }

    private void getFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.JPG")));
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    private void getFromLocal(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
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
            addImage.setImageBitmap(bitmap);
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
                uploadImg(Url.upload_img, filepath, "seller_"+simpleDateFormat.format(new Date()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadImg(final String token_url, final String data, final String key){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    String result = NetCore.postResulttoNet(token_url, params);
                    JSONObject jsonObject = new JSONObject(result);
                    String token = jsonObject.getString("uptoken");
                    Log.d("upload", "result"+result);
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(data, key, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                            Log.d("upload", "D"+s+"D"+responseInfo.isOK()+"D"+responseInfo+"D"+jsonObject);
                            isUpload = responseInfo.isOK();
                            if(isUpload) {
                                //update database
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Constants.seller_img = Url.touxiang + key;
                                            List<NameValuePair> paramsx = new ArrayList<NameValuePair>();
                                            paramsx.clear();
                                            paramsx.add(new BasicNameValuePair("seller_id", Constants.seller_id));
                                            paramsx.add(new BasicNameValuePair("seller_img", Constants.seller_img));
                                            paramsx.add(new BasicNameValuePair("seller_name", Constants.seller_name));
                                            paramsx.add(new BasicNameValuePair("seller_address", Constants.seller_address));
                                            paramsx.add(new BasicNameValuePair("seller_contact", Constants.seller_contact));
                                            String resultx = NetCore.postResulttoNet(Url.modify_11, paramsx);
                                            JSONObject jsonObjectx = new JSONObject(resultx);
                                            if (jsonObjectx.getString("state").equals("0")) {
                                                handler.sendEmptyMessage(1);
                                            } else {
                                                handler.sendEmptyMessage(2);
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }else{
                                handler.sendEmptyMessage(2);//
                            }
                        }
                    }, null);
                }catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();
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

    public void initView(){
        back = (ImageView)findViewById(R.id.mine_back);
        logout = (ImageView)findViewById(R.id.logout);
        addImage = (ImageView) findViewById(R.id.add_image);

        bussName = (TextView) findViewById(R.id.mine_buss_name);
        bussAddr = (TextView) findViewById(R.id.mine_buss_addr);
        bussTel = (TextView) findViewById(R.id.mine_buss_tel);
        bussName.setText(Constants.seller_name);
        bussAddr.setText(Constants.seller_address);
        bussTel.setText(Constants.seller_contact);
        Log.d("mine", "A"+Constants.seller_img+"A"+Constants.seller_id);
        //img
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.add_image)
                .showImageForEmptyUri(R.mipmap.add_image)
                .showImageOnFail(R.mipmap.add_image)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        if(file.exists()){
            imageLoader.displayImage("file:///mnt/sdcard/seller_img.PNG", addImage);
        }else {
            imageLoader.displayImage(Constants.seller_img, addImage, options);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
