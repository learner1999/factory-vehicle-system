package com.isoftstone.web.controller;

import com.isoftstone.web.pojo.Emlopee;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.Arrangementdao;
import com.isoftstone.web.pojo.Arrange;
import com.isoftstone.web.pojo.Arrangement;
import com.isoftstone.web.pojo.Arrangements;
import com.isoftstone.web.pojo.Car_inf;
@RestController
public class ArrangementController {
	
	Arrangementdao ad=new Arrangementdao();
	/**
	 * 车辆数目，司机休息日数上下限可不输入
	 * @param carcount
	 * @param upredays
	 * @param dwredays
	 * @return 司机数，组员数，组数
	 */
	@RequestMapping(value ="/api/arrange/{carcount}", method = RequestMethod.GET)
	public ResponseEntity<List<Arrange>> get(
			@PathVariable("carcount") int carcount,
			@RequestParam(value = "upredays", defaultValue="6") int upredays,
			@RequestParam(value = "dwredays", defaultValue="2") int dwredays) 
	{
		if(upredays<0|| dwredays<0||upredays<dwredays){
			 return new ResponseEntity<List<Arrange>>(HttpStatus.NO_CONTENT);
		}
		else{
			List<Arrange> dr=ad.driver(carcount, upredays, dwredays);
			if(dr.isEmpty()){
				return new ResponseEntity<List<Arrange>>(HttpStatus.NO_CONTENT);
			}
			else{
				 //System.out.printf("司机的人数为："+b.getDrivers()+"\t分成"+b.getArrs()+"组\t"+"每组"+b.getArrers()+"人\t 每个司机"+b.getArrers()+"天休息一天（循环）\n");
				return new ResponseEntity<List<Arrange>>(dr,HttpStatus.OK);
			}
			
			
		}}
	
	/**
	 * @param drivers 推荐司机数（由方案决定）
	 * @param circulation 循环天数（由方案决定）
	 * @param date 日期
	 * @return
	 */
	@RequestMapping(value ="/api/arrange", method = RequestMethod.GET)
	public ResponseEntity <List<Arrangements>> getarrangement(
			@RequestParam(value = "drivers",defaultValue="7") int drivers,
			@RequestParam(value = "circulation", defaultValue="7") int circulation,
			@RequestParam(value = "date", required = false) java.sql.Date date) 
	{
		List<Arrangements> a2=new ArrayList<>();
		if(drivers<=0||circulation<=0||date==null){
			 return new ResponseEntity <List<Arrangements>>(HttpStatus.NO_CONTENT);
		}
		else{
			a2=ad.get_arr(date, drivers, circulation);
			}
			return  new ResponseEntity <List<Arrangements>>(a2,HttpStatus.OK);
		}
}
