package com.isoftstone.web.controller;

import com.isoftstone.web.pojo.Emlopee;

import java.sql.Date;
import java.util.ArrayList;
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
			@RequestParam(value = "dwredays", defaultValue="4") int dwredays) 
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
	public ResponseEntity<List<Arrangement>> getarrangement(
			@RequestParam(value = "drivers",required = false) int drivers,
			@RequestParam(value = "circulation", required = false) int circulation,
			@RequestParam(value = "date", required = false) Date date) 
	{
		if(drivers<=0||circulation<=0||date==null){
			 return new ResponseEntity<List<Arrangement>>(HttpStatus.NO_CONTENT);
		}
		else{
			long days=ad.getQuot(date.toString(),"2016-1-1");
			System.out.println("\ndays="+days+"\n");
			List<Arrange> ar=ad.arrange(days, drivers, circulation);//获取排班相关信息   
			if(ar.isEmpty()){
				return new ResponseEntity<List<Arrangement>>(HttpStatus.NO_CONTENT);
			}
			else{
				
				List<Arrangement> ag=ad.arrangement(ar,date);
				return new ResponseEntity<List<Arrangement>>(ag,HttpStatus.OK);
			}	
		}
		}
	
}
