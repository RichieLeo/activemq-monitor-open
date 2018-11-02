package net.payease.monitor.events;

/**
 * 
 * //TODO 队列实体类
 * @author qishuwei
 */
public class QueueMonitorEntity implements Event {

    private String queueName;
    private long messagePending;//待消费的消息
    private String monitorType;
    private long messagesEnqueued; //进入队列的消息
    private long messagesDequeued;//处理完成的消息
    private String mqServerIP;
    private String mqServerName;



	/**
     * 
     * @param queueName
     * @param messagePending
     * @param messagesEnqueued
     * @param messagesDequeued
     * @param monitorType
     * @param mqServerIP
     */
	public QueueMonitorEntity(String queueName,long messagePending,long messagesEnqueued,long messagesDequeued,String monitorType,String mqServerIP,String mqServerName) {
        this.queueName = queueName;
        this.messagePending =messagePending;
        this.monitorType=monitorType;
        this.messagesEnqueued=messagesEnqueued;
        this.messagesDequeued=messagesDequeued;
        this.mqServerIP=mqServerIP;
        this.mqServerName=mqServerName;
    }

	public String getQueueName() {
        return queueName;
    }
    public long getQueuePending() {
		return messagePending;
	}

	public void setQueuePending(long queuePending) {
		this.messagePending = queuePending;
	}

    public String getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}
	public long getMessagesEnqueued() {
		return messagesEnqueued;
	}

	public void setMessagesEnqueued(long messagesEnqueued) {
		this.messagesEnqueued = messagesEnqueued;
	}

	public long getMessagesDequeued() {
		return messagesDequeued;
	}

	public void setMessagesDequeued(long messagesDequeued) {
		this.messagesDequeued = messagesDequeued;
	}
	public String getMqServerIP() {
		return mqServerIP;
	}

	public void setMqServerIP(String mqServerIP) {
		this.mqServerIP = mqServerIP;
	}

    public String getMqServerName() {
		return mqServerName;
	}

	public void setMqServerName(String mqServerName) {
		this.mqServerName = mqServerName;
	}
}
