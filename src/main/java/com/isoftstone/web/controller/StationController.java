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

import com.isoftstone.web.dao.StationDao;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.pojo.TestUser2;

@RestController
public class StationController {
	private StationDao staDao=new StationDao();
	/*查询部分*/
	
	 /*获得所有的站点信息*/
	@RequestMapping(value = "/api/Station", method = RequestMethod.GET)
	public ResponseEntity<List<Station>> getAllStation()
	{
		List<Station> stationList = staDao.getAllStations();
		if(stationList.isEmpty()) {
			return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
	}
	
	/*通过站点名字模糊搜索*/
	@RequestMapping(value = "/api/Station/{s_name}", method = RequestMethod.GET)
	public ResponseEntity<List<Station>> getSomeStation(@PathVariable("s_name") String s_name)
	{
		System.out.println("获取用户通过s_name=" + s_name);
		List<Station> stationList = staDao.getStaByName(s_name);
		if(stationList.isEmpty()) {
			System.out.println("用户没有找到 s_name=" + s_name);
			return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
	}
	
	/*新增一个站点
	 * 这里的json对象是一个Station类。但是由于主键已经设置过了自增
	 * 所以id可以不赋值
	 * 反正你赋了也没得用*/
	@RequestMapping(value = "/api/Station", method = RequestMethod.POST)
	public ResponseEntity<Station> createStation(@RequestBody Station sta) { 
		System.out.println("创建站点 " + sta.getS_name());
		
		// 检查提交的数据是否完整，如果不完整，将状态码设置为 BAD_REQUEST
		if(null == sta.getS_name() || 0 == sta.getLongitude() || 0 == sta.getLatitude()) {
			return new ResponseEntity<Station>(HttpStatus.BAD_REQUEST);
		}
		
		// 检测站点名是否冲突
		if(staDao.isStationExist(sta.getS_name())) {
			System.out.println("站点重名");
			return new ResponseEntity<Station>(HttpStatus.CONFLICT);
		}
		
		//检测在这个x，y坐标上是否已经有站点了
		if(staDao.isxyExist(sta.getLongitude(), sta.getLatitude()))
		{
			System.out.println("在此位置上已经有站点了");
			return new ResponseEntity<Station>(HttpStatus.CONFLICT);
		}
		
		// 创建站点
		if(staDao.createStation(sta)) {
			return new ResponseEntity<Station>(sta, HttpStatus.CREATED);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/*修改部分*/
	/*获得id和Station对象，通过id用新的数据替换掉原来的数据*/
	@RequestMapping(value = "/api/Station/{s_id}", method = RequestMethod.PUT)
	public ResponseEntity<Station> updateUser(@PathVariable("s_id") int s_id, @RequestBody Station sta) {
		System.out.println("更新站点信息，s_id=" + s_id);
		
		// 通过 id 找到用户当前的信息
		Station staNow=staDao.getStaById(s_id);
		
		if(null == staNow) {
			System.out.println("没有找到该站点，s_id=" + s_id);
			return new ResponseEntity<Station>(HttpStatus.NOT_FOUND);
		}
		
		// 修改需要修改的属性
		if(sta.getS_name() != null)
			staNow.setS_name(sta.getS_name());
		if(sta.getLongitude() != 0) 
			staNow.setLongitude(sta.getLongitude());
		if(sta.getLatitude()!= 0)
			staNow.setLatitude(sta.getLatitude());
		
		// 数据库操作
		if(staDao.updateStaById(s_id, staNow)) {
			return new ResponseEntity<Station>(staNow, HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/*删除部分*/
	/*根据站点的id删除掉一个站点*/
	@RequestMapping(value = "/api/Station/{s_id}", method = RequestMethod.DELETE)
	public ResponseEntity<Station> deleteUser(@PathVariable("s_id") int id) {
		System.out.println("删除一个站点 s_id=" + id);

		Station sta=staDao.getStaById(id);
		if(null == sta) {
			System.out.println("找不到 s_id=" + id + " 的站点");
			return new ResponseEntity<Station>(HttpStatus.NOT_FOUND);
		}
		
		if(staDao.deleteSta(id)) {
			System.out.println("OK");
			return new ResponseEntity<Station>(HttpStatus.NO_CONTENT);
		}
		
		return  new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/*导出excel*/
	@RequestMapping(value = "/api/Excel/Station", method = RequestMethod.POST)
	public ResponseEntity<String> addStaToExcel(@RequestBody List<Station> stalist,HttpServletRequest request) {
				
		String path="/public/excel/";
		String realpath=request.getSession().getServletContext().getRealPath(path);
		if(staDao.addToExcel(stalist,realpath)){
			return new ResponseEntity<String>("/excel/站点.xls",HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
