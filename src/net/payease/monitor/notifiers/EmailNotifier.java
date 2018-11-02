package net.payease.monitor.notifiers;

import org.apache.log4j.Logger;

import com.capinfo.common.util.PageUtil;


public class EmailNotifier {

    private static Logger logger = Logger.getLogger(EmailNotifier.class);


    public EmailNotifier() {
    }

    public void mail(String mailTo, String subject, String content) {
        try {
        	logger.info( "mailSubject: " + subject);
            SendMail sendMail = new SendMail();
            sendMail.setMailBodyStyle(SendMail.STYLE_HTML);
            sendMail.mailSubject = subject;
            sendMail.mailBody = content;
            String[] mailToArray = PageUtil.splitStrToArray(mailTo, ",");
            sendMail.mailToArray = mailToArray;
            sendMail.sendMail();
            logger.info("send mail ok.");
        } catch (Exception e) {
        	logger.error("发送邮件通知异常.", e);
            e.printStackTrace();
        }
    }

}
