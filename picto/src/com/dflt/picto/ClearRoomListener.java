package com.dflt.picto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONObject;


@WebListener
public class ClearRoomListener implements ServletContextListener {

	private ScheduledExecutorService ses;
	
	@Override
	 public void contextInitialized(ServletContextEvent sce) {
		System.out.println("PICTO: app started.");
	 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ses = Executors.newSingleThreadScheduledExecutor();	
		ses.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out.println("Cleaning Rooms..." + Calendar.getInstance().getTime());
				Enumeration<String> keys = sce.getServletContext().getAttributeNames();
				Calendar currentDate = Calendar.getInstance();
				currentDate.add(Calendar.HOUR, -4);
				
				while (keys.hasMoreElements()){
					String key = (String)keys.nextElement();
			    	
					if(key.startsWith("ROOM_")) {  
						try {
							JSONObject room  = (JSONObject)sce.getServletContext().getAttribute(key);
							String dateStr   = room.getString("CREATEDDATE");
							Date createdDate = sdf.parse(dateStr);
											
							if(createdDate.before(currentDate.getTime())) {
								System.out.println("Deleting Room: " + key + ", Created: " + dateStr);
								sce.getServletContext().removeAttribute(key);
							}
						} catch (ParseException e) {	
							e.printStackTrace();
						}
					}
				}
			}
		}, 0, 15, TimeUnit.MINUTES);
	}
	 
	 
	 @Override
	 public void contextDestroyed(ServletContextEvent sce) {
		 System.out.println("PICTO: app  stoped.");
		 ses.shutdown();
	 }
}