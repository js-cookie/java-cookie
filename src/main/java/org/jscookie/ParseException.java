package org.jscookie;

class ParseException extends Exception  {
	private static final long serialVersionUID = 1;
	ParseException( Throwable cause ) {
		super( cause );
	}
	ParseException( String msg ) {
		super( msg );
	}
}
