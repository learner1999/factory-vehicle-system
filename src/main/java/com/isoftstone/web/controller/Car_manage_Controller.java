package com.isoftstone.web.controller;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.Car_dao;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.pojo.Car_inf;

@RestController
public class Car_manage_Controller {
	Car_dao car=new Car_dao();//初始化对象（方法的对象）
	
	//获取全部车辆信息的控制器
	@RequestMapping(value = "/api/car_infor", method = RequestMethod.GET)
	public ResponseEntity<List<Car_inf>> getAllcar() //
	{
		List<Car_inf> carList= car.getAllcar();
		if(carList.isEmpty()) {//结果为空
			System.out.print("结果为空");
			return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Car_inf>>(carList, HttpStatus.OK);
	}
	
	//增加车辆的控制器
	@RequestMapping(value ="/api/car_inf", method = RequestMethod.POST)
	public ResponseEntity<Car_inf> createcar(@RequestBody Car_inf car1){
		
		if(car1.getId() == 0){//判断主键id是不是为空
			System.out.print("c_id不能为空");
			return new ResponseEntity<Car_inf>(HttpStatus.BAD_REQUEST);
		}
		
		if(car.is_license(car1.getLicense())){//判断驾驶证是否存在
			System.out.print("驾驶证已存在");
			return new ResponseEntity<Car_inf>(HttpStatus.CONFLICT);
		}
		
		if(car.is_Dlicense(car1.getD_license())){//判断行驶证是否存在
			System.out.print("行驶证已存在");
			return new ResponseEntity<Car_inf>(HttpStatus.CONFLICT);
		}
		
		if(car.createcar(car1)){//增加新车辆
			System.out.print("新增车辆成功");
			return new ResponseEntity<Car_inf>(car1, HttpStatus.CREATED);
		}
		
		return new ResponseEntity<Car_inf>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
    //删除车辆信息控制器 （根据c_id）
	@RequestMapping(value ="/api/car_inf/deletebyid/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Car_inf> deletecar(@PathVariable("id") int id){
		if(false==car.is_id(id)){
			System.out.print("ID不存在");
			return new ResponseEntity<Car_inf>(HttpStatus.NO_CONTENT);
		}
		if(car.deletecar(id)){
			System.out.print("删除成功");
			return new ResponseEntity<Car_inf>(HttpStatus.OK);
		}
		return new ResponseEntity<Car_inf>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	//查询控制器(c_id,c_brand,_driving-license)
	@RequestMapping(value ="/api/car_inf/findbyid/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Car_inf>> getcarByid(@PathVariable("id") int id) //
	{
		
		List<Car_inf> carList= car.getcarByid1(id);
		if(carList.isEmpty()) {
			System.out.print("查询失败");
			return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Car_inf>>(carList, HttpStatus.OK);
	}
	@RequestMapping(value ="/api/car_inf/findbybrand/{brand}", method = RequestMethod.GET)
	public ResponseEntity<List<Car_inf>> getcarBybrand(@PathVariable("brand") String brand) //
	{
		List<Car_inf> carList= car.getcarBybrand(brand);
		if(carList.isEmpty()) {
			System.out.print("查询失败");
			return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Car_inf>>(carList, HttpStatus.OK);
	}
	
	@RequestMapping(value ="/api/car_inf/findbylicense/{d_license}", method = RequestMethod.GET)
	public ResponseEntity<List<Car_inf>> getcarByd_license(@PathVariable("d_license") String d_license) //
	{
		List<Car_inf> carList= car.getcarBydriving_license(d_license);
		if(carList.isEmpty()) {
			System.out.print("查询失败");
			return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Car_inf>>(carList, HttpStatus.OK);
	}
	
	//修改车辆信息(根据id选择要修改的信息)
	@RequestMapping(value ="/api/car_inf/updatebyid/{id}", method = RequestMethod.POST)
   public ResponseEntity<Car_inf> updatecar(@PathVariable("id") int id,@RequestBody Car_inf car1){
	  Car_inf car2=car.getcarByid(id);
	   if(null== car2){
		   System.out.print("不存在该id");
		   return new ResponseEntity<Car_inf>(HttpStatus.NOT_FOUND);
	   }
	   if(car1.getBrand()!=null){
		   car2.setBrand(car1.getBrand());
	   }
	   if(car1.getSeat()!=0){
		   car2.setSeat(car1.getSeat());
	   }
	   if(car1.getLogon()!=null){
		  car2.setLogon(car1.getLogon());
	   }
	   if(car1.getDated()!=null){
		   car2.setDated(car1.getDated());
	   }
	   if(car1.getLicense()!=null){
		   car2.setLicense(car1.getLicense());
	   }
	   if(car1.getD_license()!=null){
		   car2.setD_license(car1.getD_license());
	   }
	   if(car.updatecar(car2, id)){
		   System.out.print("更新成功");
		   return new ResponseEntity<Car_inf>(car2, HttpStatus.OK);
	   }
	   
	   return  new ResponseEntity<Car_inf>(HttpStatus.INTERNAL_SERVER_ERROR);
   }
   
	/*导出excel*/
	@RequestMapping(value = "/api/Excel/car_inf", method = RequestMethod.POST)
	public ResponseEntity<String> addStaToExcel(@RequestBody List<Car_inf> carlist,HttpServletRequest request) {
		 System.out.print("生成excel\n");
		String path="/public/excel/";
		String realpath=request.getSession().getServletContext().getRealPath(path);
		if(car.addToExcel(carlist,realpath)){
			//System.out.print("成功\n");
			return new ResponseEntity<String>("/excel/车辆信息",HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
