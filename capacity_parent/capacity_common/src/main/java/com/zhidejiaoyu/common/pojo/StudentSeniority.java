package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

/**
 * 学生排行需要封装的数据
 * 
 * @author qizhentao
 * @version 1.0
 */
public class StudentSeniority implements Serializable {

	/** 学生id */
	private Long id;
	
	/** 学生姓名 */
	private String studentName;
	
	/** 省 */
	private String province;
	
	/** 市 */
	private String city;
	
	/** 区 */
	private String area;
	
	/** 金币(剩余+已用金币) */
	private int gold;
	
	/** 勋章数量 */
	private int zs;
	
	/** 膜拜数量 */
	private int mb;
	
	/** 等级 */
	private String childName;
	
	/** 勋章数量 */
	private int xz;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getZs() {
		return zs;
	}

	public void setZs(int zs) {
		this.zs = zs;
	}

	public int getMb() {
		return mb;
	}

	public void setMb(int mb) {
		this.mb = mb;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public int getXz() {
		return xz;
	}

	public void setXz(int xz) {
		this.xz = xz;
	}

	@Override
	public String toString() {
		return "StudentSeniority [id=" + id + ", studentName=" + studentName + ", province=" + province + ", city="
				+ city + ", area=" + area + ", gold=" + gold + ", zs=" + zs + ", mb=" + mb + ", childName=" + childName
				+ ", xz=" + xz + "]";
	}
	
}
