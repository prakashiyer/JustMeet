package com.justmeet.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ExpenseReport")
@XmlAccessorType(XmlAccessType.NONE)
public class ExpenseReport {

	@XmlElement(name="expenseRows")
	private List<ExpenseRow> expenseRows;
	
	public ExpenseReport(){
		
	}

	public List<ExpenseRow> getExpenseRows() {
		return expenseRows;
	}

	public void setExpenseRows(List<ExpenseRow> expenseRows) {
		this.expenseRows = expenseRows;
	}
	
	
}
