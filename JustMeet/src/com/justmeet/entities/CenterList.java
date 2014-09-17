package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="CenterList")
@XmlAccessorType(XmlAccessType.NONE)
public class CenterList
{
	@XmlElement(name="centers")
	private List<Center> centers;
	
	public CenterList() {
	}
	
	public List<Center> getCenters() {
		return centers;
	}
	
	public void setCenters(List<Center> centers) {
		this.centers = centers;
	}
}

