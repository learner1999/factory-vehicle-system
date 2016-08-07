package com.isoftstone.web.algorithm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.util.JdbcUtil;

public class Cal {
	
	private List<Car> carList;
	private List<Emlop> emlopList;
	private MyTree<Integer> tree;
	private List<Route> route;
	private List<Plan> planlist;
	
	private int max;
	private int n[];
	private double distence[][];
	
	private int manMax;
	private int Manmin;
	private int mCount;
	//从这里是数据库有关的操作，可以另外放一个类
	static {
		try {
			System.out.println("成功加载access驱动程序");
			Class.forName("com.mysql.jdbc.Driver");
		}

		catch (ClassNotFoundException e) {
			System.out.println("在加载数据库驱动程序时抛出异常，内容如下：");
			e.printStackTrace();
		}
	}
	
	/*这个可以放在站点类里面
	 * 传入参数是公司的x和y
	 * 返回的是emlop类，具体的参见Emlop类*/
	public List<Emlop> isStationUsed(double x,double y) {
		mCount=0;
		List<Emlop> emlopList = new ArrayList<>();
		Emlop e1=new Emlop();
		e1.setSid(0);
		e1.setScount(0);
		e1.setX(x);
		e1.setY(y);
		emlopList.add(e1);
		try {
			
			Connection conn=JdbcUtil.getConnection();
			String sql="select x.s_id,count(x.e_id),y.longitude,y.latitude "
					+ "from employee_station_copy as x,station_information_copy as y "
					+ "where x.s_id=y.s_id and y.s_is_used=1 group by x.s_id "
					+ "order by (y.longitude-"+x+")*(y.longitude-"+x+")"
							+ "+(y.latitude-"+y+")*(y.latitude-"+y+")";
			PreparedStatement p=conn.prepareStatement(sql);
			ResultSet res=p.executeQuery();
			while(res.next())
			{
				Emlop e=new Emlop();
				e.setSid(res.getInt(1));
				e.setScount(res.getInt(2));
				mCount+=res.getInt(2);
				e.setX(res.getDouble(3));
				e.setY(res.getDouble(4));
				emlopList.add(e);
				//System.out.println(e.getSid()+"  "+e.getScount()+"  "+e.getX()+"  "+e.getY());
			}
			res.close();
			p.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("找不到access驱动程序");
		}
		//System.out.println(emlopList.size());
		return emlopList;
	}
	
	/*这个可以放在车类里面
	 * 返回的是车的list*/
	public List<Car> findCar() {
		List<Car> lcar=new ArrayList<Car>();
		try {
			
			Connection conn=JdbcUtil.getConnection();
			String sql="select * from car_information order by c_seat";
			PreparedStatement p=conn.prepareStatement(sql);
			ResultSet res=p.executeQuery();
			while(res.next())
			{
				Car c=new Car();
				c.setC_id(res.getInt("c_id"));
				c.setC_brand(res.getString("c_brand"));
				c.setC_seat(res.getInt("c_seat"));
				lcar.add(c);
				//System.out.println(c.getC_id()+" "+c.getC_seat());
			}
			res.close();
			p.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("找不到access驱动程序");
		}
		if(lcar.size()>0)
		{
			Manmin=lcar.get(0).getC_seat();
			manMax=lcar.get(lcar.size()-1).getC_seat();
		}
		return lcar;
	}
	
	
	/*初始化路线树，是核心来的
	 * 可以放在Mytree类里面
	 * 只有第三个参数需要手动，其他的都可以默认。是深度上限（不包括）
	 */
	public void createTree(int parent, int grandpa, int max, int index,int maxroute) {
		if (grandpa == -1) {
			for (int i = 0; i < maxroute; i++) {//这里有一个参数的
				Emlop e=(Emlop)emlopList.get(i);
				tree.add(i, parent, e.getScount(), e.getX(), e.getY());
				createTree(tree.getSize() - 1, parent, max, i,maxroute);
			}
		} else {
			if (tree.getDeep(parent) >= max) {
				tree.end(parent);
				return;
			}
			for (int i = index + 1; i < emlopList.size(); i++) {
				Emlop e=(Emlop)emlopList.get(i);
				int gpid=tree.getData(grandpa);
				Emlop e2=(Emlop)emlopList.get(index);
				Emlop e3=(Emlop)emlopList.get(gpid);
				if (isDun(e3.getX(),e3.getY(),e2.getX(),e2.getY(),e.getX(),e.getY())&&distence[index][gpid]<=26.5) 
				{
					tree.add(i, parent, e.getScount(), e.getX(), e.getY());
					createTree(tree.getSize() - 1, parent, max, i,maxroute);
				}
			}
		}
	}
	
	
	//需要的函数，判断是不是钝角
	public boolean isDun(double ax,double ay,double bx,double by,double cx,double cy)
	{
		double oneX=bx-ax;
		double oneY=by-ay;
		double twoX=bx-cx;
		double twoY=by-cy;
		double ce=(oneX*twoX+oneY*twoY);
		if(ce<0)
		{
			return true;
		}
		return false;
	}
	
