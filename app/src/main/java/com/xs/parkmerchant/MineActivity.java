package com.xs.parkmerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        initView();

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MineActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MineActivity.this, "注销登陆", Toast.LENGTH_LONG);
            }
        });

        addImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MineActivity.this, "添加头像", Toast.LENGTH_LONG);
            }
        });

    }

//    public void onClick(View v) {
//
//        switch(v.getId()){
//            case R.id.back:
//                Toast.makeText(this, "返回", Toast.LENGTH_LONG);
//                Intent intent = new Intent();
//                intent.setClass(this, MainActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.logout:
//                Toast.makeText(this, "退出",Toast.LENGTH_LONG);
//                break;
//            case R.id.add_image:
//                Toast.makeText(this, "添加头像", Toast.LENGTH_LONG );
//                break;
//        }
//
//    }

    public void initView(){
        back = (ImageView)findViewById(R.id.mine_back);
        logout = (ImageView)findViewById(R.id.logout);
        addImage = (ImageView) findViewById(R.id.add_image);

        bussName = (TextView) findViewById(R.id.mine_buss_name);
        bussAddr = (TextView) findViewById(R.id.mine_buss_addr);
        bussTel = (TextView) findViewById(R.id.mine_buss_tel);

    }
}
