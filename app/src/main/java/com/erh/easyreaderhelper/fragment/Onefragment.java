package com.erh.easyreaderhelper.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.base.FloatingDraftButton;
import com.erh.easyreaderhelper.bean.Informationpublished;
import com.erh.easyreaderhelper.ui.RouteActivity;
import com.erh.easyreaderhelper.util.AnimationUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class Onefragment extends Fragment implements AMapLocationListener, LocationSource, PoiSearch.OnPoiSearchListener, AMap.OnMapClickListener,
        AMap.OnMapLongClickListener,
        GeocodeSearch.OnGeocodeSearchListener,
        View.OnKeyListener ,
        AMap.OnMarkerClickListener{


    private View view;

    //定位样式
    private MyLocationStyle myLocationStyle = new MyLocationStyle();

    //定义一个UiSettings对象
    private UiSettings mUiSettings;

    //地理编码搜索
    private GeocodeSearch geocodeSearch;
    //解析成功标识码
    private static final int PARSE_SUCCESS_CODE = 1000;


    private MapView mapView = null;
    //地图控制器
    private AMap aMap = null;
    //位置更改监听
    private OnLocationChangedListener mListener;

    //请求权限码
    private static final int REQUEST_PERMISSIONS = 9527;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //标点列表
    private List<Marker> markerList = new ArrayList<>();


    //POI查询对象
    private PoiSearch.Query query;
    //POI搜索对象
    private PoiSearch poiSearch;
    //城市码
    private String cityCode = null;
    //浮动按钮  清空地图标点
    private FloatingActionButton fabClearMarker;
    //路线按钮
    private FloatingDraftButton fabRoute;

    private Toolbar toolbar;

    //城市
    private String city;

    private  String add;


    private AutoTransition autoTransition;//过渡动画
    private Animation bigShowAnim;//放大显示
    private Animation smallHideAnim;//缩小隐藏
    private int width;//屏幕宽度
    private boolean isOpen = false;//顶部搜索布局的状态


    private ImageView ivSearch;//搜索图标
    private EditText edSearch;//搜索输入框
    private ImageView ivClose;//关闭图标
    private RelativeLayout laySearch;//搜索布局


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_one1, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //获取屏幕宽高
        WindowManager manager = getActivity().getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;  //获取屏幕的宽度 像素
        ivSearch = getActivity().findViewById(R.id.iv_search);
        ivClose = getActivity().findViewById(R.id.iv_close);
        edSearch = getActivity().findViewById(R.id.ed_search);
        laySearch = getActivity().findViewById(R.id.lay_search);
        // toolbar = getActivity().findViewById(R.id.toolbar);
       // ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = getActivity().findViewById(R.id.map_view);
        fabClearMarker = getActivity().findViewById(R.id.fab_clear_marker);
        fabRoute = getActivity().findViewById(R.id.fab_route);



        edSearch.setOnKeyListener(this);


        fabClearMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllMarker(view);
            }
        });

        fabRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //浮动按钮  清空地图标点
                jumpRouteActivity(view);
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initExpand();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initClose();
            }
        });

        //初始化定位
        initLocation();

        //初始化地图
        initMap(savedInstanceState);


        //检查Android版本
        checkingAndroidVersion();
    }

    @Override
    public void onStart() {
        super.onStart();
        initMark();
    }

    // dp 转成 px
    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }

    // px 转成 dp
    private int px2dip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //过渡动画
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void beginDelayedTransition(ViewGroup view) {
        autoTransition = new AutoTransition();
        autoTransition.setDuration(500);
        TransitionManager.beginDelayedTransition(view,autoTransition);
    }


    /**
     * 展开
     */
    public void initExpand() {
        isOpen = true;
        edSearch.setVisibility(View.VISIBLE);//显示输入框
        ivClose.setVisibility(View.VISIBLE);//显示关闭按钮
        LinearLayout.LayoutParams LayoutParams = (LinearLayout.LayoutParams) laySearch.getLayoutParams();
        LayoutParams.width = dip2px(px2dip(width) - 24);//设置展开的宽度
        LayoutParams.setMargins(dip2px(0), dip2px(0), dip2px(0), dip2px(0));
        laySearch.setPadding(14, 0, 14, 0);
        laySearch.setLayoutParams(LayoutParams);

        //开始动画
        beginDelayedTransition(laySearch);

    }

    /**
     * 收缩
     */
    private void initClose() {
        isOpen = false;
        edSearch.setVisibility(View.GONE);
        edSearch.setText("");
        ivClose.setVisibility(View.GONE);

        LinearLayout.LayoutParams LayoutParams = (LinearLayout.LayoutParams) laySearch.getLayoutParams();
        LayoutParams.width = dip2px(48);
        LayoutParams.height = dip2px(48);
        LayoutParams.setMargins(dip2px(0), dip2px(0), dip2px(0), dip2px(0));
        laySearch.setLayoutParams(LayoutParams);

        //隐藏键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);

        //开始动画
        beginDelayedTransition(laySearch);

    }


    private void initMark() {
        Log.d("location", "addr : 11111111111" );
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//排序
        lostInfomationReqBmobQuery.findObjects(new FindListener<Informationpublished>() {
            @Override
            public void done(List<Informationpublished> list, BmobException e) {
                if (e == null) {
                    int n = list.size();
                    Log.d("location", "addr : 222222222222" );
                    for(int i = 0; i < n; i++){
                        Informationpublished informationpublished = list.get(i);
                        LatLng latLng = new LatLng(informationpublished.getLatitude(),informationpublished.getLongitude());
                        addMarkerone(latLng,informationpublished.getTitle(),informationpublished.getAddress());
                      //  addMarker(latLng);
                    }
                } else {
                    //showToast("查询数据失败");
                }
            }
        });
    }


    private void checkingAndroidVersion() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Android6.0及以上先获取权限再定位
            requestPermission();
        }else {
            //Android6.0以下直接定位
            mLocationClient.startLocation();
        }
    }

    /**
     * 动态请求权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private void requestPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (EasyPermissions.hasPermissions(getActivity(), permissions)) {
            //true 有权限 开始定位
            showMsg("已获得权限，可以定位啦！");
            mLocationClient.startLocation();
        } else {
            //false 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, permissions);
        }
    }


    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * Toast提示
     * @param msg 提示内容
     */
    private void showMsg(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制，高精度定位会产生缓存。
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 初始化地图
     * @param savedInstanceState
     */
    private void initMap(Bundle savedInstanceState) {
        mapView = getActivity().findViewById(R.id.map_view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mapView.getMap();


        //设置最小缩放等级为16 ，缩放级别范围为[3, 20]
        aMap.setMinZoomLevel(16);
        //开启室内地图
        aMap.showIndoorMap(true);

        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放按钮
        mUiSettings.setZoomControlsEnabled(false);
        //显示比例尺 默认不显示
        mUiSettings.setScaleControlsEnabled(true);


        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色  都为0则透明
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度  0 无宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色  都为0则透明
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);


        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        //设置地图点击事件
        aMap.setOnMapClickListener(this);
        //设置地图长按事件
        aMap.setOnMapLongClickListener(this);
        //设置地图Marker点击事件
        aMap.setOnMarkerClickListener(this);

        //构造GeocodeSearch对象
        geocodeSearch = new GeocodeSearch(getContext());
        //设置监听
        geocodeSearch.setOnGeocodeSearchListener(this);

    }





    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //地址
                String address = aMapLocation.getAddress();
                city = aMapLocation.getCity();
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("纬度：" + latitude + "\n");
                stringBuffer.append("经度：" + longitude + "\n");
                stringBuffer.append("地址：" + address + "\n");
                Log.d("MainActivity","address = " + address);
                Log.d("Mainactivity",stringBuffer.toString());
                Log.d("Mainactivity","11111111111111111111111111");
             //   showMsg(address);
                mLocationClient.startLocation();
                if (mListener != null){
                    mListener.onLocationChanged(aMapLocation);
                }

                cityCode = aMapLocation.getCityCode();

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁定位客户端，同时销毁本地定位服务。
        mLocationClient.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }



    /**
     * POI搜索返回
     *
     * @param poiResult POI所有数据
     * @param i
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        //解析result获取POI信息

        //获取POI组数列表
        Log.d("MainActivity","155555555555555555552");
        ArrayList<PoiItem> poiItems = poiResult.getPois();
        Log.d("MainActivity","8888888888888888888888882");
        for (PoiItem poiItem : poiItems) {
            Log.d("MainActivity", " Title：" + poiItem.getTitle() + " Snippet：" + poiItem.getSnippet());
        }
        Log.d("MainActivity","6666666666666666666666662");
    }

    /**
     * POI中的项目搜索返回
     *
     * @param poiItem 获取POI item
     * @param i
     */
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 地图单击事件
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        latlonToAddress(latLng);
       // showMsg("点击了地图，经度："+latLng.longitude+"，纬度："+latLng.latitude);
        //添加标点
       // aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));
     //   addMarker(latLng);
        //改变地图中心
        updateMapCenter(latLng);

    }

    /**
     * 地图长按事件
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        latlonToAddress(latLng);
    }

    /**
     * 坐标转地址
     * @param regeocodeResult
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        Log.d("MainActivity","rCode = " + rCode);
        //解析result获取地址描述信息
        if(rCode == PARSE_SUCCESS_CODE){
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            //显示解析后的地址
            showMsg("地址："+regeocodeAddress.getFormatAddress());
        }else {
            showMsg("获取地址失败");
        }

    }


    /**
     * 地址转坐标
     * @param geocodeResult
     * @param rCode
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
        if (rCode == PARSE_SUCCESS_CODE) {
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if(geocodeAddressList!=null && geocodeAddressList.size()>0){
                LatLonPoint latLonPoint = geocodeAddressList.get(0).getLatLonPoint();
                //显示解析后的坐标
                showMsg("坐标：" + latLonPoint.getLongitude()+"，"+latLonPoint.getLatitude());
                LatLng latLng = new LatLng(latLonPoint.getLatitude(),latLonPoint.getLongitude());
                //在对应地点做标记
                Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));
                markerList.add(marker);
            }

        } else {
            showMsg("获取坐标失败");
        }
    }


    /**
     * 通过经纬度获取地址
     * @param latLng
     */
    private void latlonToAddress(LatLng latLng) {
        //位置点  通过经纬度进行构建
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        //逆编码查询  第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);
        //异步获取地址信息
        geocodeSearch.getFromLocationAsyn(query);
    }


    /**
     * 键盘点击
     *
     * @param v
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            //获取输入框的值
            String address = edSearch.getText().toString().trim();
            if (address == null || address.isEmpty()) {
                showMsg("请输入物品");
            }else {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);

                clearAllMarker(v);

                BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
                lostInfomationReqBmobQuery.order("-updatedAt");//排序
                lostInfomationReqBmobQuery.findObjects(new FindListener<Informationpublished>() {
                    @Override
                    public void done(List<Informationpublished> list, BmobException e) {
                        if (e == null) {
                            int n = list.size();
                            int num = 0;
                            for(int i = 0; i < n; i++){
                                Informationpublished informationpublished = list.get(i);
                                if(informationpublished.getTitle().equals(address)){
                                    num++;
                                    LatLng latLng = new LatLng(informationpublished.getLatitude(),informationpublished.getLongitude());
                                    addMarkerone(latLng,informationpublished.getTitle(),informationpublished.getAddress());
                                }
                                //  addMarker(latLng);
                            }
                            if(num == 0){
                                Toast.makeText(getContext(),"未搜索到该物品",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(),"搜索失败，请稍后再试",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

/*
                // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                GeocodeQuery query = new GeocodeQuery(address,city);
                geocodeSearch.getFromLocationNameAsyn(query);

 */

            }
            return true;
        }
        return false;
    }

    /**
     * 添加地图标点
     *
     * @param latLng
     */
    private void addMarker(LatLng latLng) {
        //显示浮动按钮
        fabClearMarker.show();

        
        //添加标点
        Marker marker = aMap.addMarker(new MarkerOptions()
                .draggable(true)//可拖动
                .position(latLng)
                .title("标题")
                .snippet("详细信息"));

        //绘制Marker时显示InfoWindow
        //marker.showInfoWindow();

        //设置标点的绘制动画效果
        Animation animation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 180, 0, 0, 0);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();

        markerList.add(marker);
    }


    private void addMarkerone(LatLng latLng,String title,String address) {
        //显示浮动按钮
        fabClearMarker.show();
        add = address;
        //添加标点
        Marker marker = aMap.addMarker(new MarkerOptions()
                .draggable(true)//可拖动
                .position(latLng)
                .title(title)
                .snippet(" "));

        //绘制Marker时显示InfoWindow
        //marker.showInfoWindow();

        //设置标点的绘制动画效果
        Animation animation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 180, 0, 0, 0);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();

        markerList.add(marker);
    }

    /**
     * 清空地图Marker
     *
     * @param view
     */
    public void clearAllMarker(View view) {
        if (markerList != null && markerList.size()>0){
            for (Marker markerItem : markerList) {
                markerItem.remove();
            }
        }
        fabClearMarker.hide();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        //showMsg("点击了标点");
        String addone = add;
        //显示InfoWindow
        if (!marker.isInfoWindowShown()) {
            //显示
            marker.showInfoWindow();
            Toast.makeText(getContext(),"再次点击导航到此地",Toast.LENGTH_SHORT).show();
        } else {

            Intent intent = new Intent(getActivity(),RouteActivity.class);
            intent.putExtra("address",add);
            startActivity(intent);
            //隐藏
            marker.hideInfoWindow();

        }
        return true;
    }

    /**
     * 改变地图中心位置
     * @param latLng 位置
     */
    private void updateMapCenter(LatLng latLng) {
        // CameraPosition 第一个参数： 目标位置的屏幕中心点经纬度坐标。
        // CameraPosition 第二个参数： 目标可视区域的缩放级别
        // CameraPosition 第三个参数： 目标可视区域的倾斜度，以角度为单位。
        // CameraPosition 第四个参数： 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度
        CameraPosition cameraPosition = new CameraPosition(latLng, 16, 30, 0);
        //位置变更
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        //改变位置
        aMap.moveCamera(cameraUpdate);
        //带动画的移动
        aMap.animateCamera(cameraUpdate);

    }

    /**
     * 进入路线规划
     * @param view
     */
    public void jumpRouteActivity(View view) {
        startActivity(new Intent(getActivity(), RouteActivity.class));
    }


}
