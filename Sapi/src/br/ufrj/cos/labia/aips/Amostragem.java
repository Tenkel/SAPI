package br.ufrj.cos.labia.aips;

public class Amostragem {

	private double min;
	
	private double max;
	
	private double s1;
	
	private double s2;
	
	private int n;
	
	public Amostragem() {
		super();
	}

	public Amostragem(double sample) {
		super();
		add(sample);
	}

	public void add(double sample) {
		if (n == 0) {
			min = sample;
			max = sample;
		} else {
			if (sample < min)
				min = sample;
			
			if (sample > max)
				max = sample;
		}
		
		s2 += sample * sample;
		s1 += sample;
		n += 1;
	}
	
	public double getMean() {
		if (n == 0)
			return 0.0;
		else
			return s1 / n;
	}
	
	public double getStd() {
		if (n <= 1)
			return 0.0;
		else
			return Math.sqrt((s2-s1*s1/n) / (n-1));
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}

	public void clear() {
		n = 0;
		s1 = 0.0;
		s2 = 0.0;
		min = 0;
		max = 0;
	}
	
}
