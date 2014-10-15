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


@Path("/textshare")
public class TextbookResource {
 
	 @POST
	 @Path("/retrieve") 
	 @Consumes(MediaType.APPLICATION_JSON)
	 @Produces(MediaType.APPLICATION_JSON)
	 public String getTextbook(String input) {
		 
		 //Create a filter for retrieving all the books associated with that user
		 JSONObject obj = new JSONObject(input);
		 String user = obj.getString("user");
		 Filter userFilter = new FilterPredicate("user", FilterOperator.EQUAL, user);

		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 // Use class Query to assemble a query
		 Query q = new Query("Textbook").setFilter(userFilter);

		 // Use PreparedQuery interface to retrieve results and save it into a list
		 PreparedQuery pq = datastore.prepare(q);
		 List<Entity> textbooks = pq.asList(FetchOptions.Builder.withDefaults());
		
		 String json = new Gson().toJson(textbooks);
		 return json;
	 }
	 
	 @POST
	 @Path("/add")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void addTextbook(String input)	throws ParseException {
		
		 //Parse the input parameters form the JSON object sent from client side
		 JSONObject text = new JSONObject(input);
		 
		 /*
		  * Are these going to be used or are we depending on the information from the client side
		  */
		// UserService userService = UserServiceFactory.getUserService();
		// User user = userService.getCurrentUser();
		 Date date = new Date();
		 
		 String matched = MatchingFunction.checkForMatch(
				 text.getString("title"), 
				 text.getString("author"), 
				 text.getString("edition"), 
				 text.getString("condition"), 
				 text.getString("type"), 
				 text.getString("user"), 
				 text.getString("email"));
		 
		 // Create an textbook entity using the user input 
		 Entity textbook = new Entity("Textbook");
		    textbook.setProperty("user", text.getString("user"));
		    textbook.setProperty("type", text.getString("type"));
		    textbook.setProperty("title", text.getString("title"));
		    textbook.setProperty("author", text.getString("author"));
		    textbook.setProperty("isbn", text.getString("isbn"));
		    textbook.setProperty("edition", text.getString("edition"));
		    textbook.setProperty("condition", text.getString("condition"));
		    textbook.setProperty("date", date);	 
		    textbook.setProperty("matched", matched);
		    textbook.setProperty("email",text.getString("email"));
		    
		    //Add the created entity on the Datastore.
		    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    datastore.put(textbook); 	
	 }
	 
	 /*
	  * Doesnt try to match the book -- user this method on DEVSERVER to create indexes.
	  */
	 @POST
	 @Path("/onlyAdd")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void addTextbookTest(String input) {
		 
		 String matched = "no";
		 JSONObject obj = new JSONObject(input);
		 UserService userService = UserServiceFactory.getUserService();
		 User user = userService.getCurrentUser();
		 
		 Textbook textbook1 = new Textbook(obj.getString("title"),
				 obj.getString("author"),
				 new Date(),
				 user,
				 obj.getString("isbn"),
				 obj.getString("condition"),
				 obj.getString("edition"),
				 obj.getString("type"),
				 matched,
				 obj.getString("email"));
		 		 
		 // Create an textbook entity using the user input 
		 Entity textbook = new Entity("Textbook");
		    textbook.setProperty("user", textbook1.getUser());
		    textbook.setProperty("type", textbook1.getType());
		    textbook.setProperty("title", textbook1.getTitle());
		    textbook.setProperty("author", textbook1.getAuthor());
		    textbook.setProperty("isbn", textbook1.getIsbn());
		    textbook.setProperty("edition", textbook1.getEdition());
		    textbook.setProperty("condition", textbook1.getCondition());
		    textbook.setProperty("date", textbook1.getAddDate());
		    textbook.setProperty("matched", "no"); //If we know a book is matched, we can omit it when searching for a match.
		    
		    //Add the created entity on the Datastore.
		    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    datastore.put(textbook);
	 }  
	 
	 @POST
	 @Path("/delete")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void deleteTextbook(String input) throws JSONException {
		 
		 JSONObject obj = new JSONObject(input);
		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 String title = obj.getString("title");
		 String author = obj.getString("author");
		 String user = obj.getString("user");
		 String type = obj.getString("type");
		 String edition = obj.getString("edition");
		//Create a filters for retrieving the textbook to be deleted
		 Filter userFilter =
				  new FilterPredicate("user",
				                      FilterOperator.EQUAL,
				                      user);
		 Filter typeFilter =
				  new FilterPredicate("type",
	                      FilterOperator.EQUAL,
	                      type);
		 
		 Filter authorFilter =
				  new FilterPredicate("author",
	                      FilterOperator.EQUAL,
	                      author);
		 
		 Filter titleFilter = 
				  new FilterPredicate("title",
	                      FilterOperator.EQUAL,
	                      title);
		 Filter editionFilter =
				  new FilterPredicate("edition",
	                      FilterOperator.EQUAL,
	                      edition);
		 Filter matchFilter = CompositeFilterOperator.and(titleFilter,
					authorFilter,
					editionFilter,
					userFilter,
					typeFilter);
		 
		 //Query the text book and delete it.
		 Query q = new Query("Textbook").setFilter(matchFilter);
		 Entity textbook = datastore.prepare(q).asSingleEntity();
		 datastore.delete(textbook.getKey());
		 
	 }
	 
	 //To update a book, you have to send back the unique id of the textbook as well.
	 //When you call the retrieve method it returns the unique id for each book that user has
	 //Store that with the rest of the textbook so it can be sent back
	 // !!!! Only works if the unique ID (created by datastore when the textbook is created) provided !!!!
	 @POST
	 @Path("/update")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void updateTextbook(String input) {
		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		 JSONObject obj = new JSONObject(input);
		 
		 Long id = Long.valueOf(obj.getString("id")).longValue();
		 String title = obj.getString("title");
		 String author = obj.getString("author");
		 String isbn = obj.getString("isbn");
		 String edition = obj.getString("edition");
		 String user = obj.getString("user");
		 String condition = obj.getString("condition");
		 String type = obj.getString("type");
		 String matched = "no";
		 String email = obj.getString("email");
	
		 Key textbookKey = KeyFactory.createKey("Textbook", id);
		 Query q = new Query(textbookKey);
		 Entity textbook = datastore.prepare(q).asSingleEntity();
		 	textbook.setProperty("user", user);
		    textbook.setProperty("type", type);
		    textbook.setProperty("title", title);
		    textbook.setProperty("author", author);
		    textbook.setProperty("isbn", isbn);
		    textbook.setProperty("edition", edition);
		    textbook.setProperty("condition", condition); 
		    textbook.setProperty("matched", matched);
		    textbook.setProperty("email", email);
		    
		 datastore.put(textbook);
	 }
 
	 @GET
	 @Path("/test")
	 public String testMethod() {
		 return "this is a test";
	 } 
}
