package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
 * Created by Administrator on 2016/5/3.
 */
public class SearchActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private EditText search ;
    private Button cancel;
    private List<Product> productList;
    private ListView searchListView;
    private SearchAdapter adapter = new SearchAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_product);
        search = (EditText) findViewById(R.id.search_product);
        cancel = (Button) findViewById(R.id.cancel);
        searchListView = (ListView) findViewById(R.id.search_product_list);
        cancel.setOnClickListener(this);
        searchListView.setOnItemClickListener(this);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){//隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    search();
                }
                return false;
            }
        });
        adjustSearchImageSize();
        MyApp.FinishListener(this);
    }

    private void adjustSearchImageSize() {
        Drawable drawable =  getResources().getDrawable(R.drawable.search);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 3, drawable.getIntrinsicHeight() / 3);
        search.setCompoundDrawables(drawable,null, null, null);

    }

    private void search() {
        String searchContent = search.getText().toString();
        if(!TextUtils.isEmpty(searchContent)){
            new SearchAsyn().execute(searchContent);
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product product = productList.get(position);
        Intent i  = new Intent(SearchActivity.this,ProductDetails.class);
        i.putExtra("product",product);
        startActivity(i);
    }

    private class SearchAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.category_product_item,null,false);
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
    private class SearchAsyn extends AsyncTask<String,Void,ResponseObject> {
        @Override
        protected ResponseObject doInBackground(String... params) {
            List<NameValuePair> searchParams = new ArrayList<>();
            searchParams.add(new BasicNameValuePair("productName",params[0]));
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost  = new HttpPost(CONST.SEARCHPRODUCT);

            if(readUtil.GET_SESSIONID(SearchActivity.this)!=null){
                httpPost.setHeader("Cookie",readUtil.GET_SESSIONID(SearchActivity.this));
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(searchParams, HTTP.UTF_8));
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
            if(responseObject.getState()==1){
                productList = (List<Product>) responseObject.getDatas();
                searchListView.setAdapter(adapter);
            }
        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject<List<Product>>  responseObject;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<List<Product>>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }

        return  responseObject;
    }
}
