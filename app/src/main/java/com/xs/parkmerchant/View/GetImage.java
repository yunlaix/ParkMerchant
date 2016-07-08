package com.xs.parkmerchant.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import java.io.File;

/**
 * Created by Man on 2016/7/8.
 */
public class GetImage {

    public GetImage(){

    }

    public static Intent getFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.JPG")));
        return intent;
    }

    public static Intent getFromLocal(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
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
//        intent.putExtra("outputX", dip2px(this, 80));
//        intent.putExtra("outputY", dip2px(this, 80));
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
//        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }
}
