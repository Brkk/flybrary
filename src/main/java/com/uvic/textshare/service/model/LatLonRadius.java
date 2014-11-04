package com.uvic.textshare.service.model;

public class LatLonRadius {
	private final double lat;
	private final double lon;
	private final double radius;

	public LatLonRadius(double a, double b, double c) {
		this.lat = a;
		this.lon = b;
		this.radius = c;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getRadius() {
		return radius;
	}
	
	
}
