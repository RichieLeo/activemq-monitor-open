
# 1ActiveMq部署 
## 1 版本说明

* Activemq版本5.13.0
* Jdk版本1.7以上
* Ant版本1.9以上

## 2 解压文件
* gunzip -c apache-activemq-5.13.0.tar.gz | tar -xvf –

## 3 mq启动

* cd apache-activemq-5. 13.1/bin/

* chmod 775 activemq
* ./activemq start
 
## 4 mq停止
* ./activemq stop

# 2 ActiveMq监控启动前MQ配置
## 1 Linux下配置IP 
* 配置ip后，通过ip访问
* 127.0.0.1               localhost.localdomain localhost
* ::1             localhost6.localdomain6 localhost6
* 192.168.111.83   VM7-amqtest

## 2 MQ配置
        <managementContext>
            <managementContext createConnector="true" connectorPort="11099" rmiServerPort="12099" >
                <property xmlns="http://www.springframework.org/schema/beans" name="environment">
                        <map xmlns="http://www.springframework.org/schema/beans">
                                <entry xmlns="http://www.springframework.org/schema/beans"
                                        key="jmx.remote.x.password.file"
                                        value="${activemq.base}/conf/jmx.password"/>
                                <entry xmlns="http://www.springframework.org/schema/beans"
                                        key="jmx.remote.x.access.file"
                                        value="${activemq.base}/conf/jmx.access"/>
                        </map>
                 </property>
            </managementContext>
        </managementContext>
## 3 安全设置
### 3.1修改jmx登陆的用户名和密码
* chmod 600 jmx.*
* jmx.access
* jmx.password

### 3.2设置为只读
* chmod 400 jmx.*
* jmx.access
* jmx.password

## 4 Linux端口配置
* 开通端口11099、12099、61616（tcp和udp）和8161 （http）

# 3ActiveMq监控程序说明

## 1 jmx访问方式

* service:jmx:rmi://192.168.111.80:12000/jndi/rmi://192.168.111.80:11000/jmxrmi

## 2 程序部署
* 代码路径
* (/opt/user/activemq-monitor-1.0)
* 修改执行权限：chmod 775 mqm.sh
 
* 启动mqm进程 sh mqm.sh start查看进程
* 停止mqm进程 sh mqm.sh stop查看进程
* (如果停止失败，kill -9 pid，方式停止)
* 重新启动mqm进程 sh mqm.sh restart查看进程
* 查看mqm进程 sh mqm.sh show查看进程

# 4监控发送邮件条件
* （消费者）messagePending待消费的消息数据大于阀值
* （生产者）messagesEnqueued进入队列的消息数刷新时间内未增加

