# 图表折线图的好伴侣,快速生成数据

>开源协议： Apache License Version 2.0
 
 本工具可以通过指定参数生成图表曲线图,柱状图的初始数据，配合sql实现展示数据
 
 ### 使用格式
 
 	ChartscpResult build = new ChartscpUtils.Builder(Calendar.HOUR).setStartTime("2021-01-18 00:00:00").setEndTime("2021-01-18 23:59:59").setLength(7).setInterval(8).setDataNonzero(true).setXCellFormat("HH时").build();
	
	参数说明：
		Builder(Calendar.HOUR)是指定显示x轴格式对应年，月，日，小时，分钟 必填
	
		setLength() 这个方法用来指定显示的长度 默认是7 
			eg: Builder(Calendar.YEAR).setLength(3) 是显示最近3年 xCells=[2019,2020,2021] ）
	
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
   
         //当天三班倒数据
   
        ChartscpResult build = new ChartscpUtils.Builder(Calendar.HOUR).setStartTime("2021-01-18 00:00:00").setEndTime("2021-01-18 23:59:59").setInterval(8).build();
   
        //最近7小时数据
   
        ChartscpResult build2 = new ChartscpUtils.Builder(Calendar.HOUR).build();
   
        //最近7小时非连续数据（只显示数据库有的数据）
   
        ChartscpResult build21 = new ChartscpUtils.Builder(Calendar.HOUR).setInterval(0).build();
   
        //最近7年
   
        ChartscpResult build5 = new ChartscpUtils.Builder(Calendar.YEAR).build();
   
        //最近5个月
   
        ChartscpResult build4 = new ChartscpUtils.Builder(Calendar.MONTH).setLength(5).build();
   
        //最近7天
   
        ChartscpResult build4 = new ChartscpUtils.Builder(Calendar.DATE).build();
        
        
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
