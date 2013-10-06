package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="UserInformation")
@XmlAccessorType(XmlAccessType.NONE)
public class User {
	
	@XmlElement(name="id")
	private int id;
	@XmlElement(name="name")
	private String name;
	@XmlElement(name="phone")
	private String phone;
	@XmlElement(name="groupNames")
	private List<String> groupNames;
	@XmlElement(name="pendingGroupNames")
	private List<String> pendingGroupNames;
	
	public User(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<String> getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(List<String> groupNames) {
		this.groupNames = groupNames;
	}

	public List<String> getPendingGroupNames() {
		return pendingGroupNames;
	}

	public void setPendingGroupNames(List<String> pendingGroupNames) {
		this.pendingGroupNames = pendingGroupNames;
	}
	
	

}
