package org.jscookie;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

public final class Expiration {
	private final DateTime dateTime;
	private final Integer days;
	private Expiration( DateTime dateTime ) {
		this.dateTime = dateTime;
		this.days = null;
	}
	private Expiration( Integer days ) {
		this.dateTime = null;
		this.days = days;
	}
	public static Expiration days( int days ) {
		return new Expiration( days );
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
	int toSecondsFromNow() {
		int seconds = 0;
		if ( days != null ) {
			int oneDayInSeconds = 86400;
			seconds = oneDayInSeconds * days;
		} else if ( dateTime != null ) {
			Seconds period = Seconds.secondsBetween( DateTime.now(), dateTime );
			seconds = period.getSeconds();
		}
		return seconds;
	}
}
