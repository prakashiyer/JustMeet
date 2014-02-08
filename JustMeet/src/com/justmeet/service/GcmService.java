package com.justmeet.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.justmeet.dao.GcmDAO;


public class GcmService {

   private static final Log log = LogFactory.getLog(GcmService.class);
   
   // The SENDER_ID here is the "Browser Key" that was generated when I
   // created the API keys for my Google APIs project.
   private static final String SENDER_ID = "AIzaSyCNIsNEemteiyMbNXtip2_zkfI-L5XnOVo";
	
	@Autowired
	private GcmDAO gcmDao;
	
	public void addRegId(String regId, String phone){
		log.info("Reg Id storage.");
		gcmDao.storeRegId(regId, phone);
	}
	
	public void broadcast(String collapseKey, String userMessage, List<String> phoneList){
		log.info("Fetching Ids");
		List<String> androidTargets = gcmDao.fetchRegIds(phoneList);
		
		// Instance of com.android.gcm.server.Sender, that does the
        // transmission of a Message to the Google Cloud Messaging service.
        Sender sender = new Sender(SENDER_ID);
         
        // This Message object will hold the data that is being transmitted
        // to the Android client devices.  For this demo, it is a simple text
        // string, but could certainly be a JSON object.
        Message message = new Message.Builder()
         
        // If multiple messages are sent using the same .collapseKey()
        // the android target device, if it was offline during earlier message
        // transmissions, will only receive the latest message for that key when
        // it goes back on-line.
        .collapseKey(collapseKey)
        .timeToLive(30)
        .delayWhileIdle(true)
        .addData("message", userMessage)
        .build();
         
        try {
            // use this for multicast messages.  The second parameter
            // of sender.send() will need to be an array of register ids.
            MulticastResult result = sender.send(message, androidTargets, 1);
             
            if (result.getResults() != null) {
                int canonicalRegId = result.getCanonicalIds();
                if (canonicalRegId != 0) {
                     
                }
            } else {
                int error = result.getFailure();
                System.out.println("Broadcast failure: " + error);
            }
             
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
