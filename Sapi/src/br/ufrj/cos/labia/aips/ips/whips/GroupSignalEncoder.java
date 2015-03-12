package br.ufrj.cos.labia.aips.ips.whips;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.util.Log;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

public class GroupSignalEncoder implements SignalEncoder {
	
	private static final int TERM_BITS = 40;
	private static final int MACRO_BOTTOM = 50;
	private static final int MACRO_TOP = 90;
	private static final int MICRO_BOTTOM = 30;
	private static final int MICRO_TOP = 70;
	
	private Map<String, Integer> mIndexes;
	private int[] mBuffer2;
	private int mBottom;
	private int mTop;
	private int[] mGroups;
	private int[] mBuffer1;
	
	public GroupSignalEncoder(List<WIFISignal> signals, boolean useMacroFocus, int groupSize) {
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
		
		mGroups = createGroupMapping(mIndexes.size(), groupSize);
		mBuffer1 = new int[mGroups.length];
		mBuffer2 = new int[mGroups.length * TERM_BITS];
		Log.i("Encoder", "APs found: " + mIndexes.size());
	}
	
	private int[] createGroupMapping(int numSignals, int groupSize) {
		int[] groups = new int[numSignals];
		
		Random r = new Random();
		for (int i=0;i<numSignals;++i) {
			int index = r.nextInt(numSignals-i);
			int group = (int) Math.floor(i / groupSize);
			
			if (group == groupSize)
				groups[index] = groupSize - 1;
			else
				groups[index] = group;
		}
		
		return groups;
	}
	
	/* (non-Javadoc)
	 * @see com.github.diegofps.android.scdp.ips.whips.SignalEncoder#encode(java.util.List)
	 */
	@Override
	public int[] encode(Reading reading) {
		
		for (int i=0;i<mBuffer1.length;++i) {
			mBuffer1[i] = TERM_BITS;
		}
		
		for (int i=0;i<mBuffer2.length;++i) {
			mBuffer2[i] = 1;
		}
		
		//Log.i("Encoder", "" + reading.getSignals().size() / (float) mIndexes.size());
		
		for (WIFISignal signal : reading.getSignals()) {
			if (mIndexes.containsKey(signal.getBSSID())) {
				int index = mGroups[mIndexes.get(signal.getBSSID())];
				
				int bits1 = (int) Math.round((-signal.getLevel() - mBottom)
						/ (float) (mTop - mBottom) * TERM_BITS);
				//int bits1 = -signal.getLevel() * -signal.getLevel() * 2000 / (-100*-100);
				
				if (bits1 > TERM_BITS) bits1 = TERM_BITS;
				else if (bits1 < 0) bits1 = 0;
				
				if (mBuffer1[index] > bits1) {
					mBuffer1[index] = bits1;
				}
			}
		}
		
		for (int j=0;j<mBuffer1.length;++j) {
			for (int i=0;i<TERM_BITS-mBuffer1[j];++i) {
				mBuffer2[j * TERM_BITS + i] = 0;
			}
		}
		
		return mBuffer2;
	}

	/* (non-Javadoc)
	 * @see com.github.diegofps.android.scdp.ips.whips.SignalEncoder#getOutputSize()
	 */
	@Override
	public int getOutputSize() {
		return mBuffer2.length;
	}
	
}
