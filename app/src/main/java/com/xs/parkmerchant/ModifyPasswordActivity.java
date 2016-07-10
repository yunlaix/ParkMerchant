package com.xs.parkmerchant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu on 2016/7/10.
 */
public class ModifyPasswordActivity extends AppCompatActivity{

    private ImageView back;
    private EditText passOld, passNew1, passNew2;
    private TextView confirm;
    private String passold, passnew1, passnew2;
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                SharedPreferences sharedPreferences = getSharedPreferences("login_parkmerchant", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("seller_password", passnew1);
                editor.commit();
                Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                finish();
            }else if(msg.what==2){
                Toast.makeText(getApplicationContext(), "修改失败！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        passNew1 = (EditText)findViewById(R.id.password_new1);
        passNew2 = (EditText)findViewById(R.id.password_new2);
        passOld = (EditText)findViewById(R.id.password_old);
        confirm = (TextView)findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passold = passOld.getText().toString().trim();
                passnew1 = passNew1.getText().toString().trim();
                passnew2 = passNew2.getText().toString().trim();
                if(passold.equals("")||passnew1.equals("")||passnew2.equals("")){
                    Toast.makeText(getApplicationContext(), "密码不能为空！", Toast.LENGTH_SHORT).show();
                }else if(!passnew2.equals(passnew1)){
                    Toast.makeText(getApplicationContext(), "两次密码不一致！", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("seller_id", Constants.seller_id));
                                params.add(new BasicNameValuePair("seller_oldpassword", passold));
                                params.add(new BasicNameValuePair("seller_newpassword", passnew1));
                                String result = NetCore.postResulttoNet(Url.modifyPassword_12, params);
                                Log.d("modify", "AA"+result+"AA"+passold+" "+passnew1+Constants.seller_id);
                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.getString("state").equals("0")){
                                    handler.sendEmptyMessage(1);
                                }else{
                                    handler.sendEmptyMessage(2);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                handler.sendEmptyMessage(2);
                            }
                        }
                    }).start();
                }
            }
        });
    }
}
