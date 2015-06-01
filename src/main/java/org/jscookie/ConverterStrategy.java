package org.jscookie;

interface ConverterStrategy {
	/**
	 * Apply the decoding strategy of a cookie. The return will be used as the cookie value
	 * 
	 * @return <code>null</code> if the default encoding mechanism should be used instead
	 */
	public abstract String convert( String value, String name ) throws ConverterException;
}
