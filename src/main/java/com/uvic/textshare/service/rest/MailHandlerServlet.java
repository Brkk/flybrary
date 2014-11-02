package com.uvic.textshare.service.rest;

import java.io.IOException;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.*;

public class MailHandlerServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    //email@textchngr.appspotmail.com
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());
      //System.out.println("Message from: " + message.getFrom().toString());
      //System.out.println("test");
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
