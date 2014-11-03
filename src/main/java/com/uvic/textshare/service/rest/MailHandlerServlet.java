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
    //email@textchngr.appspotmail.com
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());

      MimeMultipart content=(MimeMultipart)message.getContent();

      String contentString = content.getBodyPart(0).getContent().toString();
      String[] matchDateTokens = contentString.split("<MATCH_DATE>");
      String emailMatchedDate = matchDateTokens[1];
      String fromEmail = ((InternetAddress)message.getFrom()[0]).getAddress().toString();
      String messageID = message.getMessageID();

      System.out.println("MATCHDATE: " + emailMatchedDate);
      System.out.println("Message content: " + contentString);
      System.out.println("Message from: " + fromEmail);
      System.out.println("Message id: " + messageID);

      Filter matchFilter = new FilterPredicate("matchDate", FilterOperator.EQUAL, emailMatchedDate);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query q = new Query("Match").setFilter(matchFilter);
      Entity match = datastore.prepare(q).asSingleEntity();

      if(match == null) {
        System.out.println("NO MATCH");
      } else {
        String email1 = match.getProperty("firsUsersEmail").toString();
        String email2 = match.getProperty("secondUsersEmail").toString();
        System.out.println("firsUsersEmail: " + email1);
        System.out.println("secondUsersEmail: " + email2);

        String toEmail = "";

        if(fromEmail.equals(email2)) {
          toEmail = email1;
        } else {
          toEmail = email2;
        }

        System.out.println("toEmail: " + toEmail);

        Properties propsEmailOut = new Properties();
        Session sessionEmailOut = Session.getDefaultInstance(propsEmailOut, null);

        //Create the mail body and send it to both of the users from team.textshare@gmail.com
        String msgBody = contentString;

        try {
            Message msg = new MimeMessage(sessionEmailOut);
            try {
          msg.setFrom(new InternetAddress("email@textchngr.appspotmail.com", "Team Flybrary"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace(); //log these errors
        }
            try {
          msg.addRecipient(Message.RecipientType.TO,
                           new InternetAddress(toEmail, "Receiver Name"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace(); //log these errors
        }
            msg.setSubject(" got a match, don't forget to check it eh");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
      }

    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
