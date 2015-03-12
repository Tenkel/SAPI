package br.ufrj.cos.labia.aips.ips.whips;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import br.ufrj.cos.labia.aips.dal.ZipBuilder;
import br.ufrj.cos.labia.aips.dal.ZipReader;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.IPSException;
import br.ufrj.cos.labia.aips.ips.InvalidModelException;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

public class WHIPS implements IPS {
	
	
	private static final String WISARD1 = "br.ufrj.cos.labia.aips.ips.whips.WiSARD1";

	private SignalEncoder mEncoder;

	private Wisard mWisard;

	private Map<Long, Integer> mPointIdToInt;

	private List<Long> mIntToPointId;

	public WHIPS(List<WIFISignal> signals) {
		boolean isMacro = true;
		mEncoder = new ClassicSignalEncoder(signals, isMacro);
		//mEncoder = new GroupSignalEncoder(signals, isMacro, 2);
		mWisard = new Wisard(mEncoder.getOutputSize(), isMacro ? 8 : 4);
		mPointIdToInt = new TreeMap<Long, Integer>();
		mIntToPointId = new ArrayList<Long>();
	}

	private WHIPS(List<Long> intToPointId, Map<Long, Integer> pointIdToInt,
			SignalEncoder encoder, Wisard wisard) {
		mIntToPointId = intToPointId;
		mPointIdToInt = pointIdToInt;
		mEncoder = encoder;
		mWisard = wisard;
	}

	@Override
	public void learn(Reading reading, Location location) {
		int target = getTargetFor(location.getPointId());
		mWisard.learn(mEncoder.encode(reading), target);
	}

	@Override
	public Location predict(Reading reading) {
		int predicted = mWisard.read(mEncoder.encode(reading));
		Log.e("WHIPS", "Predicted " + predicted);
		Location l = new Location();
		l.setPointId(getPointIdFor(predicted));
		Log.e("WHIPS", "PointId " + l.getPointId());
		return l;
	}

	@Override
	public float getConfidence() {
		return mWisard.getConfidence();
	}

	@Override
	public float getConfidence(Location location) {
		int target = getTargetFor(location.getPointId());
		return mWisard.getConfidence(target);
	}

	@Override
	public void close() {
		mWisard.close();
	}

	private int getTargetFor(Long pointId) {
		if (!mPointIdToInt.containsKey(pointId)) {
			Log.e("WHIPS", "Training " + pointId + " " + mIntToPointId.size());
			mPointIdToInt.put(pointId, mIntToPointId.size());
			mIntToPointId.add(pointId);
		}
		
		return mPointIdToInt.get(pointId);
	}

	private Long getPointIdFor(int predicted) {
		if (predicted < 0 || predicted >= mIntToPointId.size())
			throw new IPSException("Invalid predicted value");
		
		return mIntToPointId.get(predicted);
	}

	public static IPS load(Context context, String nomeVersao, String filename) throws InvalidModelException {
		if (nomeVersao.equals(WISARD1)) {
			return loadWisard1(context, filename);
		} else {
			throw new IPSException("Versão não reconhecida");
		}
	}

	@SuppressWarnings("unchecked")
	private static IPS loadWisard1(Context context, String filename) {
		try {
			File models = context.getDir("models", Context.MODE_PRIVATE);
			File tmp = context.getDir("tmp", Context.MODE_PRIVATE);
			
			File zipFile = new File(models, filename);
			File wisardFile = new File(tmp, UUID.randomUUID().toString());
			
			ByteArrayOutputStream javaStream = new ByteArrayOutputStream();
			FileOutputStream wisardStream = new FileOutputStream(wisardFile);
			
			new ZipReader(zipFile)
					.get("javaStuff", javaStream)
					.get("wisardStuff", wisardStream);
			
			ByteArrayInputStream javaFileReader = new ByteArrayInputStream(
					javaStream.toByteArray());
			
			// Carrega os dados em java
			ObjectInputStream ois = new ObjectInputStream(javaFileReader);
			List<Long> intToPointId = (List<Long>) ois.readObject();
			Map<Long, Integer> pointIdToInt = (Map<Long, Integer>) ois.readObject();
			SignalEncoder encoder = (SignalEncoder) ois.readObject();
			ois.close();
			
			// Carrega a wisard
			Wisard wisard = new Wisard(wisardFile.getAbsolutePath());
			
			if (intToPointId == null || pointIdToInt == null || encoder == null || wisard == null)
				throw new IPSException("Failed to load the model");
			
			return new WHIPS(intToPointId, pointIdToInt, encoder, wisard);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IPSException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String getNomeVersao() {
		return WISARD1;
	}

	@Override
	public void save(Context context, String filename) {
		try {
			File models = context.getDir("models", Context.MODE_PRIVATE);
			File tmp = context.getDir("tmp", Context.MODE_PRIVATE);
			
			File zipFile = new File(models.getAbsolutePath(), filename);
			File javaFile = new File(tmp.getAbsolutePath(), UUID.randomUUID().toString());
			File wisardFile = new File(tmp.getAbsolutePath(), UUID.randomUUID().toString());
			
			// Grava o arquivo com os dados serializados em java
			ObjectOutputStream joos = new ObjectOutputStream(
					new FileOutputStream(javaFile));
			
			joos.writeObject(mIntToPointId);
			joos.writeObject(mPointIdToInt);
			joos.writeObject(mEncoder);
			joos.close();
			
			// Grava o arquivo binario da wisard
			mWisard.exportTo(wisardFile.getAbsolutePath());
			
			// Comprime ambos em um único arquivo
			new ZipBuilder(zipFile)
				.put("javaStuff", new FileInputStream(javaFile))
				.put("wisardStuff", new FileInputStream(wisardFile))
				.close();
			
			// Remove os arquivos temporários
			javaFile.delete();
			wisardFile.delete();
			
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		throw new IPSException("Could not export model");
	}

	public static boolean canLoad(String nomeVersao) {
		if (nomeVersao == null) return false;
		if (nomeVersao.equals(WISARD1)) return true;
		return false;
	}

}
