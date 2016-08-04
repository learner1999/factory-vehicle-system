package com.isoftstone.web.pojo;

public class ExcelResult {
	private String sname[];
	private int count[];
	
	public ExcelResult(int size)
	{
		sname=new String[size];
		count=new int[size];
	}
	public String[] getSname() {
		return sname;
	}
	public void setSname(String[] sname) {
		this.sname = sname;
	}
	public int[] getCount() {
		return count;
	}
	public void setCount(int[] count) {
		this.count = count;
	} 
}
