package com.dwg.egou.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwg.egou.config.CONST;
import com.dwg.egou.Acitivty.GenerateOrder;
import com.dwg.egou.MyApp;
import com.dwg.egou.utils.NetworkDetector;
import com.dwg.egou.R;
import com.dwg.egou.entity.Cart;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.utils.readUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/4.
 */
public class CartFragment extends android.support.v4.app.Fragment implements FragmentListener, View.OnClickListener{
    private ListView cartItemView;
    private List<Cart> cartList = new ArrayList<>();
    private MyAdapter adapter= new MyAdapter();
    private TextView totalMoney;
    private Button payment;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart,container,false);
        cartItemView = (ListView) view.findViewById(R.id.cartItemView);
        totalMoney = (TextView) view.findViewById(R.id.totalMoney);
        payment = (Button) view.findViewById(R.id.payment);
        payment.setOnClickListener(this);
        if(MyApp.user!=null){
            initCartItemView();
        }
        return view;
    }

    private void initCartItemView() {
        if(MyApp.user!=null){
            new GetCartAsyn().execute();
        }else{
            cartList.clear();
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void upDateSet() {
        initCartItemView();
        totalMoney.setText("￥0.0");
    }

    @Override
    public void onClick(View v) {

            switch(v.getId()){
                case R.id.payment:{
                    if (NetworkDetector.netWorkIsValid(getActivity())){
                        toPayment();
                    }else{
                        Toast.makeText(getActivity(), "网络有问题", Toast.LENGTH_SHORT).show();
                    }

                }break;
            }


    }

    private void toPayment() {//付款
        if(MyApp.user==null){
            Toast.makeText(getActivity(), "没有登录", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(cartList==null||cartList.size()==0){
            Toast.makeText(getActivity(), "没有数据", Toast.LENGTH_SHORT).show();
            return ;
        }
        Intent i = new Intent(getActivity(),GenerateOrder.class);
        startActivityForResult(i,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1&&requestCode==resultCode){
            int state = data.getIntExtra("state",0);
            if(state==1){
                Toast.makeText(getActivity(), "结算完成", Toast.LENGTH_SHORT).show();
                upDateSet();//更新数据
            }
        }
    }

    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cartList.size();
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
            MyHolder myHolder;
            MyListener myListener;
            if(convertView == null){
                myHolder = new MyHolder();

                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.cart_item,null,false);
                myHolder.productImage = (ImageView) convertView.findViewById(R.id.productImage);
                myHolder.productName = (TextView) convertView.findViewById(R.id.product_name);
                myHolder.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
                myHolder.productNumber = (TextView) convertView.findViewById(R.id.productNumber);
                myHolder.addProduct = (Button) convertView.findViewById(R.id.addNumber);
                myHolder.reductProduct = (Button) convertView.findViewById(R.id.reduceNumber);

                myListener = new MyListener();
                myHolder.addProduct.setOnClickListener(new MyListener());
                myHolder.reductProduct.setOnClickListener(new MyListener());

                convertView.setTag(myHolder);

            }else{
                myHolder = (MyHolder) convertView.getTag();
            }
            myHolder.reductProduct.setTag(position);
            myHolder.addProduct.setTag(position);

            Cart cart  = cartList.get(position);
            System.out.println("position>>>"+position);
            myHolder.productName.setText(cart.getProduct_name());
            Picasso.with(parent.getContext()).load(CONST.HOST+cartList.
            get(position).getProduct_image()).placeholder(R.drawable.dot_current).into(myHolder.productImage);
            myHolder.productNumber.setText(cart.getProduct_num()+"");
            myHolder.productPrice.setText("￥"+cart.getProduct_price());
            return convertView;

    }
        public  class MyListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                Button bt = (Button) v;
                int  position = (int) v.getTag();
                System.out.println("选择了:"+position);

                    switch (v.getId()){
                        case R.id.addNumber :{
                            new AddProductAsyn().execute(position);
                            Toast.makeText(getActivity(), "增加成功", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }break;
                        case R.id.reduceNumber:{
                            new ReduceProductAsyn().execute(position);
                            System.out.println(position+":减少");
                            Toast.makeText(getActivity(), "减少成功", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }break;
                    }
                    new GetCartAsyn().execute();


            }
            }
       }
    public class MyHolder{
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productNumber;
        private Button addProduct;
        private Button reductProduct;
    }
    public class GetCartAsyn extends AsyncTask<Void,Void,ResponseObject>{

        @Override
        protected ResponseObject doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet  = new HttpGet(CONST.CART);
            if(readUtil.GET_SESSIONID(getActivity())!=null){
                httpGet.setHeader("Cookie",readUtil.GET_SESSIONID(getActivity()));
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
            if(responseObject!=null&&responseObject.getState()==1){
                cartList = (List<Cart>) responseObject.getDatas();
                if(cartList.size()!=0){
                    cartItemView.setAdapter(adapter);//渲染数据
                    updateTotalMoney();//改变总价
                }
            }
//            else{//还没有登录
//                Toast.makeText(getActivity(), "网络有问题", Toast.LENGTH_SHORT).show();
//            }
            cartItemView.setAdapter(adapter);
        }
    }

    private void updateTotalMoney() {
        double sum = 0;
        for (Cart cart:cartList){
            sum +=cart.getProduct_price()*cart.getProduct_num();
        }
        totalMoney.setText("￥"+sum);
    }
    public class AddProductAsyn extends AsyncTask<Integer,Void,Void>{

        @Override
        protected Void doInBackground(Integer... params) {
            int position = params[0];
            Cart cart = cartList.get(position);
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.ADDCART+"?id="+cart.getProduct_id());
            if(readUtil.GET_SESSIONID(getActivity())!=null){
                httpPost.setHeader("Cookie",readUtil.GET_SESSIONID(getActivity()));
            }

            try {
                client.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public class ReduceProductAsyn extends AsyncTask<Integer,Void,Void>{

        @Override
        protected Void doInBackground(Integer... params) {
            int position = params[0];
            Cart cart = cartList.get(position);
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.REDUCE+"?id="+cart.getProduct_id()+"&number="+cart.getProduct_num());
            if(readUtil.GET_SESSIONID(getActivity())!=null){
                httpPost.setHeader("Cookie",readUtil.GET_SESSIONID(getActivity()));
            }
            try {
                client.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject<List<Cart>>  responseObject;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<List<Cart>>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }

        return  responseObject;
    }
}
