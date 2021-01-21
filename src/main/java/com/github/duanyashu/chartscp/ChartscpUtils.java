package com.github.duanyashu.chartscp;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *eg : 默认查询7条数据 通过setLength指定   默认数据间隔1 通过setInterval指定
 *      //当天三班倒数据
 *      ChartscpResult build = new ChartscpUtils.Builder(Calendar.HOUR).setStartTime("2021-01-18 00:00:00").setEndTime("2021-01-18 23:59:59").setInterval(8).build();
 *      //最近7小时数据
 *      ChartscpResult build2 = new ChartscpUtils.Builder(Calendar.HOUR).build();
 *      //最近7小时非连续数据（只显示数据库有的数据）
 *      ChartscpResult build21 = new ChartscpUtils.Builder(Calendar.HOUR).setInterval(0).build();
 *      //最近7年
 *      ChartscpResult build5 = new ChartscpUtils.Builder(Calendar.YEAR).build();
 *      //最近7个月
 *      ChartscpResult build4 = new ChartscpUtils.Builder(Calendar.MONTH).build();
 *      //最近7天
 *      ChartscpResult build4 = new ChartscpUtils.Builder(Calendar.DATE).build();
 *
 *
 * @description: 实现初始化参数工具类
 * @author: duanyashu
 * @time: 2021-01-15 16:54
 */
public class ChartscpUtils<T> {


        private int calendarField;

        private Calendar startTime;

        private Calendar endTime;

        private int length;

        private int interval;

        private  boolean dataNonzero;

        private String xCellFormat;


    //构造方法
    private ChartscpUtils() {
    }

    //构造方法
    private ChartscpUtils(Builder builder){
        this.calendarField = builder.calendarField;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.length = builder.length;
        this.interval = builder.interval;
        this.dataNonzero= builder.dataNonzero;
        this.xCellFormat=builder.xCellFormat;

    }

    /**
     *静态内部类 Builder
     */

    public static class Builder{
        private int calendarField;

        private Calendar startTime;

        private Calendar endTime;

        private int length = 7;

        private int interval = 1;

        private  boolean dataNonzero = false;

        private  String xCellFormat;

        private Class <? extends ChartscpResult>  chartscpResultSub = ChartscpResult.class;

        //构造方法
        private Builder() {

        }
        /**
         * 显示日期格式  年（Calendar.YEAR） 月（Calendar.MONTH）  日（Calendar.DATE）  时（Calendar.HOUR）  分（Calendar.MINUTE）
         * @param calendarField
         * @return
         */
        public Builder(int calendarField) {
            this.calendarField = calendarField;
        }

        public Builder setStartTime(String startTime) {
            this.startTime=DateUtils.strConvertCalendar(startTime);
            return this;
        }

        public Builder setEndTime(String endTime) {
            Calendar endTimeCalendar = DateUtils.strConvertCalendar(endTime);
            if (endTimeCalendar!=null){
                endTimeCalendar.add(calendarField,+1);
            }
            this.endTime = endTimeCalendar;
            return this;
        }

        /**
         * 显示的长度 默认7
         * @param length
         * @return
         */
        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        /**
         * 间隔  两条数据的间隔
         * @param interval
         * @return
         */
        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        /**
         * x轴数据格式化
         * @param xCellFormat
         * @return
         */
        public Builder setXCellFormat(String xCellFormat) {
            this.xCellFormat = xCellFormat;
            return this;
        }
        /**
         * 数据是否不能为0（eg:价格 没有变化显示原价）
         * @param dataNonzero
         * @return
         */
        public Builder setDataNonzero (boolean dataNonzero) {
            this.dataNonzero = dataNonzero;
            return this;
        }
        //构建结果集对象
        public <T> T build(Class<T> chartscpResultSub) {
            return (T) new ChartscpUtils(this).generatorInitData(chartscpResultSub);
        }
        //构建结果集对象
        public ChartscpResult build() {
            return (ChartscpResult) new ChartscpUtils(this).generatorInitData(this.chartscpResultSub);
        }
    }




    /**
     * 构建初始化折线图数据
     * @return
     */
    private <T> T generatorInitData(Class<T> chartscpResultSub){
        if (endTime!=null && startTime!=null){
            length=DateUtils.dateDiffCalender(calendarField,endTime,startTime);
        }
        return generatorData(chartscpResultSub);
    }


    /**
     * 构建初始化折线图数据
     * @return
     */
    private <T> T generatorData(Class<T> chartscpResultSub) {

            String format = getFormat(calendarField);
            List<String> dates = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            //如果包括今天，计算开始日期时需要后推一天
            calendar.add(calendarField,+1);
            if (endTime!=null){
                calendar=DateUtils.getCalendar(endTime.getTimeInMillis());
            }
            //计算遍历次数 如果没有有结束日期 需要长度/间隔
            int iterationLength = endTime==null? length: interval==0? length:length/interval;
            length = interval==0? iterationLength : interval==1 || endTime!= null ? iterationLength* interval: iterationLength* interval-1;
            if (dataNonzero){
                iterationLength++;
                length++;
            }
            calendar.add(calendarField,-length);
            Date startTime = calendar.getTime();
            if (interval>0){
                for (int i = 0; i <iterationLength; i++) {
                    dates.add(DateUtils.formatDate(calendar.getTime(),format));
                    counts.add(0);
                    if (i==iterationLength-1){
                        //间隔大于1需要的时区间时间 最后需要指定结束时间
                        calendar.add(calendarField, interval>1 && endTime!=null? +interval-1:0);
                    }else{
                        calendar.add(calendarField,+interval);
                    }
                }
            }else {
                calendar.add(calendarField,+length-1);
            }
            Date endTime = calendar.getTime();
            ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
            chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
            format(calendarField, chartscpResult);
            return (T) chartscpResult;
    }

