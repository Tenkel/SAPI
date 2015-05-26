package com.tenkel.comm;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import android.util.Log;

public class BasicConnector {
	
	
	public static SoapObject registrarDispositivo(String login, String email, String senha, String nomepais) throws CommunicationException {
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
				//Get the response
		        response = (SoapObject) envelope.bodyIn;
		        
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("BasicConnector", "Erro no SOAP");
			}
	        if (Integer.parseInt(response.getProperty(0).toString())!=0){
	        	Log.i("BasicConnector", response.getProperty(1).toString());
	        	return null;
	        }
	        else{
	        	return response;
	        	}
		    
		    	}
	
	public static SoapObject registrarAndar(String token, int userid, int MarcaId) throws CommunicationException {
		SoapObject response = null;
		//Create request	
		SoapObject request = new SoapObject(Config.NAMESPACE, Config.ANDARES);
		String SOAP_ACTION = Config.BASE_URL + Config.ANDARES;
		
		//Add the property to request object
		request.addProperty("Token",token);
		request.addProperty("UsuarioEmpresaId",userid);
	    request.addProperty("MarcaId",MarcaId);
	    
		
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
//			Log.d("dump Request: " ,androidHttpTransport.requestDump);
//			Log.d("dump response: " ,androidHttpTransport.responseDump);
			//Get the response
	        response = (SoapObject) envelope.bodyIn;
	        
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("BasicConnector", "Erro no SOAP");
		}
        if (Integer.parseInt(response.getProperty(0).toString())!=0){
        	Log.i("BasicConnector", response.getProperty(1).toString());
        	return null;
        }
        else{
        	return response;
        	}
		
	}
	
	public static SoapObject ListarPI(String token, int userid, int MarcaId, int AndarId) throws CommunicationException {
		SoapObject response = null;
		//Create request	
		SoapObject request = new SoapObject(Config.NAMESPACE, Config.PONTOS_DE_INTERESSE);
		String SOAP_ACTION = Config.BASE_URL + Config.PONTOS_DE_INTERESSE;
		
		//Add the property to request object
		request.addProperty("Token",token);
		request.addProperty("UsuarioEmpresaId",userid);
	    request.addProperty("MarcaId",MarcaId);
	    request.addProperty("GeoWiFiAndarId",AndarId);
	    
		
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
//			Log.d("dump Request: " ,androidHttpTransport.requestDump);
//			Log.d("dump response: " ,androidHttpTransport.responseDump);
			//Get the response
	        response = (SoapObject) envelope.bodyIn;
	        
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("BasicConnector", "Erro no SOAP");
		}
        if (Integer.parseInt(response.getProperty(0).toString())!=0){
        	Log.i("BasicConnector", response.getProperty(1).toString());
        	return null;
        }
        else{
        	return response;
        	}
		
	}
	
	public static SoapObject DefinirPI(String token, int userid, int andar, String nome, int x, int y, String referencia, int IdHandset) throws CommunicationException {
		SoapObject response = null;
		//Create request	
		SoapObject request = new SoapObject(Config.NAMESPACE, Config.PONTOS_DE_INTERESSE);
		String SOAP_ACTION = Config.BASE_URL + Config.PONTOS_DE_INTERESSE;
		
		//Add the property to request object
		request.addProperty("Token",token);
		request.addProperty("UsuarioEmpresaId",userid);
	    request.addProperty("AndarId",andar);
	    request.addProperty("NomePontoInteresse",nome);
	    request.addProperty("X",x);
	    request.addProperty("Y",y);
	    request.addProperty("Referencia",referencia);
	    request.addProperty("IDHandset",IdHandset);
	    
		
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
//			Log.d("dump Request: " ,androidHttpTransport.requestDump);
//			Log.d("dump response: " ,androidHttpTransport.responseDump);
			//Get the response
	        response = (SoapObject) envelope.bodyIn;
	        
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("BasicConnector", "Erro no SOAP");
		}
        if (Integer.parseInt(response.getProperty(0).toString())!=0){
        	Log.i("BasicConnector", response.getProperty(1).toString());
        	return null;
        }
        else{
        	return response;
        	}
		
	}
	
}