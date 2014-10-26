package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.dao.CenterDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Center;
import com.justmeet.entities.Plan;
import com.justmeet.entities.User;
import com.justmeet.entities.UserList;

public class UserService {

	private static final Log log = LogFactory.getLog(UserService.class);

	@Autowired
	private UserDAO userDao;

	@Autowired
	private CenterDAO centerDao;

	@Autowired
	private PlanDAO planDao;

	public User addUser(String name, String phone, String bloodGroup,
			String dob, String sex, String address, String doctorFlag,
			String primaryCenterId, String primaryDoctorId, String centers) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date dobDate = new Date();
		try {
			dobDate = formatter.parse(dob);
		} catch (ParseException e) {
			log.warn("Date Parse exception. This should never happen!");
		}

		userDao.addUser(name, phone, bloodGroup, dobDate, sex, address,
				doctorFlag, primaryCenterId, primaryDoctorId, centers);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User added successfully: " + phone + "/" + name);
			return user;
		} else {
			return new User();
		}
	}

	public User fetchUser(String phone) {
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User fetched successfully: " + phone + " : center phone : " +user.getPrimaryCenterId()
					+ " : doctor phone : " + user.getPrimaryDoctorId());
			return user;
		} else {
			log.error("User fetch failed: " + phone);
			return new User();
		}
	}

	public byte[] uploadUserImage(String phone, MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();
			boolean success = userDao.addUserImage(phone, inputStream);
			if (success) {
				InputStream image = userDao.fetchUserImage(phone);
				if (image != null) {
					return IOUtils.toByteArray(image);
				}
			}
		} catch (IOException e) {
			log.error("Image Upload failed: " + phone);
		}

		return null;
	}
	
	public User editUser(String name, String phone, String bloodGroup,
			String dob, String sex, String address, String doctorFlag) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date dobDate = new Date();
		try {
			dobDate = formatter.parse(dob);
		} catch (ParseException e) {
			log.warn("Date Parse exception. This should never happen!");
		}

		userDao.editUser(name, phone, bloodGroup, dobDate, sex, address,
				doctorFlag);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User added successfully: " + phone + "/" + name);
			return user;
		} else {
			return new User();
		}
	}
	public User addDoctor(String phone, String primaryDoctorId) {
		userDao.addDoctor(phone, primaryDoctorId);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("Doctor added successfully: " + phone + "/" + primaryDoctorId);
			return user;
		} else {
			return new User();
		}
	}
	
	public User addCenter(String phone, String primaryCenterId) {
		userDao.addCenter(phone, primaryCenterId);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("Doctor added successfully: " + phone + "/" + primaryCenterId);
			return user;
		} else {
			return new User();
		}
	}
	
	public UserList fetchDoctorsList(String phoneList) {
		List<User> users = userDao.fetchDocList(phoneList);
		if (users != null) {
			log.info("User List fetched successfully, Size is: " + users.size());
			UserList userList = new UserList();
			userList.setUsers(users);
			return userList;
		} else {
			log.error("User List fetch failed ");
			return new UserList();
		}
	}
	
	public UserList searchDoctors(String name) {
		List<User> users = userDao.searchDoctors(name);
		if (users != null) {
			log.info("User List fetched successfully, Size is: " + users.size());
			UserList userList = new UserList();
			userList.setUsers(users);
			return userList;
		} else {
			log.error("User List fetch failed ");
			return new UserList();
		}
	}
	
	public byte[] fetchUserImage(String phone) {
		try {
			InputStream image = userDao.fetchUserImage(phone);
			if (image != null) {
				return IOUtils.toByteArray(image);
			}
		} catch (IOException e) {
			log.error("Image fetch failed: " + phone);
		}
		return null;
   }
	
	public void deleteUser(String phone) {
	User userInformation = userDao.fetchUser(phone);
	if (userInformation != null) {
		List<String> centers = userInformation.getCenters();
		if (centers != null && !centers.isEmpty()) {
			for (String centerId : centers) {
				Center center = centerDao.fetchCenter(centerId);
				if (center != null) {
					
					List<String> members = center.getMembers();
					List<String> centerMembersList = new ArrayList<String>();
					if(members != null && !members.isEmpty()){
						centerMembersList.addAll(members);
						centerMembersList.remove(phone);
						centerDao.updateCenterWithUser(centerId, centerMembersList);
					}
					
					String adminPhone = center.getAdminPhone();
					List<String> userAndAdminsList = new ArrayList<String>();
					userAndAdminsList.add(phone);
					userAndAdminsList.add(adminPhone);
					List<Plan> plans = planDao.fetchUpcomingPlans(userAndAdminsList, userInformation.getDoctorFlag());
					
					if (plans != null && !plans.isEmpty()) {
						for(Plan plan:plans){
							if("Y".equals(plan.getCenterPlanFlag())){
								String planFile = plan.getPlanFile();
								planFile = StringUtils.replace(planFile, phone+"|Y,", "");
								planFile = StringUtils.replace(planFile, phone+"|N,", "");
								planDao.updateRsvp(String.valueOf(plan.getId()), plan.getUserRsvp(), plan.getDocRsvp(), planFile);
							} else {
								planDao.deletePlan(String.valueOf(plan.getId()));
							}
						}
					}
				}
			}
		}
		userDao.deleteUser(phone);

	  }
	}


}
