package com.github.duanyashu.chartscp;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

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

    private java.util.Calendar startTime;

    private Calendar endTime;

    private int length;

    private int interval;

    private  boolean dataNonzero;
    private String xCellFormat;


    public final static int YEAR = 1;
    public final static int MONTH = 2;
    public final static int DATE = 5;
    public final static int HOUR = 10;
    public final static int MINUTE = 12;
    public final static int SECOND = 13;
    public final static int WEEK = 90;

    public final static int MINUTE_WHOLE_HOUR = 91;
    public final static int HOUR_WHOLE_DAY = 92;
    public final static int DAY_WHOLE_MONTH = 93;
    public final static int MONTH_WHOLE_YEAR = 94;
    public final static int QUARTER = 95;


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

        private int length;

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
            if (!"".equals(startTime.trim())&&null!=startTime){
                try {
                    this.startTime=DateUtils.strConvertCalendar(startTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }
        public Builder setStartTime(Date startTime) {
            this.startTime=DateUtils.getCalendar(startTime.getTime());
            return this;
        }

        public Builder setEndTime(String endTime) {
            if (!"".equals(endTime.trim())&&null!=endTime){
                try {
                    Calendar endTimeCalendar = DateUtils.strConvertCalendar(endTime);
                    if (endTimeCalendar!=null && calendarField<90){
                        endTimeCalendar.add(calendarField,+1);
                    }
                    this.endTime = endTimeCalendar;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }
        public Builder setEndTime(Date endTime) {
            if (endTime!=null){
                Calendar endTimeCalendar = DateUtils.getCalendar(endTime.getTime());
                if (calendarField<90){
                    endTimeCalendar.add(calendarField,+1);
                }
                this.endTime = endTimeCalendar;
            }
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
         * x轴数据格式化
         * @param xCellFormat
         * @return
         */
        public Builder setXCellFormat(String xCellFormat) {
            this.xCellFormat = xCellFormat.trim();
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


    private void initLengthAndInterval() {
        length = length == 0 ? 1 : length;
    }
    /**
     * 周数据
     * @param <T>
     * @return
     */
    private <T> T generatorWeek(Class<T> chartscpResultSub){
        initLengthAndInterval();
        String format = getFormat(calendarField);
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Calendar calendar = endTime==null ? Calendar.getInstance():DateUtils.getCalendar(endTime.getTimeInMillis());
        int dayOfWeek = DateUtils.getDayOfWeek(endTime==null?null:endTime.getTime());
        calendar.add(Calendar.DATE,7-dayOfWeek);
        Date endTime = calendar.getTime();
        int l = (length * 7) - 1;
        int il = length*7;
        int iterationLength = il;
        if (dataNonzero){
            iterationLength =(iterationLength / interval)+1;
            l+=interval;
        }
        calendar.add(Calendar.DATE,-l);
        Date startTime = calendar.getTime();
        if (dataNonzero&& il%interval!=0){
            iterationLength ++;
        }
        for (int i = 0; i <iterationLength; i++) {
            dates.add(DateUtils.formatDate(calendar.getTime(),format));
            counts.add(0);
            calendar.add(Calendar.DATE,+interval);
        }
        ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
        chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
        format(Calendar.DATE, chartscpResult);
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
        return (T) chartscpResult;
    }
    private <T> T generatorMonth(Class<T> chartscpResultSub){
        initLengthAndInterval();
        String format = getFormat(calendarField);
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Calendar calendar = DateUtils.getYMD();
        if (endTime!=null){
            calendar= endTime;
        }
        int actualMaximum = calendar.getActualMaximum(Calendar.DATE);
        calendar.add(Calendar.DATE,(actualMaximum-calendar.get(Calendar.DATE)));
        Date endTime = calendar.getTime();
        int startDayNum = actualMaximum-1;
        if (dataNonzero){
            startDayNum+=interval;
        }
        calendar.add(Calendar.DATE,-startDayNum);
        calendar.add(Calendar.MONTH,-(length-1));
        Date startTime = calendar.getTime();
        length=DateUtils.dateDiffCalender(Calendar.DATE,endTime,startTime)+1;
        int il =length / interval;
        if (dataNonzero&& length%interval!=0){
            il ++;
        }
        for (int i = 0; i <il; i++) {
            dates.add(DateUtils.formatDate(calendar.getTime(),format));
            counts.add(0);
            calendar.add(Calendar.DATE,+interval);
        }
        ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
        chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
        format(Calendar.MONTH, chartscpResult);
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
        return (T) chartscpResult;
    }


    private <T> T generatorYear(Class<T> chartscpResultSub){
        initLengthAndInterval();
        String format = getFormat(calendarField);
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Calendar calendar = DateUtils.getYM();
        if (endTime!=null){
            calendar= endTime;
        }
        calendar.add(Calendar.MONTH,(11-calendar.get(Calendar.MONTH)));
        Date endTime = calendar.getTime();
        int startDayNum = 11;
        if (dataNonzero){
            startDayNum+=interval;
        }
        calendar.add(Calendar.MONTH,-startDayNum);
        calendar.add(Calendar.YEAR,-(length-1));
        Date startTime = calendar.getTime();
        length=DateUtils.dateDiffCalender(Calendar.MONTH,endTime,startTime)+1;
        int il =length / interval;
        if (dataNonzero&& length%interval!=0){
            il ++;
        }
        for (int i = 0; i <il; i++) {
            dates.add(DateUtils.formatDate(calendar.getTime(),format));
            counts.add(0);
            calendar.add(Calendar.MONTH,+interval);
        }
        ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
        chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
        format(Calendar.MONTH, chartscpResult);
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
        return (T) chartscpResult;
    }
    private <T> T generatorDay(Class<T> chartscpResultSub){
        initLengthAndInterval();
        String format = getFormat(calendarField);
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Calendar calendar = DateUtils.getYMDH();
        if (endTime!=null){
            calendar= endTime;
        }
        calendar.add(Calendar.HOUR,(23-calendar.get(Calendar.HOUR_OF_DAY)));
        Date endTime = calendar.getTime();
        int startDayNum = 23;
        if (dataNonzero){
            startDayNum+=interval;
        }
        calendar.add(Calendar.HOUR,-startDayNum);
        calendar.add(Calendar.DATE,-(length-1));
        Date startTime = calendar.getTime();
        length=DateUtils.dateDiffCalender(Calendar.HOUR,endTime,startTime)+1;
        int il =length / interval;
        if (dataNonzero&& length%interval!=0){
            il ++;
        }
        for (int i = 0; i <il; i++) {
            dates.add(DateUtils.formatDate(calendar.getTime(),format));
            counts.add(0);
            calendar.add(Calendar.HOUR,+interval);
        }
        ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
        chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
        format(Calendar.HOUR, chartscpResult);
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
        return (T) chartscpResult;
    }
    private <T> T generatorHour(Class<T> chartscpResultSub){
        initLengthAndInterval();
        String format = getFormat(calendarField);
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Calendar calendar = DateUtils.getYMDHM();
        if (endTime!=null){
            calendar= endTime;
        }
        calendar.add(Calendar.MINUTE,(59-calendar.get(Calendar.MINUTE)));
        Date endTime = calendar.getTime();
        int startDayNum = 59;
        if (dataNonzero){
            startDayNum += interval;
        }
        calendar.add(Calendar.MINUTE,-startDayNum);
        calendar.add(Calendar.HOUR,-(length-1));
        Date startTime = calendar.getTime();
        length=DateUtils.dateDiffCalender(Calendar.MINUTE,endTime,startTime)+1;
        int il = length / interval;
        if (dataNonzero && length%interval!=0){
            il++;
        }
        for (int i = 0; i <il; i++) {
            dates.add(DateUtils.formatDate(calendar.getTime(),format));
            counts.add(0);
            calendar.add(Calendar.MINUTE,+interval);
        }
        ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
        chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
        format(Calendar.MINUTE, chartscpResult);
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
        return (T) chartscpResult;
    }

    /**
     * 季度
     * @param chartscpResultSub
     * @param <T>
     * @return
     */
    private <T> T generatorQuarter(Class<T> chartscpResultSub){
        initLengthAndInterval();
        String format = getFormat(calendarField);
        dataNonzero=false;
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Calendar calendar = DateUtils.getYM();
        if (endTime!=null){
            calendar= endTime;
        }
        //((MONTH(C2)-1)/3)+1
        //获取月
        int currentMonth = calendar.get(Calendar.MONTH);
        //获取季度
        int i1 = (currentMonth/3)+1;
        //获取季度开始的月
        int startMonth = (i1 - 1) * 3;
        //当前月份需要前推几月是开始月份
        int i2 = ((length - 1) * 3) + (currentMonth-startMonth);
        calendar.add(Calendar.MONTH,-i2);
        Date startTime = calendar.getTime();
        for (int i = 0; i <3*length; i++) {
            dates.add(DateUtils.formatDate(calendar.getTime(),format));
            counts.add(0);
            if (i<3*length-1){
                calendar.add(Calendar.MONTH,+1);
            }
        }
        Date endTime = calendar.getTime();
        ChartscpResult  chartscpResult = createChartscpResult(chartscpResultSub, dates, counts, startTime, endTime);
        chartscpResult.setResultDateFormat(javaDateFormatToMysqlDateFormat(format));
        format(Calendar.MONTH, chartscpResult);
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
        return (T) chartscpResult;
    }


    /**
     * 构建初始化折线图数据
     * @return
     */
    private <T> T generatorData(Class<T> chartscpResultSub) {
        if(calendarField==WEEK){
            return  generatorWeek(chartscpResultSub);
        }
        if(calendarField==DAY_WHOLE_MONTH){
            return  generatorMonth(chartscpResultSub);
        }
        if(calendarField==MONTH_WHOLE_YEAR){
            return  generatorYear(chartscpResultSub);
        }
        if(calendarField==HOUR_WHOLE_DAY){
            return  generatorDay(chartscpResultSub);
        }
        if(calendarField==MINUTE_WHOLE_HOUR){
            return  generatorHour(chartscpResultSub);
        }
        if(calendarField==QUARTER){
            return  generatorQuarter(chartscpResultSub);
        }

        //设置length的默认值
        length = length==0? 7:length;
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
        if (dataNonzero&& interval!=0){
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
        chartscpResult.setCalendarField(calendarField);
        chartscpResult.setxCellFormat(xCellFormat);
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
      /* if (containsDateFormat(xCellFormat,"yy","M","d","H","m","s")){
           return xCellFormat;
       }*/
        String format;
        String xCellFmt;
        switch (field){
            case Calendar.MINUTE:
                xCellFmt="HH:mm";
                format= length>60?"yyyy-MM-dd HH:mm":xCellFmt;
                break;
            case ChartscpUtils.MINUTE_WHOLE_HOUR:
                xCellFmt="HH:mm";
                format= length>1?"yyyy-MM-dd HH:mm":xCellFmt;
                break;
            case Calendar.HOUR:
                xCellFmt="HH";
                format= length>24?"yyyy-MM-dd HH":xCellFmt;
                break;
            case ChartscpUtils.HOUR_WHOLE_DAY:
                xCellFmt="HH";
                format= length>1?"yyyy-MM-dd HH":xCellFmt;
                break;
            case Calendar.DATE:
                xCellFmt="MM-dd";
                format= length>30?"yyyy-MM-dd":xCellFmt;
                break;
            case ChartscpUtils.DAY_WHOLE_MONTH:
                xCellFmt="MM-dd";
                format= length>1?"yyyy-MM-dd":xCellFmt;
                break;
            case Calendar.MONTH:
                xCellFmt="MM";
                format= length>12?"yyyy-MM":xCellFmt;
                break;
            case ChartscpUtils.MONTH_WHOLE_YEAR:
                xCellFmt="MM";
                format= length>1?"yyyy-MM":xCellFmt;
                break;
            case Calendar.YEAR:
                xCellFmt = format = "yyyy";
                break;
            case Calendar.SECOND:
                xCellFmt="HH:mm:ss";
                format= length>60?"yyyy-MM-dd HH:mm:ss":xCellFmt;
                break;
            case ChartscpUtils.WEEK:
                format= "yyyy-MM-dd";
                xCellFmt="星期%s";
                break;
            case ChartscpUtils.QUARTER:
                format="yyyy-MM";;
                xCellFmt="%s季度";
                break;
            default:
                xCellFmt = format="yyyy-MM-dd HH:mm:ss";
                break;
        }
        if (!containsDateFormat(xCellFormat,"yy","M","d","H","m","s","%d","%s")){
            xCellFormat=xCellFmt;
        }
        return  format;
    }

    public static boolean containsDateFormat (String dateFormat, String... format) {
        if (!"".equals(dateFormat)&&null!=dateFormat){
            for (String s : format) {
                if (dateFormat.contains(s)){
                    return true;
                }
            }
        }
        return false;
    }

    private void format(int field, ChartscpResult chartscpResult) {
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(chartscpResult.getStartTime());
        Calendar endCalender = Calendar.getInstance();
        endCalender.setTime(chartscpResult.getEndTime());
        switch (field){
            case Calendar.SECOND:
                chartscpResult.setGroupByDateFormat("%Y-%m-%d %H:%i:%s");
                break;
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
                int dayOfMonth = endCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
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
        String str = javaFormat.replace("yyyy", "%Y").replace("MM", "%m")
                .replace("dd", "%d").replace("HH", "%H")
                .replace("mm", "%i").replace("ss", "%s");
        str = str.replace("yy", "%y").replace("M", "%c");
        if (!str.contains("%d")){
            str = str.replace("d", "%e");
        }
        if (!str.contains("%m")){
            str = str.replace("m", "%i");
        }
        if (!str.contains("%H")){
            str = str.replace("H", "%k");
        }
        if (!str.contains("%s")){
            str = str.replace("s", "%s");
        }
        return  str;
    }
}
