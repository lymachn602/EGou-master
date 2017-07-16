package com.dwg.egou.view;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dwg.egou.Acitivty.CartGoryList;
import com.dwg.egou.Acitivty.CateGory;
import com.dwg.egou.Acitivty.SearchActivity;
import com.dwg.egou.config.CONST;
import com.dwg.egou.widget.MyGridView;
import com.dwg.egou.Acitivty.ProductDetails;
import com.dwg.egou.R;
import com.dwg.egou.entity.Product;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.utils.readUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/4.
 */
public class MainFragment extends android.support.v4.app.Fragment implements LocationListener, View.OnClickListener {
    private TextView city;
    private String cityName;
    private LocationManager locationManager;
    public static final int requestCode = 1;
    private int requestCityCode = 2;
    private GridView sortView;
    private ViewPager scollViewPager;
    private List<ImageView> scollImages = new ArrayList<>();
    private String[] sortName = {"家用电器", "手机数码", "电脑办公", "家具家具", "个护化妆", "更多"};
    private String[] cateGoryList=
            {"家用电器","手机、数码","电脑、办公",
                    "家具、家居、家装、厨具",
                    "个护化妆、清洁用品",
                    "食品、酒类、特产",
                    "图书、音像、电子书",
                    "鞋靴、箱包、钟表、奢侈品",
                    "鞋靴、箱包、钟表、奢侈品"};
    private int[] sortImagesID = {R.drawable.sort_1, R.drawable.sort_2, R.drawable.sort_3, R.
            drawable.sort_4, R.drawable.sort_5, R.drawable.sort_6};
    private int currentIndex = 1;//定义当前幻灯片是哪个图片
    private List<ImageView> dots = new ArrayList<>();
    private List<Product> listProduct;
    private MyGridView listProductView;
    private PullToRefreshScrollView scrollView;
    ProductAdapter productAdapter = null;
    private ImageView searchBt;
    public static int page = 1;//页码
    public static int size = 4;//每次加载几个图片
    public static int count;
    private static String SESSION_ID = null;
    private Handler scollHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    scollViewPager.setCurrentItem(currentIndex, false);
                    for (int i = 0; i < 3; i++) {
                        if (i == currentIndex) {
                            dots.get(i).setImageResource(R.drawable.dot_current);
                        } else {
                            dots.get(i).setImageResource(R.drawable.dot);
                        }
                    }
                    break;
            }
            currentIndex++;
            if (currentIndex == 3) {
                currentIndex = 0;
            }
        }
    };
    //接受更新地址消息，更新地址
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    city.setText(cityName);
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainpage, container, false);
        city = (TextView) view.findViewById(R.id.city);
        searchBt = (ImageView) view.findViewById(R.id.searchBt);
        //获取数据并且显示
        city.setText(readUtil.getCityName(getActivity()));
        city.setClickable(true);
        city.setFocusable(true);
        city.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        initSortView(view);//初始化SortView
        initScollViewPager(view);
        listProductView = (MyGridView) view.findViewById(R.id.product_list);//初始化listview
        listProductView.setOnItemClickListener(new GridViewItemClick());
        scrollView = (PullToRefreshScrollView) view.findViewById(R.id.scollPullToRefresh);
        scrollView.setMode(PullToRefreshBase.Mode.BOTH);
        scrollView.setScrollingWhileRefreshingEnabled(true);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (scrollView.getScrollY() < 0)//现在是从上往下刷新
                {
                    page = 1;
                    scrollView.setMode(PullToRefreshBase.Mode.BOTH);
                    new ProductDataTask().execute();
                } else {
                    page++;
                    new ProductDataTask().execute();
                }
            }
        });

        new ProductDataTask().execute();//得到商品数据

        return view;
    }

    private void initScollViewPager(View view) {
        scollViewPager = (ViewPager) view.findViewById(R.id.scollImage);
        ImageView imageView1 = new ImageView(getActivity());
        ImageView imageView2 = new ImageView(getActivity());
        ImageView imageView3 = new ImageView(getActivity());

        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

        imageView1.setImageResource(R.drawable.ad1);
        imageView2.setImageResource(R.drawable.ad2);
        imageView3.setImageResource(R.drawable.ad3);
        scollImages.add(imageView1);
        scollImages.add(imageView2);
        scollImages.add(imageView3);


        //初始化三个小圆点
        ImageView dot1 = (ImageView) view.findViewById(R.id.dot1);

        ImageView dot2 = (ImageView) view.findViewById(R.id.dot2);

        ImageView dot3 = (ImageView) view.findViewById(R.id.dot3);

        dots.add(dot1);

        dots.add(dot2);

        dots.add(dot3);

        new Thread(new Runnable() {//开启幻灯片播放
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                        scollHander.sendEmptyMessage(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
        scollViewPager.setAdapter(new myPagerAdapter());

    }

    private void initSortView(View view) {
        sortView = (GridView) view.findViewById(R.id.sort);
        sortView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 5) {
                    Intent i = new Intent(getActivity(), CateGory.class);
                    startActivity(i);
                }else{
                    Intent  i = new Intent(getActivity(),CartGoryList.class);
                    i.putExtra("category",cateGoryList[position]);
                    startActivity(i);
                }
            }
        });
        sortView.setAdapter(new SortAdapter());
    }

    @Override
    public void onStart() {
        super.onStart();
        // checkGpsIsOpen();开启GPS定位功能
    }

    private void checkGpsIsOpen() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isOpen) {//进入设置界面打开GPS
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(i, requestCode);
        }
        Log.e("》》》》》", "开启GPS之后");
        startLocation();
    }

    private void startLocation() {

        Log.e("》》》》》", "开始城市定位");
    }

    @Override
    public void onLocationChanged(Location location) {
        UpdateLocation(location);
        Log.e("》》》》》", "调用了城市定位函数");
    }

    private void UpdateLocation(Location location) {
        Log.e("》》》》》", "UpdateLocation");
        double lat = 0.0, lng = 0.0;
        lat = location.getLatitude();
        lng = location.getLongitude();
        Geocoder go = new Geocoder(getActivity());
        List<Address> lists = null;
        try {
            lists = go.getFromLocation(lat, lng, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lists != null) {
            for (int i = 0; i < lists.size(); i++) {
                Address ad = lists.get(i);
                cityName = ad.getLocality();
            }
        }
        handler.sendEmptyMessage(1);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



    @Override
    public void onClick(View v) {
        Log.e(">>>>>>....", "点击了选择城市");
        switch (v.getId()) {
            case R.id.searchBt:{
                Intent i = new Intent(getActivity(),SearchActivity.class);
                startActivity(i);
            }break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public class SortAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sortName.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder myHolder = new Holder();
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.sort_item, null, false);
                myHolder.sortImage = (ImageView) convertView.findViewById(R.id.sortImage);
                myHolder.sortText = (TextView) convertView.findViewById(R.id.sortDesc);
                convertView.setTag(myHolder);
            } else {
                myHolder = (Holder) convertView.getTag();
            }
            myHolder.sortImage.setImageResource(sortImagesID[position]);
            myHolder.sortText.setText(sortName[position]);
            return convertView;
        }

        public class Holder {
            ImageView sortImage;
            TextView sortText;
        }
    }

    public class myPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return scollImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(scollImages.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(scollImages.get(position));
            return scollImages.get(position);
        }
    }

    public class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listProduct != null)
                return listProduct.size();
            else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProductHolder productHolder = null;
            if (convertView == null) {
                productHolder = new ProductHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.rough_product, null, false);
                productHolder.productName = (TextView) convertView.findViewById(R.id.productName);
                productHolder.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
                productHolder.productImage = (ImageView) convertView.findViewById(R.id.imageProduct);

                convertView.setTag(productHolder);
            } else {
                productHolder = (ProductHolder) convertView.getTag();
            }
            productHolder.productName.setText(listProduct.get(position).getName());
            productHolder.productPrice.setText("￥" + listProduct.get(position).getPrice() + "");
