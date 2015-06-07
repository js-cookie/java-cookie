package org.jscookie;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Expiration {
	private DateTimeFormatter EXPIRES_FORMAT = DateTimeFormat
		.forPattern( "EEE, dd MMM yyyy HH:mm:ss 'GMT'" )
		.withLocale( Locale.US );
	private final DateTime date;
	private Expiration( DateTime dateTime ) {
		this.date = dateTime;
	}
	public static Expiration days( int days ) {
		DateTime withDays = new DateTime().plusDays( days );
		return new Expiration( withDays );
	}
	public static Expiration date( DateTime dateTime ) {
		if ( dateTime == null ) {
			throw new IllegalArgumentException();
		}
		return new Expiration( dateTime );
	}
	public static Expiration date( Date date ) {
		if ( date == null ) {
			throw new IllegalArgumentException();
		}
		DateTime dateTime = new DateTime( date );
		return new Expiration( dateTime );
	}
	String toExpiresString() {
		return date.toString( EXPIRES_FORMAT );
	}
}
