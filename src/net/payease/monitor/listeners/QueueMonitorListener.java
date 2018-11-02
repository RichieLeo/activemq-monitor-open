package net.payease.monitor.listeners;

import net.payease.monitor.events.Event;
import net.payease.monitor.events.QueueMonitorEntity;
import net.payease.monitor.notifiers.EmailNotifier;

import org.apache.log4j.Logger;

/**
 * 
 * //TODO 监听类
 * @author qishuwei
 */
public class QueueMonitorListener implements Listener {

    private static Logger logger = Logger.getLogger(QueueMonitorListener.class);
    
    private EmailNotifier emailNotifier;
    private String recipients ;
    private String companyName;
    static private String NEW_LINE = "\r\n";

    public QueueMonitorListener(String rec, String companyName) {
        recipients=rec;
        this.companyName = companyName;
        this.emailNotifier = new EmailNotifier();

    }

    public void notify(Event event) {
        if (event instanceof QueueMonitorEntity) {
            QueueMonitorEntity queueMonitorEntity = (QueueMonitorEntity) event;

            String mailTo=recipients;
            String subject = "MQ监控通知";
            if(queueMonitorEntity.getMonitorType().equalsIgnoreCase("producerMonitor")){
                subject = "TONGNIUTWO-ACK-MQ异常(生产端),服务器IP: "+queueMonitorEntity.getMqServerIP()+",服务器名称: "+queueMonitorEntity.getMqServerName();
            }else if(queueMonitorEntity.getMonitorType().equalsIgnoreCase("consumerMonitor")){
                subject = "TONGNIUTWO-ACK-MQ异常(消费端),服务器IP: "+queueMonitorEntity.getMqServerIP()+",服务器名称: "+queueMonitorEntity.getMqServerName();
            }
			String content = subject + NEW_LINE + NEW_LINE + "异常服务器IP: "
					+ queueMonitorEntity.getMqServerIP()+ ", 待处理消息(MessagesPending): "
					+ queueMonitorEntity.getQueuePending() + ", 处理完成的消息(MessagesDequeued): "
					+ queueMonitorEntity.getMessagesDequeued() + ", 进入队列的消息数(MessagesEnqueued): "
					+ queueMonitorEntity.getMessagesEnqueued();
			// 发送邮件
            logger.info(content);
			emailNotifier.mail(mailTo, subject, content);
        
        }
    }
    /**
     * 
     * //TODO 暂时未使用
     * @see net.payease.monitor.listeners.Listener#notifyError(java.lang.String, java.lang.String)
     *
     */
    public void notifyError(String recipients,String serverIP) {

            String mailTo=recipients;
            String subject = "MQ服务器异常";
			String content = subject + NEW_LINE + NEW_LINE + "服务器IP: "
					+ serverIP;
			// 发送邮件
            logger.info(content);
			emailNotifier.mail(mailTo, subject, content);
        
    }

}
