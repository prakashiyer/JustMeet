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
	@XmlElement(name="bloodGroup")
	private String bloodGroup;
	@XmlElement(name="dob")
	private String dob;
	@XmlElement(name="sex")
	private String sex;
	@XmlElement(name="address")
	private String address;
	@XmlElement(name="doctorFlag")
	private String doctorFlag;
	@XmlElement(name="primaryCenterId")
	private String primaryCenterId;
	@XmlElement(name="primaryDoctorId")
	private String primaryDoctorId;
	@XmlElement(name="image")
	private byte[] image;
	@XmlElement(name="centers")
	private List<String> centers;

 
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
	
	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDoctorFlag() {
		return doctorFlag;
	}

	public void setDoctorFlag(String doctorFlag) {
		this.doctorFlag = doctorFlag;
	}

	public String getPrimaryCenterId() {
		return primaryCenterId;
	}

	public void setPrimaryCenterId(String primaryCenterId) {
		this.primaryCenterId = primaryCenterId;
	}

	public String getPrimaryDoctorId() {
		return primaryDoctorId;
	}

	public void setPrimaryDoctorId(String primaryDoctorId) {
		this.primaryDoctorId = primaryDoctorId;
	}

	public List<String> getCenters() {
		return centers;
	}

	public void setCenters(List<String> centers) {
		this.centers = centers;
	}
}
