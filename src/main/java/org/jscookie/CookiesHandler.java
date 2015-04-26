package org.jscookie;

import javax.servlet.http.Cookie;

import org.jscookie.Cookies.Attributes;

public interface CookiesHandler {
	/**
	 * Retrieve a cookie
	 * 
	 * @return <code>null</code> if the cookie doesn't exist
	 */
	Cookie get( String name );

	/**
	 * Create or update an existing cookie overriding the default attributes
	 */
	void set( String name, String value, Attributes attributes );

	/**
	 * Create or update an existing cookie using the default attributes
	 */
	void set( String name, String value );

	/**
	 * Remove an existing cookie
	 * 
	 * @param attributes
	 *        You must pass the exact same path, domain and secure attributes that were used to set
	 *        the cookie, unless you're relying on the default attributes
	 */
	void remove( String name, Attributes attributes );

	/**
	 * Remove an existing cookie using the default attributes
	 */
	void remove( String name );

	/**
	 * Change the default attributes of this instance
	 */
	void setDefaults( Attributes attributes );
}
