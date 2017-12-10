package com.sps.sec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.util.WSSecurityUtil;
import org.w3c.dom.Document;

public class WSSecurity {
	private static final String cryptoProps = "keystore.properties";
	private static final String cryptoVerifyProps = "truststore.properties";

	public static SOAPMessage sign(SOAPMessage msg) throws Exception {
		WSSecSignature signature = new WSSecSignature();
		signature.setUserInfo("sps", "changeit");
		signature.setDigestAlgo("http://www.w3.org/2001/04/xmlenc#sha256");
		signature.setKeyIdentifierType(WSConstants.ISSUER_SERIAL);

		Document plainDoc = msg.getSOAPPart();
		WSSecHeader header = new WSSecHeader();
		header.insertSecurityHeader(plainDoc);
		Crypto crypto = CryptoFactory.getInstance(cryptoProps);
		Document signedDoc = signature.build(plainDoc, crypto, header);

		String soapStr = document2String(signedDoc, false);
		ByteArrayInputStream is = new ByteArrayInputStream(soapStr.getBytes());
		SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, is);

		return soapMessage;
	}

	public static List<WSSecurityEngineResult> verify(SOAPMessage msg) throws Exception {
		Document doc = getSOAPDocument(msg, false);
		Crypto crypto = CryptoFactory.getInstance(cryptoVerifyProps);

		WSSecurityEngine secEngine = new WSSecurityEngine();
		List<WSSecurityEngineResult> result = secEngine.processSecurityHeader(doc, null, null, crypto);
		WSSecurityEngineResult actionResult = result == null ? null
				: WSSecurityUtil.fetchActionResult(result, WSConstants.SIGN);
		if (result == null || actionResult == null) {
			throw new Exception("No Signature");
		}

		return result;
	}

	private static String document2String(Document toBeWritten, boolean indent)
			throws TransformerException, IOException {

		StringWriter out = new StringWriter();
		out = (StringWriter) writeDocument(toBeWritten, out, indent);

		return out.toString();
	}

	private static Writer writeDocument(Document toBeWritten, Writer out, boolean indent)
			throws TransformerException, IOException {

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		if (indent) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		DOMSource source = new DOMSource(toBeWritten);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);

		out.flush();
		return out;
	}

	private static Document getSOAPDocument(SOAPMessage msg, boolean indent)
			throws TransformerException, SOAPException {

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		if (indent) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		Source source = msg.getSOAPPart().getContent();
		DOMResult result = new DOMResult();
		transformer.transform(source, result);
		Document doc = (Document) result.getNode();

		return doc;
	}
}
