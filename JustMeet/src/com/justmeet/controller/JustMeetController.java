package com.justmeet.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.entities.Expense;
import com.justmeet.entities.ExpenseList;
import com.justmeet.entities.ExpenseReport;
import com.justmeet.entities.Group;
import com.justmeet.entities.GroupList;
import com.justmeet.entities.Plan;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;
import com.justmeet.entities.UserList;
import com.justmeet.service.ExpenseService;
import com.justmeet.service.GcmService;
import com.justmeet.service.GroupService;
import com.justmeet.service.PlanService;
import com.justmeet.service.UserService;
import com.justmeet.util.ObjectUtility;

@Controller
@RequestMapping("/operation")
public class JustMeetController {

	private static final Logger logger = Logger.getLogger(JustMeetController.class);
	
	@Autowired
	private GcmService gcmService;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private PlanService planService;

	@Autowired
	private ExpenseService expenseService;

	@RequestMapping(method = RequestMethod.GET, value = "/addRegId")
	public @ResponseBody
	void addRegId(@RequestParam(value = "regId") String regId,
			@RequestParam(value = "phone") String phone) {
		logger.info("Reg Id storage.");
		this.gcmService.addRegId(regId, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addUser")
	public @ResponseBody
	User addUser(@RequestParam(value = "name") String name,
			@RequestParam(value = "phone") String phone) {
		logger.info("New User addition: " + phone + "/" + name);
		User user = userService.addUser(name, phone);
		logger.info("addUser response: "+ObjectUtility.convert(user));
		return user;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUser")
	public @ResponseBody
	User fetchUser(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch User: " + phone);
		User user = userService.fetchUser(phone);
		logger.info("fetchUser response: "+ObjectUtility.convert(user));
		return user;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addGroup")
	public @ResponseBody
	Group addGroup(@RequestParam(value = "name") String groupName,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "members") String members,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Add Group: " + phone + "/" + groupName);
		Group group= groupService.addGroup(groupName.replace("%20", " "), phone, members, file);
		gcmService.broadcast("Just Meet", "A new group: " +group.getName()+" has been created.,NewGroup,"+group.getId(), group.getMembers());
		logger.info("addGroup response: "+ObjectUtility.convert(group));
		return group;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/addMembers")
	public @ResponseBody
	Group addMembers(@RequestParam(value = "groupId") String groupId,
			@RequestParam(value = "members") String members) {
		logger.info("Add Group members: "  + members);
		Group group= groupService.updateMembers(groupId, members);
		gcmService.broadcast("Just Meet", "New members have been added to Group: " +group.getName()+".,NewMembers,"+group.getId(), group.getMembers());
		logger.info("addMembers response: "+ObjectUtility.convert(group));
		return group;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/editGroup", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	Group editGroup(@RequestParam(value = "groupId") String groupId,
			@RequestParam(value = "name") String groupName,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Edit Group: "  + groupName);
		Group group = groupService.editGroup(groupId, groupName.replace("%20", " "), file);
		logger.info("editGroup response: "+ObjectUtility.convert(group));
		return group;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUpcomingPlans")
	public @ResponseBody
	PlanList fetchUpcomingPlans(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch Upcoming plans for " + phone);
		PlanList planList = planService.fetchUpcomingPlans(phone);
		logger.info("fetchUpcomingPlans response: "+ObjectUtility.convert(planList));
		return planList;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadUserImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] uploadUserImage(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Image upload started.");
		return userService.uploadUserImage(phone, file);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/editUser")
	public @ResponseBody
	User editUser(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "image", required = false) MultipartFile file,
			@RequestParam(value = "name") String name) {
		logger.info("Edit user.");
		User user = userService.editUser(phone, file, name);
		logger.info("editUser response: "+ObjectUtility.convert(user));
		return user;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUserImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] fetchUserImage(@RequestParam(value = "phone") String phone) {
		logger.info("Image fetch started.");
		return userService.fetchUserImage(phone);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadGroupImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] uploadGroupImage(
			@RequestParam(value = "image") MultipartFile file,
			@RequestParam(value = "groupIndex") String groupIndex) {
		logger.info("Image upload started.");
		return groupService.uploadGroupImage(file, groupIndex);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] fetchGroupImage(@RequestParam(value = "groupIndex") String groupIndex) {
		logger.info("Group Image fetch started: "+groupIndex);
		return groupService.fetchGroupImage(groupIndex);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/searchGroup")
	public @ResponseBody
	Group searchGroup(@RequestParam(value = "groupName") String groupName) {
		logger.info("Group search begin: "+ groupName);
		Group group = groupService.searchGroup(groupName.replace("%20", " "));
		logger.info("searchGroup response: "+ObjectUtility.convert(group));
		return group;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroup")
	public @ResponseBody
	Group fetchGroup(@RequestParam(value = "groupIndex") String groupIndex) {
		logger.info("Group search begin: "+ groupIndex);
		Group group = groupService.fetchGroup(groupIndex);
		logger.info("fetchGroup response: "+ObjectUtility.convert(group));
		return group;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupPlans")
	public @ResponseBody
	PlanList fetchGroupPlans(@RequestParam(value = "groupIndex") String groupIndex) {
		logger.info("Fetching plans for the group: "+groupIndex);
		PlanList planList = planService.fetchGroupPlans(groupIndex);
		logger.info("fetchGroupPlans response: "+ObjectUtility.convert(planList));
		return planList;
	}


	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlan")
	public @ResponseBody
	Plan fetchPlan(@RequestParam(value = "planIndex") String planIndex) {
		Plan plan = planService.fetchPlan(planIndex);
		logger.info("fetchPlan response: "+ObjectUtility.convert(plan));
		return plan;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rsvpPlan")
	public @ResponseBody
	Plan rsvpPlan(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planIndex") String planIndex,
			@RequestParam(value = "rsvp") String rsvp) {
		Plan plan = planService.fetchPlan(planIndex);
		if(plan != null) {
			String planName = plan.getTitle();
			String phoneName = null;
			User user = new User();
			user = this.userService.fetchUser(phone);
			phoneName = user.getName();
			logger.info("phoneName : " +phoneName);
			if (rsvp.equals("yes")) {
			gcmService.broadcast("Just Meet", phoneName+ " is attending Plan: " +planName+",Rsvp,"+plan.getId(), plan.getMembersAttending());
			} else if (rsvp.equals("no") && plan.getMembersAttending().size() == 1) {
				logger.info("only one member in plan.. deleting...");
				gcmService.broadcast("Just Meet", phoneName+ " is not attending Plan: " +planName+" and the plan has been deleted,DeletePlan,"+plan.getId(), plan.getMembersAttending());
			} else if (rsvp.equals("no")) {
				gcmService.broadcast("Just Meet", phoneName+ " is not attending Plan: " +planName+",Rsvp,"+plan.getId(), plan.getMembersAttending());
			}
			logger.info("Plan RSVP for  : "+ planName+"/"+phone);
			Plan rsvpPlan = planService.rsvpPlan(phone, planIndex, rsvp);
			logger.info("fetchPlan response: "+ObjectUtility.convert(rsvpPlan));
			return rsvpPlan;
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deletePlan")
	public @ResponseBody
	Plan deletePlan(@RequestParam(value = "id") String planIndex) {
		Plan plan = planService.fetchPlan(planIndex);
		String planName = plan.getTitle();
		planService.deletePlan(planIndex);
		logger.info("Plan deleted: "+planName);
		gcmService.broadcast("Just Meet", " Plan :"+planName+" has been deleted,DeletePlan,"+plan.getId(), plan.getMembersInvited());
		return plan;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlanHistory")
	public @ResponseBody
	PlanList fetchPlanHistory(
			@RequestParam(value = "phone") String phone) {
		PlanList planList = planService.fetchPlanHistory(phone);
		logger.info("fetchPlanHistory response: "+ObjectUtility.convert(planList));
		return planList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupPlanHistory")
	public @ResponseBody
	PlanList fetchGroupPlanHistory(
			@RequestParam(value = "groupId") String groupId) {
		PlanList planList = planService.fetchGroupPlanHistory(groupId);
		logger.info("fetchGroupPlanHistory response: "+ObjectUtility.convert(planList));
		return planList;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/joinGroup")
	public @ResponseBody
	Group joinGroup(@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "groupIndex") String groupIndex,
			@RequestParam(value = "phone") String phone) {
		Group group = groupService.joinGroup(groupName, groupIndex, phone);
		logger.info("joinGroup response: "+ObjectUtility.convert(group));
		return group;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/leaveGroup")
	public @ResponseBody
	Group leaveGroup(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "groupIndex") String groupIndex) {
		Group group = groupService.fetchGroup(groupIndex);
		gcmService.broadcast("Just Meet", phone+" has left the group '"+group.getName()+"'", group.getMembers());
		Group editGroup = groupService.leaveGroup(phone, groupIndex);
		logger.info("leaveGroup response: "+ObjectUtility.convert(editGroup));
		return editGroup;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/deleteGroup")
	public @ResponseBody
	void deleteGroup(@RequestParam(value = "id") String groupIndex) {
		Group group = groupService.fetchGroup(groupIndex);
		gcmService.broadcast("Just Meet", " Group '"+group.getName()+"' has been deleted,DeleteGroup,"+group.getId(), group.getMembers());
		groupService.deleteGroup(groupIndex);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/editPlan")
	public @ResponseBody
	Plan editPlan(@RequestParam(value = "newName") String newPlanName,
			@RequestParam(value = "oldName") String oldPlanName,
			@RequestParam(value = "planIndex") String planIndex,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value = "endTime") String endTime,
			@RequestParam(value = "location") String planLocation,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "groupIndex") String groupIndex) {
		Plan plan = planService.fetchPlan(planIndex);
		gcmService.broadcast("Just Meet", "Plan: "+newPlanName+" has been edited.,EditPlan,"+plan.getId(), plan.getMembersAttending());
		Plan editplan = planService.editPlan(newPlanName, oldPlanName, planIndex, planDate,
				planTime, planLocation, groupName, groupIndex, endDate, endTime);
		logger.info("editplan response: "+ObjectUtility.convert(editplan));
		return editplan;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteAccount")
	public @ResponseBody
	void deleteAccount(@RequestParam(value = "phone") String phone) {
		userService.deleteUser(phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addExpense")
	public @ResponseBody
	Expense addExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planIndex,
			@RequestParam(value = "title") String title,
			@RequestParam(value = "value") String value) {
		
		Plan plan = planService.fetchPlan(planIndex);
		logger.info("member names : " + plan.getMembersAttending());
		String phoneName = null;
		User user = new User();
		user = userService.fetchUser(phone);
		phoneName = user.getName();
		Expense expense =  expenseService.add(phone, planIndex, title, value);
		gcmService.broadcast("Just Meet", phoneName+ " added a new expense of Rs."+value +" to plan - '" +plan.getTitle()+".,NewExpense,"+expense.getId()+","+plan.getId(), plan.getMembersAttending());
		logger.info("addExpense response: "+ObjectUtility.convert(expense));
		return expense;
		
	}

	@RequestMapping(method = RequestMethod.GET, value = "/updateExpense")
	public @ResponseBody
	Expense updateExpense(@RequestParam(value = "id") String id,
			@RequestParam(value = "planIndex") String planIndex,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "title") String title,
			@RequestParam(value = "value") String value) {
		String phoneName = null;
		User user = new User();
		user = this.userService.fetchUser(phone);
		phoneName = user.getName();
		Plan plan = planService.fetchPlan(planIndex);
		gcmService.broadcast("Just Meet", phoneName+ " updated expense of Rs."+value +" to plan - '" +plan.getTitle()+".,UpdateExpense,"+id+","+plan.getId(),plan.getMembersAttending());
		Expense expense = expenseService.update(id, title, value);
		logger.info("updateExpense response: "+ObjectUtility.convert(expense));
		return expense;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteExpense")
	public @ResponseBody
	void deleteExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planIndex") String planIndex,
			@RequestParam(value = "id") String id) {
		Plan plan = planService.fetchPlan(planIndex);
		expenseService.delete(id);
		String phoneName = null;
		User user = new User();
		user = userService.fetchUser(phone);
		phoneName = user.getName();
		gcmService.broadcast("Just Meet", phoneName+ " deleted an expense from plan - '" +plan.getTitle()+".,DeleteExpense,"+id+","+plan.getId(),plan.getMembersAttending());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchExpenses")
	public @ResponseBody
	ExpenseList fetchExpenses(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planIndex") String planIndex) {
		ExpenseList expenseList = expenseService.fetch(phone, planIndex);
		logger.info("fetchExpenses response: "+ObjectUtility.convert(expenseList));
		return expenseList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchExpense")
	public @ResponseBody
	Expense fetchExpense(@RequestParam(value = "expenseId") String id) {
		Expense expense = expenseService.fetchExpense(id);
		logger.info("fetchExpense response: "+ObjectUtility.convert(expense));
		return expense;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/generateReport")
	public @ResponseBody
	ExpenseReport generateReport(@RequestParam(value = "planIndex") String planIndex) {
		ExpenseReport expenseReport = expenseService.generateReport(planIndex);
		logger.info("generateReport response: "+ObjectUtility.convert(expenseReport));
		return expenseReport;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/fetchExistingUsers")
	public @ResponseBody
	UserList fetchExistingUsers(@RequestParam(value = "phoneList") String phoneList) {
		UserList userList = userService.fetchUserList(phoneList);
		logger.info("fetchExistingUsers response: "+ObjectUtility.convert(userList));
		return userList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchInviteList")
	public @ResponseBody
	UserList fetchInviteList(@RequestParam(value = "groupId") String groupId,
			@RequestParam(value = "phoneList") String phoneList) {
		Group group = groupService.fetchGroup(groupId);
		String[] phonesArray = StringUtils.commaDelimitedListToStringArray(phoneList);
		if(phonesArray != null){
			List<String> phones = Arrays.asList(phonesArray);
			if(phones != null && !phones.isEmpty() && group != null){
				List<String> members = group.getMembers();
				if(members != null && !members.isEmpty()){
					phones.removeAll(members);
				}
				return userService.fetchUserList(phoneList);
			}
		}		
		return userService.fetchUserList(phoneList);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlanUsers")
	public @ResponseBody
	UserList fetchPlanUsers(@RequestParam(value = "planId") String planId) {
		UserList userList = userService.fetchPlanUsers(planId);
		logger.info("fetchPlanUsers response: "+ObjectUtility.convert(userList));
		return userList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupUsers")
	public @ResponseBody
	UserList fetchGroupUsers(@RequestParam(value = "groupIndex") String groupIndex) {
		Group group = groupService.fetchGroup(groupIndex);
		List<String> memberList = group.getMembers();
		String phoneList = StringUtils.collectionToCommaDelimitedString(memberList);
		UserList userList = userService.fetchUserList(phoneList);
		logger.info("fetchGroupUsers response: "+ObjectUtility.convert(userList));
		return userList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchExistingGroups")
	public @ResponseBody
	GroupList fetchExistingGroups(@RequestParam(value = "phone") String phone) {
		logger.info("Fetching Groups: " +phone);
		GroupList groupList = groupService.fetchGroupList(phone);
		logger.info("fetchExistingGroups response: "+ObjectUtility.convert(groupList));
		return groupList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/newPlan")
	public @ResponseBody
	Plan newPlan(@RequestParam(value = "name") String planName,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value = "endTime") String endTime,
			@RequestParam(value = "location") String planLocation,
			@RequestParam(value = "phoneList") String phoneList,
			@RequestParam(value = "groupList") String groupList,
			@RequestParam(value = "creator") String creator) {
		logger.info("New Plan addition");
		List<String> phones = null;
		if(!phoneList.equals("")){
			phones = Arrays.asList(phoneList.split(","));
		}
		
		List<String> groups = null;
		if(!groupList.equals("")){
			groups = Arrays.asList(groupList.split(","));
		}
		
		List<String> gcmList = new ArrayList<String>();
		gcmList.add(phone);
		if(phones != null && !phones.isEmpty()) {
			logger.info("Adding phones" +phones.size());
			gcmList.addAll(phones);
		}
		
		Plan plan = planService.newPlan(planName, phone, planDate, planTime,
				planLocation, phones, groups, creator, endDate, endTime);
		if(plan != null && plan.getTitle().equals(planName)){
			if(groups != null && !groups.isEmpty()){
				for(String groupName: groups){
					Group group = this.groupService.searchGroup(groupName.replace("%20", " "));
					if(group.getMembers() != null && !group.getMembers().isEmpty()) {
						logger.info("Adding members" +group.getMembers().size());
						gcmList.addAll(group.getMembers());
					}
				}
			}
			
			this.gcmService.broadcast("Just Meet", "A new plan: "+planName+" has been created.,NewPlan,"+plan.getId(), gcmList);
			logger.info("Plan created : "+ planName);
			logger.info("newPlan response: "+ObjectUtility.convert(plan));
			return plan;
		}
		logger.info("Plan creation failed");
		return new Plan();
	}
	
	

}
