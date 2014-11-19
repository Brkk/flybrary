package com.uvic.textshare.service.matching;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.uvic.textshare.service.model.Delay;
import com.uvic.textshare.service.model.LatLonRadius;

public class MatchingFunction {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public String checkForMatch(String isbn, String uid, String type, String title, double condition, String edition) {
		String searchType;
		String firstUsersName;
		String secondUsersName;
		String firstUsersEmail;
		String secondUsersEmail;
		Boolean withinRadius = false;
		Date matchDate = null;

		if(type.equals("offer")) {
			 searchType = "request";
		} else {
			searchType = "offer";
		}

		List<Entity> textbooks = getTextbooks(searchType, isbn, condition, edition);

		if(!textbooks.isEmpty())
		{

			Entity user = getUser(uid);
			LatLonRadius first = new LatLonRadius((Double)user.getProperty("lat"),
					(Double)user.getProperty("lon"),
					(Double)user.getProperty("radius"));

			firstUsersName = String.valueOf(user.getProperty("name"));
			firstUsersEmail = String.valueOf(user.getProperty("email"));

			for(int i = 0; i < textbooks.size(); i++) {
				Entity matchedBook = textbooks.get(i);
				LatLonRadius second = new LatLonRadius((Double)matchedBook.getProperty("lat"),
						(Double)matchedBook.getProperty("lon"),
						(Double)matchedBook.getProperty("radius"));
				
				String uid2 = (String) matchedBook.getProperty("uid");
				withinRadius = distance(first, second, type);

				if(withinRadius && !uid.equals(uid2)) {
					matchDate = new Date();

					user = getUser(uid2);
					secondUsersName = String.valueOf(user.getProperty("name"));
					secondUsersEmail = String.valueOf(user.getProperty("email"));
					
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String simpleDate = df.format(matchDate);

					Delay.oneSecondDelay();
					Entity match = new Entity("Match");
						match.setProperty("matchDate", simpleDate);
						match.setUnindexedProperty("userOneId", uid);
						match.setUnindexedProperty("userTwoId", uid2);
						match.setUnindexedProperty("firstUsersEmail", firstUsersEmail);
						match.setUnindexedProperty("secondUsersEmail", secondUsersEmail);
					datastore.put(match);

					sendEmailToUser(firstUsersName, firstUsersEmail, secondUsersName, secondUsersEmail, title, simpleDate);
					sendEmailToUser(secondUsersName, secondUsersEmail, firstUsersName, firstUsersEmail, title, simpleDate);

					Delay.oneSecondDelay();
					matchedBook.setProperty("matched", "yes");
					matchedBook.setUnindexedProperty("matchDate", simpleDate);
					datastore.put(matchedBook);

					return "yes-" + simpleDate;
				}
			}
		}
		return "no";
	}

	/*
	 * Input: Both users names, email addresses and the title of the matched book.
	 * Output: Email sent to both users from "team.texshare@gmail.com"
	 */
	private void sendEmailToUser(String receiverName, String receiverEmail, String matchedUserName, String matchedUserEmail, String title, String matchDate) {
			// user here would be the Name of the user.
		    Properties props = new Properties();
		    Session session = Session.getDefaultInstance(props, null);

		    //Create the mail body and send it to both of the users from team.textshare@gmail.com
		    String msgBody = "Hello fellow student,\n"
		    		+ "Isn't this a lucky day for ya. Remember that time you used flybrary for " + title + ". Well, we found "
		    		+ "you match. You can leave whatever you are doing and reach your match "
		    		+ matchedUserName + " by replying to this email . Have a fantastic day and remember to always fly with flybrary.\n\n"
		    		+ "Cheers,\n"
		    		+ "Team Flybrary\n\n<MATCH_DATE>"
						+ matchDate
						+"<MATCH_DATE>";

		    try {
		        Message msg = new MimeMessage(session);
		        try {
					msg.setFrom(new InternetAddress("email@textchngr.appspotmail.com", "Team Flybrary"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace(); //log these errors
				}
		        try {
					msg.addRecipient(Message.RecipientType.TO,
					                 new InternetAddress(receiverEmail, receiverName));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace(); //log these errors
				}
		        msg.setSubject(title + " got a match, don't forget to check it eh!");
		        msg.setText(msgBody);
		        Transport.send(msg);

		    } catch (AddressException e) {
		        // ...
		    } catch (MessagingException e) {
		        // ...
		    }
	}

	//Calculates the distance between two co-ordinates and compares it to the given radiuses
	private boolean distance(LatLonRadius first, LatLonRadius second, String type) {
		double lon1 = first.getLon();
		double lat1 = first.getLat();
		double radius1 = first.getRadius();
		double lon2 = second.getLon();
		double lat2 = second.getLat();
		double radius2 = second.getRadius();

		double theta = lon1 - lon2;
	  	double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	  	dist = Math.acos(dist);
	  	dist = rad2deg(dist);
	  	dist = dist * 60 * 1.1515;
	  	dist = dist * 1.609344;

	  	if(type.equals("offer") && dist <= radius2) {
	  		return true;
	  	}
	  	else if(dist <= radius1 && dist <= radius2){
	  		return true;
	  	}
	  	else
	  		return false;
	}

	//Converts degrees to radiant
	private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

	//Converts radiant to degrees
	private double rad2deg(double rad) {
	  return (rad * 180 / Math.PI);
	}

	private Entity getUser(String uid) {
		Filter uidFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
		Query query = new Query("User").setFilter(uidFilter);
		Entity user = datastore.prepare(query).asSingleEntity();
		return user;
	}

	private List<Entity> getTextbooks(String searchType, String isbn, double condition, String edition) {
		Filter typeFilter = new FilterPredicate("type", FilterOperator.EQUAL, searchType);
		Filter isbnFilter = new FilterPredicate("isbn", FilterOperator.EQUAL, isbn);
		Filter matchedFilter = new FilterPredicate("matched", FilterOperator.EQUAL, "no");
		Filter conditionFilter = new FilterPredicate("condition", FilterOperator.EQUAL, condition);
		Filter editionFilter = new FilterPredicate("edition", FilterOperator.EQUAL, edition);
		Filter searchFilter = CompositeFilterOperator.and(typeFilter, isbnFilter, matchedFilter, conditionFilter, editionFilter);
		//any condition
		if(condition == 4.0 ) {
			searchFilter = CompositeFilterOperator.and(typeFilter, isbnFilter, matchedFilter);
		}
		
		Query q = new Query("Textbook").setFilter(searchFilter).addSort("date", Query.SortDirection.ASCENDING);
		List<Entity> textbooks = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		return textbooks;
	}
}
