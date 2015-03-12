package br.ufrj.cos.labia.aips.ips.whips;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

public class ClassicSignalEncoder implements SignalEncoder, Serializable {
	
	private static final long serialVersionUID = -1622696227773687289L;
	
	private static final int TERM_BITS = 40;
	private static final int MACRO_BOTTOM = 50;
	private static final int MACRO_TOP = 90;
	private static final int MICRO_BOTTOM = 30;
	private static final int MICRO_TOP = 70;
	
	private Map<String, Integer> mIndexes;
	private int[] mBuffer;
	private int mBottom;
	private int mTop;
	
	public ClassicSignalEncoder(List<WIFISignal> signals, boolean useMacroFocus) {
		mIndexes = new HashMap<String, Integer>();
		
		if (useMacroFocus) {
			mBottom = MACRO_BOTTOM;
			mTop = MACRO_TOP;
			
			for (WIFISignal signal : signals)
				if (!mIndexes.containsKey(signal.getBSSID()))
					mIndexes.put(signal.getBSSID(), mIndexes.size());
		} else {
			mBottom = MICRO_BOTTOM;
			mTop = MICRO_TOP;
			
			for (WIFISignal signal : signals)
				if (-signal.getLevel() < MICRO_TOP)
					if (!mIndexes.containsKey(signal.getBSSID()))
						mIndexes.put(signal.getBSSID(), mIndexes.size());
		}
		
		mBuffer = new int[mIndexes.size() * TERM_BITS];
		Log.i("Encoder", "APs found: " + mIndexes.size());
	}

	/* (non-Javadoc)
	 * @see com.github.diegofps.android.scdp.ips.whips.SignalEncoder#encode(java.util.List)
	 */
	@Override
	public int[] encode(Reading reading) {
		for (int i=0;i<mBuffer.length;++i) {
			mBuffer[i] = 1;
		}
		
		//Log.i("Encoder", "" + reading.getSignals().size() / (float) mIndexes.size());
		
		for (WIFISignal signal : reading.getSignals()) {
			if (mIndexes.containsKey(signal.getBSSID())) {
				int index = mIndexes.get(signal.getBSSID());
				
				int bits1 = (int) Math.round((-signal.getLevel() - mBottom) 
						/ (float) (mTop - mBottom) * TERM_BITS);
				//int bits1 = -signal.getLevel() * -signal.getLevel() * 2000 / (-100*-100);
				
				if (bits1 > TERM_BITS) bits1 = TERM_BITS;
				else if (bits1 < 0) bits1 = 0;
				
				for (int i=0;i<TERM_BITS-bits1;++i)
					mBuffer[index * TERM_BITS + i] = 0;
			}
		}
		
		return mBuffer;
	}

	/* (non-Javadoc)
	 * @see com.github.diegofps.android.scdp.ips.whips.SignalEncoder#getOutputSize()
	 */
	@Override
	public int getOutputSize() {
		return mBuffer.length;
	}
	
}
