package com.uvic.textshare.service.model;

import java.util.Date;

public class Textbook {
	private String title;
    private String author;
    private String isbn;
    private String condition;
    private String edition;
    private String type;
    private Date addDate;
    private Date matchDate;
    private String uid;
    private String matched;
    private double lat;
    private double lon;
    
    //need to add an ID field, maybe randomly generate it?
    public Textbook(String title, String author, Date addDate, Date matchDate, String uid,
			String isbn, String condition, String edition, String type, String matched, double lat, double lon) {
		super();
		this.title = title;
		this.author = author;
		this.addDate = addDate;
		this.matchDate = matchDate;
		this.uid = uid;
		this.isbn = isbn;
		this.condition = condition;
		this.edition = edition;
		this.type = type;
		this.matched = matched;
		this.lat = lat;
		this.lon = lon;
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

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public String getUser() {
		return uid;
	}

	public void setUser(String uid) {
		this.uid = uid;
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

	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}

}