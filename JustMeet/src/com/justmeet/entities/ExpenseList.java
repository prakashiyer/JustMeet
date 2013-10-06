package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ExpenseList")
@XmlAccessorType(XmlAccessType.NONE)
public class ExpenseList {

	@XmlElement(name="expenses")
	private List<Expense> expenses;
	
	public ExpenseList(){
		
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
	}
	
	
}
