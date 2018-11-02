package net.payease.monitor.notifiers;

public class SendMail {

    public static int STYLE_TEXT = 1;
    public static int STYLE_HTML = 2;
    public static int STYLE_TEXT_EN = 3;
    public static int STYLE_HTML_EN = 4;    

    
    private String   mailServerHost = "mail.163.com"; 
    private String   mailFrom       = "showwair@163.com"; 
    private String   mailFromUser   = "showwair@163.com";
    private String   mailFromUserPw = "password";
    private int      mailBodyStyle  = STYLE_TEXT;
    
    public String[] mailToArray    ={"showwair@163.com"};
    public String   mailSubject    = ""; 
    public String   mailBody       = ""; 


    
    public SendMail(){
    }
      
      
   /**
    * 发送邮件
    */
    public void sendMail()throws Exception{ 
        MailUtil mail = new  MailUtil(mailFromUser,mailFromUserPw,mailServerHost);
        if(mailBodyStyle==STYLE_HTML){
          mail.setContentType("text/html; charset=GBK"); 
        }else if(mailBodyStyle==STYLE_TEXT){
            mail.setContentType("text/plain; charset=GBK");                  
        }else if(mailBodyStyle==STYLE_HTML_EN){
            mail.setContentType("text/html; charset=ISO-8859-1");
        }else if(mailBodyStyle==STYLE_TEXT_EN){
            mail.setContentType("text/plain; charset=ISO-8859-1");                  
        }else{
          mail.setContentType("text/plain; charset=GBK");          
        }
        mail.setSubject(mailSubject);
        mail.setBody(mailBody);
        mail.setTo(mailToArray);
        mail.setFrom(mailFrom);
        mail.sendWithoutAttachment();
    } 
      

      
      /**
       * 
       * @param args
       */
      public static void main(String[] args)
      {
          try{
              SendMail sendMail = new SendMail();
              sendMail.mailBody="test";
              sendMail.mailSubject="test";
              sendMail.sendMail();
          }catch(Exception e){
              System.out.println(e);
          }

      }

  public void setMailBodyStyle(int mailBodyStyle) {
    this.mailBodyStyle = mailBodyStyle;
  }

  public int getMailBodyStyle() {
    return mailBodyStyle;
  }
}
