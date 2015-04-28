package org.jscookie;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface CookiesDefinition {
	/**
	 * Retrieves a cookie<br>
	 * By default, assumes the characters not allowed in each cookie name are encoded with each
	 * one's UTF-8 Hex equivalent using percent-encoding.
	 * 
	 * @return <code>null</code> if the cookie doesn't exist
	 */
	String get( String name );

	/**
	 * Retrieves a cookie and parse it using the given dataType instance
	 * 
	 * @throws ParseException
	 *         If there's an error while parsing the cookie name using the given dataType
	 */
	<T> T get( String name, Class<T> dataType ) throws ParseException;

	/**
	 * Retrieves all cookies
	 * 
	 * @see #get(String)
	 */
	Map<String, String> get();

	/**
	 * Create or update an existing cookie extending the default attributes<br>
	 * By default, the characters not allowed in the cookie name or value are encoded with each
	 * one's UTF-8 Hex equivalent using percent-encoding.
	 * 
	 * @throws UnsupportedEncodingException 
	 *         If the current encoding being used to encode/decode the cookie name or value is not
	 *         supported
	 * 
	 * @see #get(String)
	 */
	void set( String name, String value, Attributes attributes ) throws UnsupportedEncodingException;

	/**
	 * Create or update an existing cookie using the default attributes<br>
	 * 
	 * @see #set(String, String, Attributes)
	 */
	void set( String name, String value ) throws UnsupportedEncodingException;

	/**
	 * Remove an existing cookie<br>
	 * By default, assumes the characters not allowed in each cookie name are encoded with each
	 * one's UTF-8 Hex equivalent using percent-encoding.
	 * 
	 * @param attributes
	 *        You must pass the exact same path, domain and secure attributes that were used to set
	 *        the cookie, unless you're relying on the default attributes
	 * 
	 * @throws UnsupportedEncodingException
	 *         If the current encoding being used to decode the cookie name is not supported
	 * 
	 * @see #get(String)
	 */
	void remove( String name, Attributes attributes ) throws UnsupportedEncodingException;

	/**
	 * Remove an existing cookie using the default attributes
	 * 
	 * @see #remove(String, Attributes) 
	 */
	void remove( String name ) throws UnsupportedEncodingException;

	/**
	 * Change the default attributes of this instance
	 */
	void setDefaults( Attributes attributes );

	/**
	 * Create a new instance of the api that overrides the default decoding implementation<br>
	 * All methods that rely in a proper decoding to work, such as
	 * {@link #remove(String, Attributes)} and {@link #get(String)}, will run the converter first
	 * for each cookie.<br>
	 * The returning String will be used as the cookie value.
	 */
	CookiesDefinition withConverter( Converter converter );

	abstract class Attributes {
		abstract Expiration expires();
		abstract String path();
		abstract String domain();
		abstract Boolean secure();
	}

	abstract class Converter {
		/**
		 * Determine the decoding strategy of a cookie. The return will be used as the cookie value
		 * 
		 * @return <code>null</code> if the default encoding mechanism should be used instead
		 */
		public abstract String convert( String value, String name ) throws ConverterException;
	}
}
