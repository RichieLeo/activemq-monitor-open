package net.payease.monitor;

import net.payease.monitor.engine.ActiveMQMonitor;
import net.payease.monitor.engine.ActiveMQMonitorProduct;
import net.payease.monitor.engine.Collector;
import org.apache.log4j.Logger;

/**
 * 
 * //TODO 入口类
 * @author qishuwei
 */
public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    private static ActiveMQMonitor activeMQMonitor;
    private static ActiveMQMonitorProduct activeMQMonitorProduct;//生产者

    public static void main(String[] args) {
    	try {
    		logger.info("Startup activemq monitor....");

    		 activeMQMonitor = new ActiveMQMonitor();
    		 activeMQMonitorProduct = new ActiveMQMonitorProduct();//生产者

    	     Collector collector = new Collector(activeMQMonitor,activeMQMonitorProduct);
    	     
    	     logger.info("Startup activemq monitor Success!");

    	}catch (Exception expt) {
			
    		logger.info("Startup activemq monitor failed.\n" + expt);
			
		}
       
    }
    
}
