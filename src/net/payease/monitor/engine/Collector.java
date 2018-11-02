package net.payease.monitor.engine;

import net.payease.monitor.config.ConfigEntity;
import net.payease.monitor.config.ParseConfig;
import net.payease.monitor.config.UtilPro;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * //TODO 监控初始化类
 * @author qishuwei
 */
public class Collector {

    private static Logger logger = Logger.getLogger(Collector.class);

    private ActiveMQMonitor activeMQMonitor;
    private ActiveMQMonitorProduct activeMQMonitorProduct;

    private static Timer timer;
    private static Timer timerp;
    private static List<ConfigEntity> configList = new ArrayList<ConfigEntity>();

    private static final Integer INTERVAL_IN_SECS = 60;


    public Collector(ActiveMQMonitor activeMQMonitor,ActiveMQMonitorProduct activeMQMonitorProduct) {
        this.activeMQMonitor = activeMQMonitor;
        this.activeMQMonitorProduct = activeMQMonitorProduct;

        ParseConfig parse = new ParseConfig();
        configList= parse.addrParseToList();
        Integer systemRefresh=Integer.parseInt((UtilPro.getValue("systemRefresh")));//系统刷新时间
        Integer producerInterval=Integer.parseInt((UtilPro.getValue("producerInterval")));//系统刷新时间

        timer = new Timer();
        timer.schedule(new MonitorTask(), 0, systemRefresh * 1000);//消费者监控
        timerp = new Timer();
        timerp.schedule(new MonitorTaskP(), 0, producerInterval * 1000);//生产者监控
    }

    class MonitorTask extends TimerTask {
        public void run() {
            logger.debug("Retrieving information");
            Integer ismonitor=Integer.parseInt((UtilPro.getValue("ismonitor")));//消费者监控
            if (ismonitor==1){
                for(ConfigEntity conf:configList)  
                {  
                	activeMQMonitor.fetch(conf.getJmxUrl(),conf.getObjectName(),conf.getMqServerIP(),conf.getJmxUserName(),conf.getJmxPassword(),false,conf.getMqServerName());
                } 
            }

        }
    }
    class MonitorTaskP extends TimerTask {
        public void run() {
            logger.debug("Retrieving information");
            Integer ismonitor=Integer.parseInt((UtilPro.getValue("ismonitor")));//生产者监控
            if (ismonitor==1){
                for(ConfigEntity conf:configList)  
                {  
                	activeMQMonitorProduct.fetch(conf.getJmxUrl(),conf.getObjectName(),conf.getMqServerIP(),conf.getJmxUserName(),conf.getJmxPassword(),true,conf.getMqServerName());
                } 
            }

        }
    }

}
