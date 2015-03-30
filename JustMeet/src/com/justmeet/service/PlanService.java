package com.justmeet.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Group;
import com.justmeet.entities.Plan;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;

public class PlanService {
	
private static final Log log = LogFactory.getLog(PlanService.class);
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private PlanDAO planDao;
	
	@Autowired
	private GroupDAO groupDao;

	public PlanList fetchUpcomingPlans(String phone) {
		User user = userDao
				.fetchUser(phone);
		PlanList planList = new PlanList();
		if (user != null) {
			log.info("Fetch Upcoming plans for user");
			List<Plan> plans = planDao.fetchUpcomingPlans(phone);
			if(plans != null && !plans.isEmpty()){
				planList.setPlans(plans);
			}		
		}
		return planList;

	}

	public PlanList fetchGroupPlans(String groupIndex) {
		List<String> groupIds = new ArrayList<String>();
		groupIds.add(groupIndex);
		log.info("Fetch Upcoming plans for group: "+groupIndex);
		List<Plan> plans = planDao.fetchUpcomingGroupPlans(groupIds);
		PlanList planList = new PlanList();
		planList.setPlans(plans);
		return planList;
	}

	public Plan fetchPlan(String planIndex) {
		Plan plan = planDao.fetchPlanInformation(planIndex);
		if (plan != null) {
			
			return plan;
		} else {
			return new Plan();
		}
	}

	public Plan rsvpPlan(String phone, String planIndex, String rsvp) {
		Plan plan = planDao.fetchPlanInformation(planIndex);
		if (plan != null) {
			List<String> members = plan.getMembersAttending();

			if (rsvp.equals("yes")) {
				members.add(phone);
			} else {
				members.remove(phone);
			}
			if (members.isEmpty()) {
				planDao.deletePlan(planIndex);
				return plan;
			} else {
				boolean success = planDao.updatePlanWithMember(
						planIndex, members);

				if (success) {
					plan = planDao.fetchPlanInformation(planIndex);					
					return plan;
				}
			}

		}
		
		return new Plan();
	}

	public boolean deletePlan(String planIndex) {
		return planDao.deletePlan(planIndex);
	}

	public PlanList fetchPlanHistory(String phone) {
		PlanList planList = new PlanList();
		List<Plan> plans = planDao.fetchPlanHistory(phone);
		if (plans != null) {
			planList.setPlans(plans);
		}
		return planList;
	}

	public Plan editPlan(String newPlanName, String oldPlanName, String planIndex,
			String planDate, String planTime, String planLocation,
			String groupName, String groupIndex, String planEndDate, String planEndTime) {
		boolean success = planDao.editPlan(oldPlanName, newPlanName, planIndex,
				planDate + " " + planTime, planLocation, planEndDate + " " + planEndTime);
		if (success) {
			return planDao.fetchPlanInformation(planIndex);
		}
		
		return new Plan();
	}

	public Plan newPlan(String planName, String phone, String planDate,
			String planTime, String planLocation, List<String> phones,
			List<String> groups, String creator, String endDate, String endTime) {
		List<String> members = new ArrayList<String>();
		members.add(phone);
		List<String> phoneList = new ArrayList<String>();
		phoneList.add(phone);
		if(phones != null && phones.size() > 0) {
			log.info("Adding phones" +phones.size());
			phoneList.addAll(phones);
		}
		
		if(groups != null && !groups.isEmpty()){
			 for(String groupIndex: groups){
		        	log.info("Fetching Group" +groupIndex);
					Group group = groupDao.fetchGroup(groupIndex);
					if(group.getMembers() != null && group.getMembers().size() > 0) {
						log.info("Adding members" +group.getMembers().size());
						phoneList.addAll(group.getMembers());
					}
				}
		}

		int index = planDao.newPlan(planName, phoneList, groups, planDate
				+ " " + planTime, planLocation, members, creator, endDate + " " + endTime);
		if (index > 0) {
			Plan plan = planDao.fetchPlanInformation(String.valueOf(index));
			return plan;	
		}
		
		return new Plan();
	}

	public PlanList fetchGroupPlanHistory(String groupId) {
		PlanList planList = new PlanList();
		List<Plan> plans = planDao.fetchPlanHistoryForGroup(groupId);
		if (plans != null) {
			planList.setPlans(plans);
		}
		return planList;
	}

}
