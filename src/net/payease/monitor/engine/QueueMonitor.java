package net.payease.monitor.engine;

import net.payease.monitor.events.QueueMonitorEntity;
import net.payease.monitor.listeners.Listener;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * //TODO 核心业务处理类
 * Checks number of dequeued messages and alerts when a queue has stopped consumption.
 * @author qishuwei
 */
public class QueueMonitor {

    private static Logger logger = Logger.getLogger(QueueMonitor.class);

    private String queueName;
    private Listener listener;

    private long lastNotificationTimestamp = 0;
    private long lastdeNotificationTimestamp = 0;
    private long minInterval = 0;
    private long producerInterval=0;

    private long MAX_PENDING_MESSAGE_SIZE=0;
    private Map<String,Long> mapdequeue = new HashMap<String, Long>();
 
    
    public QueueMonitor( Listener listener, long minInterval,long messagePending,long producerInterval) {
//        logger.info("Starting queue monitor for queue " + queueName);
        this.listener = listener;
        this.minInterval = minInterval;
        this.producerInterval=producerInterval;
        this.MAX_PENDING_MESSAGE_SIZE=messagePending;//等待消费的消息
    }

    public void check(MBeanServerConnection mbConn,String objectName,String mqServerIP,boolean isproduct,String mqServerName) throws UndeclaredThrowableException {
    	
		long messagePending;// 待消费的消息
		long messagesEnqueued; // 处理完成的消息
		long messagesDequeued;// 进入队列的消息
    	
        try {
    		ObjectName mbeanName = new ObjectName(objectName);
    		BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler
			.newProxyInstance(mbConn, mbeanName, BrokerViewMBean.class, true);
			for (ObjectName queueName : mbean.getQueues()) {
				QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler
						.newProxyInstance(mbConn, queueName, QueueViewMBean.class,
								true);
				messagePending=queueMbean.getQueueSize();
				messagesEnqueued=queueMbean.getEnqueueCount();
				messagesDequeued=queueMbean.getDequeueCount();
				if(isproduct==true){
					evaluatedequeue(messagePending,messagesEnqueued,messagesDequeued,mqServerIP,mqServerName);//producer monitor
				}else{
		            evaluate(messagePending,messagesEnqueued,messagesDequeued,mqServerIP,mqServerName);//consumer monitor
				}
			}
        } catch (UndeclaredThrowableException e) {
            logger.warn("MBean not found. Does queue " + objectName + " exist?");
            logger.debug(e);
        } catch (MalformedObjectNameException e) {
            logger.warn("MBean not found. Does queue " + objectName + " exist?");
            logger.debug(e);
        }
    }
    /**
     * 
     * //TODO 待处理队列大于MAX_PENDING_QUEUE_SIZE发送邮件
     * @param messagePending待消费的消息
     * @param messagesEnqueued进入队列的消息数
     * @param messagesDequeued处理完成的消息数
     */
    public void evaluate(long messagePending,long messagesEnqueued,long messagesDequeued,String mqServerIP,String mqServerName) {
    	if (messagePending>MAX_PENDING_MESSAGE_SIZE) {
            boolean notify = true;

            long now = System.currentTimeMillis();
            if (notify) {
//                if (now > lastNotificationTimestamp + minInterval) {
                    if (now > lastNotificationTimestamp) {

                    listener.notify(new QueueMonitorEntity(this.queueName,messagePending,messagesEnqueued,messagesDequeued,"consumerMonitor",mqServerIP,mqServerName));
                    lastNotificationTimestamp = now;
                    notify=false;
                } else {
                    logger.warn("Error notification already sent. Waiting to send another consumerMonitor");
                }
            } 
        } else {
            logger.info("ServerIP: " + mqServerIP +" .消费队列 正常.  待消费队列messagePending: " + messagePending +" .");
        }
    }
    public void evaluatedequeue(long messagepending,long messagesEnqueued,long messagesDequeued,String mqServerIP,String mqServerName) {
		if (mapdequeue.get(mqServerIP) == null) {
			mapdequeue.put(mqServerIP, messagesEnqueued);
			logger.info("init mapdequeue ip："+mqServerIP);

		} else {
			long now = System.currentTimeMillis();
//			if (now > lastdeNotificationTimestamp + producerInterval) {
				if (now > lastdeNotificationTimestamp ) {

				Long val = mapdequeue.get(mqServerIP);
				if (messagesEnqueued - val > 0) {
					mapdequeue.put(mqServerIP, messagesEnqueued);
					logger.info("mqServerIP: " + mqServerIP
							+ " ,enqueue正常前值-val：" + val
							+ " ,enqueue正常后值-enqueuedCount：" + messagesEnqueued+ " ,enqueue差值val：" + (messagesEnqueued-val));
		            logger.info("ServerIP: " + mqServerIP +" .生产队列 正常.");


				} else {
					mapdequeue.put(mqServerIP, messagesEnqueued);
					logger.info("mqServerIP: " + mqServerIP
							+ " ,enqueue不正常前值-val：" + val
							+ " ,enqueue不正常后值-enqueuedCount：" + messagesEnqueued);
					listener.notify(new QueueMonitorEntity(this.queueName,
							messagepending, messagesEnqueued, messagesDequeued,
							"producerMonitor",mqServerIP,mqServerName));
					lastdeNotificationTimestamp = now;

				}
			}else {
                logger.warn("Notification already sent. Waiting to send another producerMonitor");
            }
		}
       

    }

}
