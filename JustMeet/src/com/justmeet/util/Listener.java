package com.justmeet.util;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import java.sql.*;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Listener implements ServletContextListener {
	
	public Listener(){
		
	}
	
	public void contextDestroyed(ServletContextEvent sce)
    {
        for(Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();)
        {
            Driver driver = drivers.nextElement();
            try
            {
                DriverManager.deregisterDriver(driver);
                log.warn((new StringBuilder("deregistering jdbc driver: %s")).append(driver).toString());
            }
            catch(SQLException e)
            {
                log.warn((new StringBuilder("Error deregistering driver %s")).append(driver).toString());
            }
        }

        try
        {
            AbandonedConnectionCleanupThread.shutdown();
        }
        catch(InterruptedException e)
        {
            log.warn((new StringBuilder("SEVERE problem cleaning up: ")).append(e.getMessage()).toString());
            e.printStackTrace();
        }
    }

    public void contextInitialized(ServletContextEvent servletcontextevent)
    {
    }

    private static final Log log = LogFactory.getLog(Listener.class);



}
