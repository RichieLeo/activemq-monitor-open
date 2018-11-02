package net.payease.monitor.config;

import java.io.IOException;
import java.util.Properties;
/**
 * 
 * //TODO 读取配置文件工具类
 * @author qishuwei
 */
public class UtilPro {

    private static Properties p = new Properties();  
    
    /** 
     * 读取properties配置文件信息 
     * 
     */  
    static{  
        try {  
            p.load(UtilPro.class.getClassLoader().getResourceAsStream("activemq-monitor.properties"));  
        } catch (IOException e) {   
            e.printStackTrace();   
        }  
    }  
    /** 
     * 根据key得到value的值 
     */  
    public static String getValue(String key)  
    {  
        return p.getProperty(key);  
    } 
}
