package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Plan")
@XmlAccessorType(XmlAccessType.NONE)
public class Plan {

	@XmlElement(name = "id")
	private int id;
	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "startTime")
	private String startTime;
	@XmlElement(name = "endTime")
	private String endTime;
	@XmlElement(name = "userId")
	private int userId;
	@XmlElement(name = "userRsvp")
	private String userRsvp;
	@XmlElement(name = "docId")
	private int docId;
	@XmlElement(name = "docRsvp")
	private String docRsvp;
	@XmlElement(name = "centerPlanFlag")
	private String centerPlanFlag;
	@XmlElement(name = "centerId")
	private String centerId;
	@XmlElement(name = "planFile")
	private String planFile;

	public Plan() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserRsvp() {
		return userRsvp;
	}

	public void setUserRsvp(String userRsvp) {
		this.userRsvp = userRsvp;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getDocRsvp() {
		return docRsvp;
	}

	public void setDocRsvp(String docRsvp) {
		this.docRsvp = docRsvp;
	}

	public String getCenterPlanFlag() {
		return centerPlanFlag;
	}

	public void setCenterPlanFlag(String centerPlanFlag) {
		this.centerPlanFlag = centerPlanFlag;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getPlanFile() {
		return planFile;
	}

	public void setPlanFile(String planFile) {
		this.planFile = planFile;
	}

	
}
