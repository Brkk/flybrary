package com.uvic.textshare.service.rest;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.io.UnsupportedEncodingException;
import java.lang.String;

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


@SuppressWarnings("unused")
@Path("/")
public class TextbookResource {
	
	private static int numberOf_offered_books;
	private static int numberOf_requested_books;
	private static int numberOf_matches;
	MatchingFunction matchingFunction = new MatchingFunction();
	
/*
 * 
 * Start of REST Methods
 * 
 */
	 @POST
	 @Path("/retrieve") 
	 @Consumes(MediaType.APPLICATION_JSON)
	 @Produces(MediaType.APPLICATION_JSON)
	 public String getTextbook(String input) {
		try {
			//Create a filter for retrieving all the books associated with that user
			JSONObject obj = new JSONObject(input);
			String user_id = obj.getString("uid");
			Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, user_id);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			 
			Query q = new Query("User").setFilter(userFilter);
			Entity user = datastore.prepare(q).asSingleEntity();
			 
			if(user == null && !user_id.equals("")) {
				createUser(obj);
			 	return "[]";
			} 
			else
			{	
				Query q2 = new Query("Textbook").setFilter(userFilter);
			 	PreparedQuery pd2 = datastore.prepare(q2);
			 	List<Entity> textbooks = pd2.asList(FetchOptions.Builder.withDefaults());
			 	String json = new Gson().toJson(textbooks);
			 	return json;
			}
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			return "[]"; //something went wrong
		}
	}
	 
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTextbook(String input) throws ParseException {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			JSONObject text = new JSONObject(input);
			Date addDate = new Date();
			String matchDate = null; 
			String response = matchingFunction.checkForMatch(
					text.getString("isbn"), 
					text.getString("uid"),
					text.getString("type"),
					text.getString("title"),
					text.getDouble("condition"),
					text.getString("edition"));
			 
			
			String[] parts = response.split("-");
			String matched = parts[0];
			
			if(matched.equals("yes")) {
				matchDate = parts[1];
				numberOf_matches++;
			}
			
			double radius = text.getDouble("radius");
			radius = radius / 1000.0;
			if(radius > 30 || radius < 5) {
				radius = 15.0;
			}
	 
			Entity textbook = new Entity("Textbook");
			    textbook.setProperty("uid", text.getString("uid"));
			    textbook.setProperty("type", text.getString("type"));
			    textbook.setUnindexedProperty("title", text.getString("title"));
			    textbook.setUnindexedProperty("author", text.getString("author"));
			    textbook.setProperty("isbn", text.getString("isbn"));
			    textbook.setProperty("edition", text.getString("edition"));
			    textbook.setProperty("condition", text.getDouble("condition"));
			    textbook.setProperty("date", addDate);	 
			    textbook.setUnindexedProperty("matchDate", matchDate);
			    textbook.setProperty("matched", matched);
			    textbook.setUnindexedProperty("image", text.getString("image"));
			    textbook.setUnindexedProperty("lat", text.getDouble("lat"));
			    textbook.setUnindexedProperty("lon", text.getDouble("lon"));
			    textbook.setUnindexedProperty("radius", radius);
			datastore.put(textbook);
	
			String bookOwner = text.getString("uid");
			String typeOfEntry = text.getString("type");
			updateUserKarma(bookOwner, typeOfEntry, 5);
			
			if(typeOfEntry.equals("offer"))
				TextbookResource.numberOf_offered_books++;
			else
				TextbookResource.numberOf_requested_books++;
			
			String json = new Gson().toJson(textbook);
			return json;
		} catch(NullPointerException e) {
			e.printStackTrace();
			return "{}"; //something went wrong!
		}
	}
	
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTextbook(String input) throws JSONException {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			JSONObject obj = new JSONObject(input);
			Long id = (long) obj.getDouble("id");
			Key textbookKey = KeyFactory.createKey("Textbook", id);
			datastore.delete(textbookKey);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}	 
	 }
	
	@POST
	@Path("/updateUserRadius")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateUserRadius(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			JSONObject obj = new JSONObject(input);
			String uid = obj.getString("uid");
			double radius = obj.getDouble("radius");
			radius = radius / 1000.0;
			if(radius > 30 || radius < 5) {
				radius = 15.0;
			}
			Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
			Query q = new Query("User").setFilter(userFilter);
			Entity user = datastore.prepare(q).asSingleEntity();
				user.setUnindexedProperty("radius", radius);
			datastore.put(user);
			
			q = new Query("Textbook").setFilter(userFilter);
			List<Entity> textbooks = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults()); 
			
			if(!textbooks.isEmpty()) {
				for(int i = 0; i < textbooks.size(); i++) {
					Delay.oneSecondDelay();
					Entity textbook = textbooks.get(i);
						textbook.setUnindexedProperty("radius", radius);
					datastore.put(textbook);
				}
			}
			
		} catch(NullPointerException e) {
			e.printStackTrace();
		}	
	}

	@POST
	@Path("/updateUserLocation")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateUserLocation(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			JSONObject obj = new JSONObject(input);
			String uid = obj.getString("uid");
			double lat = obj.getDouble("lat");
			double lon = obj.getDouble("lon");
	
			Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
			Query q = new Query("User").setFilter(userFilter);
			Entity user = datastore.prepare(q).asSingleEntity();
				user.setUnindexedProperty("lat", lat);
				user.setUnindexedProperty("lon", lon);
			datastore.put(user);
			
			q = new Query("Textbook").setFilter(userFilter);
			List<Entity> textbooks = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults()); 
			
			if(!textbooks.isEmpty()) {
				for(int i = 0; i < textbooks.size(); i++) {
					Delay.oneSecondDelay();
					Entity textbook = textbooks.get(i);
						textbook.setUnindexedProperty("lat", lat);
						textbook.setUnindexedProperty("lon", lon);
					datastore.put(textbook);
				}
			}
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/unmatchTextbook")
	@Consumes(MediaType.APPLICATION_JSON)
 	public void unmatchTextbook(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			JSONObject obj = new JSONObject(input);
			Long id = (long) obj.getDouble("id");
			String title = obj.getString("title");
			String isbn = obj.getString("isbn");
			String type = obj.getString("type");
			String uid = obj.getString("uid");
			String matchDate = obj.getString("matchDate");
			double condition = obj.getDouble("condition");
			String edition = obj.getString("edition");
			String uidTwo;
			String typeTwo;
			
			if(type.equals("offer"))
				typeTwo = "request";
			else
				typeTwo = "offer";
			
			//Delete the match entry and retrieve other user's UID.
			Filter matchFilter = new FilterPredicate("matchDate", FilterOperator.EQUAL, matchDate);
			Query q = new Query("Match").setFilter(matchFilter);
			Entity match = datastore.prepare(q).asSingleEntity();
			
			if(!uid.equals((String) match.getProperty("userOneId")))
				uidTwo = (String) match.getProperty("userOneId");
			else
				uidTwo = (String) match.getProperty("userTwoId");
			
			Key matchKey = match.getKey();
			datastore.delete(matchKey);
			Delay.oneSecondDelay();
			
			//Check for a new match. Omit matching each other again.
			String matchedOne = matchingFunction.checkForMatch(isbn, uid, type, title, condition, edition);
			String matchedTwo = matchingFunction.checkForMatch(isbn, uidTwo, typeTwo, title, condition, edition);
			
			//Update database based on the result of new searches for matches.
			if(matchedOne.equals("no")) { 
				Key textbookKey = KeyFactory.createKey("Textbook", id);
				Query q2 = new Query(textbookKey);
				Entity textbook = datastore.prepare(q2).asSingleEntity();
					textbook.setProperty("matched", "no");
					textbook.setUnindexedProperty("matchDate", null);
				datastore.put(textbook);
			}
			
			Delay.oneSecondDelay();
			if(matchedTwo.equals("no")) {
				Filter isbnFilter = new FilterPredicate("isbn", FilterOperator.EQUAL,isbn);
				Filter uidFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uidTwo);
				Filter searchFilter = CompositeFilterOperator.and(isbnFilter, uidFilter);
				
				q = new Query("Textbook").setFilter(searchFilter);
				Entity textbookTwo = datastore.prepare(q).asSingleEntity();
					textbookTwo.setProperty("matched", "no");
					textbookTwo.setUnindexedProperty("matchDate", null);
				datastore.put(textbookTwo);
			}
		} catch(NullPointerException e) {
		e.printStackTrace();
		}
	}
	
	@POST
	@Path("/completeMatch")
	@Consumes(MediaType.APPLICATION_JSON)
 	public void completeMatch(String input) {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			JSONObject obj = new JSONObject(input);
			Long idOfInitiator = (long) obj.getDouble("ididOfInitiator");
			Long idOfmatch = (long) obj.getDouble("ididOfmatch");
			String uidOfInitiator = obj.getString("uidOfInitiator");
			String uidOfmatch = obj.getString("uidOfmatch");
			String matchDate = obj.getString("matchDate");
			String type = obj.getString("type");
	
		
			//Delete the match entry
			Filter matchFilter = new FilterPredicate("matchDate", FilterOperator.EQUAL, matchDate);
			Query q = new Query("Match").setFilter(matchFilter);
			Entity match = datastore.prepare(q).asSingleEntity();
			Key matchKey = match.getKey();
			datastore.delete(matchKey);
			Delay.oneSecondDelay();
			
			//Delete Books
			Key textbookKey = KeyFactory.createKey("Textbook", idOfInitiator);
			datastore.delete(textbookKey);
			textbookKey = KeyFactory.createKey("Textbook", idOfmatch);
			datastore.delete(textbookKey);
			
			if(type.equals("offer")) {
				//give user karma
				//reduce other guy
			}
			else
			{
				//reduce user karma
				//increase other guy
			}
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/getUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getUser(String input) {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			JSONObject obj = new JSONObject(input);
			String uid = obj.getString("uid");
	
			Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
			Query q = new Query("User").setFilter(userFilter);
			Entity user = datastore.prepare(q).asSingleEntity();
			if(user == null)
				return "{}";
			
			String json = new Gson().toJson(user);
			return json;
		} catch(NullPointerException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@GET
	@Path("/getLastFiveBooks")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLastFiveBooks() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Query query = new Query("Textbook").addSort("date", Query.SortDirection.DESCENDING);
	    List<Entity> textbooks = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
	 	String json = new Gson().toJson(textbooks);
	 	return json;
	}
	
	@GET
	@Path("/getStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatistics() {
		Statistics stats = new Statistics(numberOf_offered_books, numberOf_requested_books, numberOf_matches);
		String json = new Gson().toJson(stats);
		return json;
	}

/* 
 * 
 * End of REST Methods 
 * 
 */

/*
 * 
 * Start of Support Methods
 * 	
 */
	
	private void createUser(JSONObject obj) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Entity user = new Entity("User");
	 		user.setUnindexedProperty("name", obj.getString("name"));
	 		user.setProperty("uid", obj.getString("uid"));
	 		user.setUnindexedProperty("email", obj.getString("email"));
			//user.setUnindexedProperty("address", obj.getString("location"));
			user.setUnindexedProperty("lat", obj.getDouble("lat"));
			user.setUnindexedProperty("lon", obj.getDouble("lon"));
			user.setUnindexedProperty("request_karma", 0);
			user.setUnindexedProperty("offer_karma", 0);
			user.setUnindexedProperty("radius", 15.0); //default value for radius.
		datastore.put(user);
	}

	private void updateUserKarma(String uid, String type, int points) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
		Query q = new Query("User").setFilter(userFilter);
		Entity user = datastore.prepare(q).asSingleEntity();
		int updatedKarma;

		if(type.equals("offer"))
		{
			updatedKarma = Integer.parseInt(user.getProperty("offer_karma").toString());
			updatedKarma += 5;
			user.setUnindexedProperty("offer_karma", updatedKarma);
		}
		else
		{
			updatedKarma = Integer.parseInt(user.getProperty("request_karma").toString());
			updatedKarma += 5;
			user.setUnindexedProperty("request_karma", updatedKarma);
		}
		
		datastore.put(user);
	}

/*
 * 
 * End of Support Methods
 * 
 */
}
