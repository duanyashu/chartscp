package com.github.duanyashu.chartscp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @description: 数据结果集
 * @author: duanyashu
 * @time: 2021-01-15 17:02
 */

public class ChartscpResult<T> {

    /**
     * x轴数据
     */
    private List<T> xCells;

    /**
     * 数据
     */
    private List<T> datas;

    private Date startTime;

    private Date endTime;

    /**
     * 结果日期格式
     */
    private String  resultDateFormat;

    /**
     * 分组日期格式
     */
    private String  groupByDateFormat;

    /**
     * 时间间隔
     */
    private int  interval;

    /**
     * sql业务条件
     */
    private T where;


    public ChartscpResult() {
    }

    public ChartscpResult(List<T> xCells, List<T> datas, Date startTime, Date endTime, int interval) {
        this.xCells = xCells;
        this.datas = datas;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
    }

    public List<T> getxCells() {
        return xCells;
    }

    public void setxCells(List<T> xCells) {
        this.xCells = xCells;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getResultDateFormat() {
        return resultDateFormat;
    }

    public void setResultDateFormat(String resultDateFormat) {
        this.resultDateFormat = resultDateFormat;
    }

    public String getGroupByDateFormat() {
        return groupByDateFormat;
    }

    public void setGroupByDateFormat(String groupByDateFormat) {
        this.groupByDateFormat = groupByDateFormat;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public T getWhere() {
        return where;
    }

    public void setWhere(T where) {
        this.where = where;
    }

    /**
     * 更新数据
     * @param list
     */
    public void updateData(List<? extends ChartscpMap> list) throws Exception {
        for (ChartscpMap chartscpMap : list) {
            int index = xCells.indexOf(chartscpMap.getXcell());
            if (index!=-1){
                Integer data = chartscpMap.getData();
                if (null!=data){
                    datas.set(index, (T) data);
                }
                Field[] declaredFields = this.getClass().getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.getType() ==List.class){
                        Method thisGet = this.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                        List datasx = (List) thisGet.invoke(this);
                        Method get = chartscpMap.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                        Object invoke = get.invoke(chartscpMap);
                        datasx.set(index,invoke);
                    }
                }
            }
        }
    }

    // 把一个字符串的第一个字母大写、效率是最高的、
    private static String getMethodName(String fildeName){
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
