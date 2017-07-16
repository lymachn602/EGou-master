package com.dwg.egou.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/4/30.
 */

public class OrderItem
{

    public OrderItem()
    {
    }

    public String getOrder_id()
    {
        return order_id;
    }

    public void setOrder_id(String orderId)
    {
        order_id = orderId;
    }

    public String getProduct_id()
    {
        return product_id;
    }

    public void setProduct_id(String productId)
    {
        product_id = productId;
    }

    public int getBuynum()
    {
        return buynum;
    }

    public void setBuynum(int buynum)
    {
        this.buynum = buynum;
    }

    public double getMoney()
    {
        return money;
    }

    public void setMoney(double money)
    {
        this.money = money;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }
    public String getProductImageSrc() {
        return productImageSrc;
    }

    public void setProductImageSrc(String productImageSrc) {
        this.productImageSrc = productImageSrc;
    }
    public Timestamp getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Timestamp add_time) {
        this.add_time = add_time;
    }
    private String order_id;
    private String product_id;
    private int buynum;
    private double money;
    private String productName;
    private String productImageSrc;
    private Timestamp add_time;

}