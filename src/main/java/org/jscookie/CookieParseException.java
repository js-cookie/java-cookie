package org.jscookie;

public class CookieParseException extends Exception  {
	private static final long serialVersionUID = 1;
	CookieParseException( Throwable cause ) {
		super( cause );
	}
	CookieParseException( String msg ) {
		super( msg );
	}
}
