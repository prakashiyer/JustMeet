package com.justmeet.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.entities.ExpenseList;
import com.justmeet.entities.ExpenseReport;
import com.justmeet.entities.Group;
import com.justmeet.entities.Plan;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;
import com.justmeet.service.ExpenseService;
import com.justmeet.service.GcmService;
import com.justmeet.service.GroupService;
import com.justmeet.service.PlanService;
import com.justmeet.service.UserService;

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
		System.out.println("New User addition: " + phone + "/" + name);
		logger.info("New User addition: " + phone + "/" + name);
		return this.userService.addUser(name, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUser")
	public @ResponseBody
	User fetchUser(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch User: " + phone);
		return this.userService.fetchUser(phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addGroup")
	public @ResponseBody
	Group addGroup(@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "phone") String phone) {
		logger.info("Add Group: " + phone + "/" + groupName);
		return this.groupService.addGroup(groupName, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUpcomingPlans")
	public @ResponseBody
	PlanList fetchUpcomingPlans(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch Upcoming plans for " + phone);
		return planService.fetchUpcomingPlans(phone);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadUserImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] uploadUserImage(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Image upload started.");
		return userService.uploadUserImage(phone, file);
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
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Image upload started.");
		return groupService.uploadGroupImage(groupName, file);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] fetchGroupImage(@RequestParam(value = "groupName") String groupName) {
		logger.info("Group Image fetch started: "+groupName);
		return groupService.fetchGroupImage(groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/searchGroup")
	public @ResponseBody
	Group searchGroup(@RequestParam(value = "groupName") String groupName) {
		logger.info("Group search begin: "+ groupName);
		return groupService.searchGroup(groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupPlans")
	public @ResponseBody
	PlanList fetchGroupPlans(@RequestParam(value = "groupName") String groupName) {
		logger.info("Fetching plans for the group: "+groupName);
		return planService.fetchGroupPlans(groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addPlan")
	public @ResponseBody
	Plan addPlan(@RequestParam(value = "name") String planName,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value = "endTime") String endTime,
			@RequestParam(value = "location") String planLocation,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "creator") String creator) {
		logger.info("Plan addition for " +groupName);
		Plan plan = planService.addPlan(planName, phone, planDate, planTime,
				planLocation, groupName, creator, endDate, endTime);
		if(plan != null){
			Group group = this.groupService.searchGroup(groupName);
			List<String> phoneList = group.getMembers();
			this.gcmService.broadcast("Just Meet", "A new plan has been added '"+ planName+"' to '"+groupName+"'", phoneList);
			logger.info("Plan created : "+ planName);
			return plan;
		}
		logger.info("Plan creation failed");
		return new Plan();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlan")
	public @ResponseBody
	Plan fetchPlan(@RequestParam(value = "planName") String planName) {
		logger.info("Plan fetch : "+ planName);
		return planService.fetchPlan(planName);
	}

	@SuppressWarnings("null")
	@RequestMapping(method = RequestMethod.GET, value = "/rsvpPlan")
	public @ResponseBody
	Plan rsvpPlan(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "rsvp") String rsvp) {
		Plan plan = planService.fetchPlan(planName);
		String phoneName = null;
		User user = new User();
		user = this.userService.fetchUser(phone);
		phoneName = user.getName();
		logger.info("phoneName : " +phoneName);
		if (rsvp.equals("yes")) {
		gcmService.broadcast("Just Meet", phoneName+ " is attending '" +planName+"'", plan.getMemberNames());
		} else if (rsvp.equals("no") && plan.getMemberNames().size() == 1) {
			logger.info("only one member in plan.. deleting...");
			gcmService.broadcast("Just Meet", phoneName+ " is not attending '" +planName+"'" +"and the plan is deleted", plan.getMemberNames());
		} else if (rsvp.equals("no")) {
			gcmService.broadcast("Just Meet", phoneName+ " is not attending '" +planName+"'", plan.getMemberNames());
		}
		logger.info("Plan RSVP for  : "+ planName+"/"+phone);
		return planService.rsvpPlan(phone, planName, rsvp);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deletePlan")
	public @ResponseBody
	Plan deletePlan(@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName) {
		Group group = groupService.searchGroup(groupName);
		gcmService.broadcast("Just Meet", " Plan '"+planName+"' has been deleted from '"+groupName+"'", group.getMembers());
		logger.info("Plan deleted: "+planName);
		return planService.deletePlan(planName, groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlanHistory")
	public @ResponseBody
	PlanList fetchPlanHistory(
			@RequestParam(value = "groupName") String groupName) {
		return planService.fetchPlanHistory(groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/joinGroup")
	public @ResponseBody
	Group joinGroup(@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "phone") String phone) {
		return groupService.joinGroup(groupName, phone);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/setAdminDecisionForUser")
	public @ResponseBody
	Group setAdminDecisionForUser(
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "decision") String decision) {
		
		return groupService.setAdminDecision(groupName, phone, decision);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/invite")
	public @ResponseBody
	Group invite(@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "phone") String phone) {
		return groupService.invite(groupName, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/leaveGroup")
	public @ResponseBody
	Group leaveGroup(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "groupName") String groupName) {
		Group group = groupService.searchGroup(groupName);
		gcmService.broadcast("Just Meet", phone+" has left the group '"+groupName+"'", group.getMembers());
		return groupService.leaveGroup(phone, groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/editPlan")
	public @ResponseBody
	Plan editPlan(@RequestParam(value = "newName") String newPlanName,
			@RequestParam(value = "oldName") String oldPlanName,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value = "endTime") String endTime,
			@RequestParam(value = "location") String planLocation,
			@RequestParam(value = "groupName") String groupName) {
		Plan plan = planService.fetchPlan(oldPlanName);
		gcmService.broadcast("Just Meet", "Plan "+oldPlanName+" has been edited to '"+newPlanName+"'", plan.getMemberNames());
		return planService.editPlan(newPlanName, oldPlanName, planDate,
				planTime, planLocation, groupName, endDate, endTime);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteAccount")
	public @ResponseBody
	void deleteAccount(@RequestParam(value = "phone") String phone) {
		userService.deleteUser(phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addExpense")
	public @ResponseBody
	void addExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "title") String title,
			@RequestParam(value = "value") String value) {
		expenseService.add(phone, planName, groupName, title, value);
		Plan plan = planService.fetchPlan(planName);
		logger.info("member names : " + plan.getMemberNames());
		String phoneName = null;
		User user = new User();
		user = this.userService.fetchUser(phone);
		phoneName = user.getName();
		gcmService.broadcast("Just Meet", phoneName+ " added a new expense of Rs."+value +" to plan - '" +planName+"' in '"+groupName+"'",plan.getMemberNames());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/updateExpense")
	public @ResponseBody
	void updateExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "title") String title,
			@RequestParam(value = "value") String value) {
		String phoneName = null;
		User user = new User();
		user = this.userService.fetchUser(phone);
		phoneName = user.getName();
		Plan plan = planService.fetchPlan(planName);
		gcmService.broadcast("Just Meet", phoneName+ " updated expense of Rs."+value +" to plan - '" +planName+"' in '"+groupName+"'",plan.getMemberNames());
		expenseService.update(phone, planName, groupName, title, value);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteExpense")
	public @ResponseBody
	void deleteExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "title") String title) {
		Plan plan = planService.fetchPlan(planName);
		expenseService.delete(phone, planName, groupName, title);
		String phoneName = null;
		User user = new User();
		user = this.userService.fetchUser(phone);
		phoneName = user.getName();
		gcmService.broadcast("Just Meet", phoneName+ " deleted an expense from plan - '" +planName+"' in '"+groupName+"'",plan.getMemberNames());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchExpense")
	public @ResponseBody
	ExpenseList fetchExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName) {
		return expenseService.fetch(phone, planName, groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/generateReport")
	public @ResponseBody
	ExpenseReport generateReport(
			@RequestParam(value = "planName") String planName) {
		return expenseService.generateReport(planName);
	}

}
