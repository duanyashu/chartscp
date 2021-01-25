# 图表折线图的好伴侣,快速生成数据

>开源协议： Apache License Version 2.0
 
 本工具可以通过指定参数生成图表曲线图,柱状图的初始数据，配合sql实现展示数据
 
 ### 使用格式
 
 	ChartscpResult build = new ChartscpUtils.Builder(ChartscpUtils.HOUR).setStartTime("2021-01-18 00:00:00").setEndTime("2021-01-18 23:59:59").setLength(7).setInterval(8).setDataNonzero(true).setXCellFormat("HH时").build();
	
	参数说明：
		Builder(ChartscpUtils.HOUR)是指定显示x轴格式，年（YEAR），月(MONTH)，日(DATE)，小时(HOUR)，分钟(MINUTE)，整周(WEEK)，整小时(MINUTE_WHOLE_HOUR)，整天(HOUR_WHOLE_DAY)，整月(DAY_WHOLE_MONTH)，整年(MONTH_WHOLE_YEAR)， 必填
	
		setLength() 这个方法用来指定显示的长度 默认是7 
			eg: Builder(ChartscpUtils.YEAR).setLength(3) 是显示最近3年 xCells=[2019,2020,2021] ）
	
		setStartTime() 和 setEndTime() 是指定日期范围，  指定了日期范围 setLength 失效
	
		setInterval() 是指定日期步长 默认是1，如果设置为 0 代表日期不需要连续,按数据库查询显示。		
			(eg:Builder(Calendar.YEAR).setLength(3).setInterval(2) 显示最近6年每两年作为一个单位 xCells=[2017,2019,2021])
			
		setDataNonzero(true) 这个方法用来设置 当数据为0，是否显示前一个值(eg: 显示价格变化) 默认是 false
	    
		setXCellFormat("HH时")  这个方法用来自定义显示的xCell日期格式 不设置显示默认格式
		
		build() 方法生成结果对象ChartscpResult 如果需要多个datas，可以通过自定义继承ChartscpResult，在build(扩展类)中传入实现。
			eg:扩展类
				public class Kz extends ChartscpResult {
					private List<Integer> datas1;
					public List<Integer> getDatas1() {
						return datas1;
					}
					public void setDatas1(List<Integer> datas1) {
						this.datas1 = datas1;
					}
				}
			引入使用
			 Kz kz = new ChartscpUtils.Builder(Calendar.DATE).build(Kz.class);
	  
  
  常用示例：
   
        eg : 默认查询7条数据 通过setLength指定   默认数据间隔1 通过setInterval指定

         * 年为单位 最近3年
         */
        ChartscpResult yearResult = new ChartscpUtils.Builder(ChartscpUtils.YEAR).setLength(3).build();
        /**
         * 月为单位  最近5月
         */
        ChartscpResult monthResult = new ChartscpUtils.Builder(ChartscpUtils.MONTH).setXCellFormat("yy-M").setLength(5).build();
        /**
         * 日为单位 最近7日
         */
        ChartscpResult dateResult = new ChartscpUtils.Builder(ChartscpUtils.DATE).setLength(7).build();
        /**
         * 小时为单位 最近7小时
         */
        ChartscpResult hourResult = new ChartscpUtils.Builder(ChartscpUtils.HOUR).build();
        /**
         * 分钟为单位 最近7分钟
         */
        ChartscpResult minuteResult = new ChartscpUtils.Builder(ChartscpUtils.MINUTE).build();

        /**
         * 日为单位 间隔2日统计 3个日单位 (eg: xells={1 3 5}))
         */
        ChartscpResult dateIntervalResult = new ChartscpUtils.Builder(ChartscpUtils.DATE).setLength(3).setInterval(2).build();
        /**
         * 最近7天非连续数据  间隔为0  只生成开始结束日期，数据直接获取数据库
         */
        ChartscpResult dateInterval0Result = new ChartscpUtils.Builder(Calendar.DATE).setInterval(0).build();
        List<ChartscpResultMap> list1 = new ArrayList<>();
        ChartscpResultMap chartscpResultMap = new ChartscpResultMap();
        chartscpResultMap.setXcell("01-18");
        chartscpResultMap.setData(1);
        list1.add(chartscpResultMap);
        ChartscpResultMap chartscpResultMap1 = new ChartscpResultMap();
        chartscpResultMap1.setXcell("01-23");
        chartscpResultMap1.setData(4);
        list1.add(chartscpResultMap1);
        dateInterval0Result.updateData(list1);

        /**
         * 加开始 ，结束时间  指定日期的数据
         */
        ChartscpResult seResult = new ChartscpUtils.Builder(ChartscpUtils.HOUR).setStartTime("2021-01-18 00:00:00").setEndTime("2021-01-18 23:59:59").build();

        /**
         * 自定义 显示格式
         */
        ChartscpResult customXCellFormat = new ChartscpUtils.Builder(ChartscpUtils.HOUR).setXCellFormat("H点m分").setLength(3).build();

        /**
         * 自定义扩展类实现字段扩展
         */
        Kz kz = new ChartscpUtils.Builder(ChartscpUtils.DATE).build(Kz.class);
        //模拟数据库数据 ChartscpResultMapKz和Kz类字段对应  datas1
        List<ChartscpResultMapKz> list = new ArrayList<>();
        ChartscpResultMapKz sqKz = new ChartscpResultMapKz();
        sqKz.setXcell("01-22");
        sqKz.setData(1);
        sqKz.setDatas1(3);
        list.add(sqKz);
        ChartscpResultMapKz sqKz1 = new ChartscpResultMapKz();
        sqKz1.setXcell("01-24");
        sqKz1.setData(4);
        sqKz1.setDatas1(8);
        list.add(sqKz1);
        //更新数据
        kz.updateData(list);

        /**
         * 按分钟显示整小时
         */
        ChartscpResult minute_whole_hour = new ChartscpUtils.Builder(MINUTE_WHOLE_HOUR).setDataNonzero(true).setInterval(8).build();
        /**
         * 按分钟显示整小时 ,当前分钟没有数据保持前一分钟数据
         */
        ChartscpResult minute_whole_hour1 = new ChartscpUtils.Builder(MINUTE_WHOLE_HOUR).setDataNonzero(true).setEndTime("2021-01-25 08:09:23").build();
        //模拟数据库数据 ChartscpResultMapKz和Kz类字段对应  datas1
        List<ChartscpResultMap> list3 = new ArrayList<>();
        ChartscpResultMap crm1 = new ChartscpResultMap();
        crm1.setXcell("11:12");
        crm1.setData(1);
        list3.add(crm1);
        //更新数据
        minute_whole_hour1.updateData(list3);
        /**
         * 按小时显示整天  三班倒
         */
        ChartscpResult hour_whole_day = new ChartscpUtils.Builder(ChartscpUtils.HOUR_WHOLE_DAY).setInterval(8).setDataNonzero(true).build();

        /**
         * 按天显示整月
         */
        ChartscpResult day_whole_month = new ChartscpUtils.Builder(ChartscpUtils.DAY_WHOLE_MONTH).build();
        /**
         * 按月显示整年
         */
        ChartscpResult month_whole_year = new ChartscpUtils.Builder(ChartscpUtils.MONTH_WHOLE_YEAR).build();

        /**
         * 显示当前周数据
         */
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,-3);
        ChartscpResult week = new ChartscpUtils.Builder(ChartscpUtils.WEEK).setDataNonzero(true).build();

        //模拟数据库数据 ChartscpResultMapKz和Kz类字段对应  datas1
        List<ChartscpResultMap> list2 = new ArrayList<>();
        ChartscpResultMap crm = new ChartscpResultMap();
        crm.setXcell("01-27");
        crm.setData(1);
        list2.add(crm);
        //更新数据
        week.updateData(list2);
        
        
 通过上述方法返回一个ChartscpResult类，这个类中有以下属性
        
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
         * sql结果集日期格式
         */
        private String  resultDateFormat;
    
        /**
         * sql查询分组日期格式
         */
        private String  groupByDateFormat;
    
        /**
         * 数据间隔
         */
        private int  interval;
    
        /**
         * sql业务条件
         */
        private T where;   
    
