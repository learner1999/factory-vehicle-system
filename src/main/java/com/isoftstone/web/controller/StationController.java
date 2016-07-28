package com.isoftstone.web.controller;

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

import com.isoftstone.web.pojo.EmpMatchSta;
import com.isoftstone.web.dao.EmpMStaDao;
import com.isoftstone.web.dao.StationDao;
import com.isoftstone.web.pojo.Station;

@RestController
public class StationController {
	private StationDao staDao=new StationDao();
	/**查询部分*/

	/***
	 * 查询所有站点信息，如果有name参数返回根据name模糊搜索的集合
	 * @param name
	 * 			模糊搜索的地址或站点名字段
	 * @return 查询集合
	 */
	@RequestMapping(value = "/api/Station", method = RequestMethod.GET)
	public ResponseEntity<List<Station>> getAllStation(@RequestParam(value="name",required=false) String s_name)
	{
		System.out.println("获取用户通过s_name=" + s_name);
		if(s_name!=null)
		{
			List<Station> stationList = staDao.searchSta(s_name, 1);
			return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
		}
		
		List<Station> stationList = staDao.showAllSta(1);
		if(stationList.isEmpty()) {
			return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
	}
	
	/*全哥喜欢的通过id查询(●'◡'●)*/
	/***
	 * 通过id获取到对应的站点信息（由于通过id查的的结果是唯一的，所以不会出现冲突）
	 * 
	 * @param id
	 *            站点id
	 * @return 查询结果
	 */
	@RequestMapping(value="/api/Station/{s_id}",method=RequestMethod.GET)
	public ResponseEntity<Station> getStationById(@PathVariable("s_id") int s_id)
	{
		Station sta=staDao.getStaById(s_id);
		if(sta.getS_name()==null)
		{
			return new ResponseEntity<Station>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Station>(sta, HttpStatus.OK);
	}
	
	/***
	 * 目前站点是否合适
	 * 
	 * @return 合适代码。1是人员站点不匹配，建议重新同步。2是有人数为0的站点，建议删除。3是上面情况都有。0是合适
	 */
	@RequestMapping(value="/api/Station/isOk",method=RequestMethod.GET)
	public ResponseEntity<Integer> getStationById()
	{
		int isok=staDao.isOk();
		
		return new ResponseEntity<Integer>(isok,HttpStatus.OK);
	}
	
	
	/***
	 * 找出所有人数为0的站点
	 * 
	 * @return 所有人数为0的站点集合
	 */
	@RequestMapping(value = "/api/Station/Zero", method = RequestMethod.GET)
	public ResponseEntity<List<Station>> getStationZero()
	{	
		List<Station> stationList = staDao.isStationZero();
		if(stationList.isEmpty()) {
			return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
	}
	
	//关于附近的能不能默认先显示10个
	/***
	 * 查看此站点/停车点附近的员工
	 * 
	 * @param sta
	 * 			操作的站点
	 * @return 这类员工集合
	 */
	@RequestMapping(value = "/api/Station/Near", method = RequestMethod.PUT)
	public ResponseEntity<List<EmpMatchSta>> getStationNear(@RequestBody Station sta)
	{	
		EmpMStaDao emsDao=new EmpMStaDao();
		List<EmpMatchSta> emsList =emsDao.getNear(sta);
		if(emsList.isEmpty()) {
			return new ResponseEntity<List<EmpMatchSta>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<EmpMatchSta>>(emsList, HttpStatus.OK);
	}
	
	
	/***
	 * 新增一个站点。最后的是否是站点的属性可以不赋值
	 * 
	 * @param sta
	 *            新增的站点对象
	 * @return 新增的站点对象
	 */
	@RequestMapping(value = "/api/Station", method = RequestMethod.POST)
	public ResponseEntity<Station> createStation(@RequestBody Station sta) { 
		System.out.println("创建站点 " + sta.getS_name());
		
		// 检查提交的数据是否完整，如果不完整，将状态码设置为 BAD_REQUEST
		if(null == sta.getS_name() ||null==sta.getS_address() || 0 == sta.getLongitude() || 0 == sta.getLatitude()) {
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
			return new ResponseEntity<Station>(HttpStatus.BAD_GATEWAY);
		}
		
		// 创建站点
		sta.setS_is_used(1);
		if(staDao.createStation(sta)) {
			return new ResponseEntity<Station>(sta, HttpStatus.CREATED);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//反推路线
	@RequestMapping(value = "/api/StaToRoute/{s_id}", method = RequestMethod.GET)
	public ResponseEntity<List<String>> getRouteBysta(@PathVariable("s_id") int s_id)
	{
		System.out.println("获取用户通过s_id=" + s_id);
		
		List<String> strList=staDao.getS_car(s_id);
		if(strList.isEmpty()) {
			return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<String>>(strList, HttpStatus.OK);
	}
	
	
	
	/**修改部分*/
	/***
	 *  获得id，将站点转换为临时停车点
	 * 
	 * @param id
	 *            要修改的站点id
	 * @return 是否修改成功
	 */
	@RequestMapping(value = "/api/StaChange/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> updateUser(@PathVariable("id") int s_id) {
		System.out.println("更新站点信息，s_id=" + s_id);
		
		if(staDao.changeStaToPo(s_id)) {
			return new ResponseEntity<Boolean>(true,HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false,HttpStatus.NOT_FOUND);
	}
	
	/***
	 * 通过站点/临时停车点id修改站点名称（由于不存在操作冲突的情况，所以可以公用）
	 * 
	 * @param s_id,nameNow
	 * 			站点/临时停车点id，修改后的名称
	 * @return 修改后的站点/停车点信息
	 */
	@RequestMapping(value = "/api/Station/{s_id}", method = RequestMethod.PUT)
	public ResponseEntity<Station> updateUser(@PathVariable("s_id") int s_id, @RequestBody String nameNow) {
		System.out.println("更新站点信息，s_id=" + s_id);
		
		// 通过 id 找到用户当前的信息
		Station staNow=staDao.getStaById(s_id);
		
		if(null == staNow) {
			System.out.println("没有找到该站点，s_id=" + s_id);
			return new ResponseEntity<Station>(HttpStatus.NOT_FOUND);
		}
		
		// 数据库操作
		if(staDao.change(s_id, nameNow)) {
			staNow.setS_name(nameNow);
			return new ResponseEntity<Station>(staNow, HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	/**删除部分*/
	/***
	 * 根据站点/临时停车点的id删除掉一个站点（由于不存在操作冲突的情况，所以可以公用）
	 * 
	 * @param s_id
	 *            要删除的站点/临时停车点id
	 * @return 删除的站点信息
	 */
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
			return new ResponseEntity<Station>(HttpStatus.OK);
		}
		
		return  new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**导出excel*/
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
