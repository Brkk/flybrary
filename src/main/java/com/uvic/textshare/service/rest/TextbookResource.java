package com.uvic.textshare.service.rest;

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
	 
	 @POST
	 @Path("/retrieve") 
	 @Consumes(MediaType.APPLICATION_JSON)
	 @Produces(MediaType.APPLICATION_JSON)
	 public String getTextbook(String input) {
		 
		//Create a filter for retrieving all the books associated with that user
		JSONObject obj = new JSONObject(input);
		String user_id = obj.getString("uid");
		Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, user_id);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 
		/*
		*	If the user exists, retrieve its entries
		*	Else create a new user and return an empty entries list
		*/
		Query q = new Query("User").setFilter(userFilter);
		Entity user = datastore.prepare(q).asSingleEntity();
		 
		if(user == null) {
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
	}
	 
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTextbook(String input)	throws ParseException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		//Parse the input parameters from the JSON object sent from client side
		JSONObject text = new JSONObject(input);
		Date addDate = new Date();
		Date matchDate = null; 
		String matched = MatchingFunction.checkForMatch(
				text.getString("isbn"), 
				text.getString("uid"),
				text.getString("type"),
				text.getString("title"));
		 
		if(matched.equals("yes"))
			matchDate = new Date();

		// Create an textbook entity using the user input 
		Entity textbook = new Entity("Textbook");
		    textbook.setProperty("uid", text.getString("uid"));
		    textbook.setProperty("type", text.getString("type"));
		    textbook.setProperty("title", text.getString("title"));
		    textbook.setProperty("author", text.getString("author"));
		    textbook.setProperty("isbn", text.getString("isbn"));
		    textbook.setProperty("edition", text.getString("edition"));
		    textbook.setProperty("condition", text.getString("condition"));
		    textbook.setProperty("date", addDate);	 
		    textbook.setProperty("matchDate", matchDate);
		    textbook.setProperty("matched", matched);
		datastore.put(textbook);

		String bookOwner = text.getString("uid");
		String typeOfEntry = text.getString("type");
		updateUserKarma(bookOwner, typeOfEntry);
		
		String json = new Gson().toJson(textbook);
		return json;
	}
	 //not key, just key.
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTextbook(String input) throws JSONException {
		JSONObject obj = new JSONObject(input);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Long id = Long.valueOf(obj.getString("id")).longValue();
		Key textbookKey = KeyFactory.createKey("Textbook", id);
		datastore.delete(textbookKey);	 
	 }
	 
	@POST
	@Path("/updateTextbook")
	@Consumes(MediaType.APPLICATION_JSON)
 	public void updateTextbook(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		JSONObject obj = new JSONObject(input);

		Long id = Long.valueOf(obj.getString("id")).longValue();
		String title = obj.getString("title");
		String author = obj.getString("author");
		String isbn = obj.getString("isbn");
		String edition = obj.getString("edition");
		String condition = obj.getString("condition");

	
		Key textbookKey = KeyFactory.createKey("Textbook", id);
		Query q = new Query(textbookKey);
		Entity textbook = datastore.prepare(q).asSingleEntity();
		    textbook.setProperty("title", title);
		    textbook.setProperty("author", author);
		    textbook.setProperty("isbn", isbn);
		    textbook.setProperty("edition", edition);
		    textbook.setProperty("condition", condition);    
		datastore.put(textbook);
	}
	
	@POST
	@Path("/updateUserRadius")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateUserRadius(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		JSONObject obj = new JSONObject(input);
		String uid = obj.getString("uid");
		double radius = obj.getDouble("radius");

		Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
		Query q = new Query("User").setFilter(userFilter);
		Entity user = datastore.prepare(q).asSingleEntity();
			user.setUnindexedProperty("radius", radius);
		datastore.put(user);
	}

	@POST
	@Path("/updateUserLocation")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateUserLocation(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
	}
	
	
	/*
	 * Required JSON string for this method to work
	 * {
				id: "4785074604081152"
				uid: "1234567890"
				title: "math"
				isbn: "123123"
				type: "request"
			}
		}
	 * 
	 * 
	 */
	@POST
	@Path("/unmatchTextbook")
	@Consumes(MediaType.APPLICATION_JSON)
 	public void unmatchTextbook(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		JSONObject obj = new JSONObject(input);

		Long id = Long.valueOf(obj.getString("id")).longValue();
		String title = obj.getString("title");
		String isbn = obj.getString("isbn");
		String type = obj.getString("type");
		String uid = obj.getString("uid");
		//Check for a match again
		String matched = MatchingFunction.checkForMatch(isbn, uid, type, title);
		
		//Not found
		if(matched.equals("no")) {
			Key textbookKey = KeyFactory.createKey("Textbook", id);
			Query q = new Query(textbookKey);
			Entity textbook = datastore.prepare(q).asSingleEntity();
				textbook.setProperty("matched", "no");
				textbook.setProperty("matchDate", null);
			datastore.put(textbook);
		}
	}

	private void createUser(JSONObject obj) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Entity user = new Entity("User");
	 		user.setProperty("name", obj.getString("name"));
	 		user.setProperty("uid", obj.getString("uid"));
	 		user.setProperty("email", obj.getString("email"));
			user.setProperty("location", obj.getString("location"));
			user.setUnindexedProperty("lat", obj.getDouble("lat"));
			user.setUnindexedProperty("lon", obj.getDouble("lon"));
			user.setUnindexedProperty("request_karma", 0);
			user.setUnindexedProperty("offer_karma", 0);
			user.setUnindexedProperty("radius", 15.0); //default value for radius.
		datastore.put(user);
	}

	private void updateUserKarma(String uid, String type) {
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
}











