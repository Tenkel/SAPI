package br.ufrj.cos.labia.aips.ips.kde;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/*
 * Instrumental class for KDE storage and management.
 * 
 * KDE distributions of RSSI's usually go from -40 to -100, with minor variations.
 * For sturdiness reasons it's stored as a 0..120 array index (with DEMAX = 120). 
 * 
 */
public class KDEFunction implements Serializable {
	// BSSID
	private String BSSID;
	// Automatic created serial version for saved file (class) version control.
	private static final long serialVersionUID = 8577653431740491416L;
	// Gaussian convertion from MAD to std dev
	private static final double K = 1.48260221850560186054707652936042343132670320259031289653626627524567444762269507362139420351582823911612666986905846932;
	// h = bandwidth
	private double h;
	// List of computed RSSI for this function
	// TODO check if it cannot be moved to another class.
	CircularFifoQueue<Integer> RSSI_list;
	// -DEMAX is expected to be the lowest value.
	private static final int DEMAX = 120;
	// LOG KDE data distribution
	private float[] log_de = new float[DEMAX + 1];
	// Standard KDE for undiscovered wifi signals.
	public static final float KDEstd = -200;

	/*
	 * Read the column values from the samplelist and use it as base for the
	 * KDE.
	 */
	public KDEFunction(String BSSID) {
		setBSSID(BSSID);
		RSSI_list = new CircularFifoQueue<Integer>(50);
	}

	public KDEFunction(String BSSID, int memory_size) {
		setBSSID(BSSID);
		RSSI_list = new CircularFifoQueue<Integer>(memory_size);
	}

	public KDEFunction(int[] samplelist, int samplesize) {
		recalulate(samplelist, samplesize);
	}

	/*
	 * Armazena a nova potência, porém NÃO processa.
	 * 
	 * @see br.ufrj.cos.labia.aips.ips.kde.learn
	 */
	public void memorize(int signallevel) {
		RSSI_list.add(Math.abs(signallevel));
	}

	/*
	 * Processa todos os dados amazenados.
	 * 
	 * @return largura de banda do kernel.
	 */
	public double learn() {
		return processQueue();
	}

	/*
	 * Amazena a nova potência, em seguida, processa todos os dados amazenados.
	 * 
	 * @return largura de banda do kernel.
	 */
	public double learn(int signallevel) {
		RSSI_list.add(Math.abs(signallevel));
		return processQueue();
	}

	/*
	 * Agrupa as duas funções, reescala o tamanho do buffer para acomodar as
	 * duas, se necessário.
	 * 
	 * @return largura de banda do kernel.
	 */
	public double merge(KDEFunction kde) {
		if (RSSI_list.maxSize() < RSSI_list.size() + kde.RSSI_list.size()) {
			CircularFifoQueue<Integer> RSSI_new_list = new CircularFifoQueue<Integer>(
					RSSI_list.size() + kde.RSSI_list.size());
			RSSI_new_list.addAll(RSSI_list);
			RSSI_new_list.addAll(kde.RSSI_list);
			RSSI_list = RSSI_new_list;
		} else
			RSSI_list.addAll(kde.RSSI_list);

		return processQueue();
	}

	private double processQueue() {

		int n = RSSI_list.size();

		double[] kernel = new double[2 * DEMAX + 1];

		// Finding sub-optimal h
		DescriptiveStatistics base_stats = new DescriptiveStatistics();

		Iterator<Integer> sample = RSSI_list.iterator();

		while (sample.hasNext())
			base_stats.addValue(sample.next());

		double median = base_stats.getPercentile(50);

		DescriptiveStatistics dev_stats = new DescriptiveStatistics();

		sample = RSSI_list.iterator();

		while (sample.hasNext())
			dev_stats.addValue(Math.abs(sample.next() - median));

		// "conversion" from MAD to std dev
		double sig = K * dev_stats.getPercentile(50);

		// if coulnd't compute, give some spread info
		if (sig <= 0)
			sig = base_stats.getMax() - base_stats.getMin();

		if (sig > 0) // Silverman rule of DUMB
			h = Math.pow(4.0 / (3 * n), 0.2) * sig;
		else
			// if everything goes wrong e.g. No std dev
			h = 1;

		// Creating Kernel
		double a = 1.0 / (n * h * Math.pow(2 * Math.PI, 0.5));
		double b = 1.0 / Math.pow(Math.E, 1.0 / (2 * h * h));

		for (int i = 0; i < 2 * DEMAX + 1; i++)
			kernel[i] = a * Math.pow(b, (i - DEMAX) * (i - DEMAX));

		// Creating DE (Kernel convolution)
		double[] de = new double[DEMAX + 1];

		for (int p = 0; p < DEMAX + 1; p++)
			de[p] = 0;

		sample = RSSI_list.iterator();

		while (sample.hasNext()) {
			int kernel_center = sample.next();

			for (int p = 0; p < DEMAX + 1; p++)
				de[p] += kernel[DEMAX + p - kernel_center];
		}

		for (int p = 0; p < DEMAX + 1; p++)
			log_de[p] = (float) Math.log(de[p]);

		return h;

	}

	// power is minus the index of the array.
	public float log_prob(int power) {
		return log_de[Math.abs(power)];
	}

	public double bandwidth() {
		return h;
	}

	/*
	 * Calculate KDE from samples (and its size 'n') and return the found
	 * bandwidth value.
	 */
	public double recalulate(int[] sample, int n) {

		double[] kernel = new double[2 * DEMAX + 1];

		// Finding sub-optimal h
		DescriptiveStatistics base_stats = new DescriptiveStatistics();

		for (int i = 0; i < n; i++) {
			sample[i] = Math.abs(sample[i]);
			base_stats.addValue(sample[i]);
		}

		double median = base_stats.getPercentile(50);

		DescriptiveStatistics dev_stats = new DescriptiveStatistics();

		for (int i = 0; i < n; i++)
			dev_stats.addValue(Math.abs(sample[i] - median));

		// "conversion" from MAD to std dev
		double sig = K * dev_stats.getPercentile(50);

		// if coulnd't compute, give some spread info
		if (sig <= 0)
			sig = base_stats.getMax() - base_stats.getMin();

		if (sig > 0) // Silverman rule of DUMB
			h = Math.pow(4.0 / (3 * n), 0.2) * sig;
		else
			// if everything goes wrong e.g. No std dev
			h = 1;

		// Creating Kernel
		double a = 1.0 / (n * h * Math.pow(2 * Math.PI, 0.5));
		double b = 1.0 / Math.pow(Math.E, 1.0 / (2 * h * h));

		for (int i = 0; i < 2 * DEMAX + 1; i++)
			kernel[i] = a * Math.pow(b, (i - DEMAX) * (i - DEMAX));

		// Creating DE (Kernel convolution)
		double[] de = new double[DEMAX + 1];

		for (int p = 0; p < DEMAX + 1; p++)
			de[p] = 0;

		for (int i = 0; i < n; i++)
			for (int p = 0; p < DEMAX + 1; p++)
				de[p] += kernel[DEMAX + p + sample[i]];

		for (int p = 0; p < DEMAX + 1; p++)
			log_de[p] = (float) Math.log(de[p]);

		return h;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

}