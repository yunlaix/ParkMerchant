package com.xs.parkmerchant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Man on 2016/7/5.
 */
public class PublishActivity extends AppCompatActivity{
    private ImageView editBack;
    private Button editPublish;

    private LinearLayout editActivityImage;

    private EditText editActivityName;
    private EditText editActivityLocation;
    private EditText editActivityStart;
    private EditText editActivityEnd;
    private EditText editActivityDetails;

    private boolean isFinished;

    String activity_name,activity_location, activity_start,activity_end,activity_details;
    String[] ValueName = {activity_name,activity_location, activity_start,activity_end,activity_details};
    /**
     * 存储上传信息的键值对
     * */
    HashMap<String, String> map = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

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
                if(checkFinished()){
                    updateActivity();
                    Toast.makeText(PublishActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PublishActivity.this, "失败", Toast.LENGTH_LONG).show();
                }
            }
        });

        editActivityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void initView(){

        editBack = (ImageView) findViewById(R.id.edit_back);
        editPublish = (Button) findViewById(R.id.edit_publish);

        editActivityImage = (LinearLayout) findViewById(R.id.edit_activity_image);
        editActivityName = (EditText) findViewById(R.id.edit_activity_name);
        editActivityLocation = (EditText) findViewById(R.id.edit_activity_location);
        editActivityStart = (EditText) findViewById(R.id.edit_activity_starttime);
        editActivityEnd = (EditText) findViewById(R.id.edit_activity_endtime);
        editActivityDetails = (EditText) findViewById(R.id.edit_activity_details);

    }

    /**
     * 获取editText传入的内容，并存储到hashmap里面去
     * */
    public void getInfo(){
        map = new HashMap<String, String>();

        activity_name = editActivityName.getText().toString().trim();
        activity_location = editActivityLocation.getText().toString().trim();
        activity_start = editActivityStart.getText().toString().trim();
        activity_end = editActivityEnd.getText().toString().trim();
        activity_details = editActivityDetails.getText().toString().trim();

        map.put(ValueName[0], activity_name);
        map.put(ValueName[1], activity_location);
        map.put(ValueName[2], activity_start);
        map.put(ValueName[3], activity_end);
        map.put(ValueName[4], activity_details);

    }

    /***
     * 为什么没用？
     * **/
//    public void onClick(View v){
//        switch(v.getId()){
//            case R.id.edit_back:
//                finish();
//                break;
//            case R.id.edit_publish:
//                if(checkFinished()){
//                    updateActivity();
//                    Toast.makeText(this, "提交成功", Toast.LENGTH_LONG).show();
//                }else{
//                    Toast.makeText(this, "失败", Toast.LENGTH_LONG).show();
//                }
//
//                break;
//        }
//    }

    /**
     * 检查活动内容是否填写完整
     * */
    public boolean checkFinished(){

        isFinished = true;

        return isFinished;

    }

    /**
     * 上传新增活动的信息
     * */
    public void updateActivity(){


    }

}
