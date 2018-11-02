package net.payease.monitor.config;

import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;
/**
 * 
 * //TODO 配置文件解析类
 * @author qishuwei
 */
public class ParseConfig {
    private static Logger logger = Logger.getLogger(ParseConfig.class);

	private List<ConfigEntity> configList = new ArrayList<ConfigEntity>();
	private ConfigEntity config = new ConfigEntity();

	public ConfigEntity addrParseToObject(String ipaddr, String brokerName,String serverName,String connectorPort,String rmiServerPort,String jmxUserName,String jmxPassword) {
		String surl = "service:jmx:rmi://" + ipaddr + ":" + rmiServerPort + "/jndi/rmi://"+ ipaddr + ":" + connectorPort + "/jmxrmi";
		
		String objectName = "org.apache.activemq:brokerName=" + brokerName
				+ ",type=Broker";
		ConfigEntity config = new ConfigEntity();
		config.setJmxUrl(surl);
		config.setObjectName(objectName);
		config.setMqServerIP(ipaddr);
		config.setJmxPassword(jmxPassword);
		config.setJmxUserName(jmxUserName);
		config.setMqServerName(serverName);
		return config;

	}

	public List<ConfigEntity> addrParseToList() {
		String connectorPort=UtilPro.getValue("connectorPort");
		String rmiServerPort=UtilPro.getValue("rmiServerPort");
		String jmxUserName=UtilPro.getValue("jmxUserName");
		String jmxPassword=UtilPro.getValue("jmxPassword");

		
		String[] addrOriginal = UtilPro.getValue("servers").split("\\|");
		for (int i = 0; i < addrOriginal.length; i++) {
			String[] ser = addrOriginal[i].split(",");
			config = addrParseToObject(ser[0], ser[1],ser[2],connectorPort,rmiServerPort,jmxUserName,jmxPassword);
			configList.add(config);
		}
		logger.info("Server cluster address Parse complete!");
		return configList;
	}
}
