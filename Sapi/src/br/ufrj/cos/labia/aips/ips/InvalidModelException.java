package br.ufrj.cos.labia.aips.ips;

public class InvalidModelException extends Exception {

	private static final long serialVersionUID = -4331850922736016705L;

	public InvalidModelException() {
		super();
	}

	public InvalidModelException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public InvalidModelException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidModelException(Throwable throwable) {
		super(throwable);
	}

}
