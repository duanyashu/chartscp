package com.github.duanyashu.chartscp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
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


    /**
     * 图例
     */
    private List<T> legend;

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
     * 数据是否不能为0（eg:价格 没有变化显示原价）
     */
    private  boolean dataNonzero;

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

    public ChartscpResult(List<T> xCells, List<T> datas, List<T> legend, Date startTime, Date endTime, int interval) {
        this.xCells = xCells;
        this.datas = datas;
        this.legend=legend;
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

    public List<T> getLegend() {
        return legend;
    }

    public void setLegend(List<T> legend) {
        this.legend = legend;
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

    public void setDataNonzero(boolean dataNonzero) {
        this.dataNonzero = dataNonzero;
    }

    /**
     * 更新数据
     * @param list
     */
    public void updateData(List<? extends ChartscpMap> list){
        if (this.interval==0){
            setAtWillData( list);
        }else{
            setContinuityData(list);
        }
    }

    /**
     * 设置时间连续数据
     * @param list
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setContinuityData(List<? extends ChartscpMap> list){
        boolean first=true;
        for (ChartscpMap chartscpMap : list) {
            int index = xCells.indexOf(chartscpMap.getXcell());
            if (index!=-1) {
                Integer data = chartscpMap.getData();
                if (setData(index, datas, data, first)){
                    //如果是首次 删除超出查询范围的x轴数据
                    xCells.remove(0);
                }
                if (chartscpMap.getClass() != ChartscpMap.class) {
                    //获取当前类的扩展类中的属性方法
                    Field[] declaredFields = this.getClass().getDeclaredFields();
                    for (Field declaredField : declaredFields) {
                        if (declaredField.getType() == List.class) {
                            try {
                                Method thisGet = this.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                                List datasx = (List) thisGet.invoke(this);
                                Method get = chartscpMap.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                                Object invoke = get.invoke(chartscpMap);
                                setData(index, datasx, invoke,first);
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                first = false;
            }
        }
    }

    private boolean setData(int index, List datas, Object data,boolean first) {
        if (null!= data){
            int totalSize = dataNonzero? datas.size()-1:index;
            for (int i = index; i <=totalSize; i++) {
                datas.set(i, data);
            }
        }
        //如果是首次 删除超出查询范围的数据
        if (first && dataNonzero){
            datas.remove(0);
            return true;
        }
        return false;
    }

    /**
     * 设置不连续数据
     * @param list
     */
    private void setAtWillData(List<? extends ChartscpMap> list){
        for (ChartscpMap chartscpMap : list) {
            xCells.add((T) chartscpMap.getXcell());
            datas.add((T) chartscpMap.getData());
            if (chartscpMap.getClass() != ChartscpMap.class) {
                Field[] declaredFields = this.getClass().getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.getType() == List.class) {
                        try {
                            Method thisGet = this.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                            List datasx = (List) thisGet.invoke(this);
                            Method get = chartscpMap.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                            Object invoke = get.invoke(chartscpMap);
                            datasx.add(invoke);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 把字符串的第一个字母大写、效率是最高的
     * @param fildeName
     * @return
     */
    private static String getMethodName(String fildeName){
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
