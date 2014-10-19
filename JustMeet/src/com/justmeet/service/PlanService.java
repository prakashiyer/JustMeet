package com.justmeet.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justmeet.dao.CenterDAO;
import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Center;
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
	private CenterDAO centerDao;

	public PlanList fetchUpcomingPlans(String phone) {
		List<String> userAndAdminPhones = new ArrayList<String>();
		userAndAdminPhones.add(phone);
		User user = userDao
				.fetchUser(phone);
		String docFlag = "N";
		if (user != null) {
			List<String> centerIds = user.getCenters();
			docFlag = user.getDoctorFlag();
			if(centerIds != null && !centerIds.isEmpty()){
				for(String centerId: centerIds){
					Center center = centerDao.fetchCenter(centerId);
					userAndAdminPhones.add(center.getAdminPhone());
				}
			}
		}
		log.info("Fetch Upcoming plans for user");
		List<Plan> plans = planDao.fetchUpcomingPlans(userAndAdminPhones, docFlag);
		PlanList planList = new PlanList();
		planList.setPlans(plans);
		
		return planList;

	}
	
	public Plan newPlan(String title, String planDate,
			String planTime, String userPhone, String userRsvp,
			String docPhone, String docRsvp, String centerFlag, String endDate, String endTime, String centerId, String centerPlanFile) {
		      

		int index = planDao.newPlan(title, userPhone, userRsvp, docPhone,  docRsvp, centerFlag, centerId, centerPlanFile, planDate
				+ " " + planTime, endDate + " " + endTime);
		if (index > 0) {
			Plan plan = planDao.fetchPlan(String.valueOf(index));
			return plan;
		}
		return new Plan();
	}
	
	public Plan fetchPlan(String id) {
		Plan plan = planDao.fetchPlan(id);
		if(plan != null){
			if("Y".equals(plan.getCenterPlanFlag())){
				Center center = centerDao.fetchCenter(plan.getCenterId());
				plan.setCenterName(center.getName());
			} else {
				User user = userDao.fetchUser(plan.getDocPhone());
				plan.setDocName(user.getName());
			}
			return plan;
		} 
		log.warn("Plan fetch failed for plan:" +id);
		return new Plan();
	}
	
	
	public Plan editPlan(String planId, String title, String planDate, String planTime, String endDate, String endTime){
		boolean success = planDao.editPlan(planId, title, planDate+" "+planTime, endDate+" "+endTime);
		Plan plan = planDao.fetchPlan(planId);
		if(!success){
			log.warn("Edit failed for plan:" +planId);
		} 
		return plan;
	}
	
	public void deletePlan(String planId){
		boolean success = planDao.deletePlan(planId);
		
		if(!success){
			log.warn("Delete failed for plan:" +planId);
		} 
	}
	
	public Plan updateRsvp(String planId, String userRsvp, String docRsvp, String planFile){
		boolean success = planDao.updateRsvp(planId, userRsvp, docRsvp, planFile);
		Plan plan = planDao.fetchPlan(planId);
		if("Y".equals(plan.getCenterPlanFlag())){
			String centerId = plan.getCenterId();
			Center center = centerDao.fetchCenter(centerId);
			if(center != null){
				plan.setCenterName(center.getName());
			}
		} else {
			String phone = plan.getDocPhone();
			User user = userDao.fetchUser(phone);
			if(user != null){
				plan.setDocName(user.getName());
			}
		}
		
		
		if(!success){
			log.warn("Rsvp update failed for plan:" +planId);
		} 
		return plan;
	}

	/*public PlanList fetchGroupPlans(String groupName, String groupIndex) {
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

	*/

}
