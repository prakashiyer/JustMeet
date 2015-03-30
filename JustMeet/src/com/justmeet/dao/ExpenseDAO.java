package com.justmeet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.justmeet.entities.Expense;

public class ExpenseDAO {

	private static final Log log = LogFactory.getLog(ExpenseDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int addExpense(final String phone, final String planIndex, final String title,
			final int value) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				String insertQuery = "INSERT INTO theiyers_whatsThePlan.expenses (phone, plan_id, title, value) VALUES (?, ?, ?, ?)";
                
				
				@Override
				public PreparedStatement createPreparedStatement(Connection arg0)
						throws SQLException {
					
					// TODO Auto-generated method stub
					PreparedStatement ps = arg0.prepareStatement(insertQuery, new String[] {"id"});
					ps.setString(1,phone);
					ps.setString(2,planIndex);
					ps.setString(3,title);
					ps.setInt(4,value);
					
					return ps;
				}
			    }, keyHolder);
			return keyHolder.getKey().intValue();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Expense Addition failed: "+phone);
			return 0;

		}
	}
	
	public boolean updateExpense(String id, String title, int value) {
		String insertQuery = "UPDATE theiyers_whatsThePlan.expenses SET value=?,title=? where id = ?";
		
		try {
			jdbcTemplate.update(insertQuery,value, title, id);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}

	}
	
	public List<Expense> fetchExpense(String phone, String planIndex) {
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.expenses where phone = ? and plan_id = ?";
		
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Expense>() {
				        
						public Expense mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							
							if (rs != null) {
								Expense expense = new Expense();
								expense.setId(rs.getInt(1));
								expense.setPhone(rs.getString(2));
								expense.setTitle(rs.getString(3));
								expense.setValue(rs.getInt(4));
								expense.setPlanId(rs.getString(5));
								return expense;
							}
							return null;
						}
					}, phone, planIndex);

		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}
	
	public boolean deleteExpense(String id) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.expenses WHERE id = ?";

		try {
			jdbcTemplate.update(deleteQuery, id);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public Expense fetchExpense(String id) {
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.expenses where id = ?";
		
		try {
			return jdbcTemplate.queryForObject(findQuery,
					new ParameterizedRowMapper<Expense>() {
				        
						public Expense mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							
							if (rs != null) {
								Expense expense = new Expense();
								expense.setId(rs.getInt(1));
								expense.setPhone(rs.getString(2));
								expense.setTitle(rs.getString(3));
								expense.setValue(rs.getInt(4));
								expense.setPlanId(rs.getString(5));
								return expense;
							}
							return null;
						}
					}, id);

		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

}
