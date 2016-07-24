package com.isoftstone.web.algorithm;

public class Node<T> {
	private T index;
	private int parent;
	private int d;
	private int man;
	private int hasChild;
	private double x;
	private double y;

	public void setRoot(T index, double x, double y) {
		this.index = index;
		this.parent = -1;
		this.d = 0;
		this.man = 0;
		this.hasChild = 0;
		this.x = x;
		this.y = y;
	}

	public void setPoint(T index, int parent, int d, int man, double x, double y) {
		this.index = index;
		this.parent = parent;
		this.d = d;
		this.man = man;
		this.hasChild = 0;
		this.x = x;
		this.y = y;
	}

	public int getHasChild() {
		return hasChild;
	}

	public void setHasChild(int hasChild) {
		this.hasChild = hasChild;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getMan() {
		return man;
	}

	public void setMan(int man) {
		this.man = man;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Node() {
	}

	public Node(T index) {
		this.index = index;
	}

	public Node(T index, int parent) {
		this.index = index;
		this.parent = parent;
	}
	

	public T getIndex() {
		return index;
	}

	public void setIndex(T index) {
		this.index = index;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public int getParent() {
		return this.parent;
	}
}
