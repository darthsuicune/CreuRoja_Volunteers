package com.dlgdev.directions;

/**
 * Created by lapuente on 12.01.15.
 */
public class DirectionsException extends RuntimeException {
	Exception e;
	public DirectionsException(Exception e) {
		this.e = e;
	}

	public DirectionsException(String error) {
		super(error, new RuntimeException());
	}
}
