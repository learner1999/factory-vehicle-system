package com.isoftstone.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.EmpMStaDao;
import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.EmpMatchSta;
import com.isoftstone.web.pojo.Station;

@RestController
public class EmpMatchStaController {
	
	private EmpMStaDao emsDao=new EmpMStaDao();
	
	//通过站点id查看此站点的员工
	@RequestMapping(value="/api/EmpMatchSta/{s_id}",method=RequestMethod.GET)
	public ResponseEntity<List<Emlopee>> getEmlopBySta(@PathVariable("s_id") int s_id)
	{
		List<Emlopee> emlopList=emsDao.getEmlopBySta(s_id);
		System.out.println(s_id);
		if(emlopList==null)
		{
			return new ResponseEntity<List<Emlopee>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Emlopee>>(emlopList, HttpStatus.OK);
	}
	
	//找到新增但还没有为其建造站点的员工
	@RequestMapping(value="/api/EmpMatchSta/matchE",method=RequestMethod.GET)
	public ResponseEntity<List<Emlopee>> matchEAndS()
	{
		List<Emlopee> emlopList=emsDao.matchEAndS();
		if(emlopList==null)
		{
			return new ResponseEntity<List<Emlopee>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Emlopee>>(emlopList, HttpStatus.OK);
	}
	
	//找到虽有匹配站点但没有员工信息的记录，
	@RequestMapping(value="/api/EmpMatchSta/matchS",method=RequestMethod.GET)
	public ResponseEntity<List<EmpMatchSta>> matchSAndE()
	{
		List<EmpMatchSta> emsList=emsDao.matchSAndE();
		if(emsList==null)
		{
			return new ResponseEntity<List<EmpMatchSta>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<EmpMatchSta>>(emsList, HttpStatus.OK);
	}
	
	/*新增一个。*/
	@RequestMapping(value = "/api/EmpMatchSta", method = RequestMethod.POST)
	public ResponseEntity<EmpMatchSta> createStation(@RequestBody EmpMatchSta ems) { 
		System.out.println("创建站点 " + ems.getE_id());
		
		// 检查提交的数据是否完整，如果不完整，将状态码设置为 BAD_REQUEST
		if( 0 == ems.getE_id() || 0 == ems.getS_id()) {
			return new ResponseEntity<EmpMatchSta>(HttpStatus.BAD_REQUEST);
		}
		
		// 创建站点
		if(emsDao.creatEMS(ems)) {
			return new ResponseEntity<EmpMatchSta>(ems, HttpStatus.CREATED);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<EmpMatchSta>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/*修改部分*/
	/*@RequestMapping(value = "/api/EmpMatchSta/{e_id}", method = RequestMethod.PUT)
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
		if(emsDao.updateEMSByEid(e_id, emsNow)) {
			return new ResponseEntity<EmpMatchSta>(emsNow, HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<EmpMatchSta>(HttpStatus.INTERNAL_SERVER_ERROR);
	}*/
	
	/*删除部分*/
	/*根据站点的id删除掉一个站点*/
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
