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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.Car_dao;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.pojo.Car_inf;

@RestController
public class Car_manage_Controller {
	Car_dao car=new Car_dao();//初始化对象（方法的对象）
	
	
	
	/**
	 * 新增一辆车的信息
	 * 驾驶证行驶证都不能已存在
	 * @param car1
	 * @return
	 */
	@RequestMapping(value ="/api/car_inf", method = RequestMethod.POST)
	public ResponseEntity<Car_inf> createcar(@RequestBody Car_inf car1){
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
	
	
    /**
     * 根据车辆id删除车辆信息
     * @param id
     * @return
     */
	@RequestMapping(value ="/api/car_inf/{id}", method = RequestMethod.DELETE)
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
	
	
	/**
	 * 根据车辆id查询车辆信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value ="/api/car_inf/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Car_inf>> getcarByid(@PathVariable("id") int id) //
	{
		List<Car_inf> carList= car.getcarByid1(id);
		if(carList.isEmpty()) {
			System.out.print("查询失败");
			return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Car_inf>>(carList, HttpStatus.OK);
	}
	
	
/**
 * 根据车辆品牌查询车辆信息(如果不输入参数brand,则会显示所有车辆信息)
 * @param brand
 * @return
 */
	@RequestMapping(value ="/api/car_inf", method = RequestMethod.GET)
	public ResponseEntity<List<Car_inf>> getallcar(
			@RequestParam(value = "brand", required = false) String brand) 
	{
		if(brand!=null){
			return getcarBybrand(brand);
		}
		
		List<Car_inf> carList= car.getAllcar();
		if(carList.isEmpty()) {
			System.out.print("查询失败");
			return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Car_inf>>(carList, HttpStatus.OK);
	}
	/**
	 * 根据车辆品牌返回车辆信息
	 * @param brand
	 * @return
	 */
	public ResponseEntity<List<Car_inf>> getcarBybrand(String brand) {
        List<Car_inf> carlist = car.getcarBybrand(brand);
        if(carlist.isEmpty()) {
            return new ResponseEntity<List<Car_inf>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Car_inf>>(carlist, HttpStatus.OK);
    }

	
/**
 * 根据车辆id，修改并更新车辆信息
 * @param id
 * @param car1
 * @return
 */
	@RequestMapping(value ="/api/car_inf/{id}", method = RequestMethod.PUT)
   public ResponseEntity<Car_inf> updatecar(@PathVariable("id") int id,@RequestBody Car_inf car1){
	  Car_inf car2=car.getcarByid(id);
	   if(null== car2){
		   System.out.print("未找到此ID:"+id);
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
	   if(car1.getD_license()!=null){
		   car2.setD_license(car1.getD_license());
	   }
	   if(car.updatecar(car2, id)){
		   System.out.print("更新成功");
		   return new ResponseEntity<Car_inf>(car2, HttpStatus.OK);
	   }
	   
	   return  new ResponseEntity<Car_inf>(HttpStatus.INTERNAL_SERVER_ERROR);
   }
   
	/**
	 * 输入carlist型数据，导入到车辆信息excel表格
	 * @param carlist
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/Excel/car_inf", method = RequestMethod.POST)
	public ResponseEntity<String> addStaToExcel(@RequestBody List<Car_inf> carlist,HttpServletRequest request) {
		 System.out.print("生成excel\n");
		String path="/public/excel/";//成功后到tomcat中寻找
		String realpath=request.getSession().getServletContext().getRealPath(path);
		if(car.addToExcel(carlist,realpath)){
			//System.out.print("成功\n");
			return new ResponseEntity<String>("/excel/车辆信息",HttpStatus.OK);
		}
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
