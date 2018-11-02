package net.payease.monitor.engine;

import net.payease.monitor.config.UtilPro;
import net.payease.monitor.listeners.Listener;
import net.payease.monitor.listeners.QueueMonitorListener;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * ActiveMQ through JMX 处理类
 * 
 * @author qishuwei
 */
public class ActiveMQMonitorProduct {

    private static Logger logger = Logger.getLogger(ActiveMQMonitor.class);

//    private String server;
//    private String port;
//    private String path;

//    private String domain;
//    private String brokerName;

	private LinkedList<QueueMonitor> monitors = new LinkedList<QueueMonitor>();

	private JMXConnector connector = null;
//	private Listener listener;
	private String recipients = "";

	public ActiveMQMonitorProduct() {


		long messagePending = Long.parseLong(UtilPro.getValue("messagePending"));// 待消费消息数

		recipients = UtilPro.getValue("recipients");
		long interval = Long.parseLong(UtilPro.getValue("minInterval")) * 60 * 1000;
		long producerInterval = Long.parseLong(UtilPro
				.getValue("producerInterval")) * 60 * 1000;

		String company = UtilPro.getValue("company");
		QueueMonitorListener queueMonitorListener = new QueueMonitorListener(
				recipients, company);

		QueueMonitor queueMonitor = new QueueMonitor(queueMonitorListener,
				interval, messagePending, producerInterval);
		monitors.add(queueMonitor);

	}

    /**
     * Use JMX to find activemq statistics and send them to engine
     */
    public void fetch(String jmxUrl,String objectName,String mqServerIP,String jmxUserName,String jmxPassword,boolean isproduct,String mqServerName) {
        try {
            connect(jmxUrl,jmxUserName,jmxPassword);

            MBeanServerConnection mbConn = connector.getMBeanServerConnection();

            for (QueueMonitor monitor : monitors) {
                monitor.check(mbConn,objectName,mqServerIP,isproduct,mqServerName);
            }

            disconnect();
        } catch (IOException e) {
            logger.error("Could not connect to ActiveMQ. Is instance running and reachable at: " + mqServerIP +". error: "+ e);
//            listener.notifyError(recipients, mqServerIP);
        }
    }

    /**
     * Create an RMI connector and start it
     *
     * @param server
     * @param port
     */
    private void connect(String jmxUrl,String jmxUserName,String jmxPassword) {
        JMXServiceURL url;
        try {
//          logger.debug("Connecting to " + server + " on port " + port);
        	logger.debug("Connecting to server: "+jmxUrl);
//            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + server + ":" + port + connectorPath);
        	url = new JMXServiceURL(jmxUrl);
        	Map<String, Object> auth = new HashMap<String, Object>();  
            auth.put(JMXConnector.CREDENTIALS, new String[] { jmxUserName, jmxPassword });  
             
        	connector = JMXConnectorFactory.newJMXConnector(url, auth);
            connector.connect();

            logger.debug("Connected " + connector.getConnectionId());
        } catch (MalformedURLException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void disconnect() {
        try {
            connector.close();
            logger.debug("closing connection");
        } catch (IOException e) {
            logger.error("Error closing connection", e);
        }
    }

}
