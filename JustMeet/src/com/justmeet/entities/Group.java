package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Group")
@XmlAccessorType(XmlAccessType.NONE)
public class Group
{
	@XmlElement(name="id")
    private int id;
    @XmlElement(name="name")
    private String name;
    @XmlElement(name="members")
    private List<String> members;
    @XmlElement(name="planNames")
    private List<String>  planNames;
    @XmlElement(name="planIds")
    private List<String>  planIds;
    @XmlElement(name="pendingMembers")
    private List<String> pendingMembers;
    @XmlElement(name="admin")
    private String admin;
    @XmlElement(name="image")
	private byte[] image;

	public Group()
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

    public List<String> getPlanNames()
    {
        return planNames;
    }

    public void setPlanNames(List<String> planNames)
    {
        this.planNames = planNames;
    }
    
    public List<String> getPlanIds()
    {
        return planIds;
    }

    public void setPlanIds(List<String> planIds)
    {
        this.planIds = planIds;
    }

    public List<String> getPendingMembers()
    {
        return pendingMembers;
    }

    public void setPendingMembers(List<String> pendingMembers)
    {
        this.pendingMembers = pendingMembers;
    }

    public String getAdmin()
    {
        return admin;
    }

    public void setAdmin(String admin)
    {
        this.admin = admin;
    }
    
    public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
}