	//初始化，包括距离的
	public void setAll(List<Emlop> emlopList)
	{
		max=emlopList.size();
		n=new int[max-1];
		route=new ArrayList<Route>();
		planlist=new ArrayList<Plan>();
		distence=new double[max][max];
		for (int i = 0; i < max - 1; i++) {
			Emlop ex=emlopList.get(i);
			distence[i][i] = 0; // 对角线为0
			for (int j = i + 1; j < max; j++) {
				Emlop ey=emlopList.get(j);
				distence[i][j] = Math
						.sqrt(((ex.getX() - ey.getX()) * (ex.getX() - ey.getX()) + (ex.getY()-ey.getY())
								* (ex.getY()-ey.getY()))* 100000);
				distence[j][i] = distence[i][j];
				//System.out.print(distence[i][j]+"  ");
			}
			//System.out.println("\n");
		}
		distence[max - 1][max - 1] = 0;
	} 
	
	//规划路线重点。
	public void find(int maxdeep,List<Route> r,int maxStation,int chang)
	{
		if(r.size()==0)
		{
			return;
		}
		int near[] = r.get(0).getStation();
		int nearshu=near[0];
		int result=0;
		for (int ji = 0; ji < r.size(); ji++) {
			int jia[] = r.get(ji).getStation();
			if(jia[0]!=nearshu)
			{
				break;
			}
			int a[]=r.get(ji).getStation();
			int ce[]=new int[n.length];
			int all;
			int deepall;
			//System.out.println(n.length);
			for(int i=0;i<a.length;i++)
			{
				//System.out.print(a[i]+"  ");
				int t=a[i]-1;
				ce[t]=1;
			}
			//System.out.print("\n");
			all=a.length;
			deepall=a.length;
			int plan[]=new int[maxdeep];
			plan[0]=ji;
			for(int ja=1;ja<plan.length;ja++)
			{
				if(plan[ja]==0)
				{
					int max=0;
					int maxlength=ce.length;
					for(int i=0;i<r.size();i++)
					{
						int count=0;
						int x[]=r.get(i).getStation();
						for(int j=0;j<x.length;j++)
						{
							int t=x[j]-1;
							if(ce[t]!=1)
							{
								count++;
							}
						}
						if(count>max)
						{
							max=count;
							maxlength=x.length;
							plan[ja]=i;
						}else if(count==max&&x.length<maxlength)
						{
							maxlength=x.length;
							plan[ja]=i;
						}
					}
					int b[]=r.get(plan[ja]).getStation();
					deepall+=r.get(plan[ja]).getDeep();
					for(int i=0;i<b.length;i++)
					{
						//System.out.print(b[i]+"  ");
						int t=b[i]-1;
						if(ce[t]!=1)
						{
							ce[t]=1;
							all++;
						}
					}
					//System.out.print("\n");
				}
				if(all==n.length)
				{
					//System.out.println("hsjdakldjaskkl");
					break;
				}
			}
			
			if(all==n.length&&deepall<=maxStation)
			{
				int findx[]=new int[n.length];
				int findy[]=new int[n.length];
				int findce[]=new int[n.length];
				int findshu[][]=new int[maxdeep][chang+1];
				result++;
				for(int i=0;i<plan.length;i++)
				{
					if(i>0&&plan[i]==0)
					{
						break;
					}
					Route res=r.get(plan[i]);
					int ra[]=res.getStation();
					for(int rce=0;rce<ra.length;rce++)
					{
						findshu[i][rce]=ra[rce];
						findshu[i][chang]=res.getDeep();
					}
				}
				
				for(int i=0;i<maxdeep;i++)
				{
					for(int rce=0;rce<chang;rce++)
					{
						if(findshu[i][rce]==0)
						{
							break;
						}
						int ceindex=findshu[i][rce]-1;
						if(findce[ceindex]==0)
						{
							findce[ceindex]=1;
							findx[ceindex]=i;
							findy[ceindex]=rce;
						}else
						{
							if(findshu[i][chang]>findshu[findx[ceindex]][chang])
							{
								findshu[i][rce]=0;
								findshu[i][chang]--;
							}else
							{
								findshu[findx[ceindex]][findy[ceindex]]=0;
								findshu[findx[ceindex]][chang]--;
								findx[ceindex]=i;
								findy[ceindex]=rce;
							}
						}
					}
				}
				
				Plan p=new Plan();
				List<int []> planRoute=new ArrayList<int[]>();
				int planman[]=new int[maxdeep];
				int plandeep[]=new int[maxdeep];
				double plandis[]=new double[maxdeep];
				int plancar[]=new int[maxdeep];
				double planlv[]=new double[maxdeep];
				for(int i=0;i<maxdeep;i++)
				{
					int alin[]=new int[findshu[i][chang]];
					int manshu=0;
					int sum=0;
					for(int rce=0,linrce=0;rce<chang;rce++)
					{
						if(findshu[i][rce]!=0)
						{
							Emlop e=emlopList.get(findshu[i][rce]);
							alin[linrce]=e.getSid();
							manshu+=e.getScount();
							linrce++;
							Emlop e1=emlopList.get(i);
							Emlop e2=emlopList.get(rce);
							sum+=distence[i][rce];
						}
					}
					planman[i]=manshu;
					plandeep[i]=findshu[i][chang];
					plandis[i]=sum;
					planRoute.add(alin);
					for(int rce=0;rce<carList.size()-1;rce++)
					{
						int s=0;
						Car c2=carList.get(rce);
						Car c1=carList.get(rce);
						int lrce=0;
						for(lrce=rce+1;lrce<carList.size();lrce++)
						{
							
							c1=carList.get(lrce);
							if(c1.getIsused()==0)
							{
								s=c1.getC_seat();
								break;
							}
						}
						if(Math.abs(c2.getC_seat()-planman[i])<Math.abs(s-planman[i]))
						{
							
								for(int jix=lrce;jix<carList.size();jix++)
								{
									Car c3=carList.get(jix);
									if(c3.getIsused()==0)
									{
										c3.setIsused(1);
										plancar[i]=c3.getC_id();
										planlv[i]=(double)planman[i]/(double)c3.getC_seat();
										break;
									}
								}
							
							break;
						}
					}
				}
				setCar();
				p.setRoute(planRoute);
				p.setDeep(plandeep);
				p.setMan(planman);
				p.setDistence(plandis);
				p.setCar(plancar);
				p.setLv(planlv);
				planlist.add(p);
			}
		}
		//System.out.print("共规划出"+result+"\n\n");
	}
	
