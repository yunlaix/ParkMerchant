package com.xs.parkmerchant.Activity;

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

import com.nostra13.universalimageloader.utils.L;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.Net.NetCore;
import com.xs.parkmerchant.Net.Url;
import com.xs.parkmerchant.R;
import com.xs.parkmerchant.View.QRCoderView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

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

        //context用来产生二维码
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        ticket_id = Constants.seller_id + sdf.format(new Date());
        context = ticket_id;
        Log.v("context_ticket_id", context);
        createQR(context);

        upload();

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

    public void upload(){
        Log.d("upload","uploadQR");
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.clear();
                param.add(new BasicNameValuePair("ticket_id", ticket_id));
                param.add(new BasicNameValuePair("activity_id", activity_id));
                param.add(new BasicNameValuePair("ticket_deadline", Constants.activity_endttime));
                Log.d("upload","uploadQR param"+ticket_id+" "+activity_id+" "+activity_time);

                try {
                    String data = NetCore.postResulttoNet(Url.produceTicket, param);
                    JSONObject jb = new JSONObject(data);


                    int result = Integer.parseInt(jb.getString("state"));
                    Log.d("updateQRresult = ", Integer.toString(result));

                    switch (result) {
                        case 0:
                            Looper.prepare();
                            Toast.makeText(getApplication(), "停车券生成成功", Toast.LENGTH_LONG).show();
                            Looper.loop();
                            break;
                        case 1:
                            Toast.makeText(getApplication(), "停车券生成失败", Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (Exception e) {
                    Log.d("fail shengchheng:", "ku");
                    e.printStackTrace();
                }
            }}).start();
    }


    public String getDate(){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int date = ca.get(Calendar.DATE);

        String str = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(date);

        return str;
    }

    /**产生二维码*/
    private void createQR(String content) {

        logo= BitmapFactory.decodeResource(super.getResources(),R.mipmap.logo_qr);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int w = outMetrics.widthPixels * 6 / 11;//设置宽度
        ViewGroup.LayoutParams layoutParams = QR_image.getLayoutParams();
        layoutParams.height = layoutParams.width = w;//设置高度
        QR_image.setLayoutParams(layoutParams);

        try {
            Bitmap bitmap = QRCoderView.encodeToQR(content,logo, w);//要生成二维码的内容，我这就是一个网址
//            Bitmap bitmap = EncodingUtils.createQRCode(this,content, w, w, logo);//要生成二维码的内容，我这就是一个网址

            Log.d("qractivity", "A"+content+"A");
            QR_image.setImageBitmap(bitmap);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "生成二维码失败", Toast.LENGTH_SHORT);
        }

    }

}
