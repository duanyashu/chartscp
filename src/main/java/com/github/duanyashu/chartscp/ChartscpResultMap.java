package com.github.duanyashu.chartscp;

/**
 * @description: sql 数据保存类
 * @author: duanyashu
 * @time: 2021-01-19 14:37
 */
public class ChartscpResultMap {

    private String xcell;

    private Object data;

    public String getXcell() {
        return xcell;
    }

    public void setXcell(String xcell) {
        this.xcell = xcell;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