//            productHolder.productImage.setImageBitmap();
            Picasso.with(parent.getContext()).load(CONST.HOST + listProduct.
                    get(position).getImgsrc()).placeholder(R.drawable.dot_current).into(productHolder.productImage);
            return convertView;
        }
    }

    public class ProductHolder {
        private TextView productName;
        private TextView productPrice;
        private ImageView productImage;
    }

    public class ProductDataTask extends AsyncTask<Void, Void, List<Product>> {

        @Override
        protected List<Product> doInBackground(Void... params) {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.GOOD_LIST_URI + "&page=" + page + "&size=" + size);
            if (null != SESSION_ID) {
                httpPost.setHeader("Cookie", SESSION_ID);
            }
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    String jsonString = EntityUtils.toString(httpResponse.getEntity());
                    System.out.println(jsonString);

                    Header headers = httpResponse.getFirstHeader("Set-Cookie");
                    if (headers != null) {
                        Log.e("非空", "非空");
                    }
                    if (headers != null && TextUtils.isEmpty(headers.getValue())) {
                        String setCookie = headers.getValue();
                        if (TextUtils.isEmpty(SESSION_ID)) {
                            SESSION_ID = setCookie;
                        } else {
                            SESSION_ID = SESSION_ID + "; " + setCookie;
                        }
                    }
                    System.out.println("SESSION_ID:" + SESSION_ID);
                    return parseProductJson(jsonString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            super.onPostExecute(products);
            //关闭刷新
            scrollView.onRefreshComplete();

            //设置setAdapter
            if (page == 1) {
                listProduct = products;
                productAdapter = new ProductAdapter();
                listProductView.setAdapter(productAdapter);
            } else {
                if(listProduct!=null&&products!=null){//此处判断是因为如果断网情况下，是不会加载数据的
                    listProduct.addAll(products);
                }
                productAdapter.notifyDataSetChanged();
            }
            if (page == count) {
                scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }

        }
    }

    private List<Product> parseProductJson(String jsonString) {

        Gson gSon = new Gson();
        ResponseObject<List<Product>> responseObject = gSon.fromJson(jsonString, new TypeToken<ResponseObject<List<Product>>>() {
        }.getType());
        count = responseObject.getCount();
        return responseObject.getDatas();


    }

    private class GridViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Product product = listProduct.get(position);
            Intent i = new Intent(getActivity(), ProductDetails.class);
            i.putExtra("product", product);
            startActivity(i);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("main页面被销毁");
    }


}
