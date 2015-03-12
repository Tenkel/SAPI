package br.ufrj.cos.labia.aips.ips;

public class ClosedIPSException extends RuntimeException {

	private static final long serialVersionUID = 6134986354114392L;

	public ClosedIPSException() {
		super();
	}

	public ClosedIPSException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ClosedIPSException(String detailMessage) {
		super(detailMessage);
	}

	public ClosedIPSException(Throwable throwable) {
		super(throwable);
	}

}