	public void setCar()
	{
		for(int i=0;i<carList.size();i++)
		{
			Car c=carList.get(i);
			c.setIsused(0);
		}
	}
	
	//查看所有的计划，保留
	public void showallPlan()
	{
		System.out.print("共规划出"+planlist.size()+"\n\n");
		for(int i=0;i<planlist.size();i++)
		{
			Plan p=planlist.get(i);
			List<int []> r=p.getRoute();
			int planman[]=p.getMan();
			int plandeep[]=p.getDeep();
			double plandis[]=p.getDistence();
			int plancar[]=p.getCar();
			double planlv[]=p.getLv();
			for(int j=0;j<r.size();j++)
			{
				int a[]=r.get(j);
				if(a.length>0)
				{
					for(int k=0;k<a.length;k++)
					{
						System.out.print(a[k]+"  ");
					}
					
					System.out.println("\t\t\t人数"+planman[j]+"  经过站点  "+plandeep[j]+
							"  总距离  "+plandis[j]+"  车id  "+plancar[j]+"  乘坐率  "+planlv[j]);
				}
			}
			p.setJun(getAveraged(plandis));
			p.setCha(getStandardDevition(plandis));
			p.setChengjun(getAveraged(planlv));
			System.out.print("\t路程平均  "+p.getJun()+"  "+"  路程方差  "+p.getCha()+
						"  乘坐率平均"+p.getChengjun()+"  \n\n");
		}
	}
	
	//double的，比较傻缺
	public double getAveraged(double array[]){
        double sum = 0;
        int num=0;
        for(int i = 0;i < array.length;i++){
        	if(array[i]-0<=0.000000001)
        	{
        		break;
        	}
            sum += array[i];
            num++;
        }
        String sx=String.format("%.2f", (double)(sum / num));
		double y=Double.valueOf(sx);
        return y;
    }
	
