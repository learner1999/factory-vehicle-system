package com.isoftstone.web.controller;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
	public ResponseEntity<ExcelResult> getExcelByDay(@RequestParam("day") String day,
			@RequestParam(value = "download", required = false) String download, HttpServletRequest request,
			HttpServletResponse response) {
		List<Excel> eList = etDao.getCountByDay(day);
		if (eList.isEmpty()) {
			return new ResponseEntity<ExcelResult>(HttpStatus.NO_CONTENT);
		}
		ExcelResult excelRes = new ExcelResult(eList.size());
		String sname[] = excelRes.getSname();
		int count[] = excelRes.getCount();
		for (int i = 0; i < eList.size(); i++) {
			Excel e = eList.get(i);
			sname[i] = e.getStaName();
			count[i] = e.getsCount();
		}
		excelRes.setSname(sname);
		excelRes.setCount(count);
		
		if(download != null && download.equals("true")) {
			excelDownloadDispatcher(excelRes, request, response); 
		}
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
	public ResponseEntity<ExcelResult> getExcelByWeek(@RequestParam(value="day",required=false) String day, @RequestParam(value = "download", required = false) String download, HttpServletRequest request, HttpServletResponse response)
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
		
		if(download != null && download.equals("true")) {
			excelDownloadDispatcher(excelRes, request, response); 
		}
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
	public ResponseEntity<ExcelResult> getExcelByMonth(@RequestParam(value="day",required=false) String day, @RequestParam(value = "download", required = false) String download, HttpServletRequest request, HttpServletResponse response)
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
		
		if(download != null && download.equals("true")) {
			excelDownloadDispatcher(excelRes, request, response); 
		}
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
	
	
	
	
	/***
	 * 给定日期获取这一天各路线的人员乘坐信息
	 * 
	 * @param day
	 * 		给定的日期
	 * @return 这一天各路线的人员乘坐信息
	 */
	@RequestMapping(value = "/api/Excel/routeday", method = RequestMethod.GET)
	public ResponseEntity<ExcelResult> getRouteExcelByDay(@RequestParam(value="day",required=false) String day, @RequestParam(value = "download", required = false) String download, HttpServletRequest request, HttpServletResponse response)
	{
		List<Excel> eList=etDao.getRouteCountByDay(day);
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
		
		if(download != null && download.equals("true")) {
			excelDownloadDispatcher(excelRes, request, response); 
		}
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
	
	/***
	 * 给定日期获取这一天所在一周各路线的人员乘坐信息
	 * 
	 * @param day
	 * 		给定的日期
	 * @return 这一天所在一周各路线的人员乘坐信息
	 */
	@RequestMapping(value = "/api/Excel/routeweek", method = RequestMethod.GET)
	public ResponseEntity<ExcelResult> getRouteExcelByWeek(@RequestParam(value="day",required=false) String day, @RequestParam(value = "download", required = false) String download, HttpServletRequest request, HttpServletResponse response)
	{
		String[] weekDates = eDao.getWeekStartAndEndDate(day);
		List<Excel> eList=etDao.getRouteCountByDays(weekDates[0],weekDates[1]);
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
		
		if(download != null && download.equals("true")) {
			excelDownloadDispatcher(excelRes, request, response); 
		}
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
	
	/***
	 * 给定日期获取这一天所在一月各路线的人员乘坐信息
	 * 
	 * @param day
	 * 		给定的日期
	 * @return 这一天所在一月各路线的人员乘坐信息
	 */
	@RequestMapping(value = "/api/Excel/routemonth", method = RequestMethod.GET)
	public ResponseEntity<ExcelResult> getRouteExcelByMonth(@RequestParam(value="day",required=false) String day, @RequestParam(value = "download", required = false) String download, HttpServletRequest request, HttpServletResponse response)
	{
		String[] monthDates = eDao.getMonthStartAndEndDate(day);
		List<Excel> eList=etDao.getRouteCountByDays(monthDates[0],monthDates[1]);
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
		
		if(download != null && download.equals("true")) {
			excelDownloadDispatcher(excelRes, request, response); 
		}
		return new ResponseEntity<ExcelResult>(excelRes, HttpStatus.OK);
	}
	

	@SuppressWarnings("deprecation")
	public void excelDownloadDispatcher(ExcelResult excelRes, HttpServletRequest request,
			HttpServletResponse response) {

		// 生成报表 excel
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("报表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("站点名（路线名）");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("乘车人数");
		cell.setCellStyle(style);

		for (int i = 0, len = excelRes.getSname().length; i < len; i++) {
			String[] names = excelRes.getSname();
			int[] counts = excelRes.getCount();
			
			row = sheet.createRow((int) i + 1);
			// 第四步，创建单元格，并设置值
			row.createCell((short) 0).setCellValue(names[i]);
			row.createCell((short) 1).setCellValue(counts[i]);
		}
		// 第六步，将文件存到指定位置
		try {
			String path="/public/excel/报表.xls";
			String realpath=request.getSession().getServletContext().getRealPath(path);
			FileOutputStream fout = new FileOutputStream(realpath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 重定向下载 excel
		String filePath = "/excel/报表.xls";
		String fileDisplay = null;
		try {
			fileDisplay = URLEncoder.encode("报表.xls", "UTF-8"); // 下载文件时显示的文件保存名称
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setContentType("application/x-download");// 设置为下载application/x-download
		response.addHeader("Content-Disposition", "attachment;filename=" + fileDisplay);

		try {
			RequestDispatcher dis = request.getRequestDispatcher(filePath);
			if (dis != null) {
				dis.forward(request, response);
			}
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



