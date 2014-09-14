package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Center")
@XmlAccessorType(XmlAccessType.NONE)
public class Center
{
	@XmlElement(name="id")
    private int id;
    @XmlElement(name="name")
    private String name;
    @XmlElement(name="adminPhone")
    private String adminPhone;
    @XmlElement(name="adminName")
    private String  adminName;
    @XmlElement(name="address")
    private String  address;
    @XmlElement(name="members")
    private List<String> members;
    @XmlElement(name="image")
	private byte[] image;

	public Center()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getMembers()
    {
        return members;
    }

    public void setMembers(List<String> members)
    {
        this.members = members;
    }
    
    public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getAdminPhone() {
		return adminPhone;
	}

	public void setAdminPhone(String adminPhone) {
		this.adminPhone = adminPhone;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}

