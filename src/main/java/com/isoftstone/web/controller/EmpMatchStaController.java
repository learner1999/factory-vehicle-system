package com.isoftstone.web.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor.LEMON_CHIFFON;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.CalForStaDao;
import com.isoftstone.web.dao.EmpMStaDao;
import com.isoftstone.web.dao.StationDao;
import com.isoftstone.web.pojo.CalForSta;
import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.EmpMatchSta;
import com.isoftstone.web.pojo.Station;
import com.routematrix.pojo.Coordinate;
import com.webapi.ApiOp;

@RestController
public class EmpMatchStaController {
	
	private EmpMStaDao emsDao=new EmpMStaDao();
	
	
	/***
	 * 查询数据库中所有的站点对应信息
	 * 
	 * @return 所有站点对应信息
	 */
	@RequestMapping(value="/api/EmpMatchSta",method=RequestMethod.GET)
	public ResponseEntity<List<EmpMatchSta>> getEmlopBySta(@RequestParam(value="name",required=false) String s_name)
	{
		System.out.println("获取用户通过s_name=" + s_name);
		if(s_name!=null)
		{
			List<EmpMatchSta> emslist = emsDao.search(s_name);
			return new ResponseEntity<List<EmpMatchSta>>(emslist, HttpStatus.OK);
		}
		
		List<EmpMatchSta> emslist = emsDao.showAll();
		if(emslist.isEmpty()) {
			return new ResponseEntity<List<EmpMatchSta>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<EmpMatchSta>>(emslist, HttpStatus.OK);
	}
	
	/***
	 * 通过站点id查看此站点的员工
	 * 
	 * @param s_id
	 *            要查询的站点id
	 * @return 员工id集合
	 */
	@RequestMapping(value="/api/StaMatchEmp/{s_id}",method=RequestMethod.GET)
	public ResponseEntity<List<Emlopee>> getEmlopBysid(@PathVariable("s_id") int s_id)
	{
		List<Emlopee> emlopList=emsDao.getEmlopBySta(s_id);
		System.out.println(s_id);
		if(emlopList==null)
		{
			return new ResponseEntity<List<Emlopee>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Emlopee>>(emlopList, HttpStatus.OK);
	}
	
	/***
	 * 通过员工id找到这条记录
	 * 
	 * @param e_id
	 *            员工id
	 * @return 员工站点对应信息
	 */
	@RequestMapping(value="/api/EmpMatchSta/{eid}",method=RequestMethod.GET)
	public ResponseEntity<EmpMatchSta> findEmsByEid(@PathVariable("eid") int eid)
	{
		EmpMatchSta ems=emsDao.getEmsByEid(eid);
		if(ems==null)
		{
			return new ResponseEntity<EmpMatchSta>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<EmpMatchSta>(ems, HttpStatus.OK);
	}
	
	/***同步员工。将新建的和修改的直接修改进数据库里，删除没有站点的。可能需要剪辑。
	 * 
	 * @return 同步后更新的记录条数
	 */
	@RequestMapping(value="/api/EmpMatchSta/updateEms",method=RequestMethod.GET)
	public ResponseEntity<Integer> updateEms()
	{
		Integer count=0;
		List<Emlopee> emloplist=emsDao.matchEAndS();
		count+=emloplist.size();
		for(int i=0;i<emloplist.size();i++)
		{
			Emlopee e=emloplist.get(i);
			EmpMatchSta ems=emsDao.getXYByAdd(e);
			//System.out.println(ems.getE_id()+"  "+ems.getE_address()+"  "+ems.getE_x()+"  "+ems.getE_y());
			emsDao.creatEMS(ems);
			System.out.println(ems.getE_id());
		}
		emloplist=emsDao.getAllchange();
		count+=emloplist.size();
		for(int i=0;i<emloplist.size();i++)
		{
			Emlopee e=emloplist.get(i);
			EmpMatchSta ems=emsDao.getXYByAdd(e);
			emsDao.updateEMSForAdd(ems);
			System.out.println(ems.getE_id());
		}
		List<EmpMatchSta> emsList=emsDao.matchSAndE();
		count+=emsList.size();
		for(int i=0;i<emsList.size();i++)
		{
			EmpMatchSta ems=emsList.get(i);
			emsDao.deleteEms(ems.getE_id());
			System.out.println(ems.getE_id());
		}
		if(0==count)
		{
			return new ResponseEntity<Integer>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Integer>(count, HttpStatus.OK);
	}
	
	
	/***
	 * 找到新增但还没有为其建造站点的员工
	 * 
	 * @return 这类员工信息集
	 */
	@RequestMapping(value="/api/EmpMatchSta/matchE",method=RequestMethod.GET)
	public ResponseEntity<List<EmpMatchSta>> matchEAndS()
	{
		List<EmpMatchSta> emsList=emsDao.getAllnew();
		if(emsList==null)
		{
			return new ResponseEntity<List<EmpMatchSta>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<EmpMatchSta>>(emsList, HttpStatus.OK);
	}
	
	/***
	 * 找到员工附近站点
	 * 
	 * @return 这类员工信息集
	 */
	@RequestMapping(value="/api/EmpMatchSta/Near",method=RequestMethod.PUT)
	public ResponseEntity<List<Station>> Near(@RequestBody EmpMatchSta ems)
	{
		StationDao stadao=new StationDao();
		List<Station> getNearMan=stadao.getNearMan(ems);
		if(getNearMan==null)
		{
			return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Station>>(getNearMan, HttpStatus.OK);
	}
	
	/***
	 * 智能规划
	 * 
	 * @return 规划后的员工，站点对应
	 */
	@RequestMapping(value="/api/EmpMatchSta/calcu",method=RequestMethod.GET)
	public ResponseEntity<List<CalForSta>> calcu()
	{
		CalForStaDao caldao=new CalForStaDao();
		List<CalForSta> calList=caldao.cal();
		if(calList==null)
		{
			return new ResponseEntity<List<CalForSta>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<CalForSta>>(calList, HttpStatus.OK);
	}
	
	/***
	 * 修改员工对应的站点表
	 * 
	 * @param e_id,ems
	 *            员工id,员工站点对应信息
	 * @return 修改后的信息
	 */
	@RequestMapping(value = "/api/EmpMatchSta/{e_id}", method = RequestMethod.PUT)
	public ResponseEntity<EmpMatchSta> updateUser(@PathVariable("e_id") int e_id, @RequestBody EmpMatchSta ems) {
		System.out.println("更新站点信息，e_id=" + e_id);
		
		// 通过 id 找到用户当前的信息
		EmpMatchSta emsNow=emsDao.getEmsByEid(e_id);
		if(null == emsNow) {
			System.out.println("没有找到该用户，e_id=" + e_id);
			return new ResponseEntity<EmpMatchSta>(HttpStatus.NOT_FOUND);
		}
		// 修改需要修改的属性
		if(ems.getS_id()!=0)
			emsNow.setS_id(ems.getS_id());
		
		// 数据库操作
		if(emsDao.updateEMSByEid(e_id, emsNow.getS_id())) {
			return new ResponseEntity<EmpMatchSta>(emsNow, HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<EmpMatchSta>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/***
	 * 将规划的结果添加进数据库
	 * @param calList
	 * 				智能规划的结果
	 * @return 处理的记录条数
	 */
	@RequestMapping(value = "/api/EmpMatchSta/add", method = RequestMethod.PUT)
	public ResponseEntity<Integer> addTodb(@RequestBody List<CalForSta> calList) {
		CalForStaDao caldao=new CalForStaDao();
		int count=caldao.addToDb(calList);
		if(0==count) {
			return new ResponseEntity<Integer>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Integer>(count, HttpStatus.OK);
		
	}
	
	/*删除部分*/
	/***
	 * 删除某员工对应的员工站点信息
	 * 
	 * @param e_id
	 *            员工id
	 * @return 是否删除成功
	 */
	@RequestMapping(value = "/api/EmpMatchSta/{e_id}", method = RequestMethod.DELETE)
	public ResponseEntity<EmpMatchSta> deleteUser(@PathVariable("e_id") int e_id) {
		System.out.println("删除一个站点 s_id=" + e_id);

		EmpMatchSta emsNow=emsDao.getEmsByEid(e_id);
		if(null == emsNow) {
			System.out.println("找不到 s_id=" + e_id + " 的站点");
			return new ResponseEntity<EmpMatchSta>(HttpStatus.NOT_FOUND);
		}
		
		if(emsDao.deleteEms(e_id)) {
			System.out.println("OK");
			return new ResponseEntity<EmpMatchSta>(HttpStatus.OK);
		}
		
		return  new ResponseEntity<EmpMatchSta>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
