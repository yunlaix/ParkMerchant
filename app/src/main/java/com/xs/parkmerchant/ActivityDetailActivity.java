package com.xs.parkmerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class ActivityDetailActivity extends AppCompatActivity {

    private TextView activityName;
    private ImageView detailBack;
    private TextView detailActivityTime;
    private TextView detailActivityLocation;
    private TextView detailActivityDescri;

    private Button createQR;
    private Button deleteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        initView();

        detailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityDetailActivity.this, QRActivity.class);
                startActivity(intent);
            }
        });

        deleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityDetailActivity.this, "删除活动", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initView() {

        activityName = (TextView) findViewById(R.id.activity_detail_name);
        detailBack = (ImageView) findViewById(R.id.activity_detail_back);
        detailActivityTime = (TextView) findViewById(R.id.activity_detail_time);
        detailActivityLocation = (TextView) findViewById(R.id.activity_detail_location);
        detailActivityDescri = (TextView) findViewById(R.id.activity_detail_descri);

        createQR = (Button) findViewById(R.id.createQR);
        deleteActivity = (Button) findViewById(R.id.delete_activity);

    }
}
