package com.isoftstone.web.dao;

import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.*;

//这里也用了距离计算
public class CalForStaDao {
	private StationDao stadao=new StationDao();
	private EmpMStaDao emsDao=new EmpMStaDao();
	
	//专门新建的，还要修改的设一个
	public List<CalForSta> cal()
	{
		List<EmpMatchSta> emsList=emsDao.getAllnew();
		List<CalForSta> calList=new ArrayList<CalForSta>();
		for(int i=0;i<emsList.size();i++)	 
		{
			EmpMatchSta ems=emsList.get(i);
			if(ems.getUsed()==0)
			{
				double x=0,y=0;
				ems.setUsed(1);
				CalForSta cal=new CalForSta();
				List<EmpMatchSta> eid=new ArrayList<EmpMatchSta>();
				eid.add(ems);
				x+=ems.getE_x();
				y+=ems.getE_y();
				for(int j=i+1;j<emsList.size();j++)
				{
					EmpMatchSta ems2=emsList.get(j);
					if(ems2.getE_y()-ems.getE_y()>0.01)
					{
						break;
					}
					if(caldis(ems.getE_x(),ems.getE_y(),ems2.getE_x(),ems2.getE_y())<=0.01)
					{
						eid.add(ems2);
						x+=ems2.getE_x();
						y+=ems2.getE_y();
						ems2.setUsed(1);
					}
				}
				x/=eid.size();
				y/=eid.size();
				Station sta=stadao.findNearPoint(x, y);
				cal.setEid(eid);
				cal.setSid(sta);
				calList.add(cal);
			}
		}
		show(calList);
		return calList;
	}
	
	public int addToDb(List<CalForSta> calList)
	{
		for(int i=0;i<calList.size();i++)
		{
			CalForSta cal=calList.get(i);
			Station sta=cal.getSid();
			//System.out.print("\n"+sta.getS_id()+"\n");
			if(sta.getS_is_used()==0)
			{
				stadao.changePoToSta(sta.getS_id());
			}
			List<EmpMatchSta> eid=cal.getEid();
			for(int j=0;j<eid.size();j++)
			{
				EmpMatchSta ems=eid.get(j);
				if(ems.getS_id()!=sta.getS_id())
				{
					emsDao.updateEMSByEid(ems.getE_id(), sta.getS_id());
				}
				//System.out.print(ems.getE_id()+"  ");
			}
		}
		List<Station> staList=stadao.isStationZero();
		for(int i=0;i<staList.size();i++)
		{
			Station sta=staList.get(i);
			stadao.changeStaToPo(sta.getS_id());
		}
		return calList.size();
	}
	
	public void show(List<CalForSta> calList)
	{
		for(int i=0;i<calList.size();i++)
		{
			CalForSta cal=calList.get(i);
			List<EmpMatchSta> eid=cal.getEid();
			for(int j=0;j<eid.size();j++)
			{
				EmpMatchSta ems=eid.get(j);
				
				System.out.print(ems.getE_id()+"  ");
			}
			Station sta=cal.getSid();
			System.out.print("\n"+sta.getS_id()+"\n\n");
		}
		System.out.print("\n"+calList.size()+"\n\n");
	}
	
	public double caldis(double x1,double y1,double x2,double y2)
	{
		double distence=Math
				.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		return distence;
	}
}
