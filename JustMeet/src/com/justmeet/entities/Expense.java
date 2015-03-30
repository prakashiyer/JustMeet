package com.justmeet.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Expense")
@XmlAccessorType(XmlAccessType.NONE)
public class Expense {
	
	@XmlElement(name="id")
	private int id;
	@XmlElement(name="phone")
	private String phone;
	@XmlElement(name="planId")
	private String planId;
	@XmlElement(name="title")
	private String title;
	@XmlElement(name="value")
	private int value;
	
	public Expense(){
		
	}

	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	

}
