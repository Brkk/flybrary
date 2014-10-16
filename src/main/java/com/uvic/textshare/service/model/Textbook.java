package com.uvic.textshare.service.model;

import java.util.Date;

import com.google.appengine.api.users.User;

public class Textbook {
	private String title;
    private String author;
    private Date addDate;
    private User user;
    private String isbn;
    private String condition;
    private String edition;//edition or edition?
    private String type;
    private String matched;
    private String email;
    
    //need to add an ID field, maybe randomly generate it?
    public Textbook(String title, String author, Date addDate, User user,
			String isbn, String condition, String edition, String type, String matched, String email) {
		super();
		this.title = title;
		this.author = author;
		this.addDate = addDate;
		this.user = user;
		this.isbn = isbn;
		this.condition = condition;
		this.edition = edition;
		this.type = type;
		this.matched = matched;
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public String getMatched() {
		return matched;
	}

	public void setMatched(String matched) {
		this.matched = matched;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

}