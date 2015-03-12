package br.ufrj.cos.labia.aips.ips.whips;

import br.ufrj.cos.labia.aips.ips.Reading;

public interface SignalEncoder {

	public int[] encode(Reading reading);

	public int getOutputSize();

}