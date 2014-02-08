package com.justmeet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.justmeet.entities.Expense;

public class ExpenseDAO {

	private static final Log log = LogFactory.getLog(ExpenseDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public boolean addExpense(String phone, String planName, String groupName, String title,
			int value) {
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.expenses (phone, plan, group_name, title, value) VALUES (?, ?, ?, ?, ?)";
		
		try {
			jdbcTemplate.update(insertQuery, phone, planName, groupName, title, value);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}

	}
	
	public boolean updateExpense(String phone, String planName, String groupName, String title,
			int value) {
		String insertQuery = "UPDATE theiyers_whatsThePlan.expenses SET value=? where phone = ? and plan=? and group_name=? and title=?";
		
		try {
			jdbcTemplate.update(insertQuery,value, phone, planName, groupName, title);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}

	}
	
	public List<Expense> fetchExpense(String phone, String planName, String groupName) {
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.expenses where phone = ? and plan=? and group_name=?";
		
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Expense>() {
				        
						public Expense mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							
							if (rs != null) {
								Expense expense = new Expense();
								expense.setId(rs.getInt(1));
								expense.setPhone(rs.getString(2));
								expense.setPlan(rs.getString(3));
								expense.setGroup(rs.getString(4));
								expense.setTitle(rs.getString(5));
								expense.setValue(rs.getInt(6));
								
								return expense;
							}
							return null;
						}
					}, phone, planName, groupName);

		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}
	
	public boolean deleteExpense(String phone, String planName, String groupName, String title) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.expenses WHERE phone = ? and plan=? and group_name=? and title=?";

		try {
			jdbcTemplate.update(deleteQuery, phone, planName, groupName, title);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

}
