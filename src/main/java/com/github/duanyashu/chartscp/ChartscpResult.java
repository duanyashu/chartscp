package com.github.duanyashu.chartscp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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
     * 时间间隔
     */
    private int  interval;

    /**
     * sql业务条件
     */
    private T where;

    private String  title;



    /**
     * 数据是否不能为0（eg:价格 没有变化显示原价）
     */
    private  boolean dataNonzero;

    private int calendarField;

    private String xCellFormat;

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

    public void setCalendarField(int calendarField) {
        this.calendarField = calendarField;
    }

    public void setxCellFormat(String xCellFormat) {
        this.xCellFormat = xCellFormat;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 更新数据
     * @param list
     */
    public void updateData(List<? extends ChartscpResultMap> list){
        if (this.interval==0){
            setAtWillData( list);
        }else{
            setContinuityData(list);
        }
    }


    private static final String[] cnNum ={"一","二","三","四","五","六","日"};
    /**
     * 设置时间连续数据
     * @param list
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setContinuityData(List<? extends ChartscpResultMap> list){
        boolean first=true;
        for (ChartscpResultMap chartscpMap : list) {
            int index = xCells.indexOf(chartscpMap.getXcell());
            if (index!=-1) {
                Object data = chartscpMap.getData();
                if (setData(index, datas, data, first)){
                    trimScopeData();
                }
                if (chartscpMap.getClass() != ChartscpResultMap.class) {
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
        if (first && dataNonzero){
            datas.remove(0);
            //处理扩展类
            if (list.get(0).getClass() != ChartscpResultMap.class) {
                //获取当前类的扩展类中的属性方法
                Field[] declaredFields = this.getClass().getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.getType() == List.class) {
                        try {
                            Method thisGet = this.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                            List datasx = (List) thisGet.invoke(this);
                            datasx.remove(0);
                            Method thisSet = this.getClass().getMethod("set" + getMethodName(declaredField.getName()),List.class);
                            thisSet.invoke(this,datasx);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            trimScopeData();
        }
        if (calendarField==ChartscpUtils.DAY_WHOLE_WEEK){
            List<String> strings = Arrays.asList(formatStr(xCellFormat, 1),formatStr(xCellFormat, 2), formatStr(xCellFormat, 3), formatStr(xCellFormat, 4),
                    formatStr(xCellFormat, 5), formatStr(xCellFormat, 6), formatStr(xCellFormat, 7));
            int a=0;
            for (int i = 0; i < xCells.size(); i++) {
                int i1 = a % strings.size();
                xCells.set(i, (T) strings.get(i1));
                a+=interval;
            }
        }else if (calendarField==ChartscpUtils.WEEK){
            //扩展类处理
            kzClassWeekDataHandler();
            List<String> xCells = new ArrayList<>();
            List<Integer> datas = new ArrayList<>();
            int j = 1;
            for (int i = 0; i <this.xCells.size() ; i++) {
                //格式化xCell显示内容
                String week = formatStr(xCellFormat,j);
                if (!xCells.contains(week)){
                    xCells.add(week);
                    datas.add(0);
                }
                int index = xCells.indexOf(week);
                int i2 = datas.get(index) +  ((Integer) this.datas.get(i)).intValue();
                datas.set(index,i2);
                if (i>0 && i%7==0){
                    j++;
                }
            }
            this.xCells = (List<T>) xCells;
            this.datas= (List<T>) datas;

        }else if (calendarField==ChartscpUtils.QUARTER){
            //处理扩展类
            kzClassQuarterDataHandler();
            List<String> xCells = new ArrayList<>();
            List<Integer> datas = new ArrayList<>();
            for (int i = 0; i <this.xCells.size() ; i++) {
                //转换月为数字
                int  month = Integer.parseInt(this.xCells.get(i).toString().substring(5)) - 1;
                //计算月所在季度
                int i1 = (month/3)+1;
                //格式化xCell显示内容
                String quarter = formatStr(xCellFormat,i1);
                //获取年份
                Calendar calendar = DateUtils.getCalendar(endTime.getTime());
                calendar.add(Calendar.MONTH,-(this.xCells.size()-i)+1);
                int year = calendar.get(Calendar.YEAR);
                if (year<DateUtils.getYear()){
                    quarter = String.valueOf(year).substring(2)+"年"+quarter;
                }
                if (!xCells.contains(quarter)){
                    xCells.add(quarter);
                    datas.add(0);
                }
                int index = xCells.indexOf(quarter);
                int i2 = datas.get(index) +  ((Integer) this.datas.get(i)).intValue();
                datas.set(index,i2);

            }
            this.xCells = (List<T>) xCells;
            this.datas= (List<T>) datas;
        }else{
            xCells= xCells.stream().map(item->item= (T) DateUtils.dateformat(item.toString(),xCellFormat)).collect(Collectors.toList());
        }
    }

    private void kzClassWeekDataHandler(){
        if (this.getClass()!=ChartscpResult.class) {
            //获取当前类的扩展类中的属性方法
            Field[] declaredFields = this.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.getType() == List.class) {
                    try {
                        List<String> xCells = new ArrayList<>();
                        List<Integer> datas = new ArrayList<>();
                        Method thisGet = this.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                        List datasx = (List) thisGet.invoke(this);
                        int j = 1;
                        for (int i = 0; i < this.xCells.size(); i++) {
                            //格式化xCell显示内容
                            String week = formatStr(xCellFormat, j);
                            if (!xCells.contains(week)) {
                                xCells.add(week);
                                datas.add(0);
                            }
                            int index1 = xCells.indexOf(week);
                            int i2 = datas.get(index1) + ((Integer) datasx.get(i)).intValue();
                            datas.set(index1, i2);
                            if (i > 0 && i % 7 == 0) {
                                j++;
                            }
                        }
                        Method thisSet = this.getClass().getMethod("set" + getMethodName(declaredField.getName()), List.class);
                        thisSet.invoke(this, datas);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void kzClassQuarterDataHandler(){
        if (this.getClass()!=ChartscpResult.class){
            //获取当前类的扩展类中的属性方法
            Field[] declaredFields = this.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.getType() == List.class) {
                    try {
                        List<String> xCells = new ArrayList<>();
                        List<Integer> datas = new ArrayList<>();
                        Method thisGet = this.getClass().getMethod("get" + getMethodName(declaredField.getName()));
                        List datasx = (List) thisGet.invoke(this);
                        for (int i = 0; i <this.xCells.size() ; i++) {
                            //转换月为数字
                            int  month = Integer.parseInt(this.xCells.get(i).toString().substring(5)) - 1;
                            //计算月所在季度
                            int i1 = (month/3)+1;
                            //格式化xCell显示内容
                            String quarter = formatStr(xCellFormat,i1);
                            //获取年份
                            Calendar calendar = DateUtils.getCalendar(endTime.getTime());
                            calendar.add(Calendar.MONTH,-(this.xCells.size()-i)+1);
                            int year = calendar.get(Calendar.YEAR);
                            if (year<DateUtils.getYear()){
                                quarter = String.valueOf(year).substring(2)+"年"+quarter;
                            }
                            if (!xCells.contains(quarter)){
                                xCells.add(quarter);
                                datas.add(0);
                            }
                            int index1 = xCells.indexOf(quarter);
                            int i2 = datas.get(index1) +  ((Integer) datasx.get(i)).intValue();
                            datas.set(index1,i2);

                        }
                        Method thisSet = this.getClass().getMethod("set" + getMethodName(declaredField.getName()),List.class);
                        thisSet.invoke(this,datas);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String  formatStr(String source,int num) {
        if (!source.contains("%")){
            return source+cnNum[num-1];
        }
        return String.format(source, source.contains("%d") ? num : cnNum[num - 1]);
    }

    /**
     * 删除辅助数据
     */
    private void trimScopeData() {
        //如果是首次 删除超出查询范围的x轴数据
        xCells.remove(0);
        //恢复开始日期
        Calendar calendar = DateUtils.getCalendar(startTime.getTime());
        int field = calendarField;
        if (calendarField == ChartscpUtils.DAY_WHOLE_WEEK || calendarField == ChartscpUtils.HOUR_WHOLE_DAY) {
            field = ChartscpUtils.DATE;
        } else if (calendarField == ChartscpUtils.DAY_WHOLE_MONTH || calendarField == ChartscpUtils.MONTH_WHOLE_YEAR) {
            field = ChartscpUtils.MONTH;
        } else if (calendarField == ChartscpUtils.MINUTE_WHOLE_HOUR) {
            field = ChartscpUtils.MINUTE;
        }
        calendar.add(field, +interval);
        startTime = calendar.getTime();
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
    private void setAtWillData(List<? extends ChartscpResultMap> list){
        for (ChartscpResultMap chartscpMap : list) {
            xCells.add((T) chartscpMap.getXcell());
            datas.add((T) chartscpMap.getData());
            if (chartscpMap.getClass() != ChartscpResultMap.class) {
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
