package com.justmeet.controller;

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
		gcmService.addRegId(regId, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addUser")
	public @ResponseBody
	User addUser(@RequestParam(value = "name") String name,
			@RequestParam(value = "phone") String phone) {
		System.out.println("New User addition: " + phone + "/" + name);
		logger.info("New User addition: " + phone + "/" + name);
		return userService.addUser(name, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUser")
	public @ResponseBody
	User fetchUser(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch User: " + phone);
		return userService.fetchUser(phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addGroup")
	public @ResponseBody
	Group addGroup(@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "phone") String phone) {
		logger.info("Add Group: " + phone + "/" + groupName);
		return groupService.addGroup(groupName, phone);
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
			@RequestParam(value = "location") String planLocation,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "creator") String creator) {
		logger.info("Plan addition for " +groupName);
		Plan plan = planService.addPlan(planName, phone, planDate, planTime,
				planLocation, groupName, creator);
		if(plan != null){
			Group group = groupService.searchGroup(groupName);
			List<String> phoneList = group.getMembers();
			gcmService.broadcast("New Plan Created", "A new plan has been added.", phoneList);
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

	@RequestMapping(method = RequestMethod.GET, value = "/rsvpPlan")
	public @ResponseBody
	Plan rsvpPlan(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "rsvp") String rsvp) {
		logger.info("Plan RSVP for  : "+ planName+"/"+phone);
		return planService.rsvpPlan(phone, planName, rsvp);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deletePlan")
	public @ResponseBody
	Plan deletePlan(@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName) {
		Group group = groupService.searchGroup(groupName);
		gcmService.broadcast("Plan deleted", " Plan "+planName+" has been added.", group.getMembers());
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
		return groupService.leaveGroup(phone, groupName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/editPlan")
	public @ResponseBody
	Plan editPlan(@RequestParam(value = "newName") String newPlanName,
			@RequestParam(value = "oldName") String oldPlanName,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "location") String planLocation,
			@RequestParam(value = "groupName") String groupName) {
		return planService.editPlan(newPlanName, oldPlanName, planDate,
				planTime, planLocation, groupName);
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
		gcmService.broadcast("New Expense", "A new expense has been added.", plan.getMemberNames());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/updateExpense")
	public @ResponseBody
	void updateExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "title") String title,
			@RequestParam(value = "value") String value) {
		expenseService.update(phone, planName, groupName, title, value);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteExpense")
	public @ResponseBody
	void deleteExpense(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "planName") String planName,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "title") String title) {
		expenseService.delete(phone, planName, groupName, title);
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
