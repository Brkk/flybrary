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
					matchedBook.setProperty("matchDate", simpleDate);
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
		    String msgBody = "<html>"
+"<head></head>"
+"<body style='font-size: 16pt; background-color: rgba(160, 160, 160, 0.2); font-family: 'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', sans-serif; color: #000; line-height: 1.2; font-weight: 200; margin: 20px 0 10px; padding: 30px;' bgcolor='rgba(160, 160, 160, 0.2)'>"

+"<div class='flex-container' style='height: 100%; display: flex; align-items: center; justify-content: center; margin: 0; padding: 0;'>"
+"<div class='wrapper' style='-webkit-flex-flow: row wrap; flex-flow: row wrap; text-align: center; background-color: white; max-width: 800px;' align='center'>"
+ " <header class='header' style='flex: 1 100%; font-size: 36px; line-height: 1.2; color: #000; font-weight: 200; margin: 20px 0 10px; padding: 10px;'><p style='font-size: 16pt; text-align: left;' align='left'>Congratulations,</p>"
+"<p>Isn't this a lucky day for ya. Remember that time you used flybrary for \"" + title + "\". Well, we found "
+ "you a match. You can leave whatever you are doing and reach "+matchedUserName+" to work out the details. </p>"
+  "</header><article class='main' style='flex: 1 100%; text-align: left; margin-left: 30px; padding: 10px;'><!-- <aside class='aside aside-1'> -->"
/*
//Commented out the Progress bar
+"<p>Your match has</p>"
+  "<p>"
+		"</p>"
+"<div class='progress' style='font-weight: bold; height: 35px; margin-bottom: 20px; overflow: hidden; background-color: #f5f5f5; border-radius: 4px; -webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, .1); box-shadow: inset 0 1px 2px rgba(0, 0, 0, .1);'>"
+		  "<div class='progress-bar progress-bar-success' style='width: 50%; float: left; height: 100%; font-size: 18px; line-height: 20px; color: white; text-align: center; -webkit-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, .15); box-shadow: inset 0 -1px 0 rgba(0, 0, 0, .15);"
+      "-webkit-transition: width .6s ease; transition: width .6s ease; font-family: Roboto; background-color: #5cb85c;' align='center'>"
+"        <span>5 Books Offered</span>"
+"      </div>"
+		  "<div class='progress-bar progress-bar-danger' style='width: 50%; float: left; height: 100%; font-size: 18px; line-height: 20px; color: white; text-align: center; -webkit-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, .15); box-shadow: inset 0 -1px 0 rgba(0, 0, 0, .15); "
+      "-webkit-transition: width .6s ease; transition: width .6s ease; font-family: Roboto; background-color: #d9534f;' align='center'>"
+"        <span>5 Books Requested</span>"
+"      </div>"
+"    </div>"
*/
+  		"<p>To contact your match simply reply to this email. Flybrary will keep your contact information confidential.</p>"
+  		"<p>Go back to Flybary and let us know how your match went.</p>"
+		"<table cellspacing='0' cellpadding='0' align='center'> <tr>"
+		"<td align='center' width='300' height='40' bgcolor='3f51b5' style='-webkit-border-radius: 5px; -moz-border-radius: 5px; border-radius: 5px; color: #ffffff; display: block;'>"
+		"<a href='http://www.flybrary.ca/' style='font-size:16px; font-weight: bold; font-family: Helvetica, Arial, sans-serif; text-decoration: none; line-height:40px; width:100%; display:inline-block'><span style='color: #FFFFFF'>Take me to Flybrary</span></a>"
+		"</td> "
+		"</tr> </table> "
/*
+  		"<div class='button' style='font-size: 12pt; display: inline-block; position: relative; overflow: hidden; color: black; height: 3em; line-height: 3em; vertical-align: middle; text-transform: uppercase; font-weight: bold; cursor: pointer; -webkit-font-smoothing: antialiased; "
+      "-moz-osx-font-smoothing: grayscale; padding: 0 2em; border: 2px solid black;'>Rematch Me!</div>"
+  	"<p>Please let us now if the trade went well,</p>"
+  		"<div class='button' "
+      "style='font-size: 12pt; display: inline-block; position: relative; overflow: hidden; color: black; height: 3em; line-height: 3em; vertical-align: middle; text-transform: uppercase; font-weight: bold; cursor: pointer; -webkit-font-smoothing: antialiased; "
+      "-moz-osx-font-smoothing: grayscale; padding: 0 2em; border: 2px solid black;'>"
+      "It Flew!</div>"
*/
+  	"</article><footer class='footer' style='flex: 1 100%; padding: 10px;'><p style='text-align: center; font-size: 12pt; margin-top: 40px;' align='center'><p>Have a fantastic day and remember to always let your textbooks fly with Flybrary.</p><p></p><p></p>"
+ "<p>Cheers,</p>"
+ "<p>Team Flybrary</p>"
+"<p>"
+"</p>"
+"</footer>"
+"</div>"
+"</div>"
			+"<p style='font-color: rgba(160, 160, 160, 0.2);'>Match Date: "
                        + matchDate
            +"</p>"

+"</body>"
+"</html>";

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
		        msg.setContent(msgBody, "text/html; charset=utf-8");
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
		
		Query q = new Query("Textbook").setFilter(searchFilter);
		List<Entity> textbooks = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		return textbooks;
	}
}