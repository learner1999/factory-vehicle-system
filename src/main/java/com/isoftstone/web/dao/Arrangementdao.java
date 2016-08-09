package com.isoftstone.web.dao;


import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.isoftstone.web.dao.Car_dao;
import com.isoftstone.web.pojo.Arrange;
import com.isoftstone.web.pojo.Arrangement;
import com.isoftstone.web.pojo.Arrangements;
import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.pojo.TestUser2;
import com.isoftstone.web.util.JdbcUtil;
import com.mysql.jdbc.CallableStatement;

public class Arrangementdao {
	
	String [] mou={"Mon","Tues","Wed","Thus","Fri","Sat","Sun"};
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	CallableStatement cs = null;
	
	//有多少工人决定了多少辆车，车的数量决定了司机的数量,
	//公司决定了司机的出勤率（一个月打30天算，休息几天（一般为4-6），上几天班）.
	/**
	 * 车辆的数量（c）,司机休息的上下限（默认4-6）
	 * @param c
	 * @param m
	 * @param n
	 * @return
	 */
	public List<Arrange> driver(int c,int m,int n){
	List<Arrange> List = new ArrayList<>();
	int i,j;
	float a=(30*c)/(30-n);//司机人数的下限
	float b=(30*c)/(30-m);//上限
	float e;//循环天数(e天内每一位司机都能休息一天)
	for(i=(int)a+1;i<=(int)b;i++){
	Arrange bean=new Arrange();
	//System.out.printf("i=%d\t",i);
	e=(float)i/(i-c);
	//System.out.printf("e=%f\t",e);
	j=(int)e;
	//System.out.printf("j=%f\t",(float)j);
	if(e==(float)j){
   // System.out.printf("1\n");
	bean.setDrivers(i);
	bean.setArrers(i/(i-c));
	bean.setArrs(i-c);
	List.add(bean);
	}
	}
	//System.out.printf("执行了");
	return List;
	}
	/**
	 * 给出数字和循环天数,得到该组的循环的人的编号
	 * @param a
	 * @param b
	 * @return
	 */
	int tiban(int a,int b){
	 int c=0;
		for(int i=a;i<=a+b;i++){
		 if(i%b==0){
			 c=i;
			 break;
			 }
	 }
		return c;
	}
	//days-从开始上班后的多少天     drivers-多少位司机    circulation-循环天数
	/**
	 * 排班
	 * @param days
	 * @param drivers
	 * @param circulation
	 * @return
	 */
	public List<Arrange> arrange(long days,int drivers,int circulation){
		List<Arrange> list = new ArrayList<>();
		int a=(int) (days%circulation);//每组的第几位司机轮休
		if(a==0){
			for(int i=1;i<=drivers;i++){
				if((i%circulation)!=0){
					Arrange bean=new Arrange();
					bean.setD_id(i);
					list.add(bean);
				}
				
			}
		}
		else{
			for(int i=1;i<=drivers;i++){
				if((i%circulation)!=0){
					Arrange bean=new Arrange();
					if((i%circulation)==a){
						bean.setD_id(tiban(i,circulation));
					}
					else{
						bean.setD_id(i);
					}				
					list.add(bean);
				}
			}
		}
		return list;
		
	}
	
	/**
	 * 更新排班表
	 * @param d_id
	 * @param d_name
	 * @param d_eiden
	 * @param c_id
	 * @param license
	 * @param date
	 * @return
	 */
	public boolean updatearrange(int d_id,String d_name,String d_eiden,int c_id,String license,Date date)
	{         
		
		String proc="{call updatearrange(?,?,?,?,?,?)}";
		try{
			conn =JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, d_id);
			cs.setString(2,d_name);
			cs.setString(3, d_eiden);
			cs.setInt(4, c_id);
			cs.setString(5, license);
			cs.setDate(6, (java.sql.Date) date);
			if(cs.executeUpdate()==1){
				return true;
			}		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		
		
		return false;
	}
	
	/**
	 * 从数据库中得到司机的信息
	 * @return
	 */
	public List<Emlopee> drivers(){
		List<Emlopee> em=new ArrayList<>();
		String proc="{call selectDrivers()}";
		try{
			conn =JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			result = cs.executeQuery();
			while (result.next()){
			Emlopee emp=new Emlopee();
			emp.setEid(result.getInt(1));
			emp.setEname(result.getString(2));
			emp.setEiden(result.getString(3));
			em.add(emp);
			}		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		
		return em;
	}
	/**
	 * 从数据库中得到车辆信息
	 * @return
	 */
	public List<Car_inf> cars(List<Integer> ar){
		List<Car_inf> car=new ArrayList<>();
		String proc="{call selectCars(?)}";
		  for(int j=0;j<ar.size();j++){
			  int a=(Integer)ar.get(j);
			  try{
					conn =JdbcUtil.getConnection();
					cs = (CallableStatement) conn.prepareCall(proc);
					cs.setInt(1, a);
					result = cs.executeQuery();
					while (result.next()){
					Car_inf car1=new Car_inf();
					car1.setId(result.getInt(1));
					car1.setD_license(result.getString(2));
					car.add(car1);
					}		
				}catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					JdbcUtil.close(null, stmt, conn);
					JdbcUtil.closecs(cs);
				} 
		  }
		return car;
	}
	
