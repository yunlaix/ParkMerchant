package com.xs.parkmerchant;

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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Man on 2016/7/6.
 */
public class LoginActivity extends AppCompatActivity{

    private EditText editText_id, editText_password;
    private TextView textView_login, textView_register;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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
        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login
                getEditText();
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
                                Log.d("login", "result"+result);
                                if(result!=null && !result.equals("")){
                                    JSONObject jsonObject = new JSONObject(result);
                                    String state = jsonObject.getString("state");
                                    if(state.equals("0")){
                                        Constants.seller_name = jsonObject.getString("seller_name");
                                        Constants.seller_address = jsonObject.getString("seller_address");
                                        Constants.seller_contact = jsonObject.getString("seller_contact");
                                        Constants.seller_img = jsonObject.getString("seller_img");
                                        setSharePreference();
                                        handler.sendEmptyMessage(1);
                                        Log.d("login", "ssssssssssssssssss");
                                    }else{
                                        Log.d("login", "fffffffffffffffff");
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getEditText(){
        Constants.seller_id = editText_id.getText().toString().trim();
        Constants.seller_password = editText_password.getText().toString().trim();
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
        Log.d("login", "A"+sharedPreferences.getString("seller_id", "")+sharedPreferences.getString("seller_password", "")+"B");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //auto-login
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
