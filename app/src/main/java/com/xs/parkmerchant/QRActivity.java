package com.xs.parkmerchant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.View.QRCoderView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by ml on 2016/7/7.
 */
public class QRActivity extends AppCompatActivity {

    private TextView QR_name;
    private TextView QR_time;
    private ImageView QR_image;
    private Button close_QR;

    private Bitmap logo;

    private String context,ticket_id,activity_id,activity_name,activity_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        initView();

        Intent intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
        activity_name = intent.getStringExtra("activity_name");
        activity_time = getDate();

        QR_name.setText(activity_name);
        QR_time.setText(activity_time);

        //传入活动名称+随机数
        int random = (int)(Math.random() *5);
        ticket_id = Integer.toString(random);
        context = QR_name.getText().toString() + ticket_id;
        Log.v("context", context);
        createQR(context);

        List<NameValuePair> param=new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("ticket_id", ticket_id));
        param.add(new BasicNameValuePair("activity_id", activity_id));
        param.add(new BasicNameValuePair("ticket_deadline", activity_time));
        upload(param);

        close_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initView() {

        QR_name = (TextView) findViewById(R.id.qr_name);
        QR_time = (TextView)findViewById(R.id.qr_time);
        QR_image = (ImageView)findViewById(R.id.qr_image);
        close_QR = (Button)findViewById(R.id.qr_close);

    }

    public void upload(List<NameValuePair> param){
        NetCore string=new NetCore();
        try {
            String data=string.postResulttoNet("http://139.129.24.127/parking_app/Seller/seller_produce_ticket.php", param);
            data = data.substring(0, 1);
            int result = Integer.parseInt(data);
            switch (result){
                case 0:
                    Looper.prepare();
                    Toast.makeText(getApplication(),  "生成成功", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    break;
                case 1:
                    Toast.makeText(getApplication(),  "生成失败", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getDate(){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int date = ca.get(Calendar.DATE);

        String str = Integer.toString(year)+" 年 "+ Integer.toString(month)+ " 月 " + Integer.toString(date) + " 日";

        return str;
    }

    /**产生二维码*/
    private void createQR(String content) {

        logo= BitmapFactory.decodeResource(super.getResources(),R.mipmap.logo_qr);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int w = outMetrics.widthPixels * 8 / 11;//设置宽度
        ViewGroup.LayoutParams layoutParams = QR_image.getLayoutParams();
        layoutParams.height = layoutParams.width = w;//设置高度
        QR_image.setLayoutParams(layoutParams);

        try {
            Bitmap bitmap = QRCoderView.encodeToQR(content,logo, w);//要生成二维码的内容，我这就是一个网址
            QR_image.setImageBitmap(bitmap);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "生成二维码失败", Toast.LENGTH_SHORT);
        }

    }

}
