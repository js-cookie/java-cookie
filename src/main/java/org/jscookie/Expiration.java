package org.jscookie;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class Expiration {
	private DateTime dateTime;
	private Integer days;
	private Expiration( DateTime dateTime ) {
		this.dateTime = dateTime;
	}
	private Expiration( Integer days ) {
		this.days = days;
	}
	public static Expiration days( int days ) {
		return new Expiration( days );
	}
	public static Expiration date( DateTime dateTime ) {
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
