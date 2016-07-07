package com.xs.parkmerchant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by ml on 2016/7/7.
 */
public class QRActivity extends AppCompatActivity {

    private TextView QR_name;
    private TextView QR_time;
    private ImageView QR_image;
    private Button close_QR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        initView();



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

}
