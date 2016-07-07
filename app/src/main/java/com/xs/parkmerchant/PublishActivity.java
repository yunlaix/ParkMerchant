package com.xs.parkmerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Man on 2016/7/5.
 */
public class PublishActivity extends AppCompatActivity{
    private ImageView editBack;
    private Button editPublish;

    private LinearLayout editActivityImage;

    private EditText editActivityName;
    private TextView editActivityLocation;
    private EditText editActivityStart;
    private EditText editActivityEnd;
    private EditText editActivityDetails;

    private boolean isFinished;

//    String activity_name, activity_location, activity_start, activity_end, activity_details, activity_imageurl;
    String[] ValueName = {"activity_name", "activity_location", "activity_start", "activity_end", "activity_details", "activity_imageurl"};

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

        /**
         * 上传图片
         * */
        editActivityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    /**
     * 获取editText传入的内容，并存储到hashmap里面去
     * */
    public void getInfo(){

        ValueName[0] = editActivityName.getText().toString().trim();
        ValueName[1] = editActivityLocation.getText().toString().trim();
        ValueName[2] = editActivityStart.getText().toString().trim();
        ValueName[3] = editActivityEnd.getText().toString().trim();
        ValueName[4] = editActivityDetails.getText().toString().trim();
        ValueName[5] = getImageUrl();

        if("".equals(ValueName[0])||"".equals(ValueName[1])||"".equals(ValueName[2])||"".equals(ValueName[3])||"".equals(ValueName[4])||"".equals(ValueName[5])){
            Looper.prepare();
            Toast.makeText(this,  "填写信息不能为空", Toast.LENGTH_LONG).show();
            Looper.loop();
        }
        else {
            NetCore string=new NetCore();
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("seller_id", Constants.seller_id));
            param.add(new BasicNameValuePair("activity_name", ValueName[0]));
            param.add(new BasicNameValuePair("activity_name", ValueName[1]));
            param.add(new BasicNameValuePair("activity_name", ValueName[2]));
            param.add(new BasicNameValuePair("activity_name", ValueName[3]));
            param.add(new BasicNameValuePair("activity_name", ValueName[4]));
            param.add(new BasicNameValuePair("activity_name", ValueName[5]));

            try {
                String data=string.postResulttoNet("http://1.miac.sinaapp.com/csireg.php", param);
                data = data.substring(0, 1);
                int result = Integer.parseInt(data);
                switch (result){
                    case 0:
                        Looper.prepare();
                        Toast.makeText(getApplication(),  "上传失败", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        break;
                    case 1:
                        Intent toSetting = new Intent(PublishActivity.this,MainActivity.class);
                        startActivity(toSetting);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

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

    public String getImageUrl(){
        String imageurl = "imageurl";

        return imageurl;
    }

}