	public List<Integer> getCarbyRoute() {

		List<Integer> carid = new ArrayList<>();

		String strSql = "SELECT * FROM information_schema.TABLES WHERE TABLE_NAME LIKE 'car%' AND TABLE_NAME <> 'car_information'";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			result = stmt.executeQuery();
			while (result.next()) {
				int tableName = Integer.valueOf(result.getString("TABLE_NAME").split("car")[1]);
				carid.add(tableName);
			}
		} catch (SQLException e) {
			System.out.println("获取路线表名出错！");
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return carid;
	}
	
	/**
	 * 将排班的信息添加至数据库 
	 * @param ar
	 * @param date
	 * @return
	 */
	public List<Arrangement> arrangement(List<Arrange> ar,Date date){
		List<Arrangement> ag=new ArrayList<>();
		List<Emlopee> driver=drivers();  //从数据库获取司机的列表
		List<Car_inf> car=cars(getCarbyRoute());//从数据库获取车辆信息
       for(int j=0;j<ar.size();j++){
			Arrangement agm=new Arrangement();
			Arrange c=(Arrange)ar.get(j);
			 Emlopee eml=(Emlopee)driver.get(c.getD_id());
			 Car_inf cars=(Car_inf)car.get(j);
			// System.out.print("d"+c.getD_id()+"-car"+(j+1)+"\t");
			 agm.setEname(eml.getEname());
		     agm.setEid(eml.getEid());
		     agm.setEiden(eml.getEiden());
		     agm.setCid(cars.getId());
		     agm.setC_license(cars.getD_license());
		     agm.setDate((java.sql.Date) date);
		     updatearrange(eml.getEid(), eml.getEname(),eml.getEiden(),cars.getId(),cars.getD_license(), date);
		     ag.add(agm);
		}
       return ag;
	}
	
	/**
	 * 获取日期差的天数
	 * @param time1
	 * @param time2
	 * @return
	 */
	 public static long getQuot(String time1, String time2){
		  long quot = 0;
		  SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		  try {
		   java.util.Date date1 = ft.parse( time1 );
		   java.util.Date date2 =  ft.parse( time2 );
		   quot = date1.getTime() - date2.getTime();
		   quot = quot / 1000 / 60 / 60 / 24;
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  return quot;
		 }
	 
	 /**
	  * 判断当天是否已排过班
	  * @param date
	  * @return
	  */
	 public List<Arrangement> is_date(Date date){
		 List<Arrangement> ar=new ArrayList<>();
		 String proc="{call is_date(?)}";
			try{
				conn =JdbcUtil.getConnection();
				cs = (CallableStatement) conn.prepareCall(proc);
				cs.setDate(1,date);
				result = cs.executeQuery();
				while(result.next())
				{	
					Arrangement a=new Arrangement();
					a.setEid(result.getInt(1));
					a.setEname(result.getString(2));
					a.setEiden(result.getString(3));
					a.setCid(result.getInt(4));
					a.setC_license(result.getString(5));
					a.setDate(result.getDate(6));
					ar.add(a);
				}
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				JdbcUtil.close(null, stmt, conn);
				JdbcUtil.closecs(cs);
			}
		 return ar;
	 }
	 
	 /**
	  * 将日期往后加一天
	  * @param date
	  * @return date
	  */
	 public java.util.Date dates(java.util.Date date){
		 Calendar   calendar   =   new   GregorianCalendar(); 
	     calendar.setTime(date); 
	     calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
	     date=calendar.getTime();   //这个时间就是日期往后推一天的结果
		 return date;
	 }
	 
	/**
	 * 获得所需要的排班信息     
	 * @param date  必须是星期一
	 * @param drivers
	 * @param circulation
	 * @return
	 */
	 public List<Arrangements> get_arr(Date date,int drivers,int circulation){
		 List<Arrangements> a2=new ArrayList<>();
		 List<Arrangement> al=is_date(date);//是否排过班
		 if(al.isEmpty()== false){
			 for(int i=0;i<7;i++){
				 Arrangements a1=new Arrangements();
				 if(i!=0){
					 java.util.Date utildate=dates(date);   
					  date   = new java.sql.Date (utildate.getTime());   
				     }
	         a1.setMon(mou[i]);
	         a1.setDate((java.sql.Date) date);
			 a1.setAg(al);	
			 a2.add(a1);
			 } 
		 }
		 else{
		 for(int i=0;i<7;i++){
			 Arrangements a1=new Arrangements();
			if(i!=0){
				 java.util.Date utildate=dates(date);   
				  date   = new java.sql.Date (utildate.getTime());   
			}
         a1.setMon(mou[i]);
         a1.setDate((java.sql.Date) date);
         //ArrayList al=is_date(date);//是否排过班
		long days=getQuot(date.toString(),"2016-1-1");
		List<Arrange> ar=arrange(days, drivers, circulation);//获取排班相关信息   
		List<Arrangement> ag=arrangement(ar,date);
			a1.setAg(ag);
		a2.add(a1);
	 }
		 }
	 return a2;
	 }
}
