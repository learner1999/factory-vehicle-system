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

import com.isoftstone.web.dao.StationDao;
import com.isoftstone.web.pojo.Station;

@RestController
public class PointController {
	private StationDao staDao=new StationDao();
	/**查询部分*/

	/***
	 * 查询所有停车点信息，如果有name参数返回根据name模糊搜索的集合
	 * @param name
	 * 			模糊搜索的地址或停车点名字段
	 * @return 查询集合
	 */
	@RequestMapping(value = "/api/Point", method = RequestMethod.GET)
	public ResponseEntity<List<Station>> getAllStation(@RequestParam(value="name",required=false) String s_name)
	{
		System.out.println("获取用户通过s_name=" + s_name);
		if(s_name!=null)
		{
			List<Station> stationList = staDao.searchSta(s_name, 0);
			return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
		}
		
		List<Station> stationList = staDao.showAllSta(0);
		if(stationList.isEmpty()) {
			return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Station>>(stationList, HttpStatus.OK);
	}
	
	/**修改部分*/
	/***
	 *  获得id，将临时停车点转换为站点
	 * 
	 * @param id
	 *            要修改的临时停车点id
	 * @return 是否修改成功
	 */
	@RequestMapping(value = "/api/PointChange/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> updateUser(@PathVariable("id") int s_id) {
		System.out.println("更新站点信息，s_id=" + s_id);
		
		if(staDao.changePoToSta(s_id)) {
			return new ResponseEntity<Boolean>(true,HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false,HttpStatus.NOT_FOUND);
	}
	
	/***
	 * 新增一个临时停车点。最后的是否是站点的属性可以不赋值
	 * 
	 * @param sta
	 *            新增的临时停车点对象
	 * @return 新增的临时停车点对象
	 */
	@RequestMapping(value = "/api/Point", method = RequestMethod.POST)
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
		sta.setS_is_used(0);
		if(staDao.createStation(sta)) {
			return new ResponseEntity<Station>(sta, HttpStatus.CREATED);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Station>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**修改的部分在StationController里面*/
	/**通过id查询在StationController里面*/
}
