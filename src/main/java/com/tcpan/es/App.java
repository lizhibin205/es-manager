package com.tcpan.es;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.tcpan.es.component.*;

/**
 * App
 *
 */
public class App
{
    public static Logger logger = null;

    public static void main(String[] args)
    {
    	try {
    		//param
    		AppParam appParam = AppParam.parseParam(args);
    		
    		//log4j setting
        	PropertyConfigurator.configure(appParam.getWorkpath() + "/conf/log4j.properties");
        	logger = LoggerFactory.getLogger("es-manager");

    		//run component
    		IndicesDailyDelete component = new IndicesDailyDelete(logger);
    		component.start(appParam.getEsHost(), appParam.getEsPort(), appParam.getEsDeleteDays());

    	} catch (Exception ex) {
    		logger.error(ex.getMessage());
    	}
    	//end
    	logger.info("done.");
    }
}
