package com.uvic.textshare.service.rest;

import java.io.IOException;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
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
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
          + "Cheers,\n"
          + "Team Flybrary\n\n";

      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());

      Object msgContent = message.getContent();  

      fromEmailAddress = ((InternetAddress)message.getFrom()[0]).getAddress().toString();
      emailSubject = message.getSubject();
      messageID = message.getMessageID();

      Boolean emailHasAttachment = false;
      Object content = new Object();
      /* Check if content is pure text/html or in parts */                     
      if (msgContent instanceof Multipart) {
        Multipart multipart = (Multipart) msgContent;

        for (int i = 0; i < multipart.getCount(); i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          String disposition = bodyPart.getDisposition();

          if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { 
            emailHasAttachment = true;
            break;                           
          }
          else { 
            content = bodyPart.getContent();
            try{
            } catch(Exception e) {
              e.printStackTrace();
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

        Filter matchFilter = new FilterPredicate("matchDate", FilterOperator.EQUAL, emailMatchedDate);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Match").setFilter(matchFilter);
        Entity match = datastore.prepare(q).asSingleEntity();

        if(match == null) {
          sendEmail("email@textchngr.appspotmail.com", "Team Flybrary", fromEmailAddress, "Match Does not Exist", noMatchMsgBody+"\n\n Match Date:"+emailMatchedDate);
        } else {
          String firstUsersEmail = match.getProperty("firstUsersEmail").toString();
          String secondUsersEmail = match.getProperty("secondUsersEmail").toString();

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
