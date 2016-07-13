package com.xs.parkmerchant.Activity;

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

import com.xs.parkmerchant.Net.BDMapLocation;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import com.xs.parkmerchant.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Man on 2016/7/7.
 */
public class RegisterActivity extends AppCompatActivity{
    private EditText seller_id, seller_password, seller_name, seller_address, seller_contact;
    private TextView register;
    private SharedPreferences sharedPreferences;
    private BDMapLocation bdMapLocation;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(), "账号重复！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "网络无连接", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if(!bdMapLocation.getAddr().equals("addr")){
                        seller_address.setText(bdMapLocation.getAddr());
                        Constants.seller_address = bdMapLocation.getAddr();
                        Constants.addr_lan = bdMapLocation.getLatitude();
                        Constants.addr_lon = bdMapLocation.getLontitude();
                    }else{
                        seller_address.setText("定位失败...");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        seller_id = (EditText) findViewById(R.id.seller_id);
        seller_password = (EditText) findViewById(R.id.seller_password);
        seller_name = (EditText) findViewById(R.id.seller_name);
        seller_address = (EditText) findViewById(R.id.seller_address);
        seller_contact = (EditText) findViewById(R.id.seller_contact);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditText();
                if(Constants.seller_id.equals("") || Constants.seller_password.equals("") || Constants.seller_name.equals("") || Constants.seller_address.equals("") || Constants.seller_contact.equals("")){
                    Toast.makeText(getApplicationContext(), "empty!", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("seller_id", Constants.seller_id));
                                params.add(new BasicNameValuePair("seller_password", Constants.seller_password));
                                params.add(new BasicNameValuePair("seller_name", Constants.seller_name));
                                params.add(new BasicNameValuePair("seller_address", Constants.seller_address));///
                                params.add(new BasicNameValuePair("seller_contact", Constants.seller_contact));
                                params.add(new BasicNameValuePair("seller_img", Constants.seller_img));
                                String result = NetCore.postResulttoNet(Url.register_2, params);
                                Log.d("login", "result"+result);
                                if(result!=null && !result.equals("")){
                                    JSONObject jsonObject = new JSONObject(result);
                                    String state = jsonObject.getString("state");
                                    if(state.equals("0")){
                                        setSharePreference();
                                        handler.sendEmptyMessage(2);
                                    }else{
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                if(!Constants.isNetWorkConnected(getApplicationContext())) handler.sendEmptyMessage(3);
                            }
                        }
                    }).start();
                }
            }
        });
        bdMapLocation = new BDMapLocation(getApplicationContext(), handler);
        bdMapLocation.startLocation();
    }

    private void setSharePreference(){
        sharedPreferences = getSharedPreferences("login_parkmerchant", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("seller_id", Constants.seller_id);
        editor.putString("seller_name", Constants.seller_name);
        editor.putString("seller_password", Constants.seller_password);
        editor.putString("seller_address", Constants.seller_address);
        editor.putString("seller_contact", Constants.seller_contact);
        editor.putString("seller_img", Constants.seller_img);
        editor.commit();
    }

    private void getEditText(){
        Constants.seller_id = seller_id.getText().toString().trim();
        Constants.seller_password = seller_password.getText().toString().trim();
        Constants.seller_name = seller_name.getText().toString().trim();
        Constants.seller_address = seller_address.getText().toString().trim();
        Constants.seller_contact = seller_contact.getText().toString().trim();
    }

}
