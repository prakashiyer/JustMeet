package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PlanList")
@XmlAccessorType(XmlAccessType.NONE)
public class PlanList
{

	@XmlElement(name="plans")
    private List<Plan> plans;
	
    public PlanList()
    {
    }

    public List<Plan> getPlans()
    {
        return plans;
    }

    public void setPlans(List<Plan> plans)
    {
        this.plans = plans;
    }
}

