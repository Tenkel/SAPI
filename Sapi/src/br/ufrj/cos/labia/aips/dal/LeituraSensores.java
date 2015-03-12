package br.ufrj.cos.labia.aips.dal;


public class LeituraSensores{
	public static final String ID = "id";
	public static final String IDOBSERVACAO = "idObservacao";
	public static final String IDUNIDADESENSORES = "idUnidadeSensores";
	public static final String ACCX = "accX";
	public static final String ACCY = "accY";
	public static final String ACCZ = "accZ";
	public static final String TEMP = "temp";
	public static final String GRAVX = "gravX";
	public static final String GRAVY = "gravY";
	public static final String GRAVZ = "gravZ";
	public static final String GYROX = "gyroX";
	public static final String GYROY = "gyroY";
	public static final String GYROZ = "gyroZ";
	public static final String LIGHT = "light";
	public static final String LINACCX = "linAccX";
	public static final String LINACCY = "linAccY";
	public static final String LINACCZ = "linAccZ";
	public static final String MAGX = "magX";
	public static final String MAGY = "magY";
	public static final String MAGZ = "magZ";
	public static final String ORIENTX = "orientX";
	public static final String ORIENTY = "orientY";
	public static final String ORIENTZ = "orientZ";
	public static final String PRESS = "press";
	public static final String PROX = "prox";
	public static final String HUM = "hum";
	public static final String ROTX = "rotX";
	public static final String ROTY = "rotY";
	public static final String ROTZ = "rotZ";
	public static final String ROTSCALAR = "rotScalar";
	public static final String FK_LEITURASENSORES_UNIDADESENSORES = "fk_LeituraSensores_UnidadeSensores";
	public static final String FK_LEITURASENSORES_OBSERVACAO = "fk_LeituraSensores_Observacao";
	public static final String FK_SENSORSREADING_SENSORUNITS_IDX = "fk_SensorsReading_SensorUnits_idx";
	public static final String FK_SENSORSREADING_SAMPLE_IDX = "fk_SensorsReading_Sample_idx";

	public static final String[] FIELDS = { ID, IDOBSERVACAO, IDUNIDADESENSORES, 
		ACCX, ACCY, ACCZ, TEMP, GRAVX, GRAVY, GRAVZ, GYROX, GYROY, GYROZ, LIGHT, 
		LINACCX, LINACCY, LINACCZ, MAGX, MAGY, MAGZ, ORIENTX, ORIENTY, ORIENTZ, 
		PRESS, PROX, HUM, ROTX, ROTY, ROTZ, ROTSCALAR };
	
	private Long id;
	
	private Long idObservacao;
	
	private Long idUnidadeSensores;
	
	private Float accX;
	
	private Float accY;
	
	private Float accZ;
	
	private Float temp;
	
	private Float gravX;
	
	private Float gravY;
	
	private Float gravZ;
	
	private Float gyroX;
	
	private Float gyroY;
	
	private Float gyroZ;
	
	private Float light;
	
	private Float linAccX;
	
	private Float linAccY;
	
	private Float linAccZ;
	
	private Float magX;
	
	private Float magY;
	
	private Float magZ;
	
	private Float orientX;
	
	private Float orientY;
	
	private Float orientZ;
	
	private Float press;
	
	private Float hum;
	
	private Float rotX;
	
	private Float rotY;
	
	private Float rotZ;
	
	private Float rotScalar;

	public LeituraSensores() {
	}

	public LeituraSensores(Long idObservacao,Long idUnidadeSensores,Float accX,Float accY,Float accZ,Float temp,Float gravX,Float gravY,Float gravZ,Float gyroX,Float gyroY,Float gyroZ,
			Float light,Float linAccX,Float linAccY,Float linAccZ,Float magX,Float magY,Float magZ,Float orientX,Float orientY,Float orientZ,Float press,Float hum,Float rotX,Float rotY,
			Float rotZ,Float rotScalar) {
		super();
		this.idObservacao = idObservacao;
		this.idUnidadeSensores = idUnidadeSensores;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
		this.temp = temp;
		this.gravX = gravX;
		this.gravY = gravY;
		this.gravZ = gravZ;
		this.gyroX = gyroX;
		this.gyroY = gyroY;
		this.gyroZ = gyroZ;
		this.light = light;
		this.linAccX = linAccX;
		this.linAccY = linAccY;
		this.linAccZ = linAccZ;
		this.magX = magX;
		this.magY = magY;
		this.magZ = magZ;
		this.orientX = orientX;
		this.orientY = orientY;
		this.orientZ = orientZ;
		this.press = press;
		this.hum = hum;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.rotScalar = rotScalar;
	}

