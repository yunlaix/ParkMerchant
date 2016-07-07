package com.xs.parkmerchant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xs.parkmerchant.View.QRCoderView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by ml on 2016/7/7.
 */
public class QRActivity extends AppCompatActivity {

    private TextView QR_name;
    private TextView QR_time;
    private ImageView QR_image;
    private Button close_QR;

    private Bitmap logo;

    private String context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        initView();

        Bundle bd = new Bundle();
        QR_name.setText(bd.getString("activity_name","活动名称"));
        QR_time.setText(bd.getString("activity_tiem",getDate()));

        //传入活动名称+随机数
        int index = (int)(Math.random() *5);
        context = QR_name.getText().toString() + Integer.toString(index);
        Log.v("context", context);
        createQR(context);

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
