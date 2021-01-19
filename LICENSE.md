# 图表的好伴侣,快速生成数据

>开源协议： Apache License Version 2.0
 
 本工具可以通过指定参数生成图表曲线图,柱状图的初始数据，配合sql实现展示数据
  
  使用示例：
   
        eg : 默认查询7条数据 通过setLength指定   默认数据间隔1 通过setInterval指定
   
         //当天三班倒数据
   
        ChartscpResult build = new ChartscpUtils.Builder(Calendar.HOUR).setStartTime("2021-01-18 00:00:00").setEndTime("2021-01-18 23:59:59").setInterval(8).build();
   
        //前推7小时数据
   
        ChartscpResult build2 = new ChartscpUtils.Builder(Calendar.HOUR).build();
   
        //前推7小时非连续数据（只显示数据库有的数据）
   
        ChartscpResult build21 = new ChartscpUtils.Builder(Calendar.HOUR).setInterval(0).build();
   
        //前推7年
   
        ChartscpResult build5 = new ChartscpUtils.Builder(Calendar.YEAR).build();
   
        //前推7个月
   
        ChartscpResult build4 = new ChartscpUtils.Builder(Calendar.MONTH).build();
   
        //前推7天
   
        ChartscpResult build4 = new ChartscpUtils.Builder(Calendar.DATE).build();
        
        
 通过上述方法返回一个ChartscpResult类，这个类中有以下属性
        
      /**
         * x轴数据
         */
        private List<T> xCells;
    
        /**
         * 数据
         */
        private List<T> datas1;
        /**
         * 数据
         */
        private List<T> datas2;
    
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
    
  在这个类中有两个data数据集作为预留数据，可以根据需要使用, where 属性是预留业务查询的条件字段，可以将自己的条件携带到sql中来获取。
  
  通过上述方法生成的数据是初始值0，之后需要自己通过sql查询数据，进行替换
  ###sql示例
#### mapper.java

    List<EchartsMap> selectUserByOneWeek(ChartscpResult chartscpResult);
    
 #### mapper.xml
 
       <select id="selectUserByOneWeek" resultType="util.ChartscpMap">    
            select DATE_FORMAT(create_time,#{resultDateFormat}) as xcell ,count(*) as data1 FROM user
            where create_time BETWEEN #{startTime} and #{endTime}
          GROUP BY DATE_FORMAT(create_time,#{groupByDateFormat}) ORDER BY create_time
      </select>
 
##### 以10分钟为单位的统计数据sql示例

     <select id="selectUserByOneWeek" resultType="util.ChartscpMap">    
                SELECT DATE_FORMAT(time,#{resultDateFormat}) as xcell ,count(*) as data1
                FROM
                	(SELECT  DATE_FORMAT( concat( date( create_time), ' ', HOUR ( create_time), ':', CEIL( MINUTE ( create_time) / #{interval} ) * #{interval} ), #{groupByDateFormat}  ) AS time 
                	FROM user) a 
                GROUP BY time
          </select>
      
返回结果集是ChartscpMap对象， 这个对象中有三个参数 xcell 对应日期，data1对应数据1 data2对应数据2

### 最后更新数据
    ChartscpResult中有一个updateData方法可以实现数据更新
----   
   最终使用方式        
   
    public ChartscpResult getUserByOneWeek() {
            ChartscpResult chartscpResult = new ChartscpUtils.Builder().setCalendarField(Calendar.DATE).build();
            chartscpResult.updateData(userMapper.selectUserByOneWeek(echartsResult));
            return chartscpResult;
        }
