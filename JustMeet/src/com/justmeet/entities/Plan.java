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
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "groupName")
	private String groupName;
	@XmlElement(name = "startTime")
	private String startTime;
	@XmlElement(name = "endTime")
	private String endTime;
	@XmlElement(name = "location")
	private String location;
	@XmlElement(name = "memberNames")
	private List<String> memberNames;
	@XmlElement(name = "membersInvited")
	private List<String> membersInvited;
	@XmlElement(name = "groupsInvited")
	private List<String> groupsInvited;
	@XmlElement(name = "creator")
	private String creator;

	public Plan() {
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getMemberNames() {
		return memberNames;
	}

	public void setMemberNames(List<String> memberNames) {
		this.memberNames = memberNames;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public List<String> getMembersInvited() {
		return membersInvited;
	}

	public void setMembersInvited(List<String> membersInvited) {
		this.membersInvited = membersInvited;
	}

	public List<String> getGroupsInvited() {
		return groupsInvited;
	}

	public void setGroupsInvited(List<String> groupsInvited) {
		this.groupsInvited = groupsInvited;
	}
}
