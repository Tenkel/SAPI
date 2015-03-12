package br.ufrj.cos.labia.aips.ips.whips;

import br.ufrj.cos.labia.aips.ips.ClosedIPSException;
import br.ufrj.cos.labia.aips.ips.IPSException;

public class Wisard {

	static {
		System.loadLibrary("native");
	}
	
	long mNative;
	
	public Wisard(int inputLength, int ramBits) {
		if (inputLength < 1)
			throw new IPSException("Invalid inputLength");
		
		if (ramBits < 1)
			throw new IPSException("Invalid ramBits");
		
		mNative = native_create(inputLength, ramBits);
	}

	public Wisard(String filename) {
		mNative = native_importFrom(filename);
		
		if (mNative == 0)
			throw new IPSException("Could not import wisard from this file");
	}

	public void learn(int[] pattern, int target) {
		if (mNative == 0)
			throw new ClosedIPSException();
		
		if (target < 0)
			throw new IPSException("target can not be smaller than zero");
		
		native_learn(mNative, pattern, target);
	}

	public int read(int[] pattern) {
		if (mNative == 0)
			throw new ClosedIPSException();
		
		return native_read(mNative, pattern);
	}

	public float getConfidence() {
		if (mNative == 0)
			throw new ClosedIPSException();
		
		return native_getConfidence(mNative);
	}

	public float getConfidence(int target) {
		if (mNative == 0)
			throw new ClosedIPSException();
		
		return native_getActivation(mNative, target);
	}

	public void close() {
		native_destroy(mNative);
		mNative = 0;
	}

	public void exportTo(String filename) {
		if (mNative == 0)
			throw new ClosedIPSException();
		
		if (filename == null)
			throw new IPSException("Filename is null");
		
		native_exportTo(mNative, filename);
	}

	private native long native_create(int inputLength, int ramBits);
	
	private native void native_learn(long ptr, int[] pattern, int target);
	
	private native int native_read(long ptr, int[] pattern);
	
	private native float native_getConfidence(long ptr);
	
	private native float native_getActivation(long ptr, int target);
	
	private native void native_destroy(long ptr);

	private native void native_exportTo(long ptr, String filename);
	
	private native long native_importFrom(String filename);
	
}
