package com.sps.ws.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Sum {
	@WebMethod
	int sum(int a, int b);
}