	public Long getId() {
		return id;
	}
	
	public Long getidObservacao() {
		return idObservacao;
	}

	public Long getidUnidadeSensores() {
		return idUnidadeSensores;
	}

	public Float getaccX() {
		return accX;
	}
	
	public Float getaccY() {
		return accY;
	}
	
	public Float getaccZ() {
		return accZ;
	}
	
	public Float gettemp() {
		return temp;
	}
	
	public Float getgravX() {
		return gravX;
	}
	
	public Float getgravY() {
		return gravY;
	}
	
	public Float getgravZ() {
		return gravZ;
	}
	
	public Float getgyroX() {
		return gyroX;
	}
	
	public Float getgyroY() {
		return gyroY;
	}
	
	public Float getgyroZ() {
		return gyroZ;
	}
	
	public Float getlight() {
		return light;
	}
	
	public Float getlinAccX() {
		return linAccX;
	}
	
	public Float getlinAccY() {
		return linAccY;
	}
	
	public Float getlinAccZ() {
		return linAccZ;
	}
	
	public Float getmagX() {
		return magX;
	}
	
	public Float getmagY() {
		return magY;
	}
	
	public Float getmagZ() {
		return magZ;
	}
	
	public Float getorientX() {
		return orientX;
	}
	
	public Float getorientY() {
		return orientY;
	}
	
	public Float getorientZ() {
		return orientZ;
	}
	
	public Float getpress() {
		return press;
	}
	
	public Float gethum() {
		return hum;
	}
	
	public Float getrotX() {
		return rotX;
	}
	
	public Float getrotY() {
		return rotY;
	}
	
	public Float getrotZ() {
		return rotZ;
	}
	
	public Float getrotScalar() {
		return rotScalar;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setidObservacao(Long idObservacao) {
		this.idObservacao = idObservacao;
	}

	public void setidUnidadeSensores(Long idUnidadeSensores) {
		this.idUnidadeSensores = idUnidadeSensores;
	}
	
	public void setaccX(Float accX) {
		this.accX = accX;
	}
	
	public void setaccY(Float accY) {
		this.accY = accY;
	}
	
	public void setaccZ(Float accZ) {
		this.accZ = accZ;
	}
	
	public void settemp(Float temp) {
		this.temp = temp;
	}
	
	public void setgravX(Float gravX) {
		this.gravX = gravX;
	}
	
	public void setgravY(Float gravY) {
		this.gravY = gravY;
	}
	
	public void setgravZ(Float gravZ) {
		this.gravZ = gravZ;
	}
	
	public void setgyroX(Float gyroX) {
		this.gyroX = gyroX;
	}
	
	public void setgyroY(Float gyroY) {
		this.gyroY = gyroY;
	}
	
	public void setgyroZ(Float gyroZ) {
		this.gyroZ = gyroZ;
	}
	
	public void setlight(Float light) {
		this.light = light;
	}
	
	public void setlinAccX(Float linAccX) {
		this.linAccX = linAccX;
	}
	
	public void setlinAccY(Float linAccY) {
		this.linAccY = linAccY;
	}
	
	public void setlinAccZ(Float linAccZ) {
		this.linAccZ = linAccZ;
	}
	
	public void setmagX(Float magX) {
		this.magX = magX;
	}
	
	public void setmagY(Float magY) {
		this.magY = magY;
	}
	
	public void setmagZ(Float magZ) {
		this.magZ = magZ;
	}
	
	public void setorientX(Float orientX) {
		this.orientX = orientX;
	}
	
	public void setorientY(Float orientY) {
		this.orientY = orientY;
	}
	
	public void setorientZ(Float orientZ) {
		this.orientZ = orientZ;
	}
	
	public void setpress(Float press) {
		this.press = press;
	}
	
	public void sethum(Float hum) {
		this.hum = hum;
	}
	
	public void setrotX(Float rotX) {
		this.rotX = rotX;
	}
	
	public void setrotY(Float rotY) {
		this.rotY = rotY;
	}
	
	public void setrotZ(Float rotZ) {
		this.rotZ = rotZ;
	}
	
	public void setrotScalar(Float rotScalar) {
		this.rotScalar = rotScalar;
	}
	
}
