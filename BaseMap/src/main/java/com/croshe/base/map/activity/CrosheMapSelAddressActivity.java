package com.croshe.base.map.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.base.map.R;
import com.croshe.base.map.adapter.BaseMapAddressAdapter;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by Janesen on 2017/5/8.
 */

public class CrosheMapSelAddressActivity extends CrosheBaseSlidingActivity implements AMapLocationListener, AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener, LocationSource, AMap.OnMapTouchListener {

    public static final String EXTRA_POI_ITEM = "poi_item";


    private String currCityCode;

    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private BottomSheetBehavior behavior;

    private BaseMapAddressAdapter adapter;
    private MapView mMapView = null;
    private AMap aMap = null;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mLocationClient = null;

    private ImageView imgMarkLocation;

    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private ProgressDialog progressDialog;


    private LinearLayout llBottomSheet;

    private boolean onSearchedMoveToFirst = false, isSelect = false;

    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select_address);
        EventBus.getDefault().register(this);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        initLocation();
        initView();
    }


    public void initLocation() {
        aMap.setOnCameraChangeListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setLocationSource(this);
        aMap.setOnMapTouchListener(this);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationListener(this);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);


        if (getIntent().getExtras() != null
                && getIntent().getExtras().containsKey(EXTRA_POI_ITEM)) {

            PoiItem poiItem = getIntent().getParcelableExtra(EXTRA_POI_ITEM);
            LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 1000, null);
            currCityCode = poiItem.getCityCode();
        } else {
            //启动定位
            aMap.setMyLocationEnabled(true);
        }
    }

    public void initView() {

        mLinearLayoutManager = new LinearLayoutManager(context);
        adapter = new BaseMapAddressAdapter();

        llBottomSheet = getView(R.id.llBottomSheet);
        recyclerView = getView(R.id.recyclerView);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) llBottomSheet.getLayoutParams();
        layoutParams.height = (int) (DensityUtils.getHeightInPx() - DensityUtils.dip2px(55) - DensityUtils.statusBarHeight2(context));
        layoutParams.setBehavior(new BottomSheetBehavior());
        llBottomSheet.setLayoutParams(layoutParams);

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DataItemDecoration());
        recyclerView.setAdapter(adapter);

        imgMarkLocation = getView(R.id.imgMarkLocation);

        behavior = BottomSheetBehavior.from(llBottomSheet);
        behavior.setPeekHeight(DensityUtils.dip2px(100));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setHideable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        findViewById(R.id.llDragHandler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.android_base_map_menu_select_address, menu);

        searchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        searchView.setQueryHint("输入位置关键字…");
        searchView.setMaxWidth((int) DensityUtils.getWidthInPx());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                doSearch2(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Subscribe
    public void onEvent(PoiItem poiItem) {
        Intent data = new Intent();
        data.putExtra("position", poiItem);
        setResult(RESULT_OK, data);
        isSelect = true;
        finish();
    }


    @Subscribe
    public void onEvent(String action) {
        if (StringUtils.isNotEmpty(action)) {
            if (action.equals("DragHandler")) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.jump_anim);
        animation.setRepeatCount(1);
        imgMarkLocation.startAnimation(animation);

        if (!onSearchedMoveToFirst) {
            doSearch(cameraPosition.target);
        }
        onSearchedMoveToFirst = false;
    }


    public void doSearch(LatLng latLng) {
        query = new PoiSearch.Query("", "", currCityCode);
        query.setPageSize(20);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLng.latitude,
                latLng.longitude), 5000));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();
    }

    public void doSearch2(String key) {
        onSearchedMoveToFirst = true;
        query = new PoiSearch.Query(key, "", currCityCode);
        query.setPageSize(20);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在搜索位置信息中，请稍后…");
        progressDialog.show();
        searchView.onActionViewCollapsed();
    }


    @Override
    public void onPoiSearched(PoiResult result, int i) {
        if (result != null && result.getQuery() != null) {// 搜索poi的结果
            List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
            if (poiItems.size() != 0) {

                if (onSearchedMoveToFirst) {
                    PoiItem poiItem = poiItems.get(0);
                    LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 1000, null);
                }


                adapter.setData(poiItems);
                adapter.notifyDataSetChanged();
                if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }


            } else {
                toast("未搜索到位置，请更换关键字！");
            }
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void finish() {
        if (!isSelect) {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {


    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationClient.startLocation();
        if (searchView != null) {
            searchView.onActionViewCollapsed();
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在获取位置信息中，请稍后…");
        progressDialog.show();
    }

    @Override
    public void deactivate() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 1000, null);
        currCityCode = aMapLocation.getCityCode();
        Log.d("STAG", "定位成功！");
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSlideEnable(false);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setSlideEnable(true);
                break;
        }
    }


    public class DataItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = DensityUtils.dip2px(0.5f);
            }
        }
    }
}
