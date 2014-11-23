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
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MailHandlerServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String emailBody = "";
    String fromEmailAddress = "";
    try {
      String noMatchMsgBody = "Hello student,\n"
          + "Unfortunatley the match that you are replying to has been disconnected and you are no longer able to reach your match."
          + "Fortunatley you are able to go back to www.Flybrary.ca to try and find another match for your textbook!"
          + "Have a fantastic day and remember to always fly with flybrary.\n\n"
          + "Regards,\n"
          + "Kisses from Team Flybrary\n\n";

      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());

      MimeMultipart content=(MimeMultipart)message.getContent();

      emailBody = content.getBodyPart(0).getContent().toString();
      fromEmailAddress = ((InternetAddress)message.getFrom()[0]).getAddress().toString();
      String emailSubject = message.getSubject();
      String messageID = message.getMessageID();


      Pattern pattern = Pattern.compile("\\d+/\\d+/\\d+ +\\d+:\\d+:\\d+");
      Matcher matcher = pattern.matcher(emailBody);
      String emailMatchedDate = "";
      while (matcher.find()) {
        int i = 0;
        emailMatchedDate = matcher.group(i);
        i++;
      }

      System.out.println("emailMatchedDate: " + emailMatchedDate);
      System.out.println("emailBody: " + emailBody);
      System.out.println("fromEmailAddress: " + fromEmailAddress);
      System.out.println("messageID: " + messageID);
      System.out.println("emailSubject: " + emailSubject);

      Filter matchFilter = new FilterPredicate("matchDate", FilterOperator.EQUAL, emailMatchedDate);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query q = new Query("Match").setFilter(matchFilter);
      Entity match = datastore.prepare(q).asSingleEntity();

      if(match == null) {
        sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", fromEmailAddress, "Match Does not Exist", noMatchMsgBody+"\n\n Match Date:"+emailMatchedDate);
      } else {
        String firstUsersEmail = match.getProperty("firstUsersEmail").toString();
        String secondUsersEmail = match.getProperty("secondUsersEmail").toString();

        System.out.println("firstUsersEmail: " + firstUsersEmail);
        System.out.println("secondUsersEmail: " + secondUsersEmail);

        if(fromEmailAddress.equals(secondUsersEmail)) {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", firstUsersEmail, emailSubject, emailBody);
        } else if (fromEmailAddress.equals(firstUsersEmail)) {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", secondUsersEmail, emailSubject, emailBody);
        } else {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", fromEmailAddress, "Match Does not Exist", noMatchMsgBody+"\n\n Match Date:"+emailMatchedDate);
        }
      }
    } catch (Exception e) {
        sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", "team.flybrary@gmail.com", "Error Email from User", emailBody + "\n\nThis email was sent as a result from an error from user" + fromEmailAddress);
        System.out.println(e.toString());
        e.printStackTrace();
    }
  }

  public void sendEmail(String fromEmailAddress, String fromUserName, String toEmailAddress, String emailSubject, String emailBody) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    System.out.println("toEmailAddress: " + toEmailAddress);
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
      //msg.setText(emailBody);
      msg.setContent(emailBody, "text/html; charset=utf-8");
      Transport.send(msg);
    } catch (AddressException e) {
        e.printStackTrace();
    } catch (MessagingException e) {
        e.printStackTrace();
    }
  }
}
