package com.sps;

import javax.xml.ws.Endpoint;

import com.sps.ws.server.SumImpl;

public class ServicePublisher {
	private static final String endpoint = "http://localhost:8080/ws/Sum";

	public static void main(String[] args) {
		Endpoint.publish(endpoint, new SumImpl());
		System.out.println("Service has been started at "+endpoint);
	}
}
