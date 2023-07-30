package com.bloomscorp.behemoth.contract.service;

public abstract class BehemothMiddleware<P, S extends BehemothMiddleware<P, ?>> {

	private P parameter = null;

	protected S setParameter(P parameter) {
		this.parameter = parameter;
		return this.getThis();
	}

	protected P getParameter() {
		if (this.parameter == null)
			throw new NullPointerException("setParameter(P) might not have been implemented in " + this.getClass().getName());
		return this.parameter;
	}

	protected abstract S getThis();
	public abstract boolean execute();
	public abstract String getErrorMessage();
}
