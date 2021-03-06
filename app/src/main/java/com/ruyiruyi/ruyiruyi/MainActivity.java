package com.ruyiruyi.ruyiruyi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruyiruyi.ruyiruyi.db.DbConfig;
import com.ruyiruyi.ruyiruyi.db.model.CarBrand;
import com.ruyiruyi.ruyiruyi.db.model.CarFactory;
import com.ruyiruyi.ruyiruyi.db.model.CarTireInfo;
import com.ruyiruyi.ruyiruyi.db.model.CarVerhicle;
import com.ruyiruyi.ruyiruyi.db.model.Province;
import com.ruyiruyi.ruyiruyi.db.model.TireType;
import com.ruyiruyi.ruyiruyi.ui.activity.ActionBarActivity;
import com.ruyiruyi.ruyiruyi.ui.activity.TestActivity;
import com.ruyiruyi.ruyiruyi.ui.fragment.HomeFragment;
import com.ruyiruyi.ruyiruyi.ui.fragment.MerchantFragment;
import com.ruyiruyi.ruyiruyi.ui.fragment.MyFragment;
import com.ruyiruyi.ruyiruyi.ui.fragment.YcxFragment;
import com.ruyiruyi.ruyiruyi.utils.RequestUtils;
import com.ruyiruyi.ruyiruyi.utils.UtilsRY;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.base.BaseActivity;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.cell.HomeTabsCell;
import com.ruyiruyi.rylibrary.cell.NoCanSlideViewPager;
import com.ruyiruyi.rylibrary.ui.adapter.FragmentViewPagerAdapter;
import com.ruyiruyi.rylibrary.utils.AndroidUtilities;
import com.ruyiruyi.rylibrary.utils.LayoutHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import rx.functions.Action1;

