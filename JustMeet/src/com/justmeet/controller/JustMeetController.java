package com.justmeet.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justmeet.entities.Group;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;
import com.justmeet.service.GcmService;
import com.justmeet.service.GroupService;
import com.justmeet.service.PlanService;
import com.justmeet.service.UserService;

@Controller
@RequestMapping("/operation")
public class JustMeetController {

	private static final Log log = LogFactory.getLog(JustMeetController.class);

	@Autowired
	private GcmService gcmService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;
	
	@Autowired
	private PlanService planService;

	@RequestMapping(method = RequestMethod.GET, value = "/addRegId")
	public @ResponseBody
	void addRegId(@RequestParam(value = "regId") String regId,
			@RequestParam(value = "phone") String phone) {
		log.info("Reg Id storage.");
		gcmService.addRegId(regId, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addUser")
	public @ResponseBody
	User addUser(@RequestParam(value = "name") String name,
			@RequestParam(value = "phone") String phone) {
		log.info("New User addition: " + phone + "/" + name);
		return userService.addUser(name, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUser")
	public @ResponseBody
	User fetchUser(@RequestParam(value = "phone") String phone) {
		log.info("Fetch User: " + phone);
		return userService.fetchUser(phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addGroup")
	public @ResponseBody
	Group addGroup(@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "phone") String phone) {
		log.info("Add Group: " + phone + "/" + groupName);
		return groupService.addGroup(groupName, phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchUpcomingPlans")
	public @ResponseBody
	PlanList fetchUpcomingPlans(@RequestParam(value = "phone") String phone) {
		log.info("Fetch Upcoming plans for " + phone);
		return planService.fetchUpcomingPlans(phone);

	}

}
