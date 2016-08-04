package com.isoftstone.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.ExcelDao;
import com.isoftstone.web.dao.ExcelToDbDao;
import com.isoftstone.web.pojo.Excel;
import com.isoftstone.web.pojo.ExcelResult;
import com.isoftstone.web.pojo.Station;
@RestController
public class ExcelController {
	ExcelDao eDao = new ExcelDao();
	ExcelToDbDao etDao=new ExcelToDbDao();
	
	/***
	 * 给定日期获取这一天各站点的人员乘坐信息
	 * 
	 * @param day
	 * 		给定的日期
	 * @return 这一天各站点的人员乘坐信息
	 */
	@RequestMapping(value = "/api/Excel/day", method = RequestMethod.GET)
	public ResponseEntity<ExcelResult> getExcelByDay(@RequestParam(value="day",required=false) String day)
	{
		List<Excel> eList=etDao.getCountByDay(day);
		if(eList.isEmpty())
		{
			return new ResponseEntity<ExcelResult>(HttpStatus.NO_CONTENT);
		}
		ExcelResult excelRes=new ExcelResult(eList.size());
		String sname[]=excelRes.getSname();
		int count[]=excelRes.getCount();
		for(int i=0;i<eList.size();i++)
		{
			Excel e=eList.get(i);
			sname[i]=e.getStaName();
			count[i]=e.getsCount();
		}
		excelRes.setSname(sname);
		excelRes.setCount(count);
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
	
	/***
	 * 给定日期获取这一天所在一周各站点的人员乘坐信息
	 * 
	 * @param day
	 * 		给定的日期
	 * @return 这一天所在一周各站点的人员乘坐信息
	 */
	@RequestMapping(value = "/api/Excel/week", method = RequestMethod.GET)
	public ResponseEntity<ExcelResult> getExcelByWeek(@RequestParam(value="day",required=false) String day)
	{
		String[] weekDates = eDao.getWeekStartAndEndDate(day);
		List<Excel> eList=etDao.getCountByDays(weekDates[0],weekDates[1]);
		if(eList.isEmpty())
		{
			return new ResponseEntity<ExcelResult>(HttpStatus.NO_CONTENT);
		}
		ExcelResult excelRes=new ExcelResult(eList.size());
		String sname[]=excelRes.getSname();
		int count[]=excelRes.getCount();
		for(int i=0;i<eList.size();i++)
		{
			Excel e=eList.get(i);
			sname[i]=e.getStaName();
			count[i]=e.getsCount();
		}
		excelRes.setSname(sname);
		excelRes.setCount(count);
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
	
	/***
	 * 给定日期获取这一天所在一月各站点的人员乘坐信息
	 * 
	 * @param day
	 * 		给定的日期
	 * @return 这一天所在一月各站点的人员乘坐信息
	 */
	@RequestMapping(value = "/api/Excel/month", method = RequestMethod.GET)
	public ResponseEntity<ExcelResult> getExcelByMonth(@RequestParam(value="day",required=false) String day)
	{
		String[] monthDates = eDao.getMonthStartAndEndDate(day);
		List<Excel> eList=etDao.getCountByDays(monthDates[0],monthDates[1]);
		if(eList.isEmpty())
		{
			return new ResponseEntity<ExcelResult>(HttpStatus.NO_CONTENT);
		}
		ExcelResult excelRes=new ExcelResult(eList.size());
		String sname[]=excelRes.getSname();
		int count[]=excelRes.getCount();
		for(int i=0;i<eList.size();i++)
		{
			Excel e=eList.get(i);
			sname[i]=e.getStaName();
			count[i]=e.getsCount();
		}
		excelRes.setSname(sname);
		excelRes.setCount(count);
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
}
