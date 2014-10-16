package com.uvic.textshare.service.model;

public class User {
	private String name;
	private String email;
	private String karma;
	private Double lat;
	private Double lang;
	public User(String name, String email, String karma, Double lat, Double lang) {
		super();
		this.name = name;
		this.email = email;
		this.karma = karma;
		this.lat = lat;
		this.lang = lang;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getKarma() {
		return karma;
	}
	public void setKarma(String karma) {
		this.karma = karma;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLang() {
		return lang;
	}
	public void setLang(Double lang) {
		this.lang = lang;
	}

}