public class MainActivity extends BaseActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private FrameLayout content;
    private NoCanSlideViewPager viewPager;
    private HomeTabsCell tabsCell;
    private List<String> titles;
    private HomePagerAdapeter pagerAdapter;
    private static boolean isExit = false;
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = new FrameLayout(this);
        setContentView(content,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.MATCH_PARENT));

        viewPager = new NoCanSlideViewPager(this);
        content.addView(viewPager,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.MATCH_PARENT,Gravity.TOP,0,0,0, AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT)));

        tabsCell = new HomeTabsCell(this);
        content.addView(tabsCell,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT),Gravity.BOTTOM));
        tabsCell.setViewPager(viewPager);

        initTitle();
        pagerAdapter = new HomePagerAdapeter(getSupportFragmentManager(),initPagerTitle(),initFragment());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        tabsCell.setSelected(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //获取车辆品牌数据
        initCarDataIntoDb();
        //获取车辆图标数据
        initCarBrand();
        //获取车辆型号数据
        initCarVerhicle();
        //获取车辆轮胎和排量数据
        initCarrTireInfo();
        //获取轮胎型号
        initTireType();
        //获取省市县
        initProvice();

    }

    private void initProvice() {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        List<Province> provinceList = null;
        try {
            provinceList = db.selector(Province.class)
                    .orderBy("time")
                    .findAll();
        } catch (DbException e) {
        }
        JSONObject jsonObject = new JSONObject();
        try {
            if (provinceList == null){
                jsonObject.put("time","2000-00-00 00:00:00");
            }else {
                String time = provinceList.get(provinceList.size() - 1).getTime();
                jsonObject.put("time",time);
            }
        } catch (JSONException e) {

        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "getAllPositon");
        params.addBodyParameter("reqJson",jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:getTireTypeData---" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    if (status.equals("1")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        List<Province> provinceArrayList = new ArrayList<Province>();
                        for (int i = 0; i < data.length(); i++) {

                            String name = data.getJSONObject(i).getString("name");
                            int id = data.getJSONObject(i).getInt("id");
                            int definition = data.getJSONObject(i).getInt("definition");
                            int fid = data.getJSONObject(i).getInt("fid");

                            long time = data.getJSONObject(i).getLong("time");
                            String timestampToStringAll = new UtilsRY().getTimestampToStringAll(time);


                            provinceArrayList.add(new Province(id,fid,definition,name,timestampToStringAll));
                        }

                        saveProvinceIntoDb(provinceArrayList);
                    }else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void saveProvinceIntoDb(List<Province> provinceArrayList) {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        try {
            Log.e(TAG, "provinceArrayList: 添加成功" + provinceArrayList.size());
            db.saveOrUpdate(provinceArrayList);
        } catch (DbException e) {

        }
    }

    private void initTireType() {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        List<TireType> tireTypeList = null;
        try {
            tireTypeList = db.selector(TireType.class)
                    .orderBy("time")
                    .findAll();
        } catch (DbException e) {
        }
        JSONObject jsonObject = new JSONObject();

        try {
            if (tireTypeList == null){
                jsonObject.put("time","2000-00-00 00:00:00");
            }else {
                String time = tireTypeList.get(tireTypeList.size() - 1).getTime();
                jsonObject.put("time",time);
            }
        } catch (JSONException e) {

        }

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "getTireType");
        params.addBodyParameter("reqJson",jsonObject.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:getTireTypeData---" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    if (status.equals("1")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        List<TireType> tireTypeArrayList = new ArrayList<TireType>();
                        for (int i = 0; i < data.length(); i++) {
                            String tireDiameter = data.getJSONObject(i).getString("tireDiameter");
                            String tireFlatWidth = data.getJSONObject(i).getString("tireFlatWidth");
                            String tireFlatnessRatio = data.getJSONObject(i).getString("tireFlatnessRatio");
                            int id = data.getJSONObject(i).getInt("tireTypeId");

                            long time = data.getJSONObject(i).getLong("time");
                            String timestampToStringAll = new UtilsRY().getTimestampToStringAll(time);


                            tireTypeArrayList.add(new TireType(id,tireFlatWidth,tireFlatnessRatio,tireDiameter,timestampToStringAll));
                        }

                        saveTireTypeIntoDb(tireTypeArrayList);
                    }else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void saveTireTypeIntoDb(List<TireType> tireTypeArrayList) {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        try {
            Log.e(TAG, "tireTypeArrayList: 添加成功" + tireTypeArrayList.size());
            db.saveOrUpdate(tireTypeArrayList);
        } catch (DbException e) {

        }
    }

    private void initCarrTireInfo() {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        List<CarTireInfo> carTireInfoList = null;
        try {
            carTireInfoList = db.selector(CarTireInfo.class)
                    .orderBy("time")
                    .findAll();

        } catch (DbException e) {

        }
        JSONObject jsonObject = new JSONObject();

        try {
            if (carTireInfoList == null){
                jsonObject.put("time","2000-00-00 00:00:00");
            }else {
                String time = carTireInfoList.get(carTireInfoList.size() - 1).getTime();
                Log.e(TAG, "initCarDataIntoDb: " + time);
                jsonObject.put("time",time);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "getCarTireInfoData");
        params.addBodyParameter("reqJson",jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:getCarTireInfoData---" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    if (status.equals("1")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        List<CarTireInfo> carTireInfoArrayList = new ArrayList<CarTireInfo>();
                        for (int i = 0; i < data.length(); i++) {

                            String brand = data.getJSONObject(i).getString("brand");
                            String verhicle = data.getJSONObject(i).getString("verhicle");
                            String font = data.getJSONObject(i).getString("font");
                            String name = data.getJSONObject(i).getString("name");
                            String pailiang = data.getJSONObject(i).getString("pailiang");
                            String rear = data.getJSONObject(i).getString("rear");
                            String year = data.getJSONObject(i).getString("year");
                            int id = data.getJSONObject(i).getInt("id");
                            int carBrandId = data.getJSONObject(i).getInt("carBrandId");
                            int verhicleId = data.getJSONObject(i).getInt("verhicleId");

                            long time = data.getJSONObject(i).getLong("time");
                            String timestampToStringAll = new UtilsRY().getTimestampToStringAll(time);


                            carTireInfoArrayList.add(new CarTireInfo(id,brand,carBrandId,verhicle,verhicleId,pailiang,year,name,font,rear,timestampToStringAll));
                        }
                        savaCarTireIntoDb(carTireInfoArrayList);
                    }else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void savaCarTireIntoDb(List<CarTireInfo> carTireInfoArrayList) {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        try {
            Log.e(TAG, "savaCarTireIntoDb: 添加成功");
            db.saveOrUpdate(carTireInfoArrayList);
        } catch (DbException e) {

        }
    }

    private void initCarVerhicle() {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        List<CarVerhicle> carVerhicleList = null;
        try {
            carVerhicleList = db.selector(CarVerhicle.class)
                    .orderBy("time")
                    .findAll();

        } catch (DbException e) {

        }
        JSONObject jsonObject = new JSONObject();

        try {
            if (carVerhicleList == null){
                jsonObject.put("time","2000-00-00 00:00:00");
            }else {
                String time = carVerhicleList.get(carVerhicleList.size() - 1).getTime();
                Log.e(TAG, "initCarDataIntoDb: " + time);
                jsonObject.put("time",time);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "getCarVerhicleData");
        params.addBodyParameter("reqJson",jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    if (status.equals("1")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        List<CarVerhicle> carVerhicleArrayList = new ArrayList<CarVerhicle>();
                        for (int i = 0; i < data.length(); i++) {
                            String carVersion = data.getJSONObject(i).getString("carVersion");
                            String verhicle = data.getJSONObject(i).getString("verhicle");
                            int id = data.getJSONObject(i).getInt("id");
                            int carBrandId = data.getJSONObject(i).getInt("carBrandId");
                            int factoryId = data.getJSONObject(i).getInt("factoryId");
                            int verify = data.getJSONObject(i).getInt("verify");


                            long time = data.getJSONObject(i).getLong("time");
                            String timestampToStringAll = new UtilsRY().getTimestampToStringAll(time);
                            carVerhicleArrayList.add(new CarVerhicle(id,verhicle,carBrandId,factoryId,carVersion,verify,timestampToStringAll));
                        }
                        savaCarVerhicleIntoDb(carVerhicleArrayList);
                    }else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void savaCarVerhicleIntoDb(List<CarVerhicle> verhiclesList) {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        try {
            db.saveOrUpdate(verhiclesList);
        } catch (DbException e) {

        }
    }

    private void initCarBrand() {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        List<CarBrand> carBrandList = null;
        try {
            carBrandList = db.selector(CarBrand.class)
                    .orderBy("time")
                    .findAll();

        } catch (DbException e) {

        }
        JSONObject jsonObject = new JSONObject();

        try {
            if (carBrandList == null){
                jsonObject.put("time","2000-00-00 00:00:00");
            }else {
                String time = carBrandList.get(carBrandList.size() - 1).getTime();
                jsonObject.put("time",time);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "getCarBrandData");
        params.addBodyParameter("reqJson",jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    if (status.equals("1")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        List<CarBrand> carBrandArrayList = new ArrayList<CarBrand>();
                        for (int i = 0; i < data.length(); i++) {


                            String icon = data.getJSONObject(i).getString("icon");
                            String imgUrl = data.getJSONObject(i).getString("imgUrl");
                            String name = data.getJSONObject(i).getString("name");
                            int id = data.getJSONObject(i).getInt("id");


                            long time = data.getJSONObject(i).getLong("time");
                            String timestampToStringAll = new UtilsRY().getTimestampToStringAll(time);


                            carBrandArrayList.add(new CarBrand(id,name,imgUrl,icon,timestampToStringAll));
                        }
                        saveCarBrandIntoDb(carBrandArrayList);
                    }else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void saveCarBrandIntoDb(List<CarBrand> brandList) {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        try {
            db.saveOrUpdate(brandList);
        } catch (DbException e) {

        }
    }


    private void initCarDataIntoDb() {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        List<CarFactory> carList = null;
        try {
             carList = db.selector(CarFactory.class)
                     .orderBy("time")
                     .findAll();

        } catch (DbException e) {

        }
        JSONObject jsonObject = new JSONObject();

        try {
            if (carList == null){
                jsonObject.put("time","2000-00-00 00:00:00");
            }else {
                String time = carList.get(carList.size() - 1).getTime();
                jsonObject.put("time",time);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "getCarFactoryData");
        params.addBodyParameter("reqJson",jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    if (status.equals("1")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        List<CarFactory> factoryList = new ArrayList<CarFactory>();
                        for (int i = 0; i < data.length(); i++) {
                            String factory = data.getJSONObject(i).getString("factory");
                            int id = data.getJSONObject(i).getInt("id");
                            int carBrandId = data.getJSONObject(i).getInt("carBrandId");
                            long time = data.getJSONObject(i).getLong("time");
                            String timestampToStringAll = new UtilsRY().getTimestampToStringAll(time);
                            factoryList.add(new CarFactory(id,carBrandId,factory,timestampToStringAll));
                        }
                        savaCarFactoryIntoDb(factoryList);
                    }else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 保存车辆工厂信息到数据库
     * @param factoryList
     */
    private void savaCarFactoryIntoDb(List<CarFactory> factoryList) {
        DbConfig dbConfig = new DbConfig();
        DbManager db = dbConfig.getDbManager();
        try {
            db.saveOrUpdate(factoryList);
        } catch (DbException e) {

        }
    }

    private List<Fragment> initFragment(){
        List<Fragment> fragments = new ArrayList<>();
        HomeFragment homeFragment = new HomeFragment();
        fragments.add(homeFragment);
        fragments.add(new MerchantFragment());
        fragments.add(new YcxFragment());
        fragments.add(new MyFragment());

        return fragments;
    }

    protected List<String> initPagerTitle() {
        titles = new ArrayList<>();
        titles.add("首页");
        titles.add("附近门店");
        titles.add("冬季胎");
        titles.add("我的");
        return titles;
    }

    private void initTitle() {
        tabsCell.addView(R.drawable.ic_home,R.drawable.ic_home_pressed, "首页 ");
        tabsCell.addView(R.drawable.ic_merchant,R.drawable.ic_merchant_pressed, "附近门店 ");
        tabsCell.addView(R.drawable.ic_dongjitai,R.drawable.ic_dongjitai_pressed, "冬季胎 ");
        tabsCell.addView(R.drawable.ic_my,R.drawable.ic_my_pressed, "我的 ");

    }

    class HomePagerAdapeter extends FragmentViewPagerAdapter{

        private final List<String> mPageTitle = new ArrayList<>();

        public HomePagerAdapeter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
            super(fragmentManager, fragments);
            mPageTitle.clear();
            mPageTitle.addAll(pageTitles);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitle.get(position);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {


            this.finish();
        }
    }
}
