package com.sps;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import com.sps.sec.WSSecurityHandler;
import com.sps.ws.server.Sum;

public class WebServiceClient {
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost:8080/ws/Sum");
        QName qname = new QName("http://server.ws.sps.com/", "SumImplService");
        Service service = Service.create(url, qname);
        HandlerResolver handlerResolver = new HandlerResolver() {
			
			public List<Handler> getHandlerChain(PortInfo arg0) {
				List<Handler> handlerChain = new ArrayList<Handler>();
				WSSecurityHandler handler = new WSSecurityHandler();
				handlerChain.add(handler);
				return handlerChain;
			}
		};
		service.setHandlerResolver(handlerResolver );
        Sum port = service.getPort(Sum.class);
       
		int result = port.sum(11, 22);
		System.out.println("Result: " + result);
	}
}
