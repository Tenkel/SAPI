package com.tenkel.comm;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class BasicConnector {
	
	public static SoapObject registrarDispositivo(String login, String email, String senha, String nomepais) throws CommunicationException {
		Log.i("BasicConnector", "Sending Dispositivo");
		
		SoapObject response = null;
		
			//Create request	
			SoapObject request = new SoapObject(Config.NAMESPACE, Config.REGISTRAR);
			String SOAP_ACTION = Config.BASE_URL + Config.REGISTRAR;
			String URL = "http://customcare.outsystemscloud.com/Zelum/WSGeoWiFi.asmx";
			
			//Add the property to request object
			PropertyInfo Login = new PropertyInfo();
			PropertyInfo Email = new PropertyInfo();
			PropertyInfo Senha = new PropertyInfo();
			PropertyInfo NomePais = new PropertyInfo();
			
		    Login.setName("Login");
		    Email.setName("Email");
		    Senha.setName("Senha");
		    NomePais.setName("NomePais");
		    
		    Login.setValue(login);
		    Email.setValue(email);
		    Senha.setValue(senha);
		    NomePais.setValue(nomepais);
		    
		    Login.setType(String.class);
		    Email.setType(String.class);
		    Senha.setType(String.class);
		    NomePais.setType(String.class);
		    
		    request.addProperty(Login);
		    request.addProperty(Email);
		    request.addProperty(Senha);
		    request.addProperty(NomePais);
			
			
			
		    //Add the property to request object
		    /*
		    request.addProperty("Login",login);
		    request.addProperty("Email",email);
		    request.addProperty("Senha",senha);
		    request.addProperty("NomePais",nomepais);
		    */
			//Create envelope
		    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		            SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    
		    //Set output SOAP object
		    envelope.setOutputSoapObject(request);

		    //Create HTTP call object
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			
		    //Invoke web service
	        try {
	        	androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				//Get the response
		        response = (SoapObject) envelope.bodyIn;
		        
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("BasicConnector", response.getProperty(1).toString());
			}
	        return response;
		    
		    	}

	/*
	public static void ligarMonitoramento(long idDispositivo) {
		Log.i("BasicConnector", "Ligando Monitoramento");
		
		try {
			String uri = Config.BASE_URL + Config.START_MONITORING_METHOD;
			Log.i("BasicConnector", "URI: " + uri);
			
			JSONObject json = new JSONObject();
			json.put("idDispositivo", idDispositivo);
			
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity(json.toString()));
			post.setHeader("Content-Type", "application/json");
			
			DefaultHttpClient client = new DefaultHttpClient();
			client.execute(post, new BasicResponseHandler());
			
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException();
	}

	public static void desligarMonitoramento(long idDispositivo) {
		Log.i("BasicConnector", "Desligando Monitoramento");
		
		try {
			String uri = Config.BASE_URL + Config.STOP_MONITORING_METHOD;
			Log.i("BasicConnector", "URI: " + uri);

			JSONObject json = new JSONObject();
			json.put("idDispositivo", idDispositivo);
			
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity(json.toString()));
			post.setHeader("Content-Type", "application/json");
			
			DefaultHttpClient client = new DefaultHttpClient();
			client.execute(post, new BasicResponseHandler());
			
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException();
	}

	public static void registrarPosicao(String imei, int x, int y, int w, int h) {
		Log.i("BasicConnector", "Enviando posicao");
		
		float x2 = (x + 1f) / (w + 1);
		float y2 = 1f - (y + 1f) / (h + 1);
		
		try {
			String uri = Config.BASE_URL_2 + Config.REGISTER_POSITION_METHOD + 
					"?id=" + imei + "&x=" + x2 + "&y=" + y2;
			Log.i("BasicConnector", "URI: " + uri);
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(uri);
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			client.execute(get, responseHandler);
			
		
			
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException();
	}

	public static void registrarPosicao(Long idDispositivo, Long idPosicao,
			String date) {
		Log.i("BasicConnector", "Enviando posicao");
		
		try {
			String uri = Config.BASE_URL + Config.REGISTER_POSITION_METHOD;
			Log.i("BasicConnector", "URI: " + uri);
			
			JSONObject json = new JSONObject();
			json.put("idDispositivo", idDispositivo);
			json.put("idPontoColeta", idPosicao);
			json.put("dataRecebimento", date);
			
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity(json.toString()));
			post.setHeader("Content-Type", "application/json");
			
			DefaultHttpClient client = new DefaultHttpClient();
			client.execute(post, new BasicResponseHandler());
			
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException("Could not send request");
	}

	public static List<LocalDTO> requisitarLocais(long idDispositivo) {
		Log.i("BasicConnector", "Requisitando locais cadastrados");
		List<LocalDTO> locais = new ArrayList<LocalDTO>();
		
		try {
			String uri = Config.BASE_URL + Config.REQUEST_PLACES_METHOD;
			Log.i("BasicConnector", "URI: " + uri);
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
			
			JSONObject jsonHolder = new JSONObject();
			jsonHolder.put("idDispositivo", idDispositivo);
			StringEntity entity = new StringEntity(jsonHolder.toString());
			
			post.setEntity(entity);
			post.setHeader("Content-Type", "application/json");
			
			ResponseHandlerWithTextAnswer responseHandler = new ResponseHandlerWithTextAnswer();
			client.execute(post, responseHandler);
			
			JSONArray response = new JSONArray(responseHandler.getResponse());
			for (int i=0;i<response.length();++i) {
				JSONObject jlocal = response.getJSONObject(i);
				
				if (jlocal.has("Erro"))
					throw new CommunicationException(jlocal.getString("Erro"));
				
				LocalDTO local = new LocalDTO();
				local.Id = jlocal.getLong("id");
				local.Nome = jlocal.getString("nome");
				locais.add(local);
			}
			
			return locais;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException("Falha ao baixar a lista de locais");
	}

	public static LocalDTO baixarLocal(long idDispositivo, long idLocal) {
		Log.i("BasicConnector", "Baixando local");
		
		try {
			String uri = Config.BASE_URL + Config.DOWNLOAD_PLACE_METHOD;
			Log.i("BasicConnector", "URI: " + uri);
			
			JSONObject jsonHolder = new JSONObject();
			jsonHolder.put("idDispositivo", idDispositivo);
			jsonHolder.put("idLocal", idLocal);
			StringEntity entity = new StringEntity(jsonHolder.toString());
			
			HttpPost post = new HttpPost(uri);
			post.setEntity(entity);
			post.setHeader("Content-Type", "application/json");
			
			ResponseHandlerWithTextAnswer responseHandler = new ResponseHandlerWithTextAnswer();
			DefaultHttpClient client = new DefaultHttpClient();
			client.execute(post, responseHandler);
			
			return new LocalDTO(new JSONObject(responseHandler.getResponse()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException("Falha ao baixar o local");
	}

	public static byte[] downloadBytes(String uri, long maxLength) {
		Log.i("BasicConnector", "Baixando arquivo: " + uri);
		
		try {
			Log.i("BasicConnector", "URI: " + uri);
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(uri);
			HttpResponse response = client.execute(post);
			
			int status = response.getStatusLine().getStatusCode();
			if (!(status >= 200 && status < 300)) {
            	throw new ClientProtocolException("Unexpected response status: " + status);
            }
            
			HttpEntity entity = response.getEntity();
			if (entity == null) {
            	throw new ClientProtocolException("Response entity was null");
            }
            
			long length = entity.getContentLength();
			if (length > maxLength) {
				throw new ClientProtocolException("File length is too big");
			}
			
			byte[] buffer = new byte[100 * 1024];
			ByteArrayOutputStream bytes = new ByteArrayOutputStream((int) length);
			InputStream is = entity.getContent();
	        
	        int inByte;
	        while ((inByte = is.read(buffer)) != -1 ) 
	        	bytes.write(buffer, 0, inByte);
	        bytes.flush();
	        
	        is.close();
			bytes.close();
			
			return bytes.toByteArray();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException("Falha ao fazer o download dos dados");
	}
	
	public static void enviarModelo(long idDispositivo, long idLocal, String nomeVersao, File f) {
		Log.i("BasicConnector", "Enviando arquivo, idDispositivo=" + idDispositivo 
				+ ", idLocal=" + idLocal + ", nomeVersao=" + nomeVersao);
		
		try {
			String uri = Config.BASE_URL + Config.SEND_MODEL_METHOD 
					+ "?idDispositivo=" + idDispositivo + "&idLocal=" + idLocal
					+ "&versaoModelo=" + nomeVersao;
			Log.i("BasicConnector", "URI: " + uri);
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();     
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("", new FileBody(f));
			post.setEntity(builder.build());
			
			String rustic = client.execute(post, new ResponseHandlerWithTextAnswer());
			Log.e("Rustic", rustic);
			
			return;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new CommunicationException("Falha ao enviar o modelo");
	}
	
	public static void baixarModelo(long idDispositivo, long idLocal, String nomeVersao, OutputStream s) {
		Log.i("BasicConnector", "Baixando modelo, idDispositivo=" + idDispositivo 
				+ ", idLocal=" + idLocal + ", nomeVersao=" + nomeVersao);
		
		try {
			String uri = Config.BASE_URL + Config.DOWNLOAD_MODEL_METHOD;
			Log.i("BasicConnector", "URI: " + uri);
			
			JSONObject jsonHolder = new JSONObject();
			jsonHolder.put("idDispositivo", idDispositivo);
			jsonHolder.put("idLocal", idLocal);
			jsonHolder.put("versaoModelo", nomeVersao);
			
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity(jsonHolder.toString()));
			post.setHeader("Content-Type", "application/json");

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			
			int status = response.getStatusLine().getStatusCode();
			if (!(status >= 200 && status < 300)) {
            	throw new ClientProtocolException("Unexpected response status: " + status);
            }
            
			HttpEntity entity = response.getEntity();
			if (entity == null) {
            	throw new ClientProtocolException("Response entity was null");
            }
            
//			long length = entity.getContentLength();
			
			byte[] buffer = new byte[100 * 1024];
			InputStream is = entity.getContent();
	        
	        int inByte;
	        while ((inByte = is.read(buffer)) != -1 ) 
	        	s.write(buffer, 0, inByte);
	        s.flush();
	        
	        is.close();
			s.close();
			
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not download file");
		throw new CommunicationException("Falha ao baixar o modelo");
	}

	public static List<ModeloDTO> requisitarModelos(long idDispositivo,
			Long idLocal) {
		List<ModeloDTO> modelos = new ArrayList<ModeloDTO>();
		Log.i("BasicConnector", "Requisitando modelos cadastrados em idDispositivo=" 
			+ idDispositivo + ", idLocal=" + idLocal);
		
		try {
			String uri = Config.BASE_URL + Config.REQUEST_MODELS_METHOD;
			Log.i("BasicConnector", "URI: " + uri);
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
			
			JSONObject jsonHolder = new JSONObject();
			jsonHolder.put("idDispositivo", idDispositivo);
			jsonHolder.put("idLocal", idLocal);
			StringEntity entity = new StringEntity(jsonHolder.toString());
			
			post.setEntity(entity);
			post.setHeader("Content-Type", "application/json");
			
			ResponseHandlerWithTextAnswer responseHandler = new ResponseHandlerWithTextAnswer();
			client.execute(post, responseHandler);
			
			JSONArray response = new JSONArray(responseHandler.getResponse());
			for (int i=0;i<response.length();++i) {
				JSONObject json = response.getJSONObject(i);
				if (json.has("Erro")) 
					throw new CommunicationException(json.getString("Erro"));
				modelos.add(new ModeloDTO(json));
			}
			
			return modelos;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("BasicConnector", "Could not complete request");
		throw new CommunicationException("Falha ao baixar a lista de modelos");
	}
	
	private static class ResponseHandlerWithTextAnswer implements ResponseHandler<String> {

		private String mResponse;

		@Override
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			
			HttpEntity entity = response.getEntity();
            if (entity != null) {
            	mResponse = EntityUtils.toString(entity);
            } else {
            	mResponse = null;
            }
            
			int status = response.getStatusLine().getStatusCode();
            if (!(status >= 200 && status < 300)) {
            	Log.e("Rustic", mResponse == null ? "null" : mResponse);
            	throw new ClientProtocolException("Unexpected response status: " + status);
            }
            
            return mResponse;
		}
		
		public String getResponse() {
			return mResponse;
		}
		
	}
*/
}