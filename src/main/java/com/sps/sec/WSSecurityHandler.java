package com.sps.sec;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.ws.security.WSConstants;

public class WSSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		Set<QName> headers = new HashSet<QName>();
		headers.add(new QName(WSConstants.WSSE_NS, WSConstants.WSSE_LN));
		headers.add(new QName(WSConstants.WSSE11_NS, WSConstants.WSSE_LN));

		return headers;
	}

	public boolean handleMessage(SOAPMessageContext smc) {

		SOAPMessage msg = null;
		try {
			msg = smc.getMessage();
			Boolean outgoing = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

			String soapMessage = null;

			if (outgoing) {
				msg = WSSecurity.sign(msg);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				msg.writeTo(outputStream);

				soapMessage = outputStream.toString();
				System.out.println("Message signed! " + soapMessage);
			} else {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				msg.writeTo(outputStream);

				soapMessage = outputStream.toString();

				if (soapMessage.equals("") || soapMessage == null)
					throw new Exception();

				WSSecurity.verify(msg);
				System.out.println("Message verified");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		return true;
	}

	public boolean handleFault(SOAPMessageContext messageContext) {
		return true;
	}

	public void close(MessageContext messageContext) {
	}
}
