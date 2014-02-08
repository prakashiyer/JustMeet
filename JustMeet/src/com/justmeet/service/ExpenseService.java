package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justmeet.dao.ExpenseDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Expense;
import com.justmeet.entities.ExpenseList;
import com.justmeet.entities.ExpenseReport;
import com.justmeet.entities.ExpenseRow;
import com.justmeet.entities.Plan;
import com.justmeet.entities.User;


public class ExpenseService {
	
	private static final Log log = LogFactory.getLog(ExpenseService.class);

	@Autowired
	private ExpenseDAO expenseDao;
	
	@Autowired
	private PlanDAO planDao;
	
	@Autowired
	private UserDAO userDao;

	public void add(String phone, String planName, String groupName,
			String title, String value) {
		log.info("Adding expense for " +phone);
		expenseDao.addExpense(phone, planName,
				groupName, title, Integer.valueOf(value));
		
	}

	public void update(String phone, String planName, String groupName,
			String title, String value) {
		expenseDao.updateExpense(phone, planName,
				groupName, title, Integer.valueOf(value));
		
	}

	public void delete(String phone, String planName, String groupName,
			String title) {
		expenseDao.deleteExpense(phone, planName,
				groupName, title);
	}

	public ExpenseList fetch(String phone, String planName, String groupName) {
		List<Expense> expenses = expenseDao.fetchExpense(phone,
				planName, groupName);
		ExpenseList expenseList = new ExpenseList();
		if (expenses != null && !expenses.isEmpty()) {
			expenseList.setExpenses(expenses);
		}
		
		return expenseList;
	}

	public ExpenseReport generateReport(String planName) {
		Plan plan = planDao.fetchPlanInformation(planName);
		ExpenseReport report = new ExpenseReport();
		List<ExpenseRow> expenseRows = new ArrayList<ExpenseRow>();
		if (plan != null) {
			List<String> members = plan.getMemberNames();
			String group = plan.getGroupName();

			if (members != null && !members.isEmpty()) {

				Map<String, Integer> expenseMap = new HashMap<String, Integer>();
				int totalExpense = 0;
				for (String member : members) {
					List<Expense> expenses = expenseDao.fetchExpense(
							member, planName, group);
					int memberExpense = 0;
					if (expenses != null && !expenses.isEmpty()) {
						for (Expense expense : expenses) {
							memberExpense = memberExpense + expense.getValue();
						}
					}
					totalExpense = totalExpense + memberExpense;
					expenseMap.put(member, memberExpense);
				}

				int contribution = totalExpense / members.size();
				for (Entry<String, Integer> entry : expenseMap.entrySet()) {
					ExpenseRow expenseRow = new ExpenseRow();
					String phone = entry.getKey();
					expenseRow.setPhone(phone);

					User user = userDao
							.fetchUser(phone);
					expenseRow.setName(user.getName());
					try {
						InputStream image = userDao
								.fetchUserImage(phone);
						if (image != null) {
							byte[] userImage = IOUtils.toByteArray(image);
							expenseRow.setUserImage(userImage);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					int value = entry.getValue() - contribution;

					expenseRow.setValue(value);
					expenseRows.add(expenseRow);
				}

			}

		}
		report.setExpenseRows(expenseRows);
		
		return report;
	}

}
