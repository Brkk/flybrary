package com.uvic.textshare.service.model;

@SuppressWarnings("unused")
public class Statistics {
	private final int numberOfOffers;
	private final int numberOfRequests;
	private final int numberOfMatches;

	public Statistics(int offers, int requests, int matches) {
		this.numberOfOffers = offers;
		this.numberOfRequests = requests;
		this.numberOfMatches = matches;
	}
}
