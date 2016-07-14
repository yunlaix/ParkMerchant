package com.xs.parkmerchant.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xs.parkmerchant.Net.Constants;
import com.xs.parkmerchant.R;

/**
 * Created by Man on 2016/7/14.
 */
public class PickAddressActivity extends AppCompatActivity{

    private TextView pick;
    private MapView mapView;
    private BaiduMap baiduMap;
    private MapStatus mapStatus;
    private MapStatusUpdate mapStatusUpdate;
    private OverlayOptions option;
    private GeoCoder geoCoder;
    private TextView address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());//
        setContentView(R.layout.activity_pick_address);
        address = (TextView) findViewById(R.id.address);
        pick = (TextView) findViewById(R.id.pick);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.isPicked = true;
                finish();
            }
        });
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //定义Maker坐标点
        LatLng point = new LatLng(Constants.addr_lan, Constants.addr_lon);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);
        option = new MarkerOptions().position(point).icon(bitmap).zIndex(9).draggable(true);
        baiduMap.addOverlay(option);
        //初始定位
        mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
        //监听拖拽
        baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("pickaddress", marker.getPosition().toString());
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(marker.getPosition()));
            }

            @Override
            public void onMarkerDragStart(Marker marker) {}
        });
        //反地理编码
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener(){
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(PickAddressActivity.this, "抱歉，未能找到结果",Toast.LENGTH_LONG).show();
                }else {
                    address.setText(result.getAddress());
                }
                Log.d("pick", "位置：" + result.getAddress());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
