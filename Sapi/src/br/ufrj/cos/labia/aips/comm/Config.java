package br.ufrj.cos.labia.aips.comm;

public class Config {
	
	//public static final String BASE_URL = "http://10.0.0.76:4567";
	public static final String BASE_URL = "http://scdp.cloudapp.net";
	public static final String BASE_URL_2 = "http://serv-evsheep.rhcloud.com";
	public static final String BASE_URL_3 = "http://10.0.0.76:4567";
	
	public static final String REGISTER_METHOD = "/api/dispositivo/Cadastradispositivo";
	
	public static final String START_MONITORING_METHOD = "/api/dispositivo/LigarMonitoramento";
	
	public static final String STOP_MONITORING_METHOD = "/api/dispositivo/DesligarMonitoramento";
	
	public static final String REGISTER_POSITION_METHOD = "/api/dispositivo/RegistrarLocalAtual";
	
	public static final String REQUEST_PLACES_METHOD = "/api/dispositivo/RequisitarLocais";
	
	public static final String DOWNLOAD_PLACE_METHOD = "/api/dispositivo/BaixarLocal";
	
	public static final String SEND_MODEL_METHOD = "/api/modelotreinamento/EnviarModelo";
	
	public static final String DOWNLOAD_MODEL_METHOD = "/api/modelotreinamento/BaixarModelo";
	
	public static final String REQUEST_MODELS_METHOD = "/api/modelotreinamento/RequisitarModelosLocal";
	
}
