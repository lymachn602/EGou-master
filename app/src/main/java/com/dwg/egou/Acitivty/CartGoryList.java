package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwg.egou.config.CONST;
import com.dwg.egou.MyApp;
import com.dwg.egou.entity.Product;
import com.dwg.egou.R;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.utils.readUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/1.
 */
public class CartGoryList extends Activity implements AdapterView.OnItemClickListener{
    private ListView cateGoryProductList;
    private Button back;
    private List<Product> productList;
    private CateGoryAdapter adapter = new CateGoryAdapter();
    private String cateGory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_product_list);
        Intent i = getIntent();
        cateGory = i.getStringExtra("category");
        System.out.println("收到的cateGory是："+cateGory);

        cateGoryProductList = (ListView) findViewById(R.id.category_list_view);
        back = (Button) findViewById(R.id.backToCateGory);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cateGoryProductList.setOnItemClickListener(this);
        new CateGoryAsyn().execute();

        MyApp.FinishListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product product = productList.get(position);
        Intent i  = new Intent(CartGoryList.this,ProductDetails.class);
        i.putExtra("product",product);
        startActivity(i);
    }

    private class CateGoryAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return productList.size();
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
            CateGoryHolder holder ;
            if(convertView==null){
                convertView = LayoutInflater.from(CartGoryList.this).inflate(R.layout.category_product_item,null,false);
                holder = new CateGoryHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.category_list_product_image);
                holder.name= (TextView) convertView.findViewById(R.id.category_list_product_name);
                holder.price = (TextView) convertView.findViewById(R.id.category_list_product_price);
                convertView.setTag(holder);
            }else{
                holder = (CateGoryHolder) convertView.getTag();
            }
            Picasso.with(parent.getContext()).load(CONST.HOST+productList.get(position).
                    getImgsrc()).placeholder(R.drawable.dot_current).into(holder.image);
            holder.name.setText(productList.get(position).getName());
            holder.price.setText(productList.get(position).getPrice()+"");
            return convertView;
        }
        public class CateGoryHolder{
            private ImageView image;
            private TextView name;
            private TextView price;
        }
    }
    private class CateGoryAsyn extends AsyncTask<Void,Void,ResponseObject> {
        @Override
        protected ResponseObject doInBackground(Void... params) {
            List<NameValuePair> cateGoryParams = new ArrayList<>();
            cateGoryParams.add(new BasicNameValuePair("category",cateGory));
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost  = new HttpPost(CONST.CATEGORY);

            if(readUtil.GET_SESSIONID(CartGoryList.this)!=null){
                httpPost.setHeader("Cookie",readUtil.GET_SESSIONID(CartGoryList.this));
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(cateGoryParams, HTTP.UTF_8));
                HttpResponse response =  httpClient.execute(httpPost);
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
            if(responseObject!=null){
                if(responseObject.getState()==1){
                    productList = (List<Product>) responseObject.getDatas();
                    cateGoryProductList.setAdapter(adapter);
                }
            }else{
                Toast.makeText(CartGoryList.this, "网络好像有问题....", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject<List<Product>>  responseObject=null;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<List<Product>>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }

        return  responseObject;
    }
}
