package com.isoftstone.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.EmlopeeDao;
import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.pojo.Emlopee;
@RestController
public class Employee_Controller {
	
	EmlopeeDao emp=new EmlopeeDao();
	
	@RequestMapping(value ="/api/emlopee/{eid}", method = RequestMethod.GET)
	   public ResponseEntity<List<Emlopee>> getemp_inf(@PathVariable("eid") int eid){
	    System.out.printf("\n执行了\n");  
		 List<Emlopee> e=emp.getemp_inf(eid);
		   if(e.isEmpty()){
			   return new ResponseEntity<List<Emlopee>>(HttpStatus.NO_CONTENT);
		   }
		   else{
			   return new ResponseEntity<List<Emlopee>>(e, HttpStatus.OK);
		   } 
	   }
	
	
	@RequestMapping(value ="/api/emlopee/{eid}", method = RequestMethod.PUT)
	   public ResponseEntity<List<Emlopee>> updateaddress(@PathVariable("eid") int eid,
			   @RequestParam(value = "address", required = false) String address){
		  if(address!=null){
			  System.out.print("\n更新开始");
		   if(emp.updateaddress(eid, address)){
			   System.out.print("\n更新成功");
			   return new ResponseEntity<List<Emlopee>>(HttpStatus.OK);
		   }
		   else{
			   System.out.print("\n更新失败");
			   return new ResponseEntity<List<Emlopee>>(HttpStatus.NOT_FOUND);
		   }
		  }
		  else{
		   
		   return new ResponseEntity<List<Emlopee>>(HttpStatus.CONFLICT);
	   }}
	
	
}