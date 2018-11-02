package net.payease.monitor.notifiers;

import java.util.*;

import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;

import javax.activation.*;

import com.capinfo.common.util.MailUtil_MailAutherticatorbean;
import com.capinfo.common.util.PageUtil;
import com.capinfo.common.util.PopupAuthenticator;


public class

MailUtil {
    /**
     * 调试标志
     */
    boolean debug = false;

    /**
     * 邮件服务器地址
     */
    private String mailServer = "";

    /**
     * 发送用户名
     */
    private String userName = "";

    /**
     * 发送用户密码
     */
    private String password = "";

    /**
     * 邮件ID编号
     */
    private Integer id;

    /**
     * 发送邮件地址
     */
    private String from = "";

    /**
     * 接收邮件地址
     */
    private String[] to;

    /**
     * 抄送邮件地址
     */
    private String[] cc;

    /**
     * 暗送邮件地址
     */
    private String[] bcc;

    /**
     * 邮件标题
     */
    private String subject = "";

    /**
     * 邮件正文
     */
    private String body = "";

    /**
     * 邮件发送时间
     */
    private String time = "";

    /**
     * 邮件附件
     */
    private Hashtable attachment = null;

    /**
     * 邮件附件
     */
    private String[] attachFiles = null;


    /**
     * POP3服务器地址
     */
    private String pop3Server = "";

    /**
     * 默认邮件内容编码，缺省为文本格式
     */
    private String contentType = "text/plain;charset=GBK";

    /**
     * 缺省构造函数
     */
    public MailUtil() {
    }

    /**
     * 构造函数
     * @param userName 发件用户名
     * @param password 发件用户密码
     * @param mailServer 邮件服务器地址
     */
    public MailUtil(String userName, String password, String mailServer) {
        this.userName = userName;
        this.mailServer = mailServer;
        this.password = password;
    }

    /**
     * 构造函数
     * @param mailId 邮件ID编号
     * @param userName 发件用户名
     * @param password 发件用户密码
     * @param mailServer 邮件服务器地址
     */
    public MailUtil(Integer mailId, String userName, String password, String mailServer) {
        //设置邮件参数                  
        this.id = mailId;
        this.userName = userName;
        this.mailServer = mailServer;
        this.password = password;

        Part part = null;
        Date dTime = null;
        //采用POP3协议
        String protocol = "pop3";

        try {
            //获取会话变量
            Session session = Session.getInstance(new Properties(), null);
            //设置调试模式
            session.setDebug(false);
            //连接邮件服务器
            Store store = session.getStore(protocol);
            store.connect(mailServer, -1, userName, password);
            //获取收件箱对象
            Folder folder = store.getFolder("INBOX");
            if (folder == null)
                throw new NullPointerException("Unable to get folder: " + folder);

            //打开收件箱
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_ONLY);
            }

            //获取邮件消息列表
            Message message[] = folder.getMessages();
            //设置邮件过滤规则
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add("X-Mailer");
            folder.fetch(message, fp);

            Address[] fromAddress = null;
            Address[] toAddress = null;
            for (int i = 0; i < message.length; i++) {
                if (id.intValue() == message[i].getMessageNumber()) {
                    //得到邮件发送时间
                    dTime = message[i].getSentDate();
                    this.time = dTime.toString();

                    //得到邮件发送人地址
                    fromAddress = message[i].getFrom();
                    if (fromAddress != null) {
                        for (int l = 0; l < fromAddress.length; l++) {
                            this.from = fromAddress[l].toString();
                        }
                    }

                    //得到收件人地址
                    toAddress = message[i].getAllRecipients();

                    if (toAddress != null) {
                        this.to = new String[toAddress.length];
                        for (int t = 0; t < toAddress.length; t++) {
                            this.to[t] = toAddress[t].toString();
                        }
                    }

                    //得到邮件正文内容信息
                    String disposit = "";
                    String conType = "";
                    Multipart multipart = new MimeMultipart();
                    Object content = message[i].getContent();
                    String str = "";
                    if (content instanceof Multipart) {
                        multipart = (Multipart)message[i].getContent();
                        for (int n = 0; n < multipart.getCount(); n++) {
                            part = multipart.getBodyPart(n);
                            disposit = part.getDisposition();
                            conType = part.getContentType();

                            if (disposit == null) { //only content
                                if ((conType.length() >= 10) && (conType.toLowerCase().substring(0, 10).equals("text/plain"))) {
                                    this.body = part.getContent().toString();
                                } else { // Type text/html
                                }
                            } else {
                                if (!(str.length() > 1)) //dispose txt file  !!!
                                    this.body = ((MimeMultipart)multipart.getBodyPart(0).getContent()).getBodyPart(0).getContent().toString();
                            }
                        }
                    } else {
                        this.body = message[i].getContent().toString();
                    }
                }
            }
            //关闭收件箱 
            folder.close(true);
            //关闭连接
            store.close();
        } catch (MessagingException me) {
            System.out.println("mail--MessagingException :" + me);
        } catch (Exception e) {
            System.out.println("mail--Exception :" + e);
        }
    }

    /**
     * 发送带附件邮件
     * @return 发送结果标识
     */
    public boolean sendWithAttachment() {
        //设置邮件附件文件矢量
        Vector vfile;

        //设置邮件消息矢量
        Vector vmsg;

        //邮件附件文件名称
        String fileName = "";

        //邮件消息标题
        String messageText = "";

        try {
            vfile = new Vector(10, 10);
            vmsg = new Vector(10, 10);
            System.out.println("mailserver= " + mailServer);
            //设置系统属性
            Properties props = System.getProperties();
            props.put("mail.smtp.host", mailServer);
            //获取会话变量 
            Session session = Session.getDefaultInstance(props, null);
            if (session == null)
                return false;
            //设置会话调试模式
            session.setDebug(debug);
            //生成发送消息
            MimeMessage msg = new MimeMessage(session);
            //设置发件地址
            msg.setFrom(new InternetAddress(from));

            //设置收件地址
            if (to != null) {
                InternetAddress[] address = new InternetAddress[to.length];
                for (int i = 0; i < to.length; ++i) {
                    address[i] = new InternetAddress(to[i]);
                }
                msg.setRecipients(Message.RecipientType.TO, address);
            }
            if (cc != null) {
                InternetAddress[] address = new InternetAddress[cc.length];
                for (int i = 0; i < cc.length; ++i) {
                    address[i] = new InternetAddress(cc[i]);
                }
                msg.setRecipients(Message.RecipientType.CC, address);
            }
            //判断是否有附件
            if (this.attachment == null) { //dispose them if mail have attachment
                System.out.println(" attachment file don't null !");
                return false;
            } else
                vfile.addElement(this.attachment); // dispose

            //判断邮件内容是否为空  
            if (body != "") //mail content
                vmsg.addElement(body);

            //设置邮件标题
            msg.setSubject(subject);

            //添加邮件内容
            Multipart mp = new MimeMultipart();

            Enumeration emsg = vmsg.elements();

            while (emsg.hasMoreElements()) {
                messageText = emsg.nextElement().toString();
                MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(messageText);
                mp.addBodyPart(mbp1);
            }
            vmsg.removeAllElements();

            // mail second part
            Enumeration efile = vfile.elements();
            BodyPart mbp2 = new MimeBodyPart();
            fileName = this.attachment.get("filename").toString();
            mbp2.setFileName(fileName);
            mbp2.setText(this.attachment.get("bytestream").toString());
            mp.addBodyPart(mbp2);
            vfile.removeAllElements();

            //设置邮件报文头信息
            msg.setContent(mp);

            //设置邮件发送时间
            msg.setSentDate(new Date());

            //执行邮件发送操作
            Transport.send(msg); //transport
        } catch (MessagingException mex) {
            mex.printStackTrace();
            Exception ex = null;

            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }
            return false;

        } catch (Exception e) {
            System.out.println("sendWithAttachment--Exception:" + e);
        }
        return true;
    }

    /**
     * 发送无附件邮件
     * @return 发送结果标识
     */
    public boolean sendWithoutAttachment() {
        //设置邮件内容矢量
        Vector vmsg = new Vector(10, 10);
        //邮件附件文件名称
        String fileName = "";
        //邮件消息标题
        String messageText = "";
        //设置系统属性
        Properties props = System.getProperties();
        //设置SMTP服务器地址
        props.put("mail.smtp.host", mailServer);
        //设置采用发送用户认证
        props.put("mail.smtp.auth", "true");
        //进行发送用户认证
        PopupAuthenticator popAuthenticator = new PopupAuthenticator();
        PasswordAuthentication pop = popAuthenticator.performCheck(this.userName, password);
        //获取会话变量   
        Session session = Session.getInstance(props, popAuthenticator);

        if (session == null)
            return false;
        //设置会话调试模式  
        session.setDebug(debug);

        try {
            MimeMessage msg = new MimeMessage(session);
            //设置发件地址
            msg.setFrom(new InternetAddress(from));

            //设置收件地址
            if (to != null) {
                InternetAddress[] address = new InternetAddress[to.length];
                for (int i = 0; i < to.length; ++i) {
                    address[i] = new InternetAddress(to[i]);
                }
                msg.setRecipients(Message.RecipientType.TO, address);
            }
            if (cc != null) {
                InternetAddress[] address = new InternetAddress[cc.length];
                for (int i = 0; i < cc.length; ++i) {
                    address[i] = new InternetAddress(cc[i]);
                }
                msg.setRecipients(Message.RecipientType.CC, address);
            }


            //设置邮件正文 
            if (body.equals(""))
                return false;
            else
                vmsg.addElement(body);

            //设置邮件标题
            msg.setSubject(subject);

            //添加发送消息队列
            Multipart mp = new MimeMultipart();

            Enumeration emsg = vmsg.elements();

            while (emsg.hasMoreElements()) {
                messageText = emsg.nextElement().toString();
                MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(messageText);
                mbp1.setContent(mbp1.getContent(), contentType);
                mp.addBodyPart(mbp1);
            }
            vmsg.removeAllElements();

            //设置邮件发送报文头
            msg.setContent(mp);

            //设置邮件发送时间
            msg.setSentDate(new Date());

            //执行邮件发送任务
            Transport.send(msg);

        } catch (MessagingException mex) {
            mex.printStackTrace();
            Exception ex = null;

            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            System.out.println("sendWithoutAttachment--Exception:" + e);
        }
        return true;
    }

    /**
     * 删除邮件
     * @param mailId 邮件ID编号
     * @return 是否删除成功标识
     */
    public boolean drop(Integer mailId) {
        //采用POP3协议
        String protocol = "pop3";

        //设置收件箱
        String mbox = "INBOX";

        try {
            //获取会话变量
            Session mailSession = Session.getInstance(System.getProperties(), null);
            //设置会话调试模式
            mailSession.setDebug(false);
            //连接邮件服务器
            Store store = mailSession.getStore(protocol);
            store.connect(mailServer, -1, userName, password);

            //打开缺省收件箱
            Folder folder = store.getDefaultFolder();
            if (folder == null)
                throw new NullPointerException("No default mail folder");
            //获取收件箱
            folder = store.getFolder(mbox);
            if (folder == null)
                throw new NullPointerException("Unable to get folder: " + folder);

            //打开收件箱
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_ONLY);
            }
            //获取邮件消息记录数
            int totalMessages = folder.getMessageCount();
            //判断收件箱是否为空
            if (totalMessages == 0) {
                System.out.println(folder + " is empty");
                folder.close(false);
                store.close();
                return false;
            }

            //设置邮件读取属性规则 
            Message message[] = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add("X-Mailer");
            folder.fetch(message, fp);

            //遍历邮件消息
            for (int i = 0; i < message.length; i++) {
                //delete mail if find appointed mail
                if (mailId.intValue() == message[i].getMessageNumber()) {
                    message[i].isExpunged();
                    message[i].setFlag(Flags.Flag.DELETED, true);
                }

                if (message[i].isSet(Flags.Flag.DELETED))
                    System.out.println(" The mail <" + message[i].getSubject() + "> has been deleted ！");
            }
            //关闭收件箱
            folder.close(true);
            //关闭邮件连接
            store.close();

        } catch (Exception se) {
            System.out.println("drop--Exception :" + se);
            return false;
        }
        return true;
    }

    /**
     * 获取邮件列表
     * @return 邮件ID编号Hash
     */
    public Hashtable getMailList() {
        //采用POP3协议
        String protocol = "pop3";
        //邮件列表集合
        Hashtable mailList;

        try {
            mailList = new Hashtable();
            //获取会话变量
            Session session = Session.getInstance(new Properties(), null);
            //设置会话调试模式
            session.setDebug(false);
            //连接邮件服务器
            Store store = session.getStore(protocol);
            store.connect(mailServer, -1, userName, password);
            //获取收件箱
            Folder folder = store.getFolder("INBOX");
            //判断收件箱是否为NULL
            if (folder == null)
                throw new NullPointerException("Unable to get folder: " + folder);

            //打开收件箱
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_ONLY);
            }

            //获取邮件读取规则属性
            Message message[] = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add("X-Mailer");
            folder.fetch(message, fp);

            //获取邮件消息记录数
            int totalMessages = folder.getMessageCount();
            //没有邮件消息
            if (totalMessages == 0) {
                System.out.println(folder + " is empty");
                folder.close(false);
                store.close();
                return new Hashtable();
            }

            //获取邮件消息标识和标题
            for (int i = 0; i < message.length; i++) {
                Hashtable table1 = new Hashtable();
                Object content = message[i].getContent();

                table1.put("number", new Integer(message[i].getMessageNumber()));
                table1.put("subject", message[i].getSubject());

                mailList.put(new Integer(i), table1);
            }
            //关闭收件箱
            folder.close(true);
            //关闭邮件连接
            store.close();

            return mailList;

        } catch (MessagingException me) {
            System.out.println("getMailList--MessagingException:" + me);
            return new Hashtable();
        } catch (Exception e) {
            System.out.println("getMailList--Exception:" + e);
            return new Hashtable();
        }
    }

    /**
     * 根据邮件ID编号获取邮件
     * @param mailId 邮件ID编号
     */
    public void getMailById(Integer mailId) {
        //采用POP3协议
        String protocol = "pop3";
        try {
            //获取会话变量
            Session session = Session.getInstance(new Properties(), null);
            //设置会话调试模式
            session.setDebug(false);
            //连接邮件服务器
            Store store = session.getStore(protocol);
            store.connect(mailServer, -1, userName, password); //connect mail server
            //获取收件箱
            Folder folder = store.getFolder("INBOX");
            //判断收件箱是否为NULL
            if (folder == null)
                throw new NullPointerException("Unable to get folder: " + folder);

            //打开收件箱
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_ONLY);
            }

            //获取邮件读取规则属性
            Message message[] = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add("X-Mailer");
            folder.fetch(message, fp);

            //按消息记录进行遍历
            for (int i = 0; i < message.length; i++) {
                Object content = message[i].getContent();

                if (mailId.intValue() == message[i].getMessageNumber()) {
                    if (content instanceof Multipart) {
                    } else {
                    }
                }
            }
            //关闭收件箱
            folder.close(true);
            //关闭邮件连接
            store.close();
        } catch (MessagingException me) {
            System.out.println("getMailById--MessagingException :" + me);
        } catch (Exception e) {
            System.out.println("getMailById--Exception :" + e);
        }
    }

    /**
     * 获取邮件附件
     * @return 附件集合对象
     */
    public Collection getMailAttachment() {
        //附件集合对象
        HashSet collection = new HashSet();

        String disposit = "";
        //采用POP3协议
        String protocol = "pop3";

        try {
            //获取会话变量
            Session session = Session.getInstance(new Properties(), null);
            //设置会话调试模式
            session.setDebug(false);
            //连接邮件服务器 
            Store store = session.getStore(protocol);
            store.connect(mailServer, -1, userName, password); //connect mail server
            //获取收件箱
            Folder folder = store.getFolder("INBOX");
            //判断收件箱是否为NULL
            if (folder == null)
                throw new NullPointerException("Unable to get folder: " + folder);

            //打开收件箱
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_ONLY);
            }

            //获取邮件读取规则属性
            Message message[] = folder.getMessages();

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add("X-Mailer");
            folder.fetch(message, fp);

            Multipart multipart = new MimeMultipart();

            //按消息记录进行遍历
            for (int i = 0; i < message.length; i++) {
                Object content = message[i].getContent();
                if (id.intValue() == message[i].getMessageNumber()) {
                    //判断邮件是否包含附件内容
                    if (content instanceof Multipart) {
                        multipart = (Multipart)message[i].getContent();
                        //遍历邮件附件
                        for (int n = 0; n < multipart.getCount(); n++) {
                            Part part = multipart.getBodyPart(n);
                            disposit = part.getDisposition();

                            if (disposit != null) {
                                if (disposit.equals(part.ATTACHMENT)) { //dispose attachment
                                    BufferedInputStream bis = new BufferedInputStream(part.getInputStream());
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                                    int c = bis.read();
                                    while (c != -1) {
                                        out.write(c);
                                        c = bis.read();
                                    }
                                    Hashtable table = new Hashtable();
                                    table.put("filename", part.getFileName());
                                    table.put("bytestream", out.toString());

                                    out.close();
                                    bis.close();

                                    collection.add(table);
                                }
                            }
                        }
                    } else {
                    }
                }
            }
            //关闭收件箱
            folder.close(true);
            //关闭邮件连接
            store.close();
        } catch (MessagingException me) {
            System.out.println("getMailAttachment--MessagingException :" + me);
            return null;
        } catch (IOException ioe) {
            System.out.println("getMailAttachment--IOException " + ioe);
            return null;
        } catch (Exception e) {
            System.out.println("getMailAttachment--Exception " + e);
            return null;
        }
        return collection;
    }

    /**
     * 设置邮件标题
     * @param newSubject 邮件标题
     */
    public void setSubject(String newSubject) {
        subject = newSubject;
    }

    /**
     * 获取邮件标题
     * @retrun 邮件标题
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 设置邮件内容
     * @param newBody 邮件内容
     */
    public void setBody(String newBody) {
        body = newBody;
    }

    /**
     * 获取邮件内容
     * @return 邮件内容
     */
    public String getBody() {
        return body;
    }

    /**
     * 设置邮件ID编号
     * @param newId 邮件ID编号
     */
    public void setId(Integer newId) {
        id = newId;
    }

    /**
     * 获取邮件ID编号
     * @return 邮件ID编号
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置发件人地址
     * @param newFrom 邮件发件人地址
     */
    public void setFrom(String newFrom) {
        from = newFrom;
    }

    /**
     * 获取发件人地址
     * @return 发件人地址
     */
    public String getFrom() {
        return from;
    }

    /**
     * 添加附件内容
     * @param newAttachment 附件集合
     */
    public void setAttachment(java.util.Hashtable newAttachment) {
        attachment = newAttachment;
    }

    /**
     * 获取邮件附件内容
     * @return 附件集合
     */
    public java.util.Hashtable getAttachment() {
        return attachment;
    }

    /**
     * 设置发送用户名
     * @param newUserName 发送用户名
     */
    public void setUserName(String newUserName) {
        userName = newUserName;
    }

    /**
     * 获取发送用户名
     * @return 发送用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置发送用户密码
     * @param newPassword 发送用户密码
     */
    public void setPassword(String newPassword) {
        password = newPassword;
    }

    /**
     * 获取发送用户密码
     * @return 发送用户密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置邮件服务器
     * @param newMailServer 邮件服务器地址
     */
    public void setMailServer(String newMailServer) {
        mailServer = newMailServer;
    }

    /**
     * 获取邮件服务器地址
     * @return 邮件服务器地址
     */
    public String getMailServer() {
        return mailServer;
    }

    /**
     * 设置POP3邮件服务器
     * @param newPop3Server POP3邮件服务器地址
     */
    public void setPop3Server(String newPop3Server) {
        pop3Server = newPop3Server;
    }

    /**
     * 获取POP3邮件服务器地址
     * @return POP3邮件服务器地址
     */
    public String getPop3Server() {
        return pop3Server;
    }

    /**
     * 设置收件人
     * @param newTo 收件人地址
     */
    public void setTo(String[] newTo) {
        to = newTo;
    }

    /**
     * 获取收件人
     * @return 收件人
     */
    public String[] getTo() {
        return to;
    }

    /**
     * 设置发送时间
     * @param newTime 发送时间
     */
    public void setTime(String newTime) {
        time = newTime;
    }

    /**
     * 获取发送时间
     * @return 发送时间
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置邮件内容编码
     * @param tmpContentType 内容编码
     */
    public void setContentType(String tmpContentType) {
        contentType = tmpContentType;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getCc() {
        return cc;
    }

    public void sendmail() throws Exception {
        // 可以从配置文件读取相应的参数
        Properties props = new Properties();


        javax.mail.Session mailSession; // 邮件会话对象
        javax.mail.internet.MimeMessage mimeMsg; // MIME邮件对象

        props = java.lang.System.getProperties(); // 获得系统属性对象
        props.put("mail.smtp.host", mailServer); // 设置SMTP主机
        props.put("mail.smtp.auth", "true"); // 是否到服务器用户名和密码验证
        // 到服务器验证发送的用户名和密码是否正确
        MailUtil_MailAutherticatorbean myEmailAuther = new MailUtil_MailAutherticatorbean(userName, password);
        // 设置邮件会话
        mailSession = javax.mail.Session.getInstance(props, (Authenticator)myEmailAuther);
        // 设置传输协议
        javax.mail.Transport transport = mailSession.getTransport("smtp");
        // 设置from、to等信息
        mimeMsg = new javax.mail.internet.MimeMessage(mailSession);
        if (from != null && !from.equals("")) {
            InternetAddress sentFrom = new InternetAddress(from);
            mimeMsg.setFrom(sentFrom); // 设置发送人地址
        }

        // 收件人
        if (to != null) {
            // 收件人
            InternetAddress[] address = new InternetAddress[to.length];
            for (int i = 0; i < to.length; ++i) {
                address[i] = new InternetAddress(to[i]);
            }
            mimeMsg.setRecipients(javax.mail.internet.MimeMessage.RecipientType.TO, address);
        }

        // 抄送
        if (cc != null) {
            InternetAddress[] address = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; ++i) {
                address[i] = new InternetAddress(cc[i]);
            }
            mimeMsg.setRecipients(javax.mail.internet.MimeMessage.RecipientType.CC, address);
        }


        // 密送
        if (bcc != null) {
            InternetAddress[] address = new InternetAddress[bcc.length];
            for (int i = 0; i < bcc.length; ++i) {
                address[i] = new InternetAddress(bcc[i]);
            }
            mimeMsg.setRecipients(javax.mail.internet.MimeMessage.RecipientType.BCC, address);

        }


        mimeMsg.setSubject(subject, "gb2312");

        MimeBodyPart messageBodyPart1 = new MimeBodyPart();
        messageBodyPart1.setContent(this.body, this.contentType);

        Multipart multipart = new MimeMultipart(); // 附件传输格式
        multipart.addBodyPart(messageBodyPart1);

        if (attachFiles != null) {
            int p = -1;
            for (int i = 0; i < attachFiles.length; i++) {
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                // 选择出每一个附件名
                String attachFile = attachFiles[i];
                p = attachFile.indexOf("|");

                String filename = "";
                String displayname = "";

                if (p == -1) {
                    System.out.println("File.separator: " + File.separator);
                    filename = attachFile;
                    int j = attachFile.lastIndexOf("/") >= 0 ? attachFile.lastIndexOf("/") : attachFile.lastIndexOf("\\");
                    if (j != 1) {
                        displayname = attachFile.substring(j + 1);
                    } else {
                        displayname = attachFile;
                    }
                } else {
                    filename = attachFile.substring(0, p);
                    System.out.println("附件名：" + filename);
                    displayname = attachFile.substring(p + 1);
                    System.out.println("displayname：" + displayname);
                }

                System.out.println("filename: " + filename);
                System.out.println("displayname: " + displayname);

                // 得到数据源
                FileDataSource fds = new FileDataSource(filename);
                // 得到附件本身并设置到BodyPart
                messageBodyPart2.setDataHandler(new DataHandler(fds));
                // 得到文件名同样设置到BodyPart
                messageBodyPart2.setFileName(MimeUtility.encodeText(displayname));
                multipart.addBodyPart(messageBodyPart2);
            }
        }

        mimeMsg.setContent(multipart);
        // 设置信件头的发送日期
        mimeMsg.setSentDate(new Date());
        mimeMsg.saveChanges();

        // 发送邮件
        transport.send(mimeMsg);
        transport.close();
    }


    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setAttachFiles(String[] attachFiles) {
        this.attachFiles = attachFiles;
    }

    public String[] getAttachFiles() {
        return attachFiles;
    }


    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] arg) {
    }


}
