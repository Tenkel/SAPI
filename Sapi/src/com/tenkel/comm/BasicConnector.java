package com.tenkel.comm;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class BasicConnector {
	
	public static SoapObject registrarDispositivo(String login, String email, String senha, String nomepais) throws CommunicationException {
		Log.i("BasicConnector", "Sending Dispositivo");
		
		SoapObject response = null;
		
			//Create request	
			SoapObject request = new SoapObject(Config.NAMESPACE, Config.REGISTRAR);
			String SOAP_ACTION = Config.BASE_URL + Config.REGISTRAR;
			
			//Add the property to request object
		    request.addProperty("Login",login);
		    request.addProperty("Email",email);
		    request.addProperty("Senha",senha);
		    request.addProperty("NomePais",nomepais);
		    
			
			//Create envelope
		    mySoapEnvelope envelope = new mySoapEnvelope(
		            SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setAddAdornments(false);
		    envelope.implicitTypes = true;
		    
		    //Set output SOAP object
		    envelope.setOutputSoapObject(request);

		    //Create HTTP call object
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(Config.URL);
			
		    //Invoke web service
	        try {
	        	androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	        	androidHttpTransport.debug = true;
				androidHttpTransport.call(SOAP_ACTION, envelope);
				Log.d("dump Request: " ,androidHttpTransport.requestDump);
				Log.d("dump response: " ,androidHttpTransport.responseDump);
				//Get the response
		        response = (SoapObject) envelope.bodyIn;
		        
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("BasicConnector", response.getProperty(1).toString());
			}
	        return response;
		    
		    	}
}