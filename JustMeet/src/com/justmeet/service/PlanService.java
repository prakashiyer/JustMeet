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
		if (user != null) {
			
			log.info("Fetch Upcoming plans for groups");
			List<Plan> plans = planDao.fetchUpcomingPlans(phone);
			PlanList planList = new PlanList();
			planList.setPlans(plans);
			
			return planList;
			
		}
		
		return new PlanList();

	}

	public PlanList fetchGroupPlans(String groupName, String groupIndex) {
		List<String> groups = new ArrayList<String>();
		groups.add(groupName);
		List<String> groupIds = new ArrayList<String>();
		groupIds.add(groupIndex);
		if (!groups.isEmpty()) {
			log.info("Fetch Upcoming plans for group: "+groupName);
			List<Plan> plans = planDao.fetchUpcomingGroupPlans(groups, groupIds);
			PlanList planList = new PlanList();
			planList.setPlans(plans);
			
			return planList;
		}
		return new PlanList();
	}

	public Plan fetchPlan(String planName, String planIndex) {
		Plan plan = planDao.fetchPlanInformation(planName, planIndex);
		if (plan != null) {
			
			return plan;
		} else {
			return new Plan();
		}
	}

	public Plan rsvpPlan(String phone, String planName, String planIndex, String groupIndex, String rsvp) {
		Plan plan = planDao.fetchPlanInformation(planName, planIndex);
		if (plan != null) {
			List<String> members = plan.getMemberNames();

			if (rsvp.equals("yes")) {
				members.add(phone);
			} else {
				members.remove(phone);
			}
			if (members.isEmpty()) {
				boolean success = planDao.deletePlan(planName, planIndex);
				if (success) {
					Group group = groupDao.fetchGroup(groupIndex);
					if (group != null) {
						List<String> plans = group.getPlanNames();
						plans.remove(plan.getName());
						
						List<String> planIds = group.getPlanIds();
						planIds.remove(plan.getId());
						
						groupDao.updateGroupWithUserPlan(
								group.getName(), groupIndex, plans, planIds);
					}
					return plan;
				}
			} else {
				boolean success = planDao.updatePlanWithMember(
						planIndex, members);

				if (success) {
					plan = planDao.fetchPlanInformation(planName, planIndex);
					
					
					return plan;
				}
			}

		}
		
		return new Plan();
	}

	public Plan deletePlan(String planName, String planIndex, String groupName, String groupIndex) {
		boolean success = planDao.deletePlan(planName, planIndex);
		if (success) {
			Group group = groupDao.fetchGroup(groupIndex);
			List<String> plans = group.getPlanNames();
			plans.remove(planName);
			List<String> planIds = group.getPlanIds();
			planIds.remove(planIndex);
			boolean userUpdated = groupDao.updateGroupWithUserPlan(
					groupName, groupIndex, plans, planIds);
			if (userUpdated) {
				
			}
		}
		return new Plan();
	}

//	public Plan addPlan(String planName, String phone, String planDate,
//			String planTime, String planLocation, String groupName,
//			String creator, String endDate, String endTime) {
//		List<String> members = new ArrayList<String>();
//		members.add(phone);
//
//		boolean success = planDao.addPlan(planName, groupName, planDate
//				+ " " + planTime, planLocation, members, creator, endDate + " " + endTime);
//		if (success) {
//			Group group = groupDao.fetchGroupInformation(groupName);
//			List<String> plans = group.getPlanNames();
//			plans.add(planName);
//			boolean userUpdated = groupDao.updateGroupWithUserPlan(
//					groupName, plans);
//			if (userUpdated) {
//				Plan plan = planDao.fetchPlanInformation(planName);
//				
//				return plan;
//			}
//		}
//		
//		return new Plan();
//	}

	public PlanList fetchPlanHistory(String groupName, String groupIndex) {
		PlanList planList = new PlanList();
		List<Plan> plans = planDao.fetchPlanHistory(groupName, groupIndex);
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
			Group group = groupDao.fetchGroup(groupIndex);
			List<String> plans = group.getPlanNames();
			plans.remove(oldPlanName);
			plans.add(newPlanName);
			
			boolean userUpdated = groupDao.updateGroupWithUserPlan(
					groupName, groupIndex, plans, group.getPlanIds());
			if (userUpdated) {
				Plan plan = planDao.fetchPlanInformation(newPlanName, planIndex);
				
				return plan;
			}
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
			if(groups != null && !groups.isEmpty()){
				for(String groupIndex: groups){
					Group group = groupDao.fetchGroup(groupIndex);
					List<String> plans = group.getPlanNames();
					plans.add(planName);
					
					List<String> planIds = group.getPlanIds();
					planIds.add(String.valueOf(index));
					groupDao.updateGroupWithUserPlan(
							group.getName(), groupIndex, plans, planIds);
					
				}
			}
			
			Plan plan = planDao.fetchPlanInformation(planName, String.valueOf(index));
			
			return plan;
			
		}
		
		return new Plan();
	}

}
