package com.tcpan.es;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcpan.es.component.Factory;
import com.tcpan.es.component.Component;

import java.io.File;

/**
 * App
 *
 */
public class App
{
    public static Logger logger = null;
    public static String workpath = null;

    static {
    	workpath = System.getProperty("app.workpath");
    	//log4j setting
    	PropertyConfigurator.configure(workpath + "/conf/log4j.properties");
    	logger = LoggerFactory.getLogger("es-manager");
    }

    public static void main(String[] args)
    {
    	try {
    		//check workpath
    		File workpathFile = new File(workpath);
    		if (!workpathFile.isDirectory()) {
    			throw new Exception("app.workpath: " + workpath + " is not a directory.");
    		}
    		logger.info("app workpath: " + workpath);

    		//param
    		AppParam appParam = AppParam.parseParam(args);

    		//run component
    		Component component = Factory.getComponent(appParam.getComponent());
    		if (component == null) {
    			throw new Exception("Component: " + appParam.getComponent() + " not found or init fail.");
    		}
    		component.start(logger);
    	} catch (Exception ex) {
    		logger.error(ex.getMessage());
    	}
    	//end
    	logger.info("done.");
    }
}
