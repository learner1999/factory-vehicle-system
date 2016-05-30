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
import com.isoftstone.web.pojo.TestUser2;
import com.isoftstone.web.util.JdbcUtil;
import com.mysql.jdbc.CallableStatement;
public class Car_dao {
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	CallableStatement cs = null;
	
	public List getAllcar()
	{
		List<Car_inf> carList = new ArrayList<>();
		String proc="{call FindAllcar()}";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			result = cs.executeQuery();
			while (result.next()) {
				Car_inf car = fetchcar(result);
				carList.add(car);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		return carList;
	}
	
	public Car_inf getcarByid(int id)
	{
		Car_inf car = null;
		String proc="{call GetcarByid(?)}";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, id);
			result = cs.executeQuery();
			while (result.next()) {
				car = fetchcar(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		return car;
	}
	
	public List getcarBybrand(String brand)
	{
		List<Car_inf> carList = new ArrayList<>();
		String proc="{call GetcarBybrand(?)}";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setString(1, "%" +brand+ "%");
			result =cs.executeQuery();
			while (result.next()) {
				Car_inf car = fetchcar(result);
				carList.add(car);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		return carList;
	}
	/*
	public List getcarBydriving_license(String d_license)
	{
		List<Car_inf> carList = new ArrayList<Car_inf>();
		String sql="select * from car_information where c_driving_license =?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, d_license);
			result = stmt.executeQuery();
			
			while (result.next()) {
				Car_inf car = fetchcar(result);
				carList.add(car);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return carList;
	}
	*/
	
	public List getcarByid1(int id)
	{
		List<Car_inf> carList = new ArrayList<>();
		String proc="{call GetcarByid(?)}";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, id);
			result = cs.executeQuery();
			while (result.next()) {
				Car_inf car =fetchcar(result);
				carList.add(car);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		return carList;
	}
	
	public boolean is_license(String c_license){
		String proc="{call is_license(?)}";
		try{
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setString(1, c_license);
			result = cs.executeQuery();
			if(result.next())
			{	
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
				JdbcUtil.closecs(cs);
			}
		return false;
		}
	
	public boolean is_Dlicense(String d_license){
		String proc="{call is_d_license(?)}";
		try{
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setString(1, d_license);
			result = cs.executeQuery();
			if(result.next())
			{	
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
				JdbcUtil.closecs(cs);
			}
		return false;
		}
	
	public boolean is_id(int id){
		String proc="{call is_id(?)}";
		try{
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, id);
			result = cs.executeQuery();
			if(result.next())
			{	
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
				JdbcUtil.closecs(cs);
			}
		return false;
		}
	
	public boolean deletecar(int id){
		String proc="{call deletecar(?)}";
		try{
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, id);
			int counter = cs.executeUpdate();
			if (1 == counter) {
				return true;
			}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
				JdbcUtil.closecs(cs);
			}
		return false;
		}
			
	
	public boolean createcar(Car_inf car1){
		String proc="{call increasecar(?,?,?,?,?,?,?)}";
		try{
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, car1.getId());
			cs.setString(2, car1.getBrand());
			cs.setInt(3,car1.getSeat());
			cs.setDate(4, car1.getLogon());
			cs.setDate(5, car1.getDated());
			cs.setString(6,car1.getLicense());
			cs.setString(7, car1.getD_license());
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
	
	public boolean updatecar(Car_inf car1,int id){
		String proc="{call updatecar(?,?,?,?,?,?,?)}";
		try{
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, car1.getId());
			cs.setString(2, car1.getBrand());
			cs.setInt(3,car1.getSeat());
			cs.setDate(4, car1.getLogon());
			cs.setDate(5, car1.getDated());
			cs.setString(6,car1.getLicense());
			cs.setString(7, car1.getD_license());
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
		
	public boolean addToExcel(List<Car_inf> carlist,String path)
	{
		
		System.out.println("���excel����\n");
		// ��һ��������һ��webbook����Ӧһ��Excel�ļ�  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // �ڶ�������webbook�����һ��sheet,��ӦExcel�ļ��е�sheet  
        HSSFSheet sheet = wb.createSheet("������Ϣ��");  
        // ������sheet����ӱ�ͷ��0��,ע���ϰ汾poi��Excel����������������short  
        HSSFRow row = sheet.createRow((int) 0);  
        // ���Ĳ���������Ԫ�񣬲�����ֵ��ͷ ���ñ�ͷ����  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // ����һ�����и�ʽ  
  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("����id");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 1);  
        cell.setCellValue("Ʒ��");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 2);  
        cell.setCellValue("��λ��");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 3);  
        cell.setCellValue("ע������");  
        cell.setCellStyle(style); 
        cell = row.createCell((short) 4);  
        cell.setCellValue("��������");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 5);  
        cell.setCellValue("��ʻ֤");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 6);  
        cell.setCellValue("��ʻ֤");  
        cell.setCellStyle(style);   
  
        for (int i = 0; i < carlist.size(); i++)  
        {  
            row = sheet.createRow((int) i + 1);  
            Car_inf car = (Car_inf) carlist.get(i);  
            // ���Ĳ���������Ԫ�񣬲�����ֵ  
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
        // �������ļ��浽ָ��λ��  
        try  
        {  
        	 System.out.println("true\n");
        	String name=path+"������Ϣ.xls";
            FileOutputStream fout = new FileOutputStream(name);  
            wb.write(fout);  
            fout.close();  
            System.out.println("�ɹ�\n");
            return true;
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
       
        return false;
    }  
	
	private Car_inf fetchcar(ResultSet result) throws SQLException {
		Car_inf carlist = new Car_inf();
         carlist.setId(result.getInt("c_id"));
         carlist.setBrand(result.getString("c_brand"));
         carlist.setSeat(result.getInt("c_seat"));
         carlist.setLogon(result.getDate("c_logon"));
         carlist.setDated(result.getDate("c_dated"));
         carlist.setLicense(result.getString("c_license"));
         carlist.setD_license(result.getString("c_driving_license"));
		return carlist;
	}

}
