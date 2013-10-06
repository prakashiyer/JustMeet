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
    @XmlElement(name="memberEmailIds")
    private List<String> memberEmailIds;
    @XmlElement(name="planNames")
    private List<String>  planNames;
    @XmlElement(name="pendingMembers")
    private List<String> pendingMembers;
    @XmlElement(name="admin")
    private String admin;

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

    public List<String> getMemberEmailIds()
    {
        return memberEmailIds;
    }

    public void setMemberEmailIds(List<String> memberEmailIds)
    {
        this.memberEmailIds = memberEmailIds;
    }

    public List<String> getPlanNames()
    {
        return planNames;
    }

    public void setPlanNames(List<String> planNames)
    {
        this.planNames = planNames;
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
}

