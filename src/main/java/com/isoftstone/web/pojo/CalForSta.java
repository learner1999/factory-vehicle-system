package com.isoftstone.web.pojo;

import java.util.List;

public class CalForSta {
	private List<EmpMatchSta> eid;
	private Station sid;
	
	public List<EmpMatchSta> getEid() {
		return eid;
	}
	public void setEid(List<EmpMatchSta> eid) {
		this.eid = eid;
	}
	public Station getSid() {
		return sid;
	}
	public void setSid(Station sid) {
		this.sid = sid;
	}
}

