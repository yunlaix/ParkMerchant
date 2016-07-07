package com.xs.parkmerchant;

import android.content.Intent;
import android.os.Bundle;
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

    private EditText editText_name, editText_password;
    private TextView textView_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //init
        editText_name = (EditText) findViewById(R.id.seller_name);
        editText_password = (EditText) findViewById(R.id.seller_password);
        textView_login = (TextView) findViewById(R.id.login);

        //auto-login

        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login
                getEditText();
                if(Constants.seller_name=="" || Constants.seller_password==""){
                    Toast.makeText(getApplicationContext(), "empty!", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("seller_id", Constants.seller_name));
                                params.add(new BasicNameValuePair("seller_password", Constants.seller_password));
                                String result = NetCore.postResulttoNet(Url.login_1, params);
                                Log.d("login", "result"+result);
                                if(result!=null && !result.equals("")){
                                    JSONObject jsonObject = new JSONObject(result);
                                    String state = jsonObject.getString("state");
                                    if(state.equals("0")){
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
    }

    private void getEditText(){
        Constants.seller_name = editText_name.getText().toString();
        Constants.seller_password = editText_password.getText().toString();
    }

}
