package com.tenkel.comm;

import java.io.IOException;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

public class mySoapEnvelope extends SoapSerializationEnvelope {

	public mySoapEnvelope(int version) {
		super(version);
		// TODO Auto-generated constructor stub
	}

	public void write(XmlSerializer writer) throws IOException {
	    writer.setPrefix("xsi", xsi);
	    writer.setPrefix("xsd", xsd);
	   // writer.setPrefix("c", enc);
	    writer.setPrefix("soap", env);
	    writer.startTag(env, "Envelope");
	  //  writer.startTag(env, "Header");
	  //  writeHeader(writer);
	  //  writer.endTag(env, "Header");
	    writer.startTag(env, "Body");
	    writeBody(writer);
	    writer.endTag(env, "Body");
	    writer.endTag(env, "Envelope");
	}

}
