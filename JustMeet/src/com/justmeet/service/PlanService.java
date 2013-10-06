package com.justmeet.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Plan;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;

public class PlanService {
	
private static final Log log = LogFactory.getLog(PlanService.class);
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private PlanDAO planDao;

	public PlanList fetchUpcomingPlans(String phone) {
		User user = userDao
				.fetchUser(phone);
		if (user != null) {
			List<String> groups = user.getGroupNames();
			if (!groups.isEmpty()) {
				log.info("Fetch Upcoming plans for groups");
				List<Plan> plans = planDao.fetchUpcomingPlans(groups);
				PlanList planList = new PlanList();
				planList.setPlans(plans);
				
				return planList;
			}
		}
		
		return new PlanList();

	}
}
