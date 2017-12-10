package com.sps.ws.server;

import javax.jws.HandlerChain;
import javax.jws.WebService;

@WebService(endpointInterface = "com.sps.ws.server.Sum")
@HandlerChain(file="security-binding.xml")
public class SumImpl implements Sum{

	public int sum(int a, int b) {
		return a+b;
	}
}
