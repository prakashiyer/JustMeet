package com.justmeet.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ExpenseRow")
@XmlAccessorType(XmlAccessType.NONE)
public class ExpenseRow {

	@XmlElement(name="userImage")
	private byte[] userImage;
	@XmlElement(name="name")
	private String name;
	@XmlElement(name="emailId")
	private String emailId;
	@XmlElement(name="value")
	private int value;
	
	public ExpenseRow(){
		
	}

	public byte[] getUserImage() {
		return userImage;
	}

	public void setUserImage(byte[] userImage) {
		this.userImage = userImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
