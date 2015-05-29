package org.rsimulator.aop;

public class BarException extends Exception {
	private String code;

	public BarException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
