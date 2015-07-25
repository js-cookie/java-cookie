package com.github.jscookie.javacookie;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

interface CookiesDefinition {
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
	 * @throws CookieParseException
	 *         If there's an error while parsing the cookie name using the given dataType
	 */
	<T> T get( String name, Class<T> dataType ) throws CookieParseException;

	/**
	 * Retrieves a cookie and parse it using the given type reference. The type reference is used
	 * to infer generic types from within a class and workaround Java Type Erasure.<br>
	 * 
	 * For more information check http://wiki.fasterxml.com/JacksonDataBinding#Full_Data_Binding
	 * 
	 * @throws CookieParseException
	 *         If there's an error while parsing the cookie name using the given dataType
	 * 
	 * @see #get(String, Class)
	 */
	<T> T get( String name, TypeReference<T> typeRef ) throws CookieParseException;

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
	 * @see #get(String)
	 */
	void set( String name, String value, AttributesDefinition attributes );

	/**
	 * Create or update an existing cookie extending the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String, AttributesDefinition)
	 */
	void set( String name, int value, AttributesDefinition attributes ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie extending the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String, AttributesDefinition)
	 */
	void set( String name, boolean value, AttributesDefinition attributes ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie extending the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String, AttributesDefinition)
	 */
	<T> void set( String name, List<T> value, AttributesDefinition attributes ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie extending the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String, AttributesDefinition)
	 */
	void set( String name, CookieValue value, AttributesDefinition attributes ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie using the default attributes
	 * 
	 * @see #set(String, String, AttributesDefinition)
	 */
	void set( String name, String value );

	/**
	 * Create or update an existing cookie using the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String)
	 */
	void set( String name, int value ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie using the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String)
	 */
	void set( String name, boolean value ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie using the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String)
	 */
	<T> void set( String name, List<T> value ) throws CookieSerializationException;

	/**
	 * Create or update an existing cookie extending the default attributes and serializing the typed value
	 * 
	 * @see #set(String, String)
	 */
	void set( String name, CookieValue value ) throws CookieSerializationException;

	/**
	 * Remove an existing cookie<br>
	 * By default, assumes the characters not allowed in each cookie name are encoded with each
	 * one's UTF-8 Hex equivalent using percent-encoding.
	 * 
	 * @param attributes
	 *        You must pass the exact same path, domain and secure attributes that were used to set
	 *        the cookie, unless you're relying on the default attributes
	 * 
	 * @see #get(String)
	 */
	void remove( String name, AttributesDefinition attributes );

	/**
	 * Remove an existing cookie using the default attributes
	 * 
	 * @see #remove(String, AttributesDefinition) 
	 */
	void remove( String name );

	/**
	 * Retrieve the default attributes of this instance
	 */
	AttributesDefinition defaults();

	/**
	 * Create a new instance of the api that overrides the default decoding implementation<br>
	 * All methods that rely in a proper decoding to work, such as
	 * {@link #remove(String, AttributesDefinition)} and {@link #get(String)}, will run the converter first
	 * for each cookie.<br>
	 * The returning String will be used as the cookie value.
	 */
	CookiesDefinition withConverter( ConverterStrategy converter );
}
