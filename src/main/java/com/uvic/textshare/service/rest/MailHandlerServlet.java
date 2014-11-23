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
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.activation.DataHandler;
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
    String emailSubject ="";
    String messageID;
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

      Object msgContent = message.getContent();  

      fromEmailAddress = ((InternetAddress)message.getFrom()[0]).getAddress().toString();
      emailSubject = message.getSubject();
      messageID = message.getMessageID();

      System.out.println("fromEmailAddress: " + fromEmailAddress);
      System.out.println("messageID: " + messageID);
      System.out.println("emailSubject: " + emailSubject);

      Boolean emailHasAttachment = false;
      Object content = new Object();
      /* Check if content is pure text/html or in parts */                     
      if (msgContent instanceof Multipart) {
        Multipart multipart = (Multipart) msgContent;
        System.out.println("MultiPartCount: " + multipart.getCount());

        for (int i = 0; i < multipart.getCount(); i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          String disposition = bodyPart.getDisposition();
          System.out.println("Disposition: " + disposition);

          if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { 
            System.out.println("This email has an attachment");
            emailHasAttachment = true;
            break;
            //DataHandler handler = bodyPart.getDataHandler();
            //System.out.println("file name : " + bodyPart.getFileName());
            //System.out.println("handler.getContent() : " + handler.getContent());
            //System.out.println("file name : " + bodyPart.getFileName());                            
          }
          else { 
            content = bodyPart.getContent();
            try{
              System.out.println("content in loop:" + (String) content); 
            } catch(Exception e) {
              System.out.println("Error when parsing emailBody to string Error StackTrace: " + e.toString());
            }
          }
        }
      }
      else {              
        content= message.toString();
      }

      if(emailHasAttachment) {
        sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", fromEmailAddress, "Error Attachment Found","Hi there,\n\nUnfortunatley Flybrary does not support sending attachments on emails through our servers. If you want to send an attachment ask for your matches email address and send it as a normal email.\n\nTeam Flybrary");
      } else {
        emailBody =(String) content;
        Pattern pattern = Pattern.compile("\\d+/\\d+/\\d+ +\\d+:\\d+:\\d+");
        Matcher matcher = pattern.matcher(emailBody);
        String emailMatchedDate = "";
        while (matcher.find()) {
          int i = 0;
          emailMatchedDate = matcher.group(i);
          i++;
        }

        System.out.println("emailBody: " + emailBody);
        System.out.println("emailMatchedDate: " + emailMatchedDate);

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
      }
    } catch (Exception e) {
        sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", "team.flybrary@gmail.com", "Error Email from User", emailBody + "\n\nThis email was sent as a result from an error from user \"" + fromEmailAddress +"\" with message subject \"" + emailSubject +"\".\n\nError StackTrace: " + e.toString() );
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
