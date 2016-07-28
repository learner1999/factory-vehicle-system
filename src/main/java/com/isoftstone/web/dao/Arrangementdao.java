package com.isoftstone.web.dao;


import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.isoftstone.web.dao.Car_dao;
import com.isoftstone.web.pojo.Arrange;
import com.isoftstone.web.pojo.Arrangement;
import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.pojo.TestUser2;
import com.isoftstone.web.util.JdbcUtil;
import com.mysql.jdbc.CallableStatement;

public class Arrangementdao {
	
	
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	CallableStatement cs = null;
	
	//有多少工人决定了多少辆车，车的数量决定了司机的数量,
	//公司决定了司机的出勤率（一个月打30天算，休息几天（一般为4-6），上几天班）.
	public ArrayList driver(int c,int m,int n){
	ArrayList List = new ArrayList();
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
	public ArrayList arrange(long days,int drivers,int circulation){
		ArrayList list = new ArrayList();
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
	
	
	public boolean updatearrange(int d_id,String d_name,int c_id,String license,Date date)
	{         
		
		String proc="{call updatearrange(?,?,?,?,?)}";
		try{
			conn =JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, d_id);
			cs.setString(2,d_name);
			cs.setInt(3, c_id);
			cs.setString(4, license);
			cs.setDate(5, (java.sql.Date) date);
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
	
	public ArrayList drivers(){
		ArrayList em=new ArrayList();
		String proc="{call selectDrivers()}";
		try{
			conn =JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			result = cs.executeQuery();
			while (result.next()){
			Emlopee emp=new Emlopee();
			emp.setEid(result.getInt(1));
			emp.setEname(result.getString(2));
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
	public ArrayList cars(){
		ArrayList car=new ArrayList();
		String proc="{call selectCars()}";
		try{
			conn =JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
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
		} finally {
			JdbcUtil.close(null, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		
		return car;
	}
	
	public ArrayList arrangement(List<Arrange> ar,Date date){
		ArrayList ag=new ArrayList();
		ArrayList driver=drivers();  //从数据库获取司机的列表
       ArrayList car=cars();//从数据库获取车辆信息
       for(int j=0;j<ar.size();j++){
			Arrangement agm=new Arrangement();
			Arrange c=(Arrange)ar.get(j);
			 Emlopee eml=(Emlopee)driver.get(c.getD_id());
			 Car_inf cars=(Car_inf)car.get(j);
			 System.out.print("d"+c.getD_id()+"-car"+(j+1)+"\t");
			 //ad.updatearrange(eml.getEid(), eml.getEname(),cars.getId(),cars.getD_license(), date);
		     agm.setEid(eml.getEid());
		     agm.setEname(eml.getEname());
		     agm.setCid(cars.getId());
		     agm.setC_license(cars.getD_license());
		     agm.setDate((java.sql.Date) date);
		     updatearrange(eml.getEid(), eml.getEname(),cars.getId(),cars.getD_license(), date);
		     ag.add(agm);
		}
       return ag;
	}
	 public static long getQuot(String time1, String time2){
		  long quot = 0;
		  SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		  try {
		   Date date1 = (Date) ft.parse( time1 );
		   Date date2 = (Date) ft.parse( time2 );
		   quot = date1.getTime() - date2.getTime();
		   quot = quot / 1000 / 60 / 60 / 24;
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  return quot;
		 }
	
}
