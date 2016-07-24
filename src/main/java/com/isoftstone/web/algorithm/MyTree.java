package com.isoftstone.web.algorithm;

import java.util.*;

public class MyTree<T> { 
    private int count;  //目前的规模
    List<Node<T>> nodes;
  
    public MyTree() {    
        this.nodes = new ArrayList<Node<T>>();  //初始化时先申请一个node数组
        this.count = 0;  //当前没有元素，所以是0
    }  
  
    //不指定大小
    public MyTree(T data,double x,double y) {  
        this();  
        Node<T> n=new Node<T>();
        n.setRoot(data, x, y);
        this.count = 1;  //已经有一个根节点
        this.nodes.add(n);
    }  
     
    
    //添加一个节点，并指明父节点  
    public void add(T data,int p,int nowMan,double x,double y) {
    	Node<T> n=new Node<T>();
    	int d=(((Node<T>)nodes.get(p)).getD())+1;
    	int man=(((Node<T>)nodes.get(p)).getMan())+nowMan;
    	((Node<T>)nodes.get(p)).setHasChild(1);
        n.setPoint(data, p, d, man, x, y);
        this.nodes.add(n);
        this.count++;
    } 
    
    //获取节点深度
    int getDeep(int index)
    {
    	return ((Node<T>)nodes.get(index)).getD();
    }
    
    T getData(int index)
    {
    	return ((Node<T>)nodes.get(index)).getIndex();
    }
      
    //获取整棵树有多少节点  
    public int getSize(){  
        return this.count;  
    }  
    
    public void end(int index)
    {
    	((Node<T>)nodes.get(index)).setHasChild(1);
    }
      
    //获取根节点  
    public Node<T> getRoot(){  
        return (Node<T>)nodes.get(0);  
    }    
    
    public List<Route> ceshi(int xia,int min,int max,List<Route> route)
    { 	
    	int k=0;
    	for(int i=0;i<count;i++)
    	{
    		Node<T> n=(Node<T>)nodes.get(i);
    		Route r=new Route();
    		if(n.getHasChild()==0&&n.getD()>=xia&&n.getMan()<=max&&n.getMan()>=min)
    		{
    			r.setMan(n.getMan());
    			r.setDeep(n.getD());
    			int a[]=new int[n.getD()];
	    		k++;
	    		int p=i;
	    		int c=n.getD()-1;
	    		while(p!=-1){
	    			n=(Node<T>)nodes.get(p);
	    			if(c>=0)
	    			{
	    				a[c]=(int)n.getIndex();
	    				c--;
	    			}
	    			p=n.getParent();
	    		}
	    		r.setStation(a);
	    		route.add(r);
    		}
    	}
    	return route;
    }
}
