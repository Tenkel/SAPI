package br.ufrj.cos.labia.aips.ips;

public class IPSException extends RuntimeException {

	private static final long serialVersionUID = -3068987552512982735L;

	public IPSException() {
		super();
	}

	public IPSException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public IPSException(String detailMessage) {
		super(detailMessage);
	}

	public IPSException(Throwable throwable) {
		super(throwable);
	}

}
