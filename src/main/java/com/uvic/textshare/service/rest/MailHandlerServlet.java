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
import java.util.Date;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.uvic.textshare.service.matching.MatchingFunction;
import com.uvic.textshare.service.model.*;
import com.google.appengine.api.users.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;


public class MailHandlerServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String noMatchMsgBody = "Hello student,\n"
        + "Unfortunatley the match that you are replying to has been disconnected and you are no longer able to reach your match."
        + "Fortunatley you are able to go back to the app to try and find another match for your textbook!"
        + "Have a fantastic day and remember to always fly with flybrary.\n\n"
        + "Regards,\n"
        + "Kisses from Team Flybrary\n\n<MATCH_DATE>";
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());

      MimeMultipart content=(MimeMultipart)message.getContent();

      String emailBody = content.getBodyPart(0).getContent().toString();
      String[] matchDateTokens = emailBody.split("<MATCH_DATE>");
      String emailMatchedDate = matchDateTokens[1];
      String fromEmailAddress = ((InternetAddress)message.getFrom()[0]).getAddress().toString();
      String messageID = message.getMessageID();

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
