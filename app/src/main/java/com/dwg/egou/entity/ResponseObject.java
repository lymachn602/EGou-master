package com.dwg.egou.entity;

/**
 * Created by Administrator on 2016/4/20.
 */
public class ResponseObject<T> {
    private String msg;
    private int state = 1;
    private T datas;
    private int page;
    private int size;
    private int count;
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public T getDatas() {
        return datas;
    }
    public void setDatas(T datas) {
        this.datas = datas;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public ResponseObject(int state,T datas) {
        // TODO Auto-generated constructor stub
        this.state = state;
        this.datas = datas;
    }

}