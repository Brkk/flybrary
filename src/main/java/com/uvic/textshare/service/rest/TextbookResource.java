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
		*	If the user exists, retreieve its entries
		*	Else create a new user and return an emtpy entries list
		*/
		Query q = new Query("User").setFilter(userFilter);
		PreparedQuery pq = datastore.prepare(q);
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
	public void addTextbook(String input)	throws ParseException {
		
		//Parse the input parameters from the JSON object sent from client side
		JSONObject text = new JSONObject(input);

		// UserService userService = UserServiceFactory.getUerService();
		// User user = userService.getCurrentUser();
		Date addDate = new Date();
		Date matchDate = null; 
		String matched = MatchingFunction.checkForMatch(
				text.getString("title"), 
				text.getString("author"), 
				text.getString("edition"), 
				text.getString("condition"), 
				text.getString("type"),
				text.getString("uid"),
		 		"Victoria");
		 
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
		    textbook.setProperty("location", "Victoria");
		    
		//Add the created entity on the Datastore.
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(textbook);

		String bookOwner = text.getString("uid");
		String typeOfEntry = text.getString("type");
		updateUserKarma(bookOwner, typeOfEntry);
	}
	 
	/*
	*	Doesnt try to match the book -- user this method on DEVSERVER to create indexes.
	*/
	@POST
	@Path("/onlyAdd")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addTextbookTest(String input) {
		
		JSONObject propertyMap = new JSONObject(input);
		JSONObject obj = propertyMap.getJSONObject("propertyMap");
		Date date = new Date();

		 		 
		// Create an textbook entity using the user input 
		Entity textbook = new Entity("Textbook");
		    textbook.setProperty("uid", obj.getString("uid"));
		    textbook.setProperty("type", obj.getString("type"));
		    textbook.setProperty("title", obj.getString("title"));
		    textbook.setProperty("author", obj.getString("author"));
		    textbook.setProperty("isbn", obj.getString("isbn"));
		    textbook.setProperty("edition", obj.getString("edition"));
		    textbook.setProperty("condition", obj.getString("condition"));
		    textbook.setProperty("date", date);	 
		    textbook.setProperty("matchDate", date);
		    textbook.setProperty("matched", "no");
		    textbook.setProperty("location", "Victoria");
		    
		//Add the created entity on the Datastore.
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(textbook);
	 }  
	 
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTextbook(String input) throws JSONException {
		 
		JSONObject keyPart = new JSONObject(input);
		JSONObject obj = keyPart.getJSONObject("key");
		System.out.println(obj);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Long id = obj.getLong("id");
		Key textbookKey = KeyFactory.createKey("Textbook", id);
		datastore.delete(textbookKey);
		 
	 }
	 
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
 	public void updateTextbook(String input) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		JSONObject obj = new JSONObject(input);
		JSONObject keyValues = obj.getJSONObject("key");

		Long id = Long.valueOf(keyValues.getString("id")).longValue();
		String title = obj.getString("title");
		String author = obj.getString("author");
		String isbn = obj.getString("isbn");
		String edition = obj.getString("edition");
		String condition = obj.getString("condition");
		String type = obj.getString("type");
		String uid = obj.getString("uid");
	
		Key textbookKey = KeyFactory.createKey("Textbook", id);
		Query q = new Query(textbookKey);
		Entity textbook = datastore.prepare(q).asSingleEntity();
			textbook.setProperty("uid", uid);
		    textbook.setProperty("type", type);
		    textbook.setProperty("title", title);
		    textbook.setProperty("author", author);
		    textbook.setProperty("isbn", isbn);
		    textbook.setProperty("edition", edition);
		    textbook.setProperty("condition", condition); 
		    
		datastore.put(textbook);
	}
 
	@GET
	@Path("/test")
	public String testMethod() {
		return "this is a test";
	}

	private void createUser(JSONObject obj) {
	
		Entity user = new Entity("User");
	 		user.setProperty("name", obj.getString("name"));
	 		user.setProperty("uid", obj.getString("uid"));
	 		user.setProperty("email", obj.getString("email"));
			user.setProperty("location", obj.getString("location"));
			user.setProperty("lat", obj.getString("lat"));
			user.setProperty("lon", obj.getString("lon"));
			user.setProperty("request_karma", 0);
			user.setProperty("offer_karma", 0);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		datastore.put(user);
	}

	private void updateUserKarma(String uid, String type) {
		
		Filter userFilter = new FilterPredicate("uid", FilterOperator.EQUAL, uid);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("User").setFilter(userFilter);
		PreparedQuery pq = datastore.prepare(q);
		Entity user = datastore.prepare(q).asSingleEntity();
		int updatedKarma;

		if(type.equals("offer"))
		{
			updatedKarma = Integer.parseInt(user.getProperty("offer_karma").toString());
			updatedKarma += 5;
			user.setProperty("offer_karma", updatedKarma);
		}
		else
		{
			updatedKarma = Integer.parseInt(user.getProperty("request_karma").toString());
			updatedKarma += 5;
			user.setProperty("request_karma", updatedKarma);
		}

		datastore.put(user);
	}
}