    /**
     * 构建结果集对象
     * @param chartscpResultSub
     * @param dates
     * @param counts
     * @param startTime
     * @param endTime
     * @param <T>
     * @return
     */
    private <T> ChartscpResult createChartscpResult(Class<T> chartscpResultSub, List<String> dates, List<Integer> counts, Date startTime, Date endTime) {
        try {
            ChartscpResult chartscpResult;
            if (chartscpResultSub ==ChartscpResult.class){
                chartscpResult = new ChartscpResult();
            }else {
                chartscpResult= (ChartscpResult) chartscpResultSub.newInstance();
                Method[] methods = chartscpResultSub.getDeclaredMethods ();
                for (Method method : methods){
                    if (method.getName().contains("set")){
                        method.invoke(chartscpResult,new ArrayList<>(counts));
                    }
                }
            }
            chartscpResult.setxCells(dates);
            chartscpResult.setDatas(counts);
            chartscpResult.setStartTime(startTime);
            chartscpResult.setEndTime(endTime);
            chartscpResult.setInterval(interval);
            chartscpResult.setDataNonzero(dataNonzero);
            return chartscpResult;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new ChartscpResult();
    }


    private String getFormat(int field) {
        if (!"".equals(xCellFormat)&& null!=xCellFormat){
            if (DateUtils.EN_DATE_PATTERN.contains(xCellFormat)||DateUtils.CN_DATE_PATTERN.contains(xCellFormat)||DateUtils.CN_DATETIME_PATTERN1.contains(xCellFormat)
                    ||DateUtils.NORM_DATETIME_PATTERN.contains(xCellFormat)|| DateUtils.SPECIAL_SIMPLE_DATE_PATTERN.contains(xCellFormat)){
                return  xCellFormat;
            }
        }
        String format;
        switch (field){
            case Calendar.MINUTE:
                format="HH:mm";
                break;
            case Calendar.HOUR:
                format="HH";
                break;
            case Calendar.DATE:
                format="MM-dd";
                break;
            case Calendar.MONTH:
                format="MM";
                break;
            case Calendar.YEAR:
                format="yyyy";
                break;
            default:
                format="yyyy-MM-dd HH:mm:ss";
                break;
        }
        return  format;
    }
    private static void format(int field, ChartscpResult chartscpResult) {
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(chartscpResult.getStartTime());
        Calendar endCalender = Calendar.getInstance();
        endCalender.setTime(chartscpResult.getEndTime());
        switch (field){
            case Calendar.MINUTE:
                startCalender.set(Calendar.SECOND,0);
                chartscpResult.setStartTime(startCalender.getTime());
                endCalender.set(Calendar.SECOND,59);
                chartscpResult.setEndTime(endCalender.getTime());
                chartscpResult.setGroupByDateFormat("%Y-%m-%d %H:%i");
                break;
            case Calendar.HOUR:
                startCalender.set(Calendar.MINUTE,0);
                startCalender.set(Calendar.SECOND,0);
                chartscpResult.setStartTime(startCalender.getTime());
                endCalender.set(Calendar.MINUTE,59);
                endCalender.set(Calendar.SECOND,59);
                chartscpResult.setEndTime(endCalender.getTime());
                chartscpResult.setGroupByDateFormat("%Y-%m-%d %H");
                break;
            case Calendar.DATE:
                startCalender.set(startCalender.get(Calendar.YEAR),startCalender.get(Calendar.MONTH),startCalender.get(Calendar.DATE),0,0,0);
                endCalender.set(endCalender.get(Calendar.YEAR),endCalender.get(Calendar.MONTH),endCalender.get(Calendar.DATE),23,59,59);
                chartscpResult.setStartTime(startCalender.getTime());
                chartscpResult.setEndTime(endCalender.getTime());
                chartscpResult.setGroupByDateFormat("%Y-%m-%d");
                break;
            case Calendar.MONTH:
                startCalender.set(startCalender.get(Calendar.YEAR),startCalender.get(Calendar.MONTH),1,0,0,0);
                int dayOfMonth = startCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
                endCalender.set(endCalender.get(Calendar.YEAR),endCalender.get(Calendar.MONTH),dayOfMonth,23,59,59);
                chartscpResult.setStartTime(startCalender.getTime());
                chartscpResult.setEndTime(endCalender.getTime());
                chartscpResult.setGroupByDateFormat("%Y-%m");
                break;
            case Calendar.YEAR:
                startCalender.set(startCalender.get(Calendar.YEAR),0,1,0,0,0);
                chartscpResult.setStartTime(startCalender.getTime());
                endCalender.set(endCalender.get(Calendar.YEAR),11,31,23,59,59);
                chartscpResult.setEndTime(endCalender.getTime());
                chartscpResult.setGroupByDateFormat("%Y");
                break;
            default:
                break;
        }
    }

    private String javaDateFormatToMysqlDateFormat(String javaFormat){
        return    javaFormat.replace("yyyy","%Y").replace("MM","%m")
                .replace("dd","%d").replace("HH","%H")
                .replace("mm","%i").replace("ss","%s");
    }
}
