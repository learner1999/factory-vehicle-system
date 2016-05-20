package com.isoftstone.web.dao;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.isoftstone.web.dao.Car_dao;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.util.JdbcUtil;
public class Car_dao {
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	
	public List getAllcar()
	{
		List<Car_inf> carList = new ArrayList<>();
		String sql="select * from car_information";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Car_inf car = new Car_inf();
				car.setId(result.getInt(1));
				car.setBrand(result.getString(2));
				car.setSeat(result.getInt(3));
				car.setLogon(result.getDate(4));
				car.setDated(result.getDate(5));
				car.setLicense(result.getString(6));
				car.setD_license(result.getString(7));
				carList.add(car);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return carList;
	}
	
	public Car_inf getcarByid(int id)
	{
		Car_inf car = new Car_inf();
		String sql="select * from car_information where c_id ="+id+"";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				car.setId(result.getInt(1));
				car.setBrand(result.getString(2));
				car.setSeat(result.getInt(3));
				car.setLogon(result.getDate(4));
				car.setDated(result.getDate(5));
				car.setLicense(result.getString(6));
				car.setD_license(result.getString(7));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return car;
	}
	
	public List getcarBybrand(String brand)
	{
		List<Car_inf> carList = new ArrayList<>();
		String sql="select * from car_information where c_brand ='"+brand+"'";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Car_inf car = new Car_inf();
				car.setId(result.getInt(1));
				car.setBrand(result.getString(2));
				car.setSeat(result.getInt(3));
				car.setLogon(result.getDate(4));
				car.setDated(result.getDate(5));
				car.setLicense(result.getString(6));
				car.setD_license(result.getString(7));
				carList.add(car);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return carList;
	}
	
	public List getcarBydriving_license(String d_license)
	{
		List<Car_inf> carList = new ArrayList<Car_inf>();
		String sql="select * from car_information where c_driving_license ='"+d_license+"'";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			
			while (result.next()) {
				Car_inf car = new Car_inf();
				car.setId(result.getInt(1));
				car.setBrand(result.getString(2));
				car.setSeat(result.getInt(3));
				car.setLogon(result.getDate(4));
				car.setDated(result.getDate(5));
				car.setLicense(result.getString(6));
				car.setD_license(result.getString(7));
				carList.add(car);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return carList;
	}
	
	public List getcarByid1(int id)
	{
		List<Car_inf> carList = new ArrayList<>();
		String sql="select * from car_information where c_id ="+id+"";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Car_inf car = new Car_inf();
				car.setId(result.getInt(1));
				car.setBrand(result.getString(2));
				car.setSeat(result.getInt(3));
				car.setLogon(result.getDate(4));
				car.setDated(result.getDate(5));
				car.setLicense(result.getString(6));
				car.setD_license(result.getString(7));
				carList.add(car);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return carList;
	}
	
	public boolean is_license(String c_license){
		String sql="select c_license from car_information where c_license='"+c_license+"'";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			if(result.next())
			{	
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
			}
		return false;
		}
	
	public boolean is_Dlicense(String d_license){
		String sql="select c_driving_license from car_information where c_driving_license'"+d_license+"'";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			if(result.next())
			{	
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
			}
		return false;
		}
	
	public boolean is_id(int id){
		String sql="select c_id from car_information where c_id="+id+"";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			if(result.next())
			{	
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
			}
		return false;
		}
	public boolean deletecar(int id){
		String sql="delete from car_information where c_id="+id+"";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
			}
		return false;
		}
			
	
	public boolean createcar(Car_inf car1){
		String sql="insert into car_information values(?,?,?,?,?,?,?)";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, car1.getId());
			stmt.setString(2, car1.getBrand());
			stmt.setInt(3,car1.getSeat());
			stmt.setDate(4, car1.getLogon());
			stmt.setDate(5, car1.getDated());
			stmt.setString(6,car1.getLicense());
			stmt.setString(7, car1.getD_license());
			if(stmt.executeUpdate()==1){
				return true;
			}		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}
		return false;
	}
	
	public boolean updatecar(Car_inf car1,int id){
		String sql="update car_information set c_id= ?,c_brand= ?,c_seat= ?,c_logon= ?,c_dated= ?,c_license= ?,c_driving_license= ? where c_id="+id+"";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, car1.getId());
			stmt.setString(2, car1.getBrand());
			stmt.setInt(3,car1.getSeat());
			stmt.setDate(4, car1.getLogon());
			stmt.setDate(5, car1.getDated());
			stmt.setString(6,car1.getLicense());
			stmt.setString(7, car1.getD_license());
			if(stmt.executeUpdate()==1){
				return true;
			}		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}
		return false;
	}	
		
	public boolean addToExcel(List<Car_inf> carlist,String path)
	{
		
		System.out.println("生成excel方法\n");
		// 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("车辆信息表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("车辆id");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 1);  
        cell.setCellValue("品牌");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 2);  
        cell.setCellValue("座位数");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 3);  
        cell.setCellValue("注册日期");  
        cell.setCellStyle(style); 
        cell = row.createCell((short) 4);  
        cell.setCellValue("保险日期");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 5);  
        cell.setCellValue("驾驶证");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 6);  
        cell.setCellValue("行驶证");  
        cell.setCellStyle(style);   
  
        for (int i = 0; i < carlist.size(); i++)  
        {  
            row = sheet.createRow((int) i + 1);  
            Car_inf car = (Car_inf) carlist.get(i);  
            // 第四步，创建单元格，并设置值  
            String logon=car.getLogon().toString();
            String dated=car.getDated().toString();
            row.createCell((short) 0).setCellValue(car.getId());  
            row.createCell((short) 1).setCellValue(car.getBrand());  
            row.createCell((short) 2).setCellValue(car.getSeat());  
            row.createCell((short) 3).setCellValue(logon); 
            row.createCell((short) 4).setCellValue(dated); 
            row.createCell((short) 5).setCellValue(car.getLicense()); 
            row.createCell((short) 6).setCellValue(car.getD_license()); 
            
        }  
        // 第六步，将文件存到指定位置  
        try  
        {  
        	 System.out.println("true\n");
        	String name=path+"车辆信息.xls";
            FileOutputStream fout = new FileOutputStream(name);  
            wb.write(fout);  
            fout.close();  
            System.out.println("成功\n");
            return true;
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
       
        return false;
    }  

}
