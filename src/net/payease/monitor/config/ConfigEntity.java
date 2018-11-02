package net.payease.monitor.config;
/**
 * 
 * //TODO 配置信心实体类
 * @author qishuwei
 */
public class ConfigEntity {
    private String jmxUrl;
    private String objectName;
    private String mqServerIP;
    private String mqServerName;
	private String jmxUserName;
	private String jmxPassword;

	public String getJmxUrl() {
		return jmxUrl;
	}
	public void setJmxUrl(String jmxUrl) {
		this.jmxUrl = jmxUrl;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getMqServerIP() {
		return mqServerIP;
	}
	public void setMqServerIP(String mqServerIP) {
		this.mqServerIP = mqServerIP;
	}
    public String getJmxUserName() {
		return jmxUserName;
	}
	public void setJmxUserName(String jmxUserName) {
		this.jmxUserName = jmxUserName;
	}
	public String getJmxPassword() {
		return jmxPassword;
	}
	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}

	public String getMqServerName() {
		return mqServerName;
	}

	public void setMqServerName(String mqServerName) {
		this.mqServerName = mqServerName;
	}
}