	//�@取��什�
    public double getStandardDevition(double array[]){
        double sum = 0;
        int num=0;
        double a=getAveraged(array);
        for(int i = 0;i < array.length;i++){
        	if(array[i]==0)
        	{
        		break;
        	}
            sum += Math.sqrt(((double)array[i] -a) * (array[i] -a));
            num++;
        }
        String sx=String.format("%.2f", (sum / (num)));
		double y=Double.valueOf(sx);
        return y;
    }
    
    /**
     * 对路线规划的结果做一个简单的排序
     * 排第一的是平均路线最短
     * 排第二的是路线方差最小
     * 排第三的是乘坐率最高
     * 剩余的随意排序
     */
    public void sortPlan() {
    	int temp = 0;
    	Plan planTemp;
    	int len = planlist.size();
    	
    	// 找出平均路程最短的方案，并且放到首位
    	for (int i = 1; i < len; i++) {
    		if (planlist.get(i).getJun() < planlist.get(temp).getJun()) {
    			temp = i;
    		}
    	}
    	planTemp = planlist.get(temp);
    	planlist.set(temp, planlist.get(0));
    	planlist.set(0, planTemp);
    	
    	
    	// 找出方差最小的方案，并且放到第二位
    	temp = 1;
    	for (int i = 2; i < len; i++) {
    		if (planlist.get(i).getCha() < planlist.get(temp).getCha()) {
    			temp = i;
    		}
    	}
    	planTemp = planlist.get(temp);
    	planlist.set(temp, planlist.get(1));
    	planlist.set(1, planTemp);
    	
    	// 找出平均乘坐率最高的，并且放到第三位
    	temp = 2;
    	for (int i = 3; i < len; i++) {
    		if (planlist.get(i).getChengjun() > planlist.get(temp).getChengjun()) {
    			temp = i;
    		}
    	}
    	planTemp = planlist.get(temp);
    	planlist.set(temp, planlist.get(2));
    	planlist.set(2, planTemp);
    }
    
    public List<Plan> calplan(double x,double y)
    {
    	//int deepmax=7,deepmin=5,carmin=45,carmax=50,maxroute=11,overmax=53;
    	//deepmax是carmax/6（评估均）+3，deepmin是carmax/6（评估均）-1
    	int deepmax=11,deepmin=7,carmin=40,carmax=50,maxroute=9,overmax=60;
    	emlopList=isStationUsed(x,y);
		setAll(emlopList);
		carList=findCar();
		int jun=mCount/emlopList.size()+1;
		deepmax=manMax/jun;
		deepmin=manMax/jun-2;
		carmax=manMax-manMax%10;
		carmin=deepmin*jun-deepmin*jun%10;
		overmax=(int)((double)(emlopList.size())*1.2);
		maxroute=overmax/deepmin+1;
		//System.out.println(emlopList.size()+"  "+deepmax+"  "+deepmin+"  "+carmin+"  "+carmax+"  "+maxroute+"  "+overmax);
		//c.ceshi();
		tree = new MyTree<Integer>(0, x,y);
		createTree(0, -1, deepmax, -1,maxroute);//只有第三个参数需要手动，是深度上限（不包括）
		route=tree.ceshi(deepmin,carmin,carmax,route);//参数都需要，分别是深度下限（包括），人数下限，人数上限
		/*(for(int i=0;i<route.size();i++)
		{
			Route r=route.get(i);
			int station[]=r.getStation();
			
				for(int j=0;j<station.length;j++)
			{
				System.out.print(station[j]+"  ");
			}
				int man=r.getMan();
			int deep=r.getDeep();
			System.out.println("\t\t"+man+"  "+deep+"\n");
			
			
		}*/
		//System.out.println(route.size());
		find(maxroute, route,overmax,deepmax);
		
		// 计算平均路程、路程方差、平均乘坐率
		for (Plan p : planlist) {
			double plandis[]=p.getDistence();
			double planlv[]=p.getLv();
			p.setJun(getAveraged(plandis));
			p.setCha(getStandardDevition(plandis));
			p.setChengjun(getAveraged(planlv));
		}
		
		// 对规划的结果做简单的排序
		sortPlan();
		//showallPlan();         
		return planlist;
    }

	public static void main(String[] args) {
		double x=120.1541,y=30.2778;
		Cal c=new Cal();
		c.calplan(x, y);
		c.showallPlan();
		//System.out.println(c.isDun(0, 1, 0, 0, 1, -1));
	}
	//如果称作率为0就不行
}