>  *data 数据集合，如果需要多个可以通过扩展实现，where 属性是预留业务查询的条件字段，可以将自己的条件携带到sql中来获取。*
  
####  通过上述方法生成的数据是初始值0，之后需要自己通过sql查询数据，进行替换
  ###sql示例
#### mapper.java

    List<EchartsMap> selectUserByOneWeek(ChartscpResult chartscpResult);
    
 #### mapper.xml
 
       <select id="selectUserByOneWeek" resultType="util.ChartscpMap">    
            select DATE_FORMAT(create_time,#{resultDateFormat}) as xcell ,count(*) as data1 FROM user
            where create_time BETWEEN #{startTime} and #{endTime}
          GROUP BY DATE_FORMAT(create_time,#{groupByDateFormat}) ORDER BY create_time
      </select>
 
##### 以指定间隔分钟（eg:10分钟）为单位的统计数据sql示例

     <select id="selectUserByOneWeek" resultType="util.ChartscpMap">    
                SELECT DATE_FORMAT(time,#{resultDateFormat}) as xcell ,count(*) as data1
                FROM
                	(SELECT  DATE_FORMAT( concat( date( create_time), ' ', HOUR ( create_time), ':', CEIL( MINUTE ( create_time) / #{interval} ) * #{interval} ), #{groupByDateFormat}  ) AS time 
                	FROM user) a 
                GROUP BY time
          </select>
      
>返回结果集是ChartscpMap对象， 这个对象中有两个参数 xcell 对应日期，data对应数据 如果有多个data可以通过扩展ChartscpMap实现
    
     public class ChartscpMapKz extends ChartscpMap {
     
         private Integer datas1;
     }

### 最后更新数据
    ChartscpResult中有一个updateData方法可以实现数据更新
----   
   最终使用方式        
   
    public ChartscpResult getUserByOneWeek() {
            ChartscpResult chartscpResult = new ChartscpUtils.Builder().setCalendarField(Calendar.DATE).build();
            chartscpResult.updateData(userMapper.selectUserByOneWeek(chartscpResult));
            return chartscpResult;
        }
