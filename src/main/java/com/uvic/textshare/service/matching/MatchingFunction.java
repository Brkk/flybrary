package com.uvic.textshare.service.matching;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
import com.google.appengine.api.datastore.Key;
import com.uvic.textshare.service.model.Textbook;

public class MatchingFunction {
	
	public static String checkForMatch(String title, String author, String edition, String condition, String type, String user1, String email1) {
		
		String searchType;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		if(type.equals("offered")) {
			 searchType = "requested";
		} else {
			searchType = "offered";
		}
		System.out.println(searchType);
		System.out.println(author);
		System.out.println(title);
		System.out.println(edition);
		//Set up filters for matching
		Filter typeFilter = new FilterPredicate("type", FilterOperator.EQUAL, searchType);
		Filter authorFilter = new FilterPredicate("author", FilterOperator.EQUAL, author);
		Filter titleFilter = new FilterPredicate("title", FilterOperator.EQUAL, title);
		Filter editionFilter = new FilterPredicate("edition", FilterOperator.EQUAL, edition);
		Filter matchFilter = new FilterPredicate("matched", FilterOperator.EQUAL, "no");
		Filter searchFilter = CompositeFilterOperator.and(titleFilter, authorFilter, editionFilter, typeFilter, matchFilter);

		Query q = new Query("Textbook").setFilter(searchFilter).addSort("date", Query.SortDirection.ASCENDING);
		List<Entity> textbooks = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		
		System.out.println(textbooks);
		//Check if there is a match
		if(!textbooks.isEmpty())
		{
			Entity matchedBook = textbooks.get(0); //would this work, don't even know...
			String user2 = (String) matchedBook.getProperty("user");
			String email2 = (String) matchedBook.getProperty("email");
			
			/*
			 * Now we have both users email addresses and names as well as the matched books info like title
			 * After this point we can send our auto generated Email to both users.
			 * 
			 */
			sendEmailToUser("berk", "yaziciogluberk@gmail.com", "berk", "yaziciogluberk@gmail.com", "bio101");
			matchedBook.setProperty("matched", "yes");
			datastore.put(matchedBook);
			return "yes";
		} else 
		{
			return "no";
		}
		
	}
	
	
	
	//Take a look at this method here, I created it using a hashmap to switch filters and broaden the results
	//Ps. not ready yet but working
public static boolean checkForMatchObj(Textbook textbook) {
		Boolean matched = false;
		String searchType;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		if(textbook.getType().equals("offered")) {
			 searchType = "requested";
		} else {
			searchType = "offered";
		}


		
		//Set up filters for matching
		String[] filters = {"type","title","matched","author","edition"};
		HashMap<String,Filter> hm = new HashMap<String,Filter>();
		
		//Add the filters into a hashmap
		hm.put("type", new FilterPredicate("type", FilterOperator.EQUAL, searchType));
		hm.put("author", new FilterPredicate("author", FilterOperator.EQUAL, textbook.getAuthor()));
		hm.put("title", new FilterPredicate("title", FilterOperator.EQUAL, textbook.getTitle()));
		hm.put("edition", new FilterPredicate("edition", FilterOperator.EQUAL, textbook.getEdition()));
		hm.put("matched", new FilterPredicate("matched", FilterOperator.EQUAL, textbook.getMatched()));
		
		
		Filter searchFilter;
		Query q;
		

		while(hm.size()>2){
			
			//This line below is just a way to guarantee that when the for run doesn't even run once
			//there will be the required filters, search one more time, and decide if there is or not any book matched
			searchFilter = CompositeFilterOperator.and(hm.get(filters[0]), hm.get(filters[1]), hm.get(filters[2]));
			
			//This for picks a filter at a time and add it to the required ones.
			//For example type, title and matched are required, then it adds first edition, and in the next loop, author
			for(int i=3; i<hm.size(); i++){
				searchFilter = CompositeFilterOperator.and(hm.get(filters[i]), hm.get(filters[0]), hm.get(filters[1]), hm.get(filters[2]));
			}
			
			q = new Query("Textbook").setFilter(searchFilter).addSort("date", Query.SortDirection.ASCENDING);
			//we could return this list, there'd more options
			List<Entity> textbooks = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			
			//At this point we have to consider that there might be an exact book with the exact same version
			//But if not, we still can retrieve some options, right?
			if(!textbooks.isEmpty())
			{
//				Entity matchedBook = textbooks.get(0); //would this work, don't even know...
//				
//				String user2 = (String) matchedBook.getProperty("user");
//				String email2 = (String) matchedBook.getProperty("email");
				
				/*
				 * Now we have both users email addresses and names as well as the matched books info like title
				 * After this point we can send our auto generated Email to both users.
				 * 
				 */
//				matchedBook.setProperty("matched", "yes");
//				datastore.put(matchedBook);
				
				
				//Show if there is any match
				for(Entity a:textbooks)
				System.out.println("You have a matched textbook\n\n"+textbook+"\n\n\nMatched book:\n"+a);
				return true;
				
			} else 
			{
				System.out.println("No textbook found");
				System.out.println("Removing: "+filters[hm.size()-1]);
				//removes the filter used to search
				hm.remove(filters[hm.size()-1]);
			}
			
			
		}
		System.out.println("No book found");
		return false;
	}

	private static void sendEmailToUser(String receiverName, String receiverEmail, String matchedUserName, String matchedUserEmail, String title) {
		// user here would be the Name of the user.
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    //create an email and send to the user
	    String msgBody = "Hello fellow student,\n"
	    		+ ""
	    		+ "We are glad to tell you that we have found a match to the " + title + ". You can reach " 
	    		+ matchedUserName + "by email from this address " + matchedUserEmail + ". Have a nice day.\n"
	    		+ "Regards,\n"
	    		+ "TextShare Team.";    		
	
	    try {
	        Message msg = new MimeMessage(session);
	        try {
				msg.setFrom(new InternetAddress("team.textshare@gmail.com", "TextShare Team"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace(); //log these errors
			}
	        try {
				msg.addRecipient(Message.RecipientType.TO,
				                 new InternetAddress(receiverEmail, receiverName));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace(); //log these errors
			}
	        msg.setSubject(title + " matched with a textbook in our database");
	        msg.setText(msgBody);
	        Transport.send(msg);
	
	    } catch (AddressException e) {
	        // ...
	    } catch (MessagingException e) {
	        // ...
	    }
	}
}
