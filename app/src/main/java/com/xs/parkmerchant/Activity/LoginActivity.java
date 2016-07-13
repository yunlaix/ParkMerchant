package com.xs.parkmerchant.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
 * 登录，提供自动登陆
 */
public class LoginActivity extends AppCompatActivity{

    private EditText editText_id, editText_password;
    private TextView textView_login, textView_register;
    private SharedPreferences sharedPreferences;
    private final int LOGIN_SUCCESS = 1;
    private final int LOGIN_FAIL = 2;
    private final int NET_FAIL = 3;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOGIN_SUCCESS:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(getApplicationContext(), "账户或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case NET_FAIL:
                    Toast.makeText(getApplicationContext(), "网络无连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //init
        editText_id = (EditText) findViewById(R.id.seller_id);
        editText_password = (EditText) findViewById(R.id.seller_password);
        textView_login = (TextView) findViewById(R.id.login);
        textView_register = (TextView) findViewById(R.id.register);
        sharedPreferences = getSharedPreferences("login_parkmerchant", MODE_PRIVATE);

        //登录入口
        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login
                Constants.seller_id = editText_id.getText().toString().trim();
                Constants.seller_password = editText_password.getText().toString().trim();
                if(Constants.seller_id=="" || Constants.seller_password==""){
                    Toast.makeText(getApplicationContext(), "empty!", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("seller_id", Constants.seller_id));
                                params.add(new BasicNameValuePair("seller_password", Constants.seller_password));
                                String result = NetCore.postResulttoNet(Url.login_1, params);
//                                Log.d("login", "result"+result);
                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.getString("state").equals("0")){
                                    Constants.seller_name = jsonObject.getString("seller_name");
                                    Constants.seller_address = jsonObject.getString("seller_address");
                                    Constants.seller_contact = jsonObject.getString("seller_contact");
                                    Constants.seller_img = jsonObject.getString("seller_img");
                                    setSharePreference();
                                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                                }else handler.sendEmptyMessage(LOGIN_FAIL);
                            }catch (Exception e){
                                e.printStackTrace();
                                if(!Constants.isNetWorkConnected(getApplicationContext())) handler.sendEmptyMessage(NET_FAIL);
                                else handler.sendEmptyMessage(LOGIN_FAIL);
                            }
                        }
                    }).start();
                }
            }
        });

        //注册入口
        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSharePreference(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("seller_id", Constants.seller_id);
        editor.putString("seller_password", Constants.seller_password);
        editor.putString("seller_name", Constants.seller_name);
        editor.putString("seller_address", Constants.seller_address);
        editor.putString("seller_contact", Constants.seller_contact);
        editor.putString("seller_img", Constants.seller_img);
        editor.commit();
//        Log.d("login", "A"+sharedPreferences.getString("seller_id", "")+sharedPreferences.getString("seller_password", ""));
    }

    //自动登陆
    @Override
    protected void onStart() {
        super.onStart();
        Constants.seller_id = sharedPreferences.getString("seller_id", "");
        Constants.seller_password = sharedPreferences.getString("seller_password", "");
        if(!Constants.seller_id.equals("") && !Constants.seller_password.equals("")){
            Constants.seller_name = sharedPreferences.getString("seller_name", "");
            Constants.seller_address = sharedPreferences.getString("seller_address", "");
            Constants.seller_contact = sharedPreferences.getString("seller_contact", "");
            Constants.seller_img = sharedPreferences.getString("seller_img", "");
            Log.d("login", "auto-login"+Constants.seller_id+Constants.seller_password);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
