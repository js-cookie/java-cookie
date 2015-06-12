package org.jscookie;

public final class CookieParseException extends Exception  {
	private static final long serialVersionUID = 1;
	@SuppressWarnings( "unused" )
	private CookieParseException() {}
	CookieParseException( Throwable cause ) {
		super( cause );
	}
	CookieParseException( String msg ) {
		super( msg );
	}
}
