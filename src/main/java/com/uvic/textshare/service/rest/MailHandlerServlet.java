package com.uvic.textshare.service.rest;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.io.UnsupportedEncodingException;


@SuppressWarnings("serial")
public class MailHandlerServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String noMatchMsgBody = "Hello student,\n"
        + "Unfortunatley the match that you are replying to has been disconnected and you are no longer able to reach your match."
        + "Fortunatley we have already started to look for an another match for your textbook! We will get back to you, "
        + "when we find one."
        + "Have a fantastic day and remember to always fly with flybrary.\n\n"
        + "Cheers,\n"
        + "Team Flybrary\n\n<MATCH_DATE>";
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());

      MimeMultipart content=(MimeMultipart)message.getContent();

      String emailBody = content.getBodyPart(0).getContent().toString();
      String[] matchDateTokens = emailBody.split("<MATCH_DATE>");
      String emailMatchedDate = matchDateTokens[1];
      String fromEmailAddress = ((InternetAddress)message.getFrom()[0]).getAddress().toString();

      Filter matchFilter = new FilterPredicate("matchDate", FilterOperator.EQUAL, emailMatchedDate);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query q = new Query("Match").setFilter(matchFilter);
      Entity match = datastore.prepare(q).asSingleEntity();

      if(match == null) {
        sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", fromEmailAddress, "Match Does not Exist", noMatchMsgBody);
      } else {
        String firstUsersEmail = match.getProperty("firstUsersEmail").toString();
        String secondUsersEmail = match.getProperty("secondUsersEmail").toString();

        if(fromEmailAddress.equals(secondUsersEmail)) {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", firstUsersEmail, "", emailBody);
        } else if (fromEmailAddress.equals(firstUsersEmail)) {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", secondUsersEmail, "", emailBody);
        } else {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", fromEmailAddress, "Match Does not Exist", noMatchMsgBody);
        }
      }
    } catch (Exception e) {
        sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", "team.textshare@gmail.com", "Email from User", noMatchMsgBody);
        e.printStackTrace();
    }
  }

  public void sendEmail(String fromEmailAddress, String fromUserName, String toEmailAddress, String emailSubject, String emailBody) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    try {
      Message msg = new MimeMessage(session);
      try {
        msg.setFrom(new InternetAddress(fromEmailAddress, fromUserName));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace(); //log these errors
      }

      try {
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress, "Receiver Name"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace(); //log these errors
      }
      msg.setSubject(emailSubject);
      msg.setText(emailBody);
      Transport.send(msg);
    } catch (AddressException e) {
        e.printStackTrace();
    } catch (MessagingException e) {
        e.printStackTrace();
    }
  }
}
