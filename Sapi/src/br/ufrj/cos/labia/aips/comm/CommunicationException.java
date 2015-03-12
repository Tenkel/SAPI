package br.ufrj.cos.labia.aips.comm;

public class CommunicationException extends RuntimeException {

	private static final long serialVersionUID = -7141062155072935676L;

	public CommunicationException() {
		super();
	}

	public CommunicationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CommunicationException(String detailMessage) {
		super(detailMessage);
	}

	public CommunicationException(Throwable throwable) {
		super(throwable);
	}

}
