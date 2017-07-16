package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwg.egou.MyApp;
import com.dwg.egou.R;
import com.dwg.egou.config.CONST;
import com.dwg.egou.entity.OrderItem;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.utils.MyUtils;
import com.dwg.egou.utils.readUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class OrderList extends Activity implements View.OnClickListener{
    private ListView orderList;
    private OrderAdapter adapter = new OrderAdapter();
    private List<OrderItem> orderItemList;
    private ImageView backToMyBt;
    private LinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        orderList = (ListView) findViewById(R.id.orderList);
        backToMyBt = (ImageView) findViewById(R.id.backToMyBt);
        loading= (LinearLayout) findViewById(R.id.ll_rublish_sms_info_loading);
        backToMyBt.setOnClickListener(this);
        if(MyApp.user!=null)//说明登录了
        {
            new GetOrderItemAsyn().execute();
        }else{
            Toast.makeText(OrderList.this, "还没有登录", Toast.LENGTH_SHORT).show();
        }
        MyApp.FinishListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backToMyBt:{
                finish();
            }break;
        }
    }

    public class OrderAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return orderItemList.size();
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
            OrderItemHolder orderItemHolder = null;
            if(convertView==null){
                orderItemHolder = new OrderItemHolder();
                convertView = LayoutInflater.from(OrderList.this).inflate(R.layout.order_item,null,false);

                orderItemHolder.orderItemImage = (ImageView) convertView.findViewById(R.id.order_item_image);
                orderItemHolder.orderItemInfo = (TextView) convertView.findViewById(R.id.order_item_info);
                orderItemHolder.orderItemNum = (TextView) convertView.findViewById(R.id.order_item_product_num);
                orderItemHolder.orderItemPrice = (TextView) convertView.findViewById(R.id.order_item_product_price);
                orderItemHolder.orderItemAddTime = (TextView) convertView.findViewById(R.id.orderItem_addTime);
                convertView.setTag(orderItemHolder);
            }else{
                orderItemHolder= (OrderItemHolder) convertView.getTag();
            }
            OrderItem orderItem = orderItemList.get(position);
            Picasso.with(parent.getContext()).load(CONST.HOST+orderItem.getProductImageSrc()).placeholder(R.drawable.dot_current).into(orderItemHolder.orderItemImage);
            orderItemHolder.orderItemInfo.setText(orderItem.getProductName());
            orderItemHolder.orderItemNum.setText(orderItem.getBuynum()+"");
            orderItemHolder.orderItemPrice.setText(orderItem.getMoney()*orderItem.getBuynum()+"");
            orderItemHolder.orderItemAddTime.setText(MyUtils.trimMs(orderItem.getAdd_time()));
            return convertView;
        }

        private class OrderItemHolder {
            private ImageView orderItemImage;
            private TextView orderItemInfo;
            private TextView orderItemNum;
            private TextView orderItemPrice;
            private TextView orderItemAddTime;
        }
    }

    private class GetOrderItemAsyn extends AsyncTask<Void,Void,ResponseObject>{
        @Override
        protected ResponseObject doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet  = new HttpGet(CONST.ORDERITEMLIST);
            if(readUtil.GET_SESSIONID(OrderList.this)!=null){
                httpGet.setHeader("Cookie",readUtil.GET_SESSIONID(OrderList.this));
            }
            try {
                HttpResponse response =  httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                return parseCartJson(json);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseObject responseObject) {
            super.onPostExecute(responseObject);
            loading.setVisibility(View.INVISIBLE);
            if(responseObject!=null){
                if(responseObject.getState()==1){
                    orderItemList = (List<OrderItem>) responseObject.getDatas();
                    orderList.setAdapter(adapter);
                }
            }else{
                Toast.makeText(OrderList.this, "网络好像有问题", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject  responseObject;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<List<OrderItem>>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }

        return  responseObject;
    }
}

