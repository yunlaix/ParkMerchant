package com.xs.parkmerchant.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import com.xs.parkmerchant.R;

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
 * 个人信息维护
 * 包括修改头像、地址、名称、联系方式和密码
 */
public class MineActivity extends AppCompatActivity {

    private ImageView back;
    private ImageView logout;
    private ImageView addImage;
    private EditText bussName;
    private EditText bussAddr;
    private EditText bussAddrDetail;
    private EditText bussTel;
    private TextView modify_info, modify_password;
    private DisplayImageOptions options;
    private File file;
    private String filepath;
    private final String [] methods = {"从图库", "从拍照"};
    private String name, address, contact;

    private boolean isUpload = false, isModifyOn = false;
    private final int REQUEST_CODE_CHOOSE_IMAGE = 1;
    private final int REQUEST_CODE_CROP_IMAGE = 2;
    private final int REQUEST_CODE_TAKE_PHOTO = 3;
    private final int REQUEST_CAMERA = 4;
    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                sharedPreferences = getSharedPreferences("login_parkmerchant", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("seller_img", Constants.seller_img).commit();
                isUpload = false;
            }else if(msg.what==2){
                if(file.exists()) file.delete();
                isUpload = false;
            }else if(msg.what==3){//modify success
                bussName.setEnabled(false);
                bussAddr.setEnabled(false);
                bussTel.setEnabled(false);
                modify_info.setText("修改信息");
                Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                isModifyOn = false;
            }else if(msg.what==4){//modify fail
                Toast.makeText(getApplicationContext(), "修改失败！", Toast.LENGTH_SHORT).show();
                isModifyOn = false;
            }
            if(!Constants.isNetWorkConnected(getApplicationContext()))Toast.makeText(getApplicationContext(), "网络无连接", Toast.LENGTH_SHORT).show();
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
            public void onClick(View view) {logOut();}
        });
        addImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMethod();
            }
        });
    }

    private void logOut(){
        sharedPreferences = getSharedPreferences("login_parkmerchant", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
        if(file.exists()) file.delete();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void selectMethod(){
        new AlertDialog.Builder(MineActivity.this).setTitle("选择头像").setItems(methods, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        getFromLocal();
                        break;
                    case 1:
                        getAppVersionAndTakePermission();
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

    private void getAppVersionAndTakePermission(){
        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= 23){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }else{
                getFromCamera();
            }
        }else {
            getFromCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CAMERA){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getFromCamera();
            }else {
                Toast.makeText(getApplicationContext(), "摄像头权限拒绝", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                case REQUEST_CODE_TAKE_PHOTO:
                    Uri uri_photo = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.JPG"));
                    startCropImage(uri_photo);//not intent.getData()
                    break;
                case REQUEST_CODE_CHOOSE_IMAGE:
                    if (data.getData() != null) {
                        Uri iconUri = data.getData();
                        startCropImage(iconUri);
                    }
                    break;
                case REQUEST_CODE_CROP_IMAGE:
                    if(data!=null) setImageToHeadView(data);
                    break;
            }
        }
    }

    private void setImageToHeadView(Intent data){
        Bundle bundle = data.getExtras();
        if(bundle!=null){
            Bitmap bitmap = data.getParcelableExtra("data");
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(bitmap.getWidth()/2);
            roundedBitmapDrawable.setAntiAlias(true);
            addImage.setImageDrawable(roundedBitmapDrawable);
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
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//to be modified
                uploadImg(Url.refresh_img, filepath, "seller");
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
                    params.add(new BasicNameValuePair("key", key));
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
                                            paramsx.add(new BasicNameValuePair("seller_address", Constants.seller_address+"%"+Constants.seller_address_detail));
                                            paramsx.add(new BasicNameValuePair("seller_contact", Constants.seller_contact));
                                            paramsx.add(new BasicNameValuePair("seller_location_j", ""+Constants.addr_lan));
                                            paramsx.add(new BasicNameValuePair("seller_location_w", ""+Constants.addr_lon));
                                            String resultx = NetCore.postResulttoNet(Url.modify_11, paramsx);Log.d("mine", resultx);
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
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
//        intent.putExtra("circleCrop", "true");
        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    public void initView(){
        back = (ImageView)findViewById(R.id.mine_back);
        logout = (ImageView)findViewById(R.id.logout);
        addImage = (ImageView) findViewById(R.id.add_image);
        bussName = (EditText) findViewById(R.id.mine_buss_name);
        bussAddr = (EditText) findViewById(R.id.mine_buss_addr);
        bussAddrDetail = (EditText) findViewById(R.id.mine_buss_addr_detail);
        bussAddr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PickAddressActivity.class);
                startActivity(intent);
            }
        });
        bussTel = (EditText) findViewById(R.id.mine_buss_tel);
        modify_info = (TextView) findViewById(R.id.modify_info);
        modify_info.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modify_info.getText().equals("修改信息")){//editable
                    bussName.setEnabled(true);
                    bussAddr.setEnabled(true);
                    bussTel.setEnabled(true);
                    modify_info.setText("确认修改");
                }else{//confirm and update
                    if(isModifyOn) {
                        Toast.makeText(getApplicationContext(), "请等待当前操作完成！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isModifyOn = true;
                    modifyInfo();
                }
            }
        });
        modify_password = (TextView) findViewById(R.id.modify_password);
        modify_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });
        bussName.setText(Constants.seller_name);
        bussAddr.setText(Constants.seller_address);
        bussAddrDetail.setText(Constants.seller_address_detail);
        bussTel.setText(Constants.seller_contact);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.add_image)
                .showImageForEmptyUri(R.mipmap.add_image)
                .showImageOnFail(R.mipmap.add_image)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        if(file.exists()){
            imageLoader.displayImage("file:///mnt/sdcard/seller_img.PNG", addImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {}

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {}

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(bitmap.getWidth()/2);
                    roundedBitmapDrawable.setAntiAlias(true);
                    ((ImageView)view).setImageDrawable(roundedBitmapDrawable);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {}
            });
        }else {
            if(Constants.seller_img.equals("")) return;
            imageLoader.displayImage(Constants.seller_img, addImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {}

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {}

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Log.d("mine", "#"+Constants.seller_img+" "+bitmap);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(bitmap.getWidth()/2);
                    roundedBitmapDrawable.setAntiAlias(true);
                    ((ImageView)view).setImageDrawable(roundedBitmapDrawable);
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

                @Override
                public void onLoadingCancelled(String s, View view) {}
            });
        }
    }

    private void modifyInfo(){
        name = bussName.getText().toString().trim();
        address = bussAddr.getText().toString().trim();
        contact = bussTel.getText().toString().trim();
        if(name.equals("")||address.equals("")||contact.equals("")){
            isModifyOn = false;
            Toast.makeText(getApplicationContext(), "请完善信息！", Toast.LENGTH_SHORT).show();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("seller_id", Constants.seller_id));
                        params.add(new BasicNameValuePair("seller_img", Constants.seller_img));
                        params.add(new BasicNameValuePair("seller_name", name));
                        params.add(new BasicNameValuePair("seller_address", Constants.seller_address+"%"+Constants.seller_address_detail));
                        params.add(new BasicNameValuePair("seller_contact", Constants.seller_contact));
                        params.add(new BasicNameValuePair("seller_location_j", ""+Constants.addr_lan));
                        params.add(new BasicNameValuePair("seller_location_w", ""+Constants.addr_lon));
                        String result = NetCore.postResulttoNet(Url.modify_11, params);
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getString("state").equals("0")){
                            Constants.seller_name = name;
                            Constants.seller_address = address;
                            Constants.seller_contact = contact;
                            handler.sendEmptyMessage(3);
                        }else{
                            handler.sendEmptyMessage(4);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        handler.sendEmptyMessage(4);
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Constants.isPicked){
            Constants.isPicked = false;
            bussAddr.setText(Constants.seller_address);
        }
    }

}